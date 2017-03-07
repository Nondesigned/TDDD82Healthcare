package tddd82.healthcare;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Clynch on 2017-03-06.
 */

public class Contact {
    private String name;
    private int number;

    Contact(String name, int number){
        this.name = name;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
