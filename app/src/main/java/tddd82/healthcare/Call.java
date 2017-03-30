package tddd82.healthcare;


import android.app.Activity;
import android.widget.ImageView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Call {

    private final int UDP_SOCKET_TIMEOUT = 10000;

    private DatagramSocket socket;

    private InetAddress address;
    private String host;
    private int port;
    private boolean alive;
    private boolean initialized;

    private int senderNumber;
    private int receiverNumber;

    private VoiceBuffer voiceReceiverBuffer;
    private VoiceBuffer voiceRecordBuffer;
    private int voiceSendSequenceNumber;
    private int voiceLastReceivedSequenceNumber;
    private int videoSendSequenceNumber;
    private int videoLastReceivedSequenceNumber;

    private VideoBuffer videoReceiverBuffer;
    private VideoBuffer videoRecordBuffer;

    private CallEvent eventHandler;

    private VoiceCall voiceCall;
    private VideoCall videoCall;

    private Activity activity;


    public Call(String host, int port, int senderPhoneNumber, int receiverPhoneNumber, CallEvent eventHandler, ImageView displayView, Activity activity){
        this.host = host;
        this.port = port;
        this.eventHandler = eventHandler;
        this.senderNumber = senderPhoneNumber;
        this.receiverNumber = receiverPhoneNumber;

        this.voiceReceiverBuffer = new VoiceBuffer();
        this.voiceRecordBuffer = new VoiceBuffer();
        this.videoReceiverBuffer = new VideoBuffer();
        this.videoRecordBuffer = new VideoBuffer();

        this.voiceSendSequenceNumber = 0;
        this.voiceLastReceivedSequenceNumber = 0;
        this.videoLastReceivedSequenceNumber = 0;
        this.videoSendSequenceNumber = 0;

        this.voiceCall = new VoiceCall(voiceReceiverBuffer, voiceRecordBuffer, eventHandler);
        this.videoCall = new VideoCall(videoRecordBuffer, videoReceiverBuffer, displayView, activity);

        this.alive = false;
        this.initialized = false;
    }

    public CallError initialize(){
       CallError voiceErr = voiceCall.initialize();

        if (voiceErr != CallError.SUCCESS)
            return voiceErr;

        CallError videoErr = videoCall.initialize();

        if (videoErr != CallError.SUCCESS)
            return videoErr;

        try{
            this.address = InetAddress.getByName(host);
        } catch (UnknownHostException ex){
            System.out.println(ex.getMessage());
            return CallError.SERVER_NOT_REACHABLE;
        }

        try {
            socket = new DatagramSocket(this.port);
            socket.setSoTimeout(UDP_SOCKET_TIMEOUT);
        } catch (SocketException ex){
            System.out.println(ex.getMessage());
            return CallError.SOCKET_ERROR;
        }

        this.initialized = true;

        return CallError.SUCCESS;
    }

    public boolean start(){
        if (!initialized)
            return false;

        voiceCall.start();
        videoCall.start();

        this.alive = true;

        new Thread(new Runnable(){
            @Override
            public void run(){
                receiverWorker();
            }
        }).start();


        new Thread(new Runnable(){
            @Override
            public void run(){
                senderWorker();
            }
        }).start();


        return true;
    }

    public void terminate(){
        alive = false;
        initialized = false;

        voiceCall.terminate();

        if(socket != null){
            socket.disconnect();
            socket.close();
        }

    }

    private void receiverWorker(){

        while(alive){
            DataPacket data = new DataPacket(DataPacket.MAX_SIZE);
            DatagramPacket p = new DatagramPacket(data.getBuffer(), data.getLength());

            try {
                socket.receive(p);
            } catch (SocketTimeoutException ex) {
                eventHandler.onTimeout(this.voiceSendSequenceNumber, this.receiverNumber);
                continue;
            } catch (IOException ex){
                continue;
            }

            if (data.validChecksum()) {
                if (data.hasFlag(DataPacket.FLAG_IS_VIDEO) && data.getSequenceNumber() >= videoLastReceivedSequenceNumber) {
                    videoReceiverBuffer.push(data);
                    videoLastReceivedSequenceNumber++;
                } else if (!data.hasFlag(DataPacket.FLAG_IS_VIDEO) && data.getSequenceNumber() >= voiceLastReceivedSequenceNumber) {
                    voiceReceiverBuffer.push(data);
                    voiceLastReceivedSequenceNumber++;
                }
            }
        }
    }

    private DataPacket getSendPacket() {
        if (!voiceRecordBuffer.empty()) {
            return voiceRecordBuffer.poll();
        }

        if (!videoRecordBuffer.empty()) {
            return videoRecordBuffer.poll();
        }

        return null;

    }

    private void senderWorker(){

        while(alive){

            DataPacket data = getSendPacket();
            if (data != null){
                data.setSource(this.senderNumber);
                data.setDestination(this.receiverNumber);
                data.setSequenceNumber(data.hasFlag(DataPacket.FLAG_IS_VIDEO) ? videoSendSequenceNumber++ : voiceSendSequenceNumber++);
                data.addChecksum();

                DatagramPacket p = new DatagramPacket(data.getBuffer(), 0, data.getLength(), this.address, this.port);

                try {
                    socket.send(p);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                sleep(1);
            }
        }
    }


    private void sleep(int ms){
        try{
            Thread.sleep(ms);
        } catch (Exception ex){

        }
    }
}
