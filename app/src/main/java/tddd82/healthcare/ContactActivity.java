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

public class ContactActivity extends AppCompatActivity {

    Contact[] contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ListView contactListView = (ListView) findViewById(R.id.list_view);
        contactList = new Contact[20];

        contactList[0] = new Contact("Pelle", 0000);
        contactList[1] = new Contact("Pelle2", 1111);
        contactList[2] = new Contact("Pelle3", 2222);
        contactList[3] = new Contact("Pelle4", 3333);
        contactList[4] = new Contact("Pelle5", 4444);
        contactList[5] = new Contact("Pelle6", 5555);
        contactList[6] = new Contact("Pelle7", 6666);
        contactList[7] = new Contact("Pelle8", 7777);
        contactList[8] = new Contact("Pelle9", 8888);
        contactList[9] = new Contact("Pelle10", 9999);
        contactList[10] = new Contact("Pelle", 1337);
        contactList[11] = new Contact("Pelle2", 1337);
        contactList[12] = new Contact("Pelle3", 1337);
        contactList[13] = new Contact("Pelle4", 1337);
        contactList[14] = new Contact("Pelle5", 1337);
        contactList[15] = new Contact("Pelle6", 1337);
        contactList[16] = new Contact("Pelle7", 1337);
        contactList[17] = new Contact("Pelle8", 1337);
        contactList[18] = new Contact("Pelle9", 1337);
        contactList[19] = new Contact("Pelle10", 1337);

        String[] test = {"ett", "två", "tre"};


        ListAdapter customAdapter = new CustomAdapter(this, contactList);
        contactListView.setAdapter(customAdapter);
    }
}
