package tddd82.healthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * Created by Clynch on 2017-02-15.
 * This AsyncTask handles logging into the system.
 * It takes the parameters and sends them by HTTP via the POST method.
 * It responds to the user via a message returned from the server.
 * Also gives feedback if something went wrong.
 */
class LoginTask extends AsyncTask<String, Void, String> {
    private Context context;
    private TaskCallback callback;
    private AlertDialog alertDialog;
    private JSONObject response;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static final String SHARED_PREDS_TOKEN = "TOKEN";
    private static final String JSON_PASSWORD = "password";
    private static final String JSON_CARD = "card";
    private static final String JSON_FCMTOKEN = "fcmtoken";
    private static final String JSON_ACCEPTED = "accepted";
    private static final String JSON_STATUS = "status";
    private static final String JSON_DECLINED = "failed";
    private static final String JSON_TOKEN = "token";
    private static final String JSON_MESSAGE = "message";
    private static final String TEST_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6I" +
            "kpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.EkN-DOsnsuRjRO6BxXemmJDm3HbxrbRzXglbN2S4sOkopdU4IsDxTI8jO19W_A4K8ZPJijNLis4EZ" +
            "sHeY559a4DFOd50_OqgHGuERTqYZyuhtF39yxJPAjUESwxk2J5k_4zM3O-vtd1Ghyo4IbqKKSy6J9mTniYJPenn5-HIirE";

    public LoginTask(Context context, TaskCallback callback) {
        this.context = context;
        this.callback = callback;

        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("test");

    }

    protected String doInBackground(String... params) {
        String card = params[0];
        String password = params[1];
        String url = params[2];

        String fcmtoken = FirebaseInstanceId.getInstance().getToken();

        Check();

        final boolean connectedToServer = true;

        if (connectedToServer) {
            JSONObject credentials = new JSONObject();
            try {
                credentials.put(JSON_CARD, Long.parseLong(card));
                credentials.put(JSON_PASSWORD, password);
                credentials.put(JSON_FCMTOKEN, fcmtoken);
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestQueue mRequestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);


            final JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.POST, url, credentials, new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(JSONObject m_response) {
                            response = m_response;

                            try {

                                preferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
                                editor = preferences.edit();
                                editor.putString(SHARED_PREDS_TOKEN, response.getString(JSON_TOKEN));
                                editor.apply();

                                String token = preferences.getString("TOKEN", null);
                                if (token != null) {
                                    Log.v("TOKENEN", token);
                                }

                                if (response.getString(JSON_STATUS).equals(JSON_ACCEPTED)) {
                                    callback.done();
                                    //return response.getString(JSON_TOKEN);
                                } else if (response.getString(JSON_STATUS).equals(JSON_DECLINED)) {


                                } else {
                                    //return response.getString(JSON_MESSAGE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ((LoginActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast wrong = Toast.makeText(context, "Wrong credentials", Toast.LENGTH_LONG);
                                    wrong.setGravity(Gravity.TOP | Gravity.CENTER, 0, 20);
                                    wrong.show();
                                }
                            });


                        }
                    }
                    );
            mRequestQueue.add(jsonRequest);
            //RQ.start();
            // Start the queue
            mRequestQueue.start();
        } else {

            try {
                response = new JSONObject();
                response.put(JSON_STATUS, JSON_ACCEPTED);
                response.put(JSON_MESSAGE, "TESTMEDDELANDE");
                response.put(JSON_TOKEN, TEST_TOKEN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "Initialized login";
    }


    @Override
    protected void onPreExecute() {
        alertDialog.setTitle("Retriving ID");
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_SHORT);
    }

    public Boolean Check() {
        ConnectivityManager cn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            return true;
        } else {
            Toast.makeText(context, "No internet connection.!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
