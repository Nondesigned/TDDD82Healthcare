package tddd82.healthcare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AntonsLog";
    EditText usernameInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = (EditText)findViewById(R.id.cardIDInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);

    }

    public void login(View view){

        String password = passwordInput.getText().toString();
        String username = usernameInput.getText().toString();

        LoginTask loginTask = new LoginTask(this);
        loginTask.execute(username, password);
        finish();
    }
}
