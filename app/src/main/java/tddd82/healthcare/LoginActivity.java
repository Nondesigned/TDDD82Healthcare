package tddd82.healthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import android.content.pm.PackageManager;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

public class LoginActivity extends AppCompatActivity implements TaskCallback{

    private final static int MY_PERMISSIONS_REQUEST = 1;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    TextView UsernameInfo;
    EditText passwordInput;
    NfcActivity nfcActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        preferences = this.getSharedPreferences("tddd82.healthcare", this.MODE_PRIVATE);
        Log.v("TOKENENE", String.valueOf(preferences.contains("TOKEN")));

        UsernameInfo = (TextView)findViewById(R.id.cardIDInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);


        nfcActivity = new NfcActivity(this);
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            Toast.makeText(this, "NFC adapter found", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.NFC)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.NFC},MY_PERMISSIONS_REQUEST);

        }
    }

    public void login(View view){
        Log.v(AntonsLog.TAG, "ETT");
        LoginTask loginTask = new LoginTask(this, this);
        loginTask.execute(UsernameInfo.getText().toString(), passwordInput.getText().toString(), "https://itkand-3-1.tddd82-2017.ida.liu.se:8080/login");
        // TODO listener to finish acitivity instead of starting anotherone on top of it.
        //finish();
    }
    //Activated when NFC device is found
    @Override
    protected void onNewIntent(Intent intent){
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

    //Read the information, converts and puts it in the UsernameInfo textview
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
            }
        }
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String rfid = bin2int(tag.getId());
        UsernameInfo.setText(rfid);
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

    @Override
    public void done() {
        Toast.makeText(this, "Inloggad", Toast.LENGTH_LONG);
        finish();
    }
}
