package tddd82.healthcare;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactActivity extends AppCompatActivity {

    private static Contact[] contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        JSONArray contactsJSON = CacheManager.getJSON("/contacts", this);

        contactList = new Contact[contactsJSON.length()];

        for (int i = 0; i < contactsJSON.length(); i++) {

            try {
                JSONObject row = contactsJSON.getJSONObject(i);
                contactList[i] = new Contact(row.getString("name"), row.getInt("phonenumber"));

            } catch (JSONException e) {

            }

        }
        ContactActivity.setContactList(contactList);

        ListView contactListView = (ListView) findViewById(R.id.list_view);
        ListAdapter customAdapter = new CustomAdapter(this, contactList);
        contactListView.setAdapter(customAdapter);
    }

    public static void setContactList(Contact[] list){
        contactList = list;
    }
}
