package tddd82.healthcare;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class PushNoticeId extends FirebaseInstanceIdService {
    public PushNoticeId() {
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // TODO refresh token in DB
    }

}
