package tddd82.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.auth0.android.jwt.JWT;

public class callingActivity extends AppCompatActivity {
    int caller;
    int sourceNr;
    Context context = this;
    InitCall init = new InitCall();
    VoiceCall callInstance;
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

        /*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString("TOKEN","default");
        JWT jwt = new JWT(token);
        sourceNr = Integer.parseInt(jwt.getSubject());
*/
        init.init(sourceNr,caller, new Event(){

            @Override
            public void onCallEnded() {

                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        finish();
                    }
                });
            }

            @Override
            public void onCallStarted(String host, int port, int sender, int receiver) {

            }
        });

        //Sends accept message
        answer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO get sourceNr
                init.send(2);
                init.start();
                //Intent activeCallIntent = new Intent(context,ActiveCall.class);
                //startActivity(activeCallIntent);
                callInstance = new VoiceCall("130.236.181.196", 1338, 111, caller, new CallEvent() {
                    @Override
                    public void onTimeout(int currentSequenceNumber, int destinationNumber) {

                    }
                });

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
                }
                else {
                    init.send(3);
                }
                finish();
            }
        });

}
}
