package tddd82.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.securepreferences.SecurePreferences;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static tddd82.healthcare.ControlFlag.INITCALL;

public class StartActivity extends AppCompatActivity {
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        requestRecordAudioPermission();

//TODO Check from sharedPreferences if user is logged in or not!
        boolean inloggad = true;
        preferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
        editor = preferences.edit();

        //Returns true if "TOKEN" exists
        if (!(preferences.contains("TOKEN"))) {
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
        }
        if (preferences.contains("TOKEN")) {
            Log.d("bob","tjotjotjo");
            try {
                Intent intent = getIntent();
                //TODO add field with key.

                if (intent.hasExtra("TYPE")) {
                    String type = intent.getStringExtra("TYPE");
                    if (type.equals("call")) {
                        Intent callingIntent = new Intent(this, callingActivity.class);
                        String callerid = intent.getStringExtra("CALLER");
                        callingIntent.putExtra("CALLER", callerid);
                        startActivity(callingIntent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void requestRecordAudioPermission(){
        //check API version, do nothing if API version < 23
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
                }
            }
        }
    public void logout(View view){
        preferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.remove("TOKEN");
        editor.apply();
        Log.v(AntonsLog.TAG,"LOGGAR UT");
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    public void getContacts(View view){
        Intent callIntent = new Intent(context,ActiveCall.class);
        callIntent.putExtra("DEST","111");
        startActivity(callIntent);
    }
}

