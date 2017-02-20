package tddd82.healthcare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        usernameInput = (EditText)findViewById(R.id.cardIDInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);

    }

    public void login(View view){

        String password = passwordInput.getText().toString();
        String username = usernameInput.getText().toString();

        LoginTask loginTask = new LoginTask(this, new LoginTask.AsyncResponse(){
            @Override
            public void processFinish(String output){
                setToken(output);
                Intent intent=new Intent();
                intent.putExtra("RESULT_STRING", output);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        loginTask.execute(username, password, "www.test.se");
    }

    public void setToken(String token) {
        this.token = token;
        //Toast toast = Toast.makeText(getApplicationContext(), this.token, Toast.LENGTH_SHORT);
        //toast.show();
    }
    public String getToken(){
        return this.token;
    }
}
