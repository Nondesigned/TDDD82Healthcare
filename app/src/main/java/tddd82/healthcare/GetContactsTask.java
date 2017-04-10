package tddd82.healthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
    }

    protected String doInBackground(String... params) {
        String url = params[0];

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

            mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack(context));

            JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray m_response) {
                    CacheManager.put(m_response.toString(),"/contacts", context);
                    addContacts(m_response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.getMessage().equals("org.json.JSONException: Value null of type org.json.JSONObject$1" +
                                    " cannot be converted to JSONArray")){
                        addContacts(null);
                    }else{
                        addContacts(CacheManager.getJSON("/contacts", context));

                    }
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

        }

        return "Initialized login";
    }

    private void addContacts(JSONArray response) {
        if(response == null){
            Contact[] ca = new Contact[1];
            ca[0] = new Contact("Inga vänner", 112);
            ContactActivity.setContactList(ca);
            return;
            //banan
        }
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


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String result) {
        ContactActivity.updateTheView();
        Toast.makeText(context, result, Toast.LENGTH_SHORT);
    }

    public Boolean Check() {
        ConnectivityManager cn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            return true;
        } else {
//            Toast.makeText(context, "No internet connection.!",
//                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
