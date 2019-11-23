package fmt.febe.helper;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fmt.febe.R;

import static android.content.Context.ALARM_SERVICE;

//Created by Febin M Thomas on 01-Dec-17.
//Updated by Febin M Thomas on 25-May-18.


public class BasicFunctions {


    private Context mContext;

    private RequestQueue mRequestQueue;

    public static Typeface weatherFont;

    private static String today;

    private SharedPreferences LOGIN_PREFERENCE;
    private SharedPreferences.Editor LOGIN_PREF_EDITOR;
    private static final String LOGIN_PREF_NAME = "LOGIN_PREF";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences FCM_NOT_PREFERENCE;
    private SharedPreferences.Editor FCM_NOT_PREF_EDITOR;
    private static final String FCM_NOT_PREF_NAME = "FCM_NOT_PREF";
    private static final String FCM_NOT_TOKEN = "FCM_NOT_TOKEN";
    private static final int FCM_NOT_SMALL_NOTIFICATION = 235;

    public final String TWITTER_KEY = "3YTldmp6c7hTdP8dPYSKNmCqT";

    public final String TWITTER_SECRET = "Uap0PJ3q54GBUx1bB7ApH8ABLqnZv9ArhOUspZq7r2e4VirapF";

    public final String OWM_CURRENT_FORECAST_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

    public final String OWM_HOURLY_FORECAST_URL =
            "http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=metric&cnt=16";

    public final String OWM_DAILY_FORECAST_URL =
            "http://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&units=metric&cnt=10";


    public final String JSON_ARRAY = "result";

    public final String KEY_USER_ID = "user_id";
    public final String KEY_USER_USERNAME = "username";
    private final String KEY_USER_PASSWORD = "password";
    private final String KEY_USER_EMAIL = "email";
    public final String KEY_CHAT_ROOM_ID = "chat_room_id";
    public final String KEY_NOTIFICATION_POST_USERREAD = "post_user_read";
    public final String KEY_NOTIFICATION_USER_ID = "notification_user_id";
    public final String KEY_NOTIFICATION_USERNAME = "notification_username";
    public final String KEY_POST_TIMESTAMP = "timestamp";
    public final String KEY_POST_ID = "post_id";
    final String KEY_NOTIFICATION_COUNT = "notification_count";
    final String KEY_CHAT_COUNT = "chat_count";

    private static final String BASE_URL = "https://febtech.000webhostapp.com/android_feb/";

    public static final String ADD_CHAT = BASE_URL + "addChat.php";
    private static final String BASIC_FUNCTIONS = BASE_URL + "basicFunctions.php";
    public static final String DELETE_CHAT = BASE_URL + "deleteChat.php";
    public static final String DELETE_NOTIFICATION = BASE_URL + "deleteNotification.php";
    public static final String GET_ALL_CHAT_ROOMS = BASE_URL + "getAllChatRooms.php?user_id=";
    public static final String GET_ALL_NOTIFICATIONS = BASE_URL + "getAllNotifications.php?current_user_id=";
    public static final String GET_CHAT_ROOM = BASE_URL + "getChatRoom.php?chat_room_id=";
    static final String GET_COUNT = BASE_URL + "getCount.php?user_id=";
    private static final String GET_CURRENT_USER = BASE_URL + "getCurrentUser.php?email=";
    public static final String LOGIN = BASE_URL + "login.php";
    static final String LOGOUT = BASE_URL + "logout.php";
    public static final String SEARCH_FOR_PROFILES = BASE_URL + "searchForProfiles.php?index=";
    private static final String SEND_EMAIL = BASE_URL + "sendEmail.php";
    public static final String SEND_MESSAGE = BASE_URL + "sendMessage.php?chat_room_id=";
    public static final String SIGNIN = BASE_URL + "signin.php";

    private static final String DATABASE_NAME = "feb.db";
    private static final int DATABASE_VERSION = 1;

    public String MY_ALARM_SETTINGS_TABLE = "my_alarm_settings";
    public String ALARM_SETTING_NAME = "alarm_setting_name";
    public String ALARM_SETTING_VALUE = "alarm_setting_value";

