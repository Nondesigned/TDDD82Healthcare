package tddd82.healthcare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pergu on 21-Mar-17.
 */

public class GetGroupTask extends AsyncTask<String,Void,String> {

    private Context context;
    private JSONArray response;
    private MapsActivity mapsActivity;


    public GetGroupTask(Context context, MapsActivity mapsActivity){
        this.context = context;
        this.mapsActivity = mapsActivity;
    }

    protected String doInBackground(String... params) {
        String url = params[0];

        RequestQueue mRequestQueue;
        mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack(context));
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray m_response) {
                response = m_response;
                Log.v("GROUP RESPONSE","RESPONSE!");

                HashMap<String, String> groupMap = new HashMap<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject row = response.getJSONObject(i);
                        groupMap.put(row.getString("id"), row.getString("name"));
                        Log.d("PUTTING", row.getString("id"));
                        Log.d("PUTTING", row.getString("name"));
                    } catch (JSONException e) {

                    }
                }
                Log.d("GROUP", "Sets GroupMap");
                String[] groupArray = new String[groupMap.size()];
                Object[] valuesArray = groupMap.values().toArray();
                Object[] idArray = groupMap.keySet().toArray();
                for(int i=0; i<groupMap.size(); i++){
                    groupArray[i] = idArray[i].toString()+":"+valuesArray[i].toString();
                }

                mapsActivity.setGroupArray(groupArray);
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

        return "Fetching groups";
    }

}
