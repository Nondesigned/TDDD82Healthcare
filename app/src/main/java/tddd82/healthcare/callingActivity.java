package tddd82.healthcare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class callingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        TextView tokenText = (TextView) findViewById(R.id.textviewtoken);
        Intent intent = getIntent();
        tokenText.setText(intent.getStringExtra("CALLER") + " is calling");

        Button decline = (Button) findViewById(R.id.decline);
        Button answer = (Button) findViewById(R.id.answer);
        answer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

}
}
