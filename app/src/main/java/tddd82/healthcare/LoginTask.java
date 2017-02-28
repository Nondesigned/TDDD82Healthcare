package tddd82.healthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    private static String token;
    public AsyncResponse delegate = null;
    private JSONObject response;
    private static final String JSON_ACCEPTED = "accepted";
    private static final String JSON_STATUS = "status";
    private static final String JSON_DECLINED = "declined";
    private static final String JSON_TOKEN = "token";
    private static final String JSON_MESSAGE = "message";

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
            credentials.put("card", card);
            credentials.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, url, credentials, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject m_response) {
                        // TODO We need to encrypt the token
                        Log.v(AntonsLog.TAG, "We get response");
                        SharedPreferences login = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = login.edit();
                        try {
                            editor.putString("TOKEN", response.getString(JSON_TOKEN));
                            Log.v(AntonsLog.TAG, login.getString("TOKEN", "Default Value"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        response = m_response;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        try {
            if(response.getString(JSON_STATUS).equals(JSON_ACCEPTED))
                return response.getString(JSON_TOKEN);
            else
                return response.getString(JSON_MESSAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "ERROR";
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
