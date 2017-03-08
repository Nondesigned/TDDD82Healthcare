package tddd82.healthcare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActiveCall extends AppCompatActivity {
    InitCall init = new InitCall();

    int sourceNr;
    int destNr;
    int initCall = 0;
    int stopCall = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_call);
        TextView status = (TextView) findViewById(R.id.callStatus);
        status.setText("Initializing call");
        final Button endCall = (Button) findViewById(R.id.endCall);


        //TODO set value of sourceNr - sourceNr gets from sharePreferences
        Intent intent = getIntent();
        if (intent.hasExtra("DEST")) {
            destNr = Integer.parseInt(intent.getStringExtra("DEST"));
        }





        //init.init(sourceNr,destNr);
        init.init(222,111);
        init.send(initCall);
        init.run();

        endCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //init.send(stopCall);
                finish();
            }
        });

        //String key = init.getKey();

        //UDP samtal
    }
}
