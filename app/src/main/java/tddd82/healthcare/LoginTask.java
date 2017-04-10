package tddd82.healthcare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.design.internal.BottomNavigationMenu;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Clynch on 2017-02-15.
 * This AsyncTask handles logging into the system.
 * It takes the parameters and sends them by HTTP via the POST method.
 * It responds to the user via a message returned from the server.
 * Also gives feedback if something went wrong.
 */
class LoginTask extends AsyncTask<String, Void, String> {
    private Context context;
    private TaskCallback callback;
    private AlertDialog alertDialog;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static final String SHARED_PREDS_TOKEN = "TOKEN";
    private static final String JSON_PASSWORD = "password";
    private static final String JSON_CARD = "card";
    private static final String JSON_FCMTOKEN = "fcmtoken";
    private static final String JSON_ACCEPTED = "accepted";
    private static final String JSON_STATUS = "status";
    private static final String JSON_TOKEN = "token";

    public LoginTask(Context context, TaskCallback callback) {
        this.context = context;
        this.callback = callback;

        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("test");

    }

    protected String doInBackground(String... params) {
        String card = params[0];
        String password = params[1];
        String url = params[2];

        String fcmtoken = FirebaseInstanceId.getInstance().getToken();

        Check();


        JSONObject credentials = new JSONObject();
        try {
            credentials.put(JSON_CARD, Long.parseLong(card));
            credentials.put(JSON_PASSWORD, password);
            credentials.put(JSON_FCMTOKEN, fcmtoken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue mRequestQueue;

        mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack(context));

        final JsonObjectRequest jsonRequest = new JsonObjectRequest
            (Request.Method.POST, url, credentials, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {

                        preferences = context.getSharedPreferences("tddd82.healthcare", context.MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.putString(SHARED_PREDS_TOKEN, response.getString(JSON_TOKEN));
                        editor.apply();

                        String token = preferences.getString("TOKEN", null);
                        if (token != null) {
                            Log.v("TOKENEN", token);
                        }

                        if (response.getString(JSON_STATUS).equals(JSON_ACCEPTED)) {
                            callback.done();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    makeToast("Wrong credentials");

                }
            }
            );
        mRequestQueue.add(jsonRequest);
        //RQ.start();
        // Start the queue
        mRequestQueue.start();
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
            makeToast("No internet connection.!");
            return false;
        }
    }
    private void makeToast(final String s){
        ((LoginActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast wrong = Toast.makeText(context, s, Toast.LENGTH_LONG);
                wrong.setGravity(Gravity.TOP | Gravity.CENTER, 0, 20);
                wrong.show();
            }
        });
    }

}
