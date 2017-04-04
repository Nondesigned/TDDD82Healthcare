package tddd82.healthcare;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ActiveCall extends AppCompatActivity {

    private final Event CallState = new Event(){

        @Override
        public void onCallEnded() {
            if (callInstance != null)
                callInstance.terminate();

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    finish();
                }
            });
        }

        @Override
        public void onCallStarted(String host, int port, int sender, int receiver, byte[] IV, byte[] key) {
            //TODO pass key to Call
            callInstance = new Call(host, 1338, sender, receiver,  new CallEvent() {
                @Override
                public void onTimeout(int currentSequenceNumber, int destinationNumber) {

                }
            }, new CallCrypto(IV, key), (ImageView)findViewById(R.id.imageView2), thisIsIt);

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
    private Call callInstance;
    Activity thisIsIt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_call);
        thisIsIt = this;
        TextView status = (TextView) findViewById(R.id.callStatus);
        status.setText("Initializing call");
        final Button endCall = (Button) findViewById(R.id.endCall);

        //Get token from SharedPref
        SharedPreferences sharedPreferences = this.getSharedPreferences("tddd82.healthcare", this.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN","default");
        com.auth0.android.jwt.JWT jwt = new com.auth0.android.jwt.JWT(token);
        sourceNr = Integer.parseInt(jwt.getSubject());
        Intent intent = getIntent();
        destNr = intent.getIntExtra(GlobalVariables.getIntentCallNumber(), -1);


        init = new InitCall();
        init.init(sourceNr,destNr, CallState,this);
        init.send(initCall, null);
        init.start();
        endCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                init.send(stopCall, null);
                CallState.onCallEnded();
                finish();
            }
        });
    }

}
