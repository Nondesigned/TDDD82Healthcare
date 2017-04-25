package tddd82.healthcare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class StartActivity extends AppCompatActivity {
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        BatteryMng.setContext(this);
        Toast.makeText(context, String.valueOf(BatteryMng.getPercentage()), Toast.LENGTH_SHORT).show();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        requestRecordAudioPermission();
        requestCameraPermission();
        preferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
        editor = preferences.edit();


        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_login:
                                logout();
                                break;
                            case R.id.action_contacts:
                                getContacts();
                                break;
                            case R.id.action_map:
                                showMap();
                                break;
                        }
                        return true;
                    }
                });

        //Returns true if "TOKEN" exists
        if (!(preferences.contains("TOKEN"))) {
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
        }
        if (preferences.contains("TOKEN")) {
            System.out.println("Går in i satsen");
            bottomNavigation.getMenu().findItem(R.id.action_login).setTitle("Log out");
            try {
                Intent intent = getIntent();
                //TODO add field with key.

                if (intent.hasExtra("TYPE")) {
                    String type = intent.getStringExtra("TYPE");
                    if (type.equals("call")) {
                        Intent callingIntent = new Intent(this, CallingActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (preferences.contains("TOKEN")) {
            System.out.println("Går in i satsen");
            bottomNavigation.getMenu().findItem(R.id.action_login).setTitle("Log out");
            try {
                Intent intent = getIntent();
                //TODO add field with key.

                if (intent.hasExtra("TYPE")) {
                    String type = intent.getStringExtra("TYPE");
                    if (type.equals("call")) {
                        Intent callingIntent = new Intent(this, CallingActivity.class);
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

    private void requestCameraPermission(){
        //check API version, do nothing if API version < 23
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
            }
        }
    }
    public void logout(){
        preferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.remove("TOKEN");
        editor.apply();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void getContacts(){
        Intent callIntent = new Intent(context,ContactActivity.class);
        startActivity(callIntent);
    }


    public void showMap(){
        Intent showMap = new Intent(context,MapsActivity.class);
        startActivity(showMap);
    }

}

