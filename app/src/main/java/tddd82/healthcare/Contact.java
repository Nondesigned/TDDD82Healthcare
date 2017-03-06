package tddd82.healthcare;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Clynch on 2017-03-06.
 */

public class Contact {
    LinearLayout rowLL;
    TextView contactNameTV;
    Button callButton;
    Context context;
    public Contact(Context context, String name){
        this.context = context;
        contactNameTV = new TextView(context);
        callButton = new Button(context);
        rowLL = new LinearLayout(context);

        rowLL.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        callButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contactNameTV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        rowLL.addView(contactNameTV, 0);
        rowLL.addView(callButton, 0);
    }
}
