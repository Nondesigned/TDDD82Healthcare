package tddd82.healthcare;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.TagTechnology;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AntonsLog";
    EditText usernameInput;
    EditText passwordInput;

    NfcActivity nfcActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = (EditText)findViewById(R.id.cardIDInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }

    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        readTextFromNFCTag(intent);
    }
    public void readTextFromNFCTag(Intent intent){
        super.onNewIntent(intent);

        if (intent!= null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for(int i = 0; i < rawMessages.length; i++){
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                usernameInput.setText("Hej");
                Toast.makeText(this, "Hej", Toast.LENGTH_SHORT).show();
                usernameInput.setText(messages.toString());
            }
        }
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String rfid = bin2int(tag.getId());
        usernameInput.setText(rfid);
    }

    static String bin2int(byte[] data) {
        byte[] reverse = new byte[data.length];
        System.out.println(bytesToHex(data));
        System.out.println("byte length: " + data.length);
        for (int i = 0; i < data.length; i++) {
            reverse[data.length-i-1] = data[i];
        }

        return Long.toString(Long.valueOf(bytesToHex(reverse),16));
    }


    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

}
