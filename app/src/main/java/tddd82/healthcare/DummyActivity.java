package tddd82.healthcare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DummyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
    }

    public void startContactActivity(View v){
        Intent startContactIntent = new Intent(this, ContactActivity.class);
        startActivity(startContactIntent);
    }
}
