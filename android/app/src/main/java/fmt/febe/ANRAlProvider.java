package fmt.febe;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ANRAlProvider extends AppCompatActivity {


    TextView ANR_AL_PR_TIME, ANR_AL_PR_LOCATION, ANR_AL_PR_DATE, ANR_AL_PR_TEMPERATURE, ANR_AL_PR_ICON,
            ANR_AL_PR_DESCRIPTION, ANR_AL_PR_HUMIDITY, ANR_AL_PR_PRESSURE, ANR_AL_PR_MINMAXTEMP,
            ANR_AL_PR_WIND_SPEED, ANR_AL_PR_WIND_ANGLE, ANR_AL_PR_LABEL;

    Button ANR_AL_PR_DISMISS, ANR_AL_PR_SNOOZE;

    LinearLayout ANR_AL_PR_FORECAST_DETAILS;

    BasicFunctions.DatabaseHelper mOpenHelper;
    SQLiteDatabase SQL_DB;
    Cursor DB_CURSOR;

    String MY_LOCATION, MY_LOCATION_LATITUDE, MY_LOCATION_LONGITUDE, LABEL;

    Integer ALA_DURATION, SNO_DURATION, VIBRATION;

    private BasicFunctions basicFunctions;

    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr_al_provider);

        ANR_AL_PR_TIME = findViewById(R.id.anr_al_pr_time);
        ANR_AL_PR_LOCATION = findViewById(R.id.anr_al_pr_location);
        ANR_AL_PR_DATE = findViewById(R.id.anr_al_pr_date);
        ANR_AL_PR_TEMPERATURE = findViewById(R.id.anr_al_pr_temperature);
        ANR_AL_PR_ICON = findViewById(R.id.anr_al_pr_icon);
        ANR_AL_PR_DESCRIPTION = findViewById(R.id.anr_al_pr_description);
        ANR_AL_PR_HUMIDITY = findViewById(R.id.anr_al_pr_humidity);
        ANR_AL_PR_PRESSURE = findViewById(R.id.anr_al_pr_pressure);
        ANR_AL_PR_MINMAXTEMP = findViewById(R.id.anr_al_pr_minmaxtemp);
        ANR_AL_PR_WIND_SPEED = findViewById(R.id.anr_al_pr_wind_speed);
        ANR_AL_PR_WIND_ANGLE = findViewById(R.id.anr_al_pr_wind_angle);
        ANR_AL_PR_LABEL = findViewById(R.id.anr_al_pr_label);

        ANR_AL_PR_FORECAST_DETAILS = findViewById(R.id.anr_al_pr_for_det);

        ANR_AL_PR_SNOOZE = findViewById(R.id.anr_al_pr_snooze);
        ANR_AL_PR_DISMISS = findViewById(R.id.anr_al_pr_dismiss);

        basicFunctions = new BasicFunctions(ANRAlProvider.this);

        mOpenHelper = new BasicFunctions.DatabaseHelper(this);


        SQL_DB = mOpenHelper.getWritableDatabase();

        String SQL_ALA_SET_CREATE = "CREATE TABLE IF NOT EXISTS '" + basicFunctions.MY_ALARM_SETTINGS_TABLE + "' ( '"
                + basicFunctions.ALARM_SETTING_NAME + "' TEXT NOT NULL, '"
                + basicFunctions.ALARM_SETTING_VALUE + "' TEXT NOT NULL ) ;";

        SQL_DB.execSQL(SQL_ALA_SET_CREATE);

        String SQL_ALA_SET_SELECT = "SELECT * FROM " + basicFunctions.MY_ALARM_SETTINGS_TABLE;

        DB_CURSOR = SQL_DB.rawQuery(SQL_ALA_SET_SELECT, null);

        if(DB_CURSOR.getCount() > 0) {

            DB_CURSOR.moveToFirst();

            ALA_DURATION = Integer.parseInt(DB_CURSOR.getString(1)) * 60 * 1000;

            DB_CURSOR.moveToNext();

            LABEL = DB_CURSOR.getString(1);

            DB_CURSOR.moveToNext();

            SNO_DURATION = Integer.parseInt(DB_CURSOR.getString(1)) * 60 * 1000;

            DB_CURSOR.moveToNext();

            VIBRATION = Integer.parseInt(DB_CURSOR.getString(1));

        }

        else {

            String SQL_ALA_SET_INSERT = "INSERT INTO '"
                    + basicFunctions.MY_ALARM_SETTINGS_TABLE + "' ( '" + basicFunctions.ALARM_SETTING_NAME + "', '"
                    + basicFunctions.ALARM_SETTING_VALUE + "' ) VALUES ( 'Alarm_duration', '15' ), ( 'Label', '' )," +
                    " ( 'Snooze_duration', '10' ), ( 'Vibration', '1' ) ;";

            SQL_DB.execSQL(SQL_ALA_SET_INSERT);

            ALA_DURATION = 15 * 60 * 1000;

            LABEL = "";

            SNO_DURATION = 10 * 60 * 1000;

            VIBRATION = 1;

        }

        DB_CURSOR.close();

        SQL_DB.close();

        if (ActivityCompat.checkSelfPermission(ANRAlProvider.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ANRAlProvider.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ANRAlProvider.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        }

        else
            getLocation();


        ANR_AL_PR_SNOOZE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= 21)
                {
                    finishAndRemoveTask();
                }
                else
                {
                    finish();
                }

                mediaPlayer.stop();


                AlarmManager alarmManager = (AlarmManager) ANRAlProvider.this.getSystemService(ALARM_SERVICE);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, (SNO_DURATION / 60000));

                Intent intent = new Intent(ANRAlProvider.this, ANRAlReceiver.class);
                intent.putExtra("For", "Alarm");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ANRAlProvider.this, 99993, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                long time = calendar.getTimeInMillis();

                assert alarmManager != null;
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

            }
        });


        ANR_AL_PR_DISMISS.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                if(Build.VERSION.SDK_INT >= 21)
                {
                    finishAndRemoveTask();
                }
                else
                {
                    finish();
                }

                mediaPlayer.stop();

                AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent notIntent = new Intent(ANRAlProvider.this, ANRAlReceiver.class);
                PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANRAlProvider.this, 99993, notIntent, 0);
                assert notAlarmManager != null;
                notAlarmManager.cancel(notPendingIntent);
                notPendingIntent.cancel();

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {

            case 0: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation();

                else {

                    Toast.makeText(ANRAlProvider.this, "Kindly grant location permission to continue !", Toast.LENGTH_LONG).show();
                    finish();

                }

                break;
            }

            default:
                break;

        }
    }


    @SuppressLint("MissingPermission")
    private void getLocation(){

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert locationManager != null;

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, new Listener());

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new Listener());

        android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        setDetails(location);

    }


    private class Listener implements LocationListener {

        public void onLocationChanged(android.location.Location location) {}
        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){}

    }


    private void setDetails(android.location.Location location){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        MY_LOCATION_LATITUDE = String.valueOf(location.getLatitude());

        MY_LOCATION_LONGITUDE = String.valueOf(location.getLongitude());

        List<Address> addresses = null;

        try {

            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        } catch (IOException e) {

            e.printStackTrace();

        }

        assert addresses != null;

        MY_LOCATION = addresses.get(0).getLocality();

        ANR_AL_PR_LOCATION.setVisibility(View.VISIBLE);
        ANR_AL_PR_LOCATION.setText(MY_LOCATION);

        setUpAlarm();

        if(basicFunctions.isConnectingToInternet()) {

            ANR_AL_PR_FORECAST_DETAILS.setVisibility(View.VISIBLE);

            new GetCurrentForecastTask().execute();

        }

        else
            ANR_AL_PR_FORECAST_DETAILS.setVisibility(View.GONE);


    }



    private void setUpAlarm() {

        try {

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if(Build.VERSION.SDK_INT >= 21)
                    {
                        finishAndRemoveTask();
                    }
                    else
                    {
                        finish();

                    }

                    mediaPlayer.stop();

                    AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent notIntent = new Intent(ANRAlProvider.this, ANRAlReceiver.class);
                    PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANRAlProvider.this, 99993, notIntent, 0);
                    assert notAlarmManager != null;
                    notAlarmManager.cancel(notPendingIntent);
                    notPendingIntent.cancel();

                }

            }, ALA_DURATION);

            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + ANRAlProvider.this.getPackageName() + "/raw/alarm_sound");

            mediaPlayer = MediaPlayer.create(this, alarmSound);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            long[] pattern = { 0, 1000, 250, 500, 250, 500, 250, 500, 250, 500};

            if(VIBRATION == 1) {

                Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

                assert vibrator != null;
                vibrator.vibrate(pattern, -1);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("EE, d / M");

        ANR_AL_PR_TIME.setText(timeFormat.format(c.getTime()));

        ANR_AL_PR_DATE.setText(dateFormat.format(c.getTime()));

        if(LABEL.equals(""))
            ANR_AL_PR_LABEL.setVisibility(View.GONE);

        else
            ANR_AL_PR_LABEL.setText(LABEL);

        String sno_dur_text;

        if(SNO_DURATION == 60000)
            sno_dur_text = "SNOOZE 1 MINUTE !";

        else
            sno_dur_text = "SNOOZE " + (SNO_DURATION / 60000) + " MINUTES !";

        ANR_AL_PR_SNOOZE.setText(sno_dur_text);

    }


    @SuppressLint("StaticFieldLeak")
    private class GetCurrentForecastTask extends AsyncTask<String, Void, JSONObject> {

        private GetCurrentForecastTask() {}

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;

            try {

                jsonWeather = basicFunctions.getWeatherJSON(basicFunctions.OWM_CURRENT_FORECAST_URL,
                        MY_LOCATION_LATITUDE, MY_LOCATION_LONGITUDE);

            } catch (Exception e) {

                Toast.makeText(ANRAlProvider.this, "Error, cannot process JSON results !", Toast.LENGTH_LONG).show();

            }

            return jsonWeather;
        }

        @SuppressLint("DefaultLocale")
        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if(json != null){

                    JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject sys = json.getJSONObject("sys");
                    JSONObject main = json.getJSONObject("main");
                    JSONObject wind = json.getJSONObject("wind");

                    String temperature;

                    temperature = String.format("%.2f", main.getDouble("temp")) + " °C";

                    String iconText = basicFunctions.setWeatherIcon(weather.getInt("id"),
                            sys.getLong("sunrise") * 1000,
                            sys.getLong("sunset") * 1000);

                    String description = weather.getString("main") + ", " + weather.getString("description");

                    String humidity = main.getDouble("humidity") + " %";

                    String pressure = main.getDouble("pressure") + " hPa";

                    String max_min_temp;

                    max_min_temp = main.getInt("temp_min") + " / " + main.getInt("temp_max") + " °C";

                    String wind_speed;

                    wind_speed = wind.getDouble("speed") + " m / s";

                    String wind_angle = wind.getDouble("deg") + " degrees";


                    ANR_AL_PR_TEMPERATURE.setText(temperature);

                    ANR_AL_PR_ICON.setTypeface(BasicFunctions.weatherFont);
                    ANR_AL_PR_ICON.setText(Html.fromHtml(iconText));

                    ANR_AL_PR_DESCRIPTION.setText(description);


                    ANR_AL_PR_PRESSURE.setText(pressure);

                    ANR_AL_PR_HUMIDITY.setText(humidity);

                    ANR_AL_PR_MINMAXTEMP.setText(max_min_temp);


                    ANR_AL_PR_WIND_SPEED.setText(wind_speed);

                    ANR_AL_PR_WIND_ANGLE.setText(wind_angle);

                }

            } catch (JSONException e) {

                Toast.makeText(ANRAlProvider.this, "Error, cannot process JSON results !", Toast.LENGTH_LONG).show();

            }

        }
    }

}