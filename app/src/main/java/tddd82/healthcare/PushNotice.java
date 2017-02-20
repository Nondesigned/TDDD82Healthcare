package tddd82.healthcare;

import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.FirebaseMessagingService;

public class PushNotice extends FirebaseMessagingService {
    public PushNotice() {
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        String token = FirebaseInstanceId.getInstance().getToken();
        Intent intent = new Intent(this,callingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("token",token);
        startActivity(intent);
    }

    @Override
    public void onDeletedMessages(){

    }
}
