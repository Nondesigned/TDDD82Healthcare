package tddd82.healthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.securepreferences.SecurePreferences;

public class DummyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        SharedPreferences prefs = new SecurePreferences(this);
        Log.e(AntonsLog.TAG, prefs.getString(GlobalVariables.getJsonTokenTag(), "DEFAULT"));
    }

    public void startContactActivity(View v){
        Intent startContactIntent = new Intent(this, ContactActivity.class);
        startActivity(startContactIntent);
    }
}
