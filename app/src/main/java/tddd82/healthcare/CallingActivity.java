package tddd82.healthcare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    boolean callerIsVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.dangerzone);
        mediaPlayer.start();

        final TextView tokenText = (TextView) findViewById(R.id.textviewtoken);
        Intent intent = getIntent();

        String[] extras = intent.getStringArrayExtra("extra");
        caller = Integer.parseInt(extras[0]);
        if("true".equals(extras[1])){
            callerIsVideo = true;
        }else
            callerIsVideo = false;

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
                mediaPlayer.stop();
                isVideo = BatteryMng.doVideo();
                callCrypto = new CallCrypto();
                displayView = (ImageView)findViewById(R.id.imageView3);
                callInstance = new Call(GlobalVariables.getCallServerIp(), GlobalVariables.getCallServerUDPPort(), sourceNr, caller,  new CallEvent() {
                    @Override
                    public void onTimeout(int currentSequenceNumber, int destinationNumber) {

                    }
                }, callCrypto, displayView, displayActivity, (TextView)findViewById(R.id.textView2),isVideo);
                if(isVideo && callerIsVideo){
                    init.send(2,8, callCrypto);
                }else
                    init.send(2, callCrypto);
                init.start();


                callInstance.initialize();
                callInstance.start();

                displayView.setRotation(-90);
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
                displayView.setLayoutParams(rlp);
                displayView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
                mediaPlayer.stop();
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
