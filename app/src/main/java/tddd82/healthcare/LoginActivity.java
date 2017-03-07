package tddd82.healthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private String token = null;
    EditText usernameInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Log.v(AntonsLog.TAG, "Login körs");

        usernameInput = (EditText)findViewById(R.id.cardIDInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);

    }

    public void login(View view){

        String password = passwordInput.getText().toString();
        String username = usernameInput.getText().toString();

        LoginTask loginTask = new LoginTask(this);
        loginTask.execute(username, password, "www.test.se");
    }

}
