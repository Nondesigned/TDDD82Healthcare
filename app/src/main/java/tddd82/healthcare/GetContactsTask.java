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
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
//import com.auth0.android.jwt.JWT;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonArray;
import com.securepreferences.SecurePreferences;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Clynch on 2017-02-15.
 * This AsyncTask handles logging into the system.
 * It takes the parameters and sends them by HTTP via the POST method.
 * It responds to the user via a message returned from the server.
 * Also gives feedback if something went wrong.
 */
class GetContactsTask extends AsyncTask<String,Void,String> {
    private Context context;
    private TaskCallback callback;
    private AlertDialog alertDialog;
    private JSONArray response;

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

    public GetContactsTask(Context context){
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("test");

    }
    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(String output);
    }

    protected String doInBackground(String... params) {
        String url = params[0];

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
                credentials.put(GlobalVariables.getJsonTokenTag(), GlobalVariables.getSharedPrefsTokenTag());
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

            JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray m_response) {
                    response = m_response;
                    Log.v(AntonsLog.TAG,"RESPONSE!");

                    Contact[] contactList = new Contact[response.length()];

                    for (int i = 0; i < response.length(); i++) {

                        try {
                            JSONObject row = response.getJSONObject(i);
                            contactList[i] = new Contact(row.getString("name"), row.getInt("phonenumber"));

                        } catch (JSONException e) {

                        }

                    }
                    ContactActivity.setContactList(contactList);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v(AntonsLog.TAG, "error");
//                    Log.v(AntonsLog.TAG, error.getMessage());

                    Log.v("ERRRRROOOORRRRRRR", error.toString());


                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = super.getHeaders();
                    Log.v(AntonsLog.TAG, "Vi stoppar token");
                    Log.v(AntonsLog.TAG, context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE).getString("TOKEN", "def"));
                    params.put("Token", context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE).getString("TOKEN", "def"));
                    return params;
                }
            };
            try {
                jsonRequest.getHeaders();
                Log.v(AntonsLog.TAG, "works");
            } catch (AuthFailureError authFailureError) {
                authFailureError.printStackTrace();
            }
            mRequestQueue.add(jsonRequest);

            //RQ.start();
            Log.v(AntonsLog.TAG,"INNAN START");
            // Start the queue
            mRequestQueue.start();
            Log.v(AntonsLog.TAG, "EFTER START");
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
            Toast.makeText(context, "No internet connection.!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
