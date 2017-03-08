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
    InitCall init = new InitCall();
    int sourceNr;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        final TextView tokenText = (TextView) findViewById(R.id.textviewtoken);
        Intent intent = getIntent();
        caller = Integer.parseInt(intent.getStringExtra("CALLER"));
        tokenText.setText(caller);

        Button decline = (Button) findViewById(R.id.decline);
        Button answer = (Button) findViewById(R.id.answer);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString("TOKEN","default");
        JWT jwt = new JWT(token);
        sourceNr = Integer.parseInt(jwt.getSubject());

        //Sends accept message
        answer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO get sourceNr
                init.initialize(sourceNr,caller,2);
                Intent conversationIntent = new Intent(context,ActiveCall.class);
                startActivity(conversationIntent);
            }
        });
        //Sends reject message
        decline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                init.initialize(sourceNr,caller,3);
            }
        });

}
}
