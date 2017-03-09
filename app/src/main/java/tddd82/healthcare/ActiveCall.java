package tddd82.healthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActiveCall extends AppCompatActivity {

    private final Event CallState = new Event(){

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
        public void onCallStarted(String host, int port, int sender, int receiver, String key) {
            //TODO pass key to VoiceCall
            callInstance = new VoiceCall(host, 1338, sender, receiver, new CallEvent() {
                @Override
                public void onTimeout(int currentSequenceNumber, int destinationNumber) {

                }
            });

            if (callInstance.initialize() != CallError.SUCCESS){

            }

            callInstance.start();

        }
    };


    InitCall init;
    int sourceNr;
    int destNr;
    int initCall = 0;
    int stopCall = 1;
    private VoiceCall callInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(AntonsLog.TAG, "AKTIVITET STARTAR");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_call);

        TextView status = (TextView) findViewById(R.id.callStatus);
        status.setText("Initializing call");
        final Button endCall = (Button) findViewById(R.id.endCall);

        //TODO set value of sourceNr - sourceNr gets from sharePreferences

        SharedPreferences sharedPreferences = this.getSharedPreferences("tddd82.healthcare", this.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN","default");
        com.auth0.android.jwt.JWT jwt = new com.auth0.android.jwt.JWT(token);
        sourceNr = Integer.parseInt(jwt.getSubject());
        Intent intent = getIntent();
        destNr = intent.getIntExtra(GlobalVariables.getIntentCallNumber(), -1);


        init = new InitCall();
        init.init(sourceNr,destNr, CallState,this);
        init.send(initCall);
        init.start();
        endCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                init.send(stopCall);
                CallState.onCallEnded();
                finish();
            }
        });

        //String key = init.getKey();

        //UDP samtal
    }

}