    public String MY_ALARMS_TABLE = "my_alarms";
    public String ALARM_ID = "alarm_id";
    public  String ALARM_STATUS = "alarm_status";
    public String ALARM_HOUR = "alarm_hour";
    public String ALARM_MINUTE = "alarm_minute";
    public String ALARM_SUNDAY = "alarm_sunday";
    public String ALARM_MONDAY = "alarm_monday";
    public String ALARM_TUESDAY = "alarm_tuesday";
    public String ALARM_WEDNESDAY = "alarm_wednesday";
    public String ALARM_THURSDAY = "alarm_thursday";
    public String ALARM_FRIDAY = "alarm_friday";
    public String ALARM_SATURDAY = "alarm_saturday";


    public String MY_NOTES_TABLE = "my_notes";
    public String NOTE_ID = "note_id";
    public String NOTE_TEXT = "note_text";

    public String MY_REMINDERS_TABLE = "my_reminders";
    public String REMINDER_ID = "reminder_id";
    public String REMINDER_TEXT = "reminder_text";
    public String REMINDER_HOUR = "reminder_hour";
    public String REMINDER_MINUTE = "reminder_minute";
    public String REMINDER_DAY = "reminder_day";
    public String REMINDER_MONTH = "reminder_month";
    public String REMINDER_YEAR = "reminder_year";

    @SuppressLint("CommitPrefEdits")
    public BasicFunctions(Context context){

        mContext = context;

        weatherFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/weathericons-regular-webfont.ttf");

        LOGIN_PREFERENCE = mContext.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE);
        LOGIN_PREF_EDITOR = LOGIN_PREFERENCE.edit();

