package tddd82.healthcare;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class VoiceCall {

    private final int DEFAULT_MINIMUM_BUFFER_SIZE = 300;

    private int minimumBuffer;

    private AudioRecord recorder;
    private AudioTrack track;
    private int recorderBufferSize;

    private int playbackSampleRate;
    private int playbackBufferSize;

    private boolean alive;
    private boolean initialized;

    private VoiceBuffer receiverBuffer;
    private VoiceBuffer recordBuffer;

    private CallEvent eventHandler;

    public VoiceCall(VoiceBuffer receiverBuffer, VoiceBuffer recordBuffer, CallEvent eventHandler){
        this.receiverBuffer = receiverBuffer;
        this.recordBuffer = recordBuffer;
        this.eventHandler = eventHandler;
        this.minimumBuffer = DEFAULT_MINIMUM_BUFFER_SIZE;

        this.playbackSampleRate = -1;
        this.playbackBufferSize = -1;
        this.alive = false;
        this.initialized = false;
    }

    public CallError initialize(){
        recorder = getAudioRecord();

        if (this.recorder == null || this.recorder.getState() != AudioRecord.STATE_INITIALIZED){
            return CallError.MIC_ERROR;
        }

        this.track = null;
        this.initialized = true;

        return CallError.SUCCESS;
    }

    public void setMinimumBufferSize(int milliseconds){
        this.minimumBuffer = milliseconds;
    }

    public int getMininmumBuffer(){
        return this.minimumBuffer;
    }

    public boolean start(){
        if (!initialized)
            return false;

        this.receiverBuffer = new VoiceBuffer();
        this.recordBuffer = new VoiceBuffer();
        this.recorder.startRecording();

        this.alive = true;

        new Thread(new Runnable(){
            @Override
            public void run(){
                recorderWorker();
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
        initialized = false;
        if(recorder != null){
            recorder.stop();
            recorder.release();
        }
    }

    private void playbackWorker(){
        while(alive) {

            if (receiverBuffer.empty()) {
                while (receiverBuffer.estimateTime() < minimumBuffer  && alive) {
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

    private void recorderWorker(){
        while(alive){
            DataPacket p = new DataPacket(recorderBufferSize);

            recorder.read(p.getBuffer(), p.getPayloadIndex(), p.getPayloadLength());
            p.setSampleRate(recorder.getSampleRate());
            p.setBufferSize(recorderBufferSize);


            recordBuffer.push(p);
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
