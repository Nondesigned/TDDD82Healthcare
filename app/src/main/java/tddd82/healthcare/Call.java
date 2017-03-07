package tddd82.healthcare;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.speech.tts.Voice;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Call {

    private DatagramSocket socket;
    private AudioRecord recorder;
    private int bufferSize;

    private InetAddress address;
    private String host;
    private int port;
    private boolean alive;

    private VoiceBuffer receiverBuffer;
    private VoiceBuffer recordBuffer;

    public Call(String host, int port){
        this.host = host;
        this.port = port;
        this.alive = false;
    }

    public boolean initialize(){
        getAudioRecord();

        if (this.recorder.getState() != AudioRecord.STATE_INITIALIZED){
            return false;
        }

        try{
            this.address = InetAddress.getByName(host);
        } catch (UnknownHostException ex){
            System.out.println(ex.getMessage());
            return false;
        }

        try {
            socket = new DatagramSocket(this.port);
        } catch (SocketException ex){
            System.out.println(ex.getMessage());
            return false;
        }

        this.receiverBuffer = new VoiceBuffer();
        this.recordBuffer = new VoiceBuffer();
        this.recorder.startRecording();

        this.alive = true;

        /*new Thread(new Runnable(){
            @Override
            public void run(){
                receiverWorker();
            }
        }).start();*/

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

        return true;
    }

    public void terminate(){
        alive = false;
        recorder.stop();
        recorder.release();
        socket.close();
    }

    private void playbackWorker(){
        while(alive){
            //if (!receiverBuffer.empty()){
                byte[] buffer = new byte[bufferSize];
                DatagramPacket p = new DatagramPacket(buffer, bufferSize);

                try{
                    socket.receive(p);
                } catch (Exception ex){

                }

              //  receiverBuffer.push(buffer);
                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, recorder.getSampleRate(), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);

               // buffer = receiverBuffer.poll();

                track.write(buffer, 0, p.getLength());
                track.play();
            try {
                Thread.sleep(100);
            } catch(Exception ex){

            }
                track.release();
            //}
        }
    }

    private void receiverWorker(){

        while(alive){

        }
    }

    private void recorderWorker(){
        while(alive){
            DataPacket p = new DataPacket(bufferSize);

            recorder.read(p.getBuffer(), p.getDataIndex(), bufferSize);
            p.setSource(0xDEAD);
            p.setDestination(0xFFFF);
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

    private void getAudioRecord(){
        for (int rate : new int[] {44100, 8000, 11025, 16000, 22050}) {
            bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)*5;
            if (bufferSize > 0) {
                // buffer size is valid, Sample rate supported
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

                if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    recorder.release();
                } else {
                    break;
                }
            }
        }
    }
}
