package tddd82.healthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.JWT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;


/**
 * Created by Clynch on 2017-03-08.
 */

public class GetContactsTask extends AsyncTask<String, Void, String>{

    private Context context;
    private AlertDialog alertDialog;
    private JSONObject response;

    SharedPreferences login;
    SharedPreferences.Editor editor;



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

        login = PreferenceManager.getDefaultSharedPreferences(context);
        editor = login.edit();

        JSONObject credentials = new JSONObject();
        try {
            credentials.put(GlobalVariables.getJsonTokenTag(), GlobalVariables.getSharedPrefsTokenTag());
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

        JSONArray array;

        try {
            array = response.getJSONArray(GlobalVariables.getJsonContactList());
            Contact[] contactList = new Contact[array.length()];

            for (int i = 0; i < array.length(); i++) {

                try {
                    JSONObject row = array.getJSONObject(i);
                    contactList[i] = new Contact(row.getString("name"), row.getInt("number"));

                } catch (JSONException e) {

                }

            }
            ContactActivity.setContactList(contactList);
        } catch (JSONException e) {
            e.printStackTrace();
        }




        //TODO Response är svaret från server. Lös så att vi får ut kontakterna från den.
        return "ERROR";
    }


    public void getStringContact(JSONObject response) {



    }


    @Override
    protected void onPreExecute() {
        alertDialog.setTitle("Retriving ID");
    }

    @Override
    protected void onPostExecute(String result) {
       /* Toast toast = Toast.makeText(context, result, Toast.LENGTH_SHORT);
        toast.show();
        Toast toast2 = Toast.makeText(context, login.getString("ID", "DEFAULT VALUE"), Toast.LENGTH_SHORT);
        toast2.show();*/
    }


}
