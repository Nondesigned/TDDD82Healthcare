package tddd82.healthcare;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class PushNoticeId extends FirebaseInstanceIdService {
    public PushNoticeId() {
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
