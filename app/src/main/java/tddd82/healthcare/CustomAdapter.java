package tddd82.healthcare;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Clynch on 2017-03-07.
 */

class CustomAdapter extends ArrayAdapter<Contact> {
    Contact[] contacts;

    CustomAdapter(Context context, Contact[] contacts) {
        super(context, R.layout.contact, contacts);
        this.contacts = contacts;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.contact,parent,false);

        TextView nameTV = (TextView) customView.findViewById(R.id.contact_name_TV);
        TextView numberTV = (TextView) customView.findViewById(R.id.phone_number_TV);
        Button callButton = (Button) customView.findViewById(R.id.call_button);

        nameTV.setText(contacts[position].getName());

        return customView;
    }

}
