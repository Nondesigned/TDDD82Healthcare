package tddd82.healthcare;


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

    private VideoBuffer videoReceiverBuffer;
    private VideoBuffer videoRecordBuffer;

    private CallEvent eventHandler;

    private VoiceCall voiceCall;

    public Call(String host, int port, int senderPhoneNumber, int receiverPhoneNumber, CallEvent eventHandler){
        this.host = host;
        this.port = port;
        this.eventHandler = eventHandler;
        this.senderNumber = senderPhoneNumber;
        this.receiverNumber = receiverPhoneNumber;

        this.voiceReceiverBuffer = new VoiceBuffer();
        this.voiceRecordBuffer = new VoiceBuffer();

        this.voiceSendSequenceNumber = 0;
        this.voiceLastReceivedSequenceNumber = 0;

        this.voiceCall = new VoiceCall(voiceReceiverBuffer, voiceRecordBuffer, eventHandler);

        this.alive = false;
        this.initialized = false;
    }

    public CallError initialize(){
        CallError voiceErr = voiceCall.initialize();

        if (voiceErr != CallError.SUCCESS)
            return voiceErr;

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
            } catch (IOException ex){

            }

            if (data.getSequenceNumber() >= voiceLastReceivedSequenceNumber)
                voiceReceiverBuffer.push(data);
        }
    }

    private void senderWorker(){

        while(alive){
            if (!voiceRecordBuffer.empty()) {

                DataPacket data = voiceRecordBuffer.poll();

                data.setSource(this.senderNumber);
                data.setDestination(this.receiverNumber);
                data.setSequenceNumber(voiceSendSequenceNumber++);

                DatagramPacket p = new DatagramPacket(data.getBuffer(), 0, data.getLength(), this.address, this.port);

                try {
                    socket.send(p);
                } catch (Exception ex) {

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
