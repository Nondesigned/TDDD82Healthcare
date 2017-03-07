package tddd82.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Clynch on 2017-03-07.
 */

public class MyPreferenceManager {
    private static SharedPreferences sharedPreferences;
    MyPreferenceManager(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
