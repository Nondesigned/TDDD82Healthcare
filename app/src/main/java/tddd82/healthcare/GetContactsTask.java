package tddd82.healthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
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
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
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
    private AlertDialog alertDialog;
    private JSONArray response;

    public GetContactsTask(Context context){
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("test");

    }

    protected String doInBackground(String... params) {
        String url = params[0];

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


                }
            }){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();

                    headers.put("Token", context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE).getString("TOKEN", "def"));
                    return headers;
                }

                @Override
                protected Response<JSONArray> parseNetworkResponse(NetworkResponse networkResponse){
                    try{
                        String jsonString = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
                        return Response.success(new JSONArray(jsonString),HttpHeaderParser.parseCacheHeaders(networkResponse));
                    }catch (UnsupportedEncodingException e){
                        return Response.error(new ParseError(e));
                    }catch (JSONException je){
                        return Response.error(new ParseError(je));
                    }
                }
            };

            mRequestQueue.add(jsonRequest);

            //RQ.start();
            // Start the queue
            mRequestQueue.start();
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
