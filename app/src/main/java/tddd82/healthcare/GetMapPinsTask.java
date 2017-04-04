package tddd82.healthcare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pergu on 2017-03-10.
 */

public class GetMapPinsTask extends AsyncTask<String, Void, String> {

    private Context context;
    private JSONArray response;
    private GoogleMap mMap;
    private MapsActivity mapsActivity;
    private LatLng[] testPins;
    private String url;

    public GetMapPinsTask(Context context, GoogleMap mMap, MapsActivity mapsActivity) {
        this.context = context;
        this.mMap = mMap;
        this.mapsActivity = mapsActivity;
    }

    protected String doInBackground(String... params) {

        url = params[0];

        RequestQueue mRequestQueue;

        mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack(context));

        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray m_response) {
                Log.d("GETMAPPINSRESPONSE", m_response.toString());
                CacheManager.put(m_response.toString(), "/pins", context);
                addPinsToMap(m_response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GETMAPPINSERROR", error.toString());
                Log.e("In ERROR", CacheManager.getJSON("/pins", context).toString());
                addPinsToMap(CacheManager.getJSON("/pins", context));
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Token", context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE).getString("TOKEN", "def"));
                return headers;
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse networkResponse) {
                try {
                    String jsonString = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
                    return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(networkResponse));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        mRequestQueue.add(jsonRequest);

        return "Fetching markers";
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        return;
    }


    private void addPinsToMap(JSONArray response) {
        JSONArray markerArray = new JSONArray();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject marker = new JSONObject();
                JSONObject row = response.getJSONObject(i);
                marker.put("type", row.getString("type"));
                LatLng markerLatLng = new LatLng(Double.parseDouble(row.getString("lat")), Double.parseDouble(row.getString("long")));
                marker.put("latlng", markerLatLng);
                marker.put("id", row.getString("id"));
                Log.d("TYPE", marker.toString());
                markerArray.put(marker);
            } catch (JSONException e) {

            }
        }

        Log.d("PINS", "Sätter ut pins");
        mapsActivity.setMarkerList(markerArray);
        Log.d("MARKERARRAY", markerArray.toString());
        mapsActivity.addPinsToMap(mMap);

    }
}
