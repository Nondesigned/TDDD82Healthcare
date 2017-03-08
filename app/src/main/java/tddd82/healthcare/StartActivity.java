package tddd82.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//TODO Check from sharedPreferences if user is logged in or not!
        boolean inloggad = false;

        if (inloggad == false) {
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
        } else if (inloggad == true) {
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
            final Button contactList =(Button)findViewById(R.id.contactButton);
            contactList.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent callIntent = new Intent(context,ActiveCall.class);
                    callIntent.putExtra("DEST","111");
                    startActivity(callIntent);
                }
            });

        }
    }
    }
