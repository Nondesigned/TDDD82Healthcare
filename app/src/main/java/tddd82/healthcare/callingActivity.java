package tddd82.healthcare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        public void onCallStarted(String host, int port, int sender, int receiver, String key) {

        }
    };

    int caller;
    int sourceNr;
    Context context = this;
    InitCall init = new InitCall();
    Call callInstance;
    boolean activeCall = false;
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


        SharedPreferences sharedPreferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN","default");
        JWT jwt = new JWT(token);
        sourceNr = Integer.parseInt(jwt.getSubject());
        Log.d("bob",Integer.toString(sourceNr) + Integer.toString(caller));
        init.init(sourceNr,caller, CallState,this);
        final Activity thisIsIt = this;
        //Sends accept message
        answer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO generara key
                init.send(2);
                init.start();
                ImageView displayView = (ImageView)findViewById(R.id.imageView3);
                callInstance = new Call("130.236.181.196", 1338, sourceNr, caller,  new CallEvent() {
                    @Override
                    public void onTimeout(int currentSequenceNumber, int destinationNumber) {

                    }
                }, displayView, thisIsIt);


                callInstance.initialize();

                callInstance.start();
                answer.setVisibility(View.GONE);
                activeCall = true;
                decline.setText("Hang Up");
            }
        });
        //Sends reject message
        decline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(activeCall){
                    init.send(1);
                    CallState.onCallEnded();

                }
                else {
                    init.send(3);
                }
                finish();
            }
        });

}
}
