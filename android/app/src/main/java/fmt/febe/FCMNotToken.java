package fmt.febe;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import fmt.febe.helper.BasicFunctions;


public class FCMNotToken extends FirebaseInstanceIdService {

    BasicFunctions basicFunctions;

    @Override
    public void onTokenRefresh() {

        basicFunctions = new BasicFunctions(this);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        basicFunctions.storeFCMNotDeviceToken(refreshedToken);

    }

}