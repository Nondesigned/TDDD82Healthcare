package tddd82.healthcare;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Call {

    private DatagramSocket socket;
    private AudioRecord recorder;
    private AudioTrack track;
    private int recorderBufferSize;

    private int playbackSampleRate;
    private int playbackBufferSize;

    private InetAddress address;
    private String host;
    private int port;
    private boolean alive;
    private int sendSequenceNumber;
    private int lastReceivedSequenceNumber;

    private int senderNumber;
    private int receiverNumber;

    private VoiceBuffer receiverBuffer;
    private VoiceBuffer recordBuffer;

    public Call(String host, int port, int senderPhoneNumber, int receiverPhoneNumber){
        this.host = host;
        this.port = port;
        this.senderNumber = senderPhoneNumber;
        this.receiverNumber = receiverPhoneNumber;
        this.sendSequenceNumber = 0;
        this.lastReceivedSequenceNumber = 0;
        this.playbackSampleRate = -1;
        this.playbackBufferSize = -1;
        this.alive = false;
    }

    public CallError initialize(){
        recorder = getAudioRecord();

        if (this.recorder == null || this.recorder.getState() != AudioRecord.STATE_INITIALIZED){
            return CallError.MIC_ERROR;
        }

        try{
            this.address = InetAddress.getByName(host);
        } catch (UnknownHostException ex){
            System.out.println(ex.getMessage());
            return CallError.SERVER_NOT_REACHABLE;
        }

        try {
            socket = new DatagramSocket(this.port);
        } catch (SocketException ex){
            System.out.println(ex.getMessage());
            return CallError.SOCKET_ERROR;
        }

        this.track = null;

        return CallError.SUCCESS;
    }

    public void start(){
        this.receiverBuffer = new VoiceBuffer();
        this.recordBuffer = new VoiceBuffer();
        this.recorder.startRecording();

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
                recorderWorker();
            }
        }).start();

        new Thread(new Runnable(){
            @Override
            public void run(){
                senderWorker();
            }
        }).start();

        new Thread(new Runnable(){
            @Override
            public void run(){
                playbackWorker();
            }
        }).start();
    }

    public void terminate(){
        alive = false;
        recorder.stop();
        recorder.release();
        socket.disconnect();
        socket.close();
    }

    private void playbackWorker(){
        while(alive) {

            if (receiverBuffer.empty()) {
                while (receiverBuffer.estimateTime() < 300) {
                    sleep(1);
                }
            }

            DataPacket data = receiverBuffer.poll();

            int sampleRate = data.getSampleRate();
            int bufferSize = data.getBufferSize();

            if (playbackSampleRate != sampleRate || bufferSize != playbackBufferSize) {
                if (track != null){
                    track.stop();
                    track.release();
                }
                playbackSampleRate = sampleRate;
                playbackBufferSize = bufferSize;
                track = new AudioTrack(AudioManager.STREAM_MUSIC, playbackSampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, playbackBufferSize, AudioTrack.MODE_STREAM);
                track.play();
            }

            track.write(data.getBuffer(), data.getPayloadIndex(), data.getBufferSize());
        }
    }

    private void receiverWorker(){

        while(alive){

            DataPacket data = new DataPacket(DataPacket.MAX_SIZE);
            DatagramPacket p = new DatagramPacket(data.getBuffer(), data.getLength());

            try {
                socket.receive(p);
            } catch (Exception ex) {

            }

            if (data.getSequenceNumber() >= lastReceivedSequenceNumber)
                receiverBuffer.push(data);
        }
    }

    private void recorderWorker(){
        while(alive){
            DataPacket p = new DataPacket(recorderBufferSize);

            recorder.read(p.getBuffer(), p.getPayloadIndex(), p.getPayloadLength());
            p.setSource(senderNumber);
            p.setDestination(receiverNumber);
            p.setSampleRate(recorder.getSampleRate());
            p.setBufferSize(recorderBufferSize);
            p.setSequenceNumber(sendSequenceNumber++);

            recordBuffer.push(p);

        }
    }

    private void senderWorker(){

        while(alive){
            if (!recordBuffer.empty()) {

                DataPacket data = recordBuffer.poll();

                DatagramPacket p = new DatagramPacket(data.getBuffer(), 0, data.getLength(), this.address, this.port);

                try {
                    socket.send(p);
                } catch (Exception ex) {
                    String msg = ex.getMessage();
                    System.out.println(ex);
                }
            }


        }
    }

    private AudioRecord getAudioRecord(){
        for (int rate : new int[] {44100, 8000, 11025, 16000, 22050}) {
            recorderBufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (recorderBufferSize > 0) {
                // buffer size is valid, Sample rate supported
                AudioRecord temp = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, recorderBufferSize);

                if (temp.getState() != AudioRecord.STATE_INITIALIZED) {
                    temp.release();
                } else {
                    return temp;
                }
            }
        }

        return null;
    }

    private void sleep(int ms){
        try{
            Thread.sleep(ms);
        } catch (Exception ex){

        }
    }
}
