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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pergu on 21-Mar-17.
 */

public class DeleteMarkerTask extends AsyncTask<String,Void,String> {

    private Context context;
    private String pinId;
    private JSONObject response;
    private MapsActivity mapsActivity;

    public DeleteMarkerTask(Context context, String pinId, MapsActivity mapsActivity){
        this.context = context;
        this.pinId = pinId;
        this.mapsActivity = mapsActivity;

    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        JSONObject pin = new JSONObject();
        try{
            pin.put("id", pinId);
        }catch (Exception e){
            Log.d("JsonFailure", e.getMessage());
        }

        RequestQueue mRequestQueue;

        mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack(context));

        Log.d("PIN", pin.toString());

        final JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, url, pin, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject m_response) {
                        response = m_response;

                        Log.v("PIN RESPONSE","RESPONSE!");

                        try {
                            if(response.getString("status").equals("ok")) {
                                //mapsActivity.updatePinsOnMap();
                                mapsActivity.removePins(pinId);

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
                        Log.v("ONDELETE", error.toString());

                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Token", context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE).getString("TOKEN", "def"));
                Log.d("TOKEN", context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE).getString("TOKEN", "def"));
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

}
