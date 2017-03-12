package tddd82.healthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.SSLCertificateSocketFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
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
import com.android.volley.toolbox.Volley;
//import com.auth0.android.jwt.JWT;
import com.google.firebase.iid.FirebaseInstanceId;
import com.securepreferences.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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

    public LoginTask(Context context, TaskCallback callback){
        this.context = context;
        this.callback = callback;

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

        String fcmtoken = FirebaseInstanceId.getInstance().getToken();



        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            InputStream stream = context.getAssets().open("cert.pem");
            BufferedInputStream bis = new BufferedInputStream(stream);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                Certificate cert = cf.generateCertificate(bis);
                trustStore.setCertificateEntry("cert" + bis.available(), cert);
            }
            KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmfactory.init(trustStore, "1234".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);


            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, tmf.getTrustManagers(), new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        }catch (Exception e){
            Log.v(AntonsLog.TAG, e.getMessage());
        }

        Check();

        final boolean connectedToServer = true;

        if (connectedToServer) {
            JSONObject credentials = new JSONObject();
            try {
                credentials.put(JSON_CARD, Integer.parseInt(card));
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

            Log.v(AntonsLog.TAG, credentials.toString());

            final JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.POST, url, credentials, new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(JSONObject m_response) {
                            response = m_response;
                            Log.v(AntonsLog.TAG,"RESPONSE!");

                            try {
                                Log.v(AntonsLog.TAG, response.getString(JSON_TOKEN));
                                //JWT jwt = new JWT(response.getString(JSON_TOKEN).getBytes());

                                preferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
                                editor = preferences.edit();
                                editor.putString(SHARED_PREDS_TOKEN, response.getString(JSON_TOKEN));
                                editor.apply();

                                String token = preferences.getString("TOKEN", null);
                                if(token!= null){
                                    Log.v("TOKENEN", token);
                                }

                                if (response.getString(JSON_STATUS).equals(JSON_ACCEPTED)) {
                                    Log.v(AntonsLog.TAG, "RESPONSE: " + response.getString(JSON_STATUS));
                                    callback.done();
                                    //return response.getString(JSON_TOKEN);
                                }else if(response.getString(JSON_STATUS).equals(JSON_DECLINED)){


                                }else {
                                    Log.v(AntonsLog.TAG, "Message är " + response.getString(JSON_MESSAGE));
                                    //return response.getString(JSON_MESSAGE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e){
                                Log.v(AntonsLog.TAG, e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ((LoginActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast wrong = Toast.makeText(context, "Wrong credentials", Toast.LENGTH_LONG);
                                    wrong.setGravity(Gravity.TOP|Gravity.CENTER,0,20);
                                    wrong.show();
                                }
                            });


                        }
                    }
                    );
            mRequestQueue.add(jsonRequest);
            //RQ.start();
            Log.v(AntonsLog.TAG,"INNAN START");
            // Start the queue
            mRequestQueue.start();
            Log.v(AntonsLog.TAG, "EFTER START");
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