        FCM_NOT_PREFERENCE = mContext.getSharedPreferences(FCM_NOT_PREF_NAME, Context.MODE_PRIVATE);
        FCM_NOT_PREF_EDITOR = FCM_NOT_PREFERENCE.edit();

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

    }


    public void setUpReminder(int id, int hour, int minute, int day, int month, int year, String text){

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(mContext, ANRAlReceiver.class);
        intent.putExtra("For", "Reminder");
        intent.putExtra("Text", text);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long time = (calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));

        if(System.currentTimeMillis()>time)
        {
            time = time + (1000*60*60*24);
        }

        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);

    }


    public void setUpAlarm(int id, int hour, int minute, int day){

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        if(day == 1)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        else if(day == 2)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        else if(day == 3)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

        else if(day == 4)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);

        else if(day == 5)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);

        else if(day == 6)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        else
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        int identifier = Integer.parseInt(String.valueOf(day) + "" + String.valueOf(id));

        Intent intent = new Intent(mContext, ANRAlReceiver.class);
        intent.putExtra("For", "Alarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, identifier, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long time = calendar.getTimeInMillis();

        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);

    }


    public void setLogin(boolean isLoggedIn) {

        LOGIN_PREF_EDITOR.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        LOGIN_PREF_EDITOR.commit();

    }


    public boolean isLoggedIn(){
        return LOGIN_PREFERENCE.getBoolean(KEY_IS_LOGGED_IN, false);
    }


    public void getCurrentUserData(String email) {

        String url = GET_CURRENT_USER + email;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON_User(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }


    private void showJSON_User(String response) {

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
            JSONObject profileData = result.getJSONObject(0);

            setUserDetails(profileData.getString(KEY_USER_ID),
                    profileData.getString(KEY_USER_USERNAME),
                    profileData.getString(KEY_USER_EMAIL),
                    profileData.getString(KEY_USER_PASSWORD));

            setLogin(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setUserDetails(String user_id, String user_name, String user_email, String user_password){

        LOGIN_PREF_EDITOR.putString(KEY_USER_ID, user_id);
        LOGIN_PREF_EDITOR.putString(KEY_USER_USERNAME, user_name);
        LOGIN_PREF_EDITOR.putString(KEY_USER_EMAIL, user_email);
        LOGIN_PREF_EDITOR.putString(KEY_USER_PASSWORD, user_password);

        LOGIN_PREF_EDITOR.commit();
    }


    void setUserDataNull(){

        String user_email = LOGIN_PREFERENCE.getString(KEY_USER_EMAIL, "");
        String user_password = LOGIN_PREFERENCE.getString(KEY_USER_PASSWORD, "");

        LOGIN_PREF_EDITOR.clear();

        FCM_NOT_PREF_EDITOR.clear();
        FCM_NOT_PREF_EDITOR.commit();

        LOGIN_PREF_EDITOR.putString(KEY_USER_EMAIL, user_email);
        LOGIN_PREF_EDITOR.putString(KEY_USER_PASSWORD, user_password);

        LOGIN_PREF_EDITOR.commit();
    }


    public String getUser_id(){ return LOGIN_PREFERENCE.getString(KEY_USER_ID, "");}

    public String getUser_name(){ return LOGIN_PREFERENCE.getString(KEY_USER_USERNAME, "");}

    public String getUser_email(){ return LOGIN_PREFERENCE.getString(KEY_USER_EMAIL, "");}

    public String getUser_password(){ return LOGIN_PREFERENCE.getString(KEY_USER_PASSWORD, "");}


    public void performTask(String method, String item_id, String item_title){

        PerformTaskBackgroundTask performtaskbackgroundTask = new PerformTaskBackgroundTask(mContext);
        performtaskbackgroundTask.execute(method, item_id, item_title, getUser_id());

    }


    @SuppressLint("StaticFieldLeak")
    private class PerformTaskBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;

        private ProgressDialog pDialog;

        PerformTaskBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ctx);
            pDialog.setMessage("Performing Task ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];
            String id = params[1];
            String comment = params[2];
            String user_id = params[3];

            try {

                URL url = new URL(BASIC_FUNCTIONS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode(method, "UTF-8")
                        + "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                        + "&" + URLEncoder.encode("comment", "UTF-8") + "=" + URLEncoder.encode(comment, "UTF-8")
                        + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS,"iso-8859-1"));

                StringBuilder response = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine())!=null)  {
                    response.append(line);
                }

                bufferedReader.close();
                httpURLConnection.disconnect();
                IS.close();

                return response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            switch(result){

                case "Notification sent successfully !":

                    pDialog.dismiss();
                    break;

                case "Success !":

                    pDialog.dismiss();
                    break;

                default:

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

            }
        }
    }


    public JSONObject getWeatherJSON(String URL, String lat, String lon){

        try {

            java.net.URL url = new URL(String.format(URL, lat, lon));

            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            String OPEN_WEATHER_MAP_API = "228a0735d5437d1e6fe3078844cebafc";

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder json = new StringBuilder(1024);
            String tmp;
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }


    public String setWeatherIcon(int actualId, long sunrise, long sunset){

        int id = actualId / 100;
        String icon = "";

        if(actualId == 800){

            long currentTime = new Date().getTime();

            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }

        } else {

            switch(id) {

                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;

            }
        }
        return icon;
    }


    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {}

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    }


    public boolean isConnectingToInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            assert connectivityManager != null;
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;

            for (Network mNetwork : networks) {

                networkInfo = connectivityManager.getNetworkInfo(mNetwork);

                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }

        } else {

            if (connectivityManager != null) {

                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

                if (info != null) {

                    for (NetworkInfo anInfo : info) {

                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    public void storeFCMNotDeviceToken(String token){

        FCM_NOT_PREFERENCE = mContext.getSharedPreferences(FCM_NOT_PREF_NAME, Context.MODE_PRIVATE);
        FCM_NOT_PREF_EDITOR.putString(FCM_NOT_TOKEN, token);
        FCM_NOT_PREF_EDITOR.commit();

    }


    public String getFCMNotDeviceToken(){

        FCM_NOT_PREFERENCE = mContext.getSharedPreferences(FCM_NOT_PREF_NAME, Context.MODE_PRIVATE);
        return  FCM_NOT_PREFERENCE.getString(FCM_NOT_TOKEN, null);

    }


    public boolean isAppInstalled(String app_id) {

        PackageManager pm = mContext.getPackageManager();

        try {

            pm.getPackageInfo(app_id, PackageManager.GET_ACTIVITIES);
            return true;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;

    }


    public void showSmallFCMNotification(String title, String message, Intent intent) {

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        FCM_NOT_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, title);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.mes_notification).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.mes_notification)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_logo_main))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(FCM_NOT_SMALL_NOTIFICATION, notification);

        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getApplicationContext().getPackageName() + "/raw/notification_sound");
            Ringtone r = RingtoneManager.getRingtone(mContext.getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("SimpleDateFormat")
    public static String getTimeStamp(String dateStr) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format1.setTimeZone(TimeZone.getTimeZone("GMT"));

        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format1.parse(dateStr);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            todayFormat.setTimeZone(TimeZone.getDefault());
            String dateToday = todayFormat.format(cal.getTime());

            if(dateToday.equals(today))
            {
                format1 = new SimpleDateFormat("HH:mm");
                format1.setTimeZone(TimeZone.getDefault());
                String date1 = format1.format(cal.getTime());
                timestamp = "Today  ||  "+ date1;
            }

            else {

                format1 = new SimpleDateFormat("dd");
                format1.setTimeZone(TimeZone.getDefault());
                String date1 = format1.format(cal.getTime());

                format1 = new SimpleDateFormat("MM");
                format1.setTimeZone(TimeZone.getDefault());
                String date2 = format1.format(cal.getTime());

                format1 = new SimpleDateFormat("yyyy");
                format1.setTimeZone(TimeZone.getDefault());
                String date3 = format1.format(cal.getTime());

                format1 = new SimpleDateFormat("HH:mm");
                format1.setTimeZone(TimeZone.getDefault());
                String date4 = format1.format(cal.getTime());

                timestamp = date1 + " | " + date2 + " | " + date3
                        + "  ||  " + date4;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;

    }

    private RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);

    }


    public void sendEmail(String from, String from_email, String to_email, String subject, String message) {

        String temp_subject, temp_message;

        switch (from) {

            case "ContactUs":

                temp_subject = to_email + " : " + subject;

                temp_message = "Application : Feb" + "\n\nName : " + to_email + "\n\nEmail-Id : " + from_email;
                temp_message += "\n\nSubject : " + subject + "\n\nMessage : " + message;

                to_email = "fmt.febulous@gmail.com";

                subject = temp_subject;

                message = temp_message;

                break;

            case "RecoverPassword":

                temp_message = "Greetings " + from_email + ",\n\n";
                temp_message += "We have received a request from your account stating that you have forgotten your password. Kindly find your password below -> ";
                temp_message += "\n\n" + "Your Password : " + message + "\n\n";
                temp_message += "If you did not initiate this password request, please contact us at : fmt.febulous@gmail.com to report the issue.";
                temp_message += "\n\n" + "Regards, \nThe Feb Team";

                from_email = "fmt.febulous@gmail.com";

                message = temp_message;

                break;

            case "SendEmail":

                break;

        }

        SendingEmailBackgroundTask emailBackgroundTask = new SendingEmailBackgroundTask(mContext);

        emailBackgroundTask.execute(from, from_email, to_email, subject, message);

    }


    @SuppressLint("StaticFieldLeak")
    private class SendingEmailBackgroundTask extends AsyncTask<String, Void, String> {

        private ProgressDialog bf_loading;

        Context ctx;
        String data;

        SendingEmailBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            bf_loading = new ProgressDialog(mContext);
            bf_loading.setMessage("Sending Email ... ");
            bf_loading.setIndeterminate(false);
            bf_loading.setCancelable(true);
            bf_loading.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];
            String from_email = params[1];
            String to_email = params[2];
            String subject = params[3];
            String message = params[4];

            try {

                URL url = new URL(SEND_EMAIL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                data = URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode(method, "UTF-8")
                        + "&" + URLEncoder.encode("from_email", "UTF-8") + "=" + URLEncoder.encode(from_email, "UTF-8")
                        + "&" + URLEncoder.encode("to_email", "UTF-8") + "=" + URLEncoder.encode(to_email, "UTF-8")
                        + "&" + URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8")
                        + "&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }

                bufferedReader.close();
                httpURLConnection.disconnect();
                IS.close();

                return response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            switch (result) {

                case "Thank You for your message, kindly wait until our Team responds to it !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

                case "An Account with this Email-Id does not exist !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

                case "A recovery email has been sent to your Email-Id !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

                case "Your Email has been sent !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

                case "Sending Email Failed !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

                default:

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

            }
        }
    }

}