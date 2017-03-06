package tddd82.healthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAKey;
import java.util.Date;

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
    private JSONObject response;

    SharedPreferences login;
    SharedPreferences.Editor editor;

    private static final int maxTokenLength = 10000;

    private static final String JSON_PASSWORD = "password";
    private static final String JSON_CARD = "card";
    private static final String JSON_ACCEPTED = "accepted";
    private static final String JSON_STATUS = "status";
    private static final String JSON_DECLINED = "declined";
    private static final String JSON_TOKEN = "token";
    private static final String JSON_MESSAGE = "message";
    private static final String TEST_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6I" +
            "kpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.EkN-DOsnsuRjRO6BxXemmJDm3HbxrbRzXglbN2S4sOkopdU4IsDxTI8jO19W_A4K8ZPJijNLis4EZ" +
            "sHeY559a4DFOd50_OqgHGuERTqYZyuhtF39yxJPAjUESwxk2J5k_4zM3O-vtd1Ghyo4IbqKKSy6J9mTniYJPenn5-HIirE";

    private static final byte[] OTPKey = new byte[maxTokenLength];



    public LoginTask(Context context){
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("test");
        login = PreferenceManager.getDefaultSharedPreferences(context);
        editor = login.edit();
        new SecureRandom().nextBytes(OTPKey);
    }
    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(String output);
    }


    protected String doInBackground(String... params) {
        String card = params[0];
        String password = params[1];
        String url = params[2];

        boolean connectedToServer = true;

        if (connectedToServer) {
            JSONObject credentials = new JSONObject();
            try {
                credentials.put("card", card);
                credentials.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestQueue RQ = Volley.newRequestQueue(context);
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.POST, url, credentials, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject m_response) {
                            response = m_response;
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
            RQ.add(jsonRequest);
            RQ.start();
        }
        else {

            try {
                response = new JSONObject();
                response.put(JSON_STATUS, JSON_ACCEPTED);
                response.put(JSON_MESSAGE, "TESTMEDDELANDE");
                response.put(JSON_TOKEN, TEST_TOKEN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            JWT jwt = new JWT(response.getString(JSON_TOKEN));

            Log.v(AntonsLog.TAG, "issuer = " + jwt.getIssuer());
            Log.v(AntonsLog.TAG, "subject = " + jwt.getSubject());


            String encryptedToken = encrypt(response.getString(JSON_TOKEN));
            editor.putString("TOKEN", encryptedToken);
            editor.commit();
            editor.putString("ID", card);
            editor.commit();


            Log.v(AntonsLog.TAG, "Innan decrypt är token " + login.getString("TOKEN", "Default Value"));
            Log.v(AntonsLog.TAG, "Efter encrypt + decrypt är token " + decrypt(login.getString("TOKEN", "Default Value")));

            if (response.getString(JSON_STATUS).equals(JSON_ACCEPTED)) {
                Log.v(AntonsLog.TAG, "Token är " + response.getString(JSON_TOKEN));
                Intent startDummy = new Intent(context, DummyActivity.class);
                context.startActivity(startDummy);
                return response.getString(JSON_TOKEN);
            } else {
                Log.v(AntonsLog.TAG, "Message är " + response.getString(JSON_MESSAGE));
                return response.getString(JSON_MESSAGE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    private String decrypt(String input) {
        //This is weak
        byte[] encoded = input.getBytes();
        byte[] restored = new byte[encoded.length];
        for (int i = 0; i < encoded.length; i++){
            restored[i] = (byte) (encoded[i] ^ OTPKey[i]);
        }
        return new String(restored);
    }

    private String encrypt(String input) {
        //This is weak
        byte[] secret = input.getBytes();
        byte[] encoded = new byte[secret.length];
        for (int i = 0; i < secret.length; i++){
            encoded[i] = (byte) (secret[i] ^ OTPKey[i]);
        }
        return new String(encoded);
    }

    @Override
    protected void onPreExecute() {
        alertDialog.setTitle("Retriving ID");
    }

    @Override
    protected void onPostExecute(String result) {
        Toast toast = Toast.makeText(context, result, Toast.LENGTH_SHORT);
        toast.show();
        Toast toast2 = Toast.makeText(context, login.getString("ID", "DEFAULT VALUE"), Toast.LENGTH_SHORT);
        toast2.show();
    }
}
