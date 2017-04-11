package tddd82.healthcare;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ricka on 11/04/2017.
 */

public class CallVariables {
    private String host = GlobalVariables.getCallServerIp();
    private int port = GlobalVariables.getCallServerUDPPort();
    private int sourceNr;
    private int destNr;
    private boolean isVideo;
    private byte[] IV;
    private byte[] key;
    private int senderPhoneNumber;
    private int receiverPhoneNumber;
    private CallEvent eventHandler;
    private CallCrypto crypto;
    private ImageView displayView;
    private Activity activity;
    private TextView packetDropView;






    public void setIV(byte[] IV) {
        this.IV = IV;
    }
    public byte[] getIV() {
        return IV;
    }

    public void setKey(byte[] IV) {
        this.key = key;
    }
    public byte[] getKey() {
        return key;
    }

    //Set and Get for isVideo
    public boolean isVideo() {
        return isVideo;
    }
    public void setVideo(boolean video) {
        isVideo = video;
    }

    //Set and get for source
    public void setSourceNr(int sourceNr){
        this.sourceNr = sourceNr;
    }
    public int getSourceNr(){
        return this.sourceNr;
    }

    //Set and get for destination
    public void setDestNr(int destNr){
        this.destNr = destNr;
    }
    public int getDestNr(){
        return this.destNr;
    }
}
