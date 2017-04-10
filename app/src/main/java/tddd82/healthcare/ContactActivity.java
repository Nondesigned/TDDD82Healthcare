package tddd82.healthcare;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private static Contact[] contactList;
    private static Context context;
    static ListView contactListView;
    static ListAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ContactActivity.context = this;
        ContactActivity.contactListView = (ListView) findViewById(R.id.list_view);


        JSONArray contactsJSON = CacheManager.getJSON("/contacts", this);

        ContactActivity.contactList = new Contact[contactsJSON.length()];

        for (int i = 0; i < contactsJSON.length(); i++) {

            try {
                JSONObject row = contactsJSON.getJSONObject(i);
                contactList[i] = new Contact(row.getString("name"), row.getInt("phonenumber"));

            } catch (JSONException e) {

            }

        }
        ContactActivity.setContactList(contactList);

        updateTheView();

        new Thread(new Runnable(){
            @Override
            public void run(){
                while(true) {
                    GetContactsTask task = new GetContactsTask(context);
                    task.execute("https://itkand-3-1.tddd82-2017.ida.liu.se:8080/contacts");
                    try {
                        Thread.sleep(7000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void setContactList(Contact[] list){
        contactList = list;
    }

    public static void updateTheView(){
        ContactActivity.customAdapter = new CustomAdapter(ContactActivity.context, contactList);
        ContactActivity.contactListView.setAdapter(customAdapter);
    }

}
