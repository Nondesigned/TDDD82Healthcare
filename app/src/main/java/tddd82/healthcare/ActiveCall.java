package tddd82.healthcare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.EventObject;

public class ActiveCall extends AppCompatActivity {
    InitCall init;
    int sourceNr;
    int destNr;
    int initCall = 0;
    int stopCall = 1;
    private VoiceCall callInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_call);
        Log.d("VAFAN", "test");

        TextView status = (TextView) findViewById(R.id.callStatus);
        status.setText("Initializing call");
        final Button endCall = (Button) findViewById(R.id.endCall);

        //TODO set value of sourceNr - sourceNr gets from sharePreferences
        Intent intent = getIntent();
        if (intent.hasExtra("DEST")) {
            destNr = Integer.parseInt(intent.getStringExtra("DEST"));
        }





        init = new InitCall();
        //init.init(sourceNr,destNr);
        init.init(222,111, new Event(){

            @Override
            public void onCallEnded() {
                callInstance.terminate();
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        finish();
                    }
                });
            }

            @Override
            public void onCallStarted(String host, int port, int sender, int receiver) {
                callInstance = new VoiceCall(host, 1338, sender, receiver, new CallEvent() {
                    @Override
                    public void onTimeout(int currentSequenceNumber, int destinationNumber) {

                    }
                });

                if (callInstance.initialize() != CallError.SUCCESS){

                }

                callInstance.start();

            }


        });
        init.send(initCall);
        init.start();
        endCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                init.send(stopCall);
                finish();
            }
        });

        //String key = init.getKey();

        //UDP samtal
    }

}
