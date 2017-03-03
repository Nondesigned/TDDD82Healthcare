package tddd82.healthcare;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.media.MediaRecorder.AudioSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class VoiceRecordInstance extends Thread {

    private final int BUFFER_SIZE = 65536;

    private AudioRecord recorder;
    private boolean alive;
    private VoiceBuffer voiceBuffer;
    private int sampleRate;



    public VoiceRecordInstance(int sampleRate, VoiceBuffer buffer){
        this.sampleRate = sampleRate;
        getValidSampleRates();
        voiceBuffer = buffer;
        alive = true;
        recorder.startRecording();
        this.start();

    }

    public void getValidSampleRates() {
        int desiredRate = 0;
        for (int rate : new int[] {44100, 8000, 11025, 16000, 22050}) {
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                // buffer size is valid, Sample rate supported
                recorder = new AudioRecord(AudioSource.MIC, rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

                if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    desiredRate = -1;
                    recorder.release();
                } else {
                    desiredRate = rate;
                    break;
                }
            }
        }
    }

    public void terminate(){
        alive = false;
        recorder.stop();
    }

    public void run(){
        while(alive){

            byte[] buffer = new byte[BUFFER_SIZE];

            //Blocking?
            int err = recorder.read(buffer, 0, BUFFER_SIZE);

            if (err != AudioRecord.SUCCESS){
                //Handle error :D
            }

            AudioTrack player = new AudioTrack(AudioManager.STREAM_VOICE_CALL,  44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_8BIT, BUFFER_SIZE, AudioTrack.MODE_STATIC);
            player.write(buffer, 0, BUFFER_SIZE);
            player.play();
            SystemClock.sleep(3000);
            //voiceBuffer.push(buffer);
        }
    }


}
