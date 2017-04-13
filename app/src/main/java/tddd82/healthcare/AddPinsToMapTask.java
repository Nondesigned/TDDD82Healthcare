package tddd82.healthcare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by pergu on 21-Mar-17.
 */

public class AddPinsToMapTask extends AsyncTask<String,Void,String> {

    private Context context;
    private LatLng latLng;
    private JSONObject response;
    private MapsActivity mapsActivity;
    private String groupId;
    private JSONObject pin;

    public AddPinsToMapTask(Context context, LatLng latLng, String groupId, MapsActivity mapsActivity){
        this.context = context;
        this.latLng = latLng;
        this.mapsActivity = mapsActivity;
        this.groupId = groupId;
    }

    public AddPinsToMapTask(Context context, MapsActivity mapsActivity){
        this.context = context;
        this.mapsActivity = mapsActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];

        pin = new JSONObject();
        if(String.valueOf(params[1]) == "new pin"){
            try{
                pin.put("groupid", groupId.split(":")[0]);
                pin.put("long", String.valueOf(latLng.longitude));
                pin.put("lat", String.valueOf(latLng.latitude));
                pin.put("type", "wounded_guy");

            }catch (Exception e){
                Log.d("JsonFailure", e.getMessage());
            }
        }else{
            try {
                pin = new JSONObject(params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        RequestQueue mRequestQueue;
        mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack(context));

        final JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, url, pin, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject m_response) {
                        response = m_response;

                        Log.v("PIN RESPONSE","RESPONSE!");

                            try {
                                if(response.getString("status").equals("ok")) {
                                    mapsActivity.updatePinsOnMap();

                                }else{
                                    Log.d("STATUS", response.getString("status"));
                                }
                            } catch (Exception e) {
                                Log.d("MAP FEL", e.getMessage());
                            }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("ONADDPIN", error.toString());
                        cachePin();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Token", context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE).getString("TOKEN", "def"));
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse){
                try{
                    String jsonString = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
                    return Response.success(new JSONObject(jsonString),HttpHeaderParser.parseCacheHeaders(networkResponse));
                }catch (UnsupportedEncodingException e){
                    return Response.error(new ParseError(e));
                }catch (JSONException je){
                    return Response.error(new ParseError(je));
                }
            }
        };
        mRequestQueue.add(jsonRequest);
        mRequestQueue.start();

        return null;
    }

    private void cachePin() {
        JSONArray localPins = CacheManager.getJSON("/localPins", context);
        localPins.put(pin);
        CacheManager.put(localPins.toString(), "/localPins", context);
    }

}
