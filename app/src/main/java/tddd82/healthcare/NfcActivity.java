package tddd82.healthcare;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.util.Log;

/**
 * Created by pergu on 16-Feb-17.
 */

public class NfcActivity {

    private NfcAdapter nfc;

    private Activity activity;
    private IntentFilter intentFilterArray[];
    private PendingIntent intent;
    private String techListArray[][];

    public NfcActivity(Activity activity){
        super();
        this.activity = activity;
        nfc = NfcAdapter.getDefaultAdapter(activity.getApplicationContext());


        intent = PendingIntent.getActivity(activity,0, new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try{
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e){

            throw new RuntimeException("Unable to specify */* Mime Type", e);
        }
        intentFilterArray = new IntentFilter[] {ndef, tag};

        techListArray = new String[][] {new String[]{NfcA.class.getName()}};
    }

    public void enableForeground()
    {
        Log.d("Demo","Foregorund enabled");
        nfc.enableForegroundDispatch(activity, intent, intentFilterArray, techListArray);
    }

    public void disableForeground()
    {
        Log.d("Demo","Foregorund disabled");

        nfc.disableForegroundDispatch(activity);
    }
    public NfcAdapter getNfc(){
        return nfc;
    }

}
