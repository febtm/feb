package fmt.febe;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.VIBRATOR_SERVICE;


public class ANRAlReceiver extends BroadcastReceiver {


    BasicFunctions basicFunctions;

    Context mContext;


    @Override
    public void onReceive(Context context, Intent intent)
    {

        this.mContext = context;

        if(intent.getStringExtra("For").equals("Reminder")) {

            setUpNotification(intent.getStringExtra("Text"));
        }

        else if(intent.getStringExtra("For").equals("Alarm")){

            Intent alarmIntent = new Intent(mContext, ANRAlProvider.class);
            alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(alarmIntent);

        }

    }

    private void setUpNotification(String message){

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_logo_main))
                        .setSmallIcon(R.drawable.anr_reminders)
                        .setContentTitle("FèBé")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setAutoCancel(true);

        Intent notificationIntent = new Intent(mContext, ANR.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 99997, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

        try {

            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification_sound");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();

            Vibrator v = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
            v.vibrate(500);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}