package tddd82.healthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Clynch on 2017-02-15.
 * This AsyncTask handles logging into the system.
 * It takes the parameters and sends them by HTTP via the POST method.
 * It responds to the user via a message returned from the server.
 * Also gives feedback if something went wrong.
 */
class LoginTask extends AsyncTask<String,Void,String> {
    private Context context;
    private AlertDialog alertDialog;
    private String token;
    public AsyncResponse delegate = null;

    public LoginTask(Context context, AsyncResponse delegate){
        this.context = context;
        this.delegate = delegate;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("test");
    }
    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(String output);
    }


    protected String doInBackground(String... params) {
        String card = params[0];
        String password = params[1];
        String url = params[2];

        JSONObject credentials = new JSONObject();
        try {
            credentials.put("password", password);
            credentials.put("card", card);
        } catch (JSONException e) {
            e.printStackTrace();
        }

/*
        JsonObjectRequest jsonRequest = new JsonObjectRequest
        (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                if (response.status.equals("accepted")) {
                    setToken(response.token);
                }else{
                    return(response.message);
                }

            }
        });
*/
        return "token";
    }
    @Override
    protected void onPreExecute() {
        alertDialog.setTitle("Retriving ID");
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
