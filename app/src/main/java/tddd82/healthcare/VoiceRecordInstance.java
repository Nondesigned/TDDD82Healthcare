package tddd82.healthcare;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class VoiceRecordInstance extends Thread {

    private final int BUFFER_SIZE = 65536;

    private AudioRecord recorder;
    private boolean alive;
    private VoiceBuffer voiceBuffer;

    public VoiceRecordInstance(int sampleRate, VoiceBuffer buffer){
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_8BIT, BUFFER_SIZE);
        voiceBuffer = buffer;
        alive = true;
        recorder.startRecording();
        this.start();

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

            voiceBuffer.push(buffer);
        }
    }


}
