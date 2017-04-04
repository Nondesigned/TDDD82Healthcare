package tddd82.healthcare;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.Map;

public class PushNotice extends FirebaseMessagingService {
    public PushNotice() {
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Intent callingintent = new Intent(this,CallingActivity.class);
        callingintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Map<String,String> payload = remoteMessage.getData();
        if(payload.containsKey("CALLER")){
            callingintent.putExtra("CALLER",payload.get("CALLER"));
        }
        startActivity(callingintent);
    }

    @Override
    public void onDeletedMessages(){
    }
}
