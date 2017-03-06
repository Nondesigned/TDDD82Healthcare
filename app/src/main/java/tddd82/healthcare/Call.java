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

        return true;
    }

    private void playbackWorker(){
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, recorder.getSampleRate(), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);
        while(alive){
            if (!receiverBuffer.empty()){
                byte[] buffer = receiverBuffer.poll();

                track.write(buffer, 0, buffer.length);
                track.play();
            }
        }
    }

    private void receiverWorker(){

        while(alive){
            byte[] buffer = new byte[bufferSize];
            DatagramPacket p = new DatagramPacket(buffer, 0, bufferSize);

            try{
                socket.receive(p);
            } catch (Exception ex){

            }

            receiverBuffer.push(buffer);
        }
    }

    private void recorderWorker(){
        while(alive){
            byte[] buffer = new byte[bufferSize];
            recorder.read(buffer, 0, bufferSize);

            recordBuffer.push(buffer);

        }
    }

    private void senderWorker(){

        while(alive){
            if (!recordBuffer.empty()) {
            /*AudioTrack player = new AudioTrack(AudioManager.STREAM_MUSIC, recorder.getSampleRate(), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);

            byte[] buffer = recordBuffer.poll();
            player.write(buffer, 0, bufferSize);
            player.play();*/

                byte[] buffer = recordBuffer.poll();

                DatagramPacket p = new DatagramPacket(buffer, 0, bufferSize, this.address, this.port);

                try {
                    socket.send(p);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }


        }
    }

    private void getAudioRecord(){
        for (int rate : new int[] {44100, 8000, 11025, 16000, 22050}) {
            bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
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
