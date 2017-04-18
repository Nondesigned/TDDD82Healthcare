package tddd82.healthcare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth0.android.jwt.JWT;

//Mottagare
public class CallingActivity extends AppCompatActivity {

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
        public void onCallStarted(String host, int port, int sender, int receiver, byte[] IV, byte[] key, boolean isVideo) {

        }
    };

    int caller;
    int sourceNr;
    Context context = this;
    InitCall init = new InitCall();
    Call callInstance;
    CallCrypto callCrypto;
    boolean activeCall = false;
    final Activity displayActivity = this;
    ImageView displayView;
    boolean isVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        final TextView tokenText = (TextView) findViewById(R.id.textviewtoken);
        Intent intent = getIntent();

        caller = Integer.parseInt(intent.getStringExtra("CALLER"));
        tokenText.setText(Integer.toString(caller));
        final Button decline = (Button) findViewById(R.id.decline);
        final Button answer = (Button) findViewById(R.id.answer);
        final Button hangup = (Button) findViewById(R.id.hangup);


        SharedPreferences sharedPreferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN","default");
        JWT jwt = new JWT(token);
        sourceNr = Integer.parseInt(jwt.getSubject());
        Log.d("bob",Integer.toString(sourceNr) + Integer.toString(caller));
        init.init(sourceNr,caller, CallState,this);
        //Sends accept message
        answer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isVideo = BatteryMng.doVideo();
                callCrypto = new CallCrypto();
                displayView = (ImageView)findViewById(R.id.imageView3);
                callInstance = new Call(GlobalVariables.getCallServerIp(), GlobalVariables.getCallServerUDPPort(), sourceNr, caller,  new CallEvent() {
                    @Override
                    public void onTimeout(int currentSequenceNumber, int destinationNumber) {

                    }
                }, callCrypto, displayView, displayActivity, (TextView)findViewById(R.id.textView2),isVideo);
                if(isVideo){
                    init.send(2,8, callCrypto);
                }
                init.send(2, callCrypto);
                init.start();


                callInstance.initialize();
                callInstance.start();

                answer.setVisibility(View.GONE);
                answer.setClickable(false);
                activeCall = true;
                decline.setVisibility(View.GONE);
                decline.setClickable(false);
                hangup.setClickable(true);
                hangup.setVisibility(View.VISIBLE);
            }
        });
        //Sends reject message
        decline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                init.send(3, null);
                finish();
            }
        });
        hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activeCall){
                    init.send(1, null);
                    CallState.onCallEnded();

                }
            }
        });
}
}
