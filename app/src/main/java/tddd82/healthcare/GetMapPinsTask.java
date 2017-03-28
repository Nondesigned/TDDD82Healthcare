package tddd82.healthcare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by pergu on 2017-03-10.
 */

public class GetMapPinsTask extends AsyncTask<String,Void,String> {

    private Context context;
    private JSONArray response;
    private GoogleMap mMap;
    private MapsActivity mapsActivity;

    public GetMapPinsTask(Context context, GoogleMap mMap, MapsActivity mapsActivity){
        this.context = context;
        this.mMap = mMap;
        this.mapsActivity = mapsActivity;
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
            Log.v("MAP ERROR:", e.getMessage());
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

                Log.v("MAP RESPONSE","RESPONSE!");

                LatLng[] mapPinList = new LatLng[response.length()];
                String[] typeOfDamageList = new String[response.length()];
                JSONArray markerArray = new JSONArray();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject marker = new JSONObject();
                        JSONObject row = response.getJSONObject(i);
                        marker.put("type", row.getString("type"));
                        LatLng markerLatLng =  new LatLng(Double.parseDouble(row.getString("lat")), Double.parseDouble(row.getString("long")));
                        marker.put("latlng", markerLatLng);
                        marker.put("id", row.getString("id"));
                        Log.d("TYPE", marker.toString());
                        markerArray.put(marker);
                        //mapPinList[i] = new LatLng(Double.parseDouble(row.getString("lat")), Double.parseDouble(row.getString("long")));
                        //typeOfDamageList[i] = new String(row.getString("type"));
                    } catch (JSONException e) {

                    }
                }
                Log.d("PINS", "Sätter ut pins");
                mapsActivity.setMarkerList(markerArray);
                Log.d("MARKERARRAY", markerArray.toString());
                //mapsActivity.setMapPinsList(mapPinList, typeOfDamageList);
                mapsActivity.addPinsToMap(mMap);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("ERRRRROOOORRRRRRR", error.toString());

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
        mRequestQueue.start();

        return "Fetching markers";
    }
    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        return;
    }

}
