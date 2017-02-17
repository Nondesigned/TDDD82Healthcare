package tddd82.healthcare;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.TagTechnology;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.nio.ByteBuffer;



public class MainActivity extends AppCompatActivity {


    private final static int MY_PERMISSIONS_REQUEST = 1;

    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.NFC};

    EditText usernameInput;
    EditText passwordInput;
    TextView testinfo;
    Button nfcbutton;


    NfcActivity nfcActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testinfo = (TextView)findViewById(R.id.testinfo);
        nfcbutton = (Button)findViewById(R.id.nfcbutton);
        nfcActivity = new NfcActivity(this);

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            Toast.makeText(this, "Fuck you", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.NFC)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.NFC},MY_PERMISSIONS_REQUEST);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        Toast.makeText(this, "Tessst", Toast.LENGTH_SHORT).show();
        super.onNewIntent(intent);
        readTextFromNFCTag(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcActivity != null){
            nfcActivity.enableForeground();

            if (!nfcActivity.getNfc().isEnabled())
            {
                Toast.makeText(getApplicationContext(), "Aktivera NFC och tryck på tillbaka.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }
        }

    }






    public void readTextFromNFCTag(Intent intent){
        super.onNewIntent(intent);
        Vibrator v= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(30);

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
        testinfo.setText(rfid);
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
