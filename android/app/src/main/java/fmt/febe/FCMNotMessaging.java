package fmt.febe;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fmt.febe.helper.BasicFunctions;


public class FCMNotMessaging extends FirebaseMessagingService {

    BasicFunctions basicFunctions;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        basicFunctions = new BasicFunctions(this);

        if (remoteMessage.getData().size() > 0) {

            try {

                if (isAppInBackground(this)) {

                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    sendPushNotification(json);

                }

            } catch (Exception e) {

                Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
    }


    private void sendPushNotification(JSONObject json) {

        try {

            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");

            Intent intent = new Intent(getApplicationContext(), Notifications.class);

            basicFunctions.showSmallFCMNotification(title, message, intent);

        } catch (JSONException e) {

            Toast.makeText(this, "Json Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    private boolean isAppInBackground(Context context) {

        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {

            assert am != null;
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }

        } else {

            assert am != null;
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }

        }

        return isInBackground;

    }

}