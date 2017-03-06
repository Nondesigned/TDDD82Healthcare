package tddd82.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Få tillstånd för internet - ta bort senare
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Intent intent = getIntent();
            if(intent.hasExtra("TYPE")){
                String type = intent.getStringExtra("TYPE");
                if (type.equals("call")) {
                    Intent callingIntent = new Intent(this, callingActivity.class);
                    String callerid = intent.getStringExtra("CALLER");
                    callingIntent.putExtra("CALLER",callerid);
                    startActivity(callingIntent);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Button call = (Button) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    // TODO skapa tcp anslutning till java samt pushnotisen via HTTP
            }
        });
    }
}
