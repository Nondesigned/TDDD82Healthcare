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

/**
 * Created by Clynch on 2017-03-08.
 */

public class GetContactsTask extends AsyncTask<String, Void, String>{

    private Context context;
    private AlertDialog alertDialog;
    private JSONObject response;

    SharedPreferences login;
    SharedPreferences.Editor editor;


    private static final String SHARED_PREFS_TOKEN = "TOKEN";
    private static final String JSON_PASSWORD = "password";
    private static final String JSON_CARD = "card";
    private static final String JSON_ACCEPTED = "accepted";
    private static final String JSON_STATUS = "status";
    private static final String JSON_DECLINED = "declined";
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
                        public void onResponse(JSONObject m_response) {response = m_response;
                        }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        RQ.add(jsonRequest);
        RQ.start();

        //TODO Response är svaret från server. Lös så att vi får ut kontakterna från den.
        return "ERROR";
    }

    private void startDummy() {
        Intent startDummy = new Intent(context, DummyActivity.class);
        context.startActivity(startDummy);
    }

    @Override
    protected void onPreExecute() {
        alertDialog.setTitle("Retriving ID");
    }

    @Override
    protected void onPostExecute(String result) {
        Toast toast = Toast.makeText(context, result, Toast.LENGTH_SHORT);
        toast.show();
        Toast toast2 = Toast.makeText(context, login.getString("ID", "DEFAULT VALUE"), Toast.LENGTH_SHORT);
        toast2.show();
    }


}
