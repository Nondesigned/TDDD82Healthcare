package tddd82.healthcare;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AntonsLog";
    private String token = null;
    private int requestCode = 1;
    EditText usernameInput;
    EditText passwordInput;
    //TODO ändra layout i main & Göra något med token som returneras i onActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(this.token==null){
            Context context = MainActivity.this;
            Class destinationActivity = LoginActivity.class;
            Intent startChildActivity = new Intent(context, destinationActivity);
            startActivityForResult(startChildActivity, this.requestCode);
        }


        usernameInput = (EditText)findViewById(R.id.cardIDInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestCode) {
            if (resultCode == RESULT_OK) {
                //Use Data to get string
                token = data.getStringExtra("RESULT_STRING");
                Toast toast = Toast.makeText(getApplicationContext(), "test" + this.token, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void login(View view){

        String password = passwordInput.getText().toString();
        String username = usernameInput.getText().toString();

        LoginTask loginTask = new LoginTask(this, new LoginTask.AsyncResponse(){
            @Override
            public void processFinish(String output){
                setToken(output);
            }
        });
        loginTask.execute(username, password, "www.test.se");
    }

    public void setToken(String token) {
            this.token = token;
        Toast toast = Toast.makeText(getApplicationContext(), this.token, Toast.LENGTH_SHORT);
        toast.show();
    }
}
