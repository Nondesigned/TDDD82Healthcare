package tddd82.healthcare;

public class CallManager {

    private static CallManager instance;
    private VoiceRecordInstance voiceRecorder;
    private VoiceBuffer voiceOutBuffer;

    private CallManager(){
        initialize();
    }


    private int initialize(){
        voiceOutBuffer = new VoiceBuffer();
        voiceRecorder = new VoiceRecordInstance(32000, voiceOutBuffer);

        return 0;
    }


    public static CallManager getInstance(){
        if (CallManager.instance == null){
            CallManager.instance = new CallManager();
            CallManager.instance.initialize();
        }

        return CallManager.instance;
    }
}
