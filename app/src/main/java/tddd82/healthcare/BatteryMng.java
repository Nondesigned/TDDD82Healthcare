package tddd82.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

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
        //final float PERCENT_LIMIT = limit;
        //return (getPercentage()) > PERCENT_LIMIT;
        PowerManager powerManager = (PowerManager)
                context.getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && powerManager.isPowerSaveMode()) {
            return false;
        }else
            return true;
    }
    private static Intent getStatus(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent i = context.registerReceiver(null, ifilter);
        return i;
    }

    public static boolean isCharging(){
        Intent batteryStatus = getStatus();
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        return isCharging;
    }
}
