package tddd82.healthcare;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Created by Oskar on 2017-03-08.
 */

public interface OnEventListener extends EventListener{

    void onEvent(EventObject... e);
}
