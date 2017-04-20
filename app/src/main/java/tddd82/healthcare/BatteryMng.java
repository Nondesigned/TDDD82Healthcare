package tddd82.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by Clynch on 2017-04-04.
 */

public class BatteryMng {
    private static Context context;
    private static float limit = 0.15f;

    public static void setContext(Context m_context){
        BatteryMng.context = m_context;
    }

    public static float getPercentage(){
        Intent batteryStatus = getStatus();

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return (level / (float)scale);
    }

    public static boolean doVideo(){
        final float PERCENT_LIMIT = limit;
        return (getPercentage()) > PERCENT_LIMIT;
    }
    private static Intent getStatus(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent i = context.registerReceiver(null, ifilter);
        return i;
    }
}
