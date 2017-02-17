package tddd82.healthcare;

import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.IBinder;
import android.util.Log;


import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.FirebaseMessagingService;

public class PushNotice extends FirebaseMessagingService {
    public PushNotice() {
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d("sd","fuck you");
    }

    @Override
    public void onDeletedMessages(){

    }
}
