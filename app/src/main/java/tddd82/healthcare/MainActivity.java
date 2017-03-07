package tddd82.healthcare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        call = null;

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String address = ((EditText) findViewById(R.id.address)).getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (call == null) {
                            call = new Call("130.236.181.196", 1338);
                            call.initialize();
                        }
                    }
                }).start();

            }

        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        if (call != null){
                            call.terminate();
                            call = null;
                        }
                    }
                }).start();
            }

        });


    }
}
