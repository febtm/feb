package fmt.febe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import fmt.febe.helper.BasicFunctions;
import fmt.febe.helper.Menu;


public class WeatherForecast extends AppCompatActivity {


    TextView WF_LOCATION, WF_ICON, WF_DESCRIPTION,
             WF_TEMPERATURE, WF_HUMIDITY, WF_PRESSURE,
             WF_WIND_SPEED, WF_WIND_ANGLE, WF_SUNRISE_TIME, WF_SUNSET_TIME;

    ImageButton MENU_BUTTON;

    ArrayList<LocationDailyForecastValues> WF_DAILY_LOCATION_LIST;
    RecyclerView mDailyRecyclerView;
    RecyclerView.Adapter mDailyAdapter;
    RecyclerView.LayoutManager mDailyLayoutManager;

    ArrayList<LocationHourlyForecastValues> WF_HOURLY_LOCATION_LIST;
    RecyclerView mHourlyRecyclerView;
    RecyclerView.Adapter mHourlyAdapter;
    RecyclerView.LayoutManager mHourlyLayoutManager;

    LinearLayout WF_CO_FEBWEATHER;

    String MY_LOCATION, MY_LOCATION_LATITUDE, MY_LOCATION_LONGITUDE;

    int COUNT;

    private ProgressDialog progressDialog;

    private BasicFunctions basicFunctions;

    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather_forecast);

        basicFunctions = new BasicFunctions(WeatherForecast.this);

        menu = new Menu(WeatherForecast.this);

        WF_CO_FEBWEATHER = findViewById(R.id.wf_co_febuweather);
        WF_LOCATION = findViewById(R.id.wf_location);
        WF_ICON = findViewById(R.id.wf_icon);
        WF_DESCRIPTION = findViewById(R.id.wf_description);
        WF_TEMPERATURE = findViewById(R.id.wf_temperature);
        WF_HUMIDITY = findViewById(R.id.wf_humidity);
        WF_PRESSURE = findViewById(R.id.wf_pressure);
        WF_WIND_SPEED = findViewById(R.id.wf_wind_speed);
        WF_WIND_ANGLE = findViewById(R.id.wf_wind_angle);
        WF_SUNRISE_TIME = findViewById(R.id.wf_sunrise_time);
        WF_SUNSET_TIME = findViewById(R.id.wf_sunset_time);
        mDailyRecyclerView = findViewById(R.id.wf_daily_forecast);
        mHourlyRecyclerView = findViewById(R.id.wf_hourly_forecast);
        MENU_BUTTON = findViewById(R.id.wf_menu);

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu.LeftDrawer.toggleLeftDrawer();

            }
        });

        if (ActivityCompat.checkSelfPermission(WeatherForecast.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(WeatherForecast.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WeatherForecast.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        }

        else {

            if(basicFunctions.isConnectingToInternet())
                getLocation();

            else {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){

                            case DialogInterface.BUTTON_POSITIVE:

                                if(basicFunctions.isConnectingToInternet())
                                    getLocation();

                                else {

                                    Toast.makeText(WeatherForecast.this,
                                            "No Internet Connection. Try again later !",
                                            Toast.LENGTH_LONG).show();

                                    dialog.dismiss();

                                }

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                Toast.makeText(WeatherForecast.this,
                                        "No Internet Connection. Try again later !",
                                        Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                                break;

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherForecast.this);
                builder.setMessage("No Internet Connection. Try again ?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }

        }

        mHourlyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHourlyLayoutManager = linearLayoutManager;
        mHourlyRecyclerView.setLayoutManager(mHourlyLayoutManager);
        WF_HOURLY_LOCATION_LIST = new ArrayList<>();
        mHourlyAdapter = new HourlyForecastAdapter(WF_HOURLY_LOCATION_LIST);
        mHourlyRecyclerView.setAdapter(mHourlyAdapter);

        mDailyRecyclerView.setHasFixedSize(true);
        mDailyLayoutManager = new LinearLayoutManager(this);
        mDailyRecyclerView.setLayoutManager(mDailyLayoutManager);
        WF_DAILY_LOCATION_LIST = new ArrayList<>();
        mDailyAdapter = new DailyForecastAdapter(WF_DAILY_LOCATION_LIST);
        mDailyRecyclerView.setAdapter(mDailyAdapter);

        WF_CO_FEBWEATHER.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=fmt.febuweather")));

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

                    Toast.makeText(WeatherForecast.this, "Kindly grant Location permission to continue !", Toast.LENGTH_LONG).show();
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

        fetchForecast();

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

    }


    private void fetchForecast(){

        progressDialog = new ProgressDialog(WeatherForecast.this);
        progressDialog.setMessage("Fetching Forecast Details ... ");
        progressDialog.show();

        new GetCurrentForecastTask().execute();

    }


    private class Listener implements LocationListener {

        public void onLocationChanged(android.location.Location location) {}
        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){}

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

                Toast.makeText(WeatherForecast.this, "Error, Cannot process JSON results", Toast.LENGTH_LONG).show();

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

                    String iconText = basicFunctions.setWeatherIcon(weather.getInt("id"),
                            sys.getLong("sunrise") * 1000,
                            sys.getLong("sunset") * 1000);

                    String description = weather.getString("main") + ", " + weather.getString("description");

                    String temperature = String.format("%.2f", main.getDouble("temp")) + " °C";

                    String humidity = main.getDouble("humidity") + " %";

                    String pressure = main.getDouble("pressure") + " hPa";

                    String sunrise_time, sunset_time;

                    long sunrise_millis = sys.getLong("sunrise") * 1000L;
                    long sunset_millis = sys.getLong("sunset") * 1000L;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

                    Date d = new Date(sunrise_millis);
                    sunrise_time = sdf.format(d);

                    d = new Date(sunset_millis);
                    sunset_time = sdf.format(d);

                    String wind_speed = wind.getDouble("speed") + " m / s";

                    String wind_angle = wind.getDouble("deg") + " degrees";

                    WF_LOCATION.setText(MY_LOCATION);

                    WF_ICON.setTypeface(BasicFunctions.weatherFont);
                    WF_ICON.setText(Html.fromHtml(iconText));

                    WF_DESCRIPTION.setText(description);
                    WF_TEMPERATURE.setText(temperature);
                    WF_HUMIDITY.setText(humidity);
                    WF_PRESSURE.setText(pressure);

                    WF_WIND_SPEED.setText(wind_speed);
                    WF_WIND_ANGLE.setText(wind_angle);
                    WF_SUNRISE_TIME.setText(sunrise_time);
                    WF_SUNSET_TIME.setText(sunset_time);

                    COUNT = 0;

                    new GetHourlyForecastTask().execute();

                }

            } catch (JSONException e) {

                Toast.makeText(WeatherForecast.this, "Error, Cannot process JSON results", Toast.LENGTH_LONG).show();

            }

        }
    }


    @SuppressLint("StaticFieldLeak")
    private class GetHourlyForecastTask extends AsyncTask<String, Void, JSONObject> {

        private GetHourlyForecastTask() {}

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;

            try {

                jsonWeather = basicFunctions.getWeatherJSON(basicFunctions.OWM_HOURLY_FORECAST_URL,
                        MY_LOCATION_LATITUDE, MY_LOCATION_LONGITUDE);

            } catch (Exception e) {

                Toast.makeText(WeatherForecast.this, "Error, Cannot process JSON results", Toast.LENGTH_LONG).show();

            }

            return jsonWeather;
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if(json != null){

                    JSONObject list = json.getJSONArray("list").getJSONObject(COUNT);
                    JSONObject weather = list.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = list.getJSONObject("main");


                    long millis = list.getLong("dt")*1000L;

                    Date d = new Date(millis);
                    Calendar original = Calendar.getInstance();
                    original.setTimeInMillis(millis);

                    Calendar today = Calendar.getInstance();

                    String time;

                    if(today.get(Calendar.DATE) == original.get(Calendar.DATE)){

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        sdf.setTimeZone(TimeZone.getDefault());
                        time = "Today, " + sdf.format(d);

                    } else {

                        SimpleDateFormat sdf = new SimpleDateFormat("EE, HH:mm");
                        sdf.setTimeZone(TimeZone.getDefault());
                        time = sdf.format(d);

                    }

                    com.luckycatlabs.sunrisesunset.dto.Location location = new com.luckycatlabs.sunrisesunset.dto.Location(MY_LOCATION_LATITUDE, MY_LOCATION_LONGITUDE);
                    SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, TimeZone.getDefault());

                    String sunriseForDate = calculator.getOfficialSunriseForDate(original) + ":00";
                    String sunsetForDate = calculator.getOfficialSunsetForDate(original) + ":00";

                    Time sunRise = Time.valueOf(sunriseForDate);
                    Time sunSet = Time.valueOf(sunsetForDate);

                    String iconText = basicFunctions.setWeatherIcon(weather.getInt("id"),
                            sunRise.getTime() * 1000, sunSet.getTime() * 1000);

                    String temperature = main.getInt("temp") + " °C";

                    String pressure = main.getInt("pressure") + " hPa";

                    String humidity = main.getInt("humidity") + " %";

                    LocationHourlyForecastValues obj = new LocationHourlyForecastValues(time, iconText, temperature, humidity, pressure);

                    WF_HOURLY_LOCATION_LIST.add(obj);

                    mHourlyAdapter.notifyDataSetChanged();

                    COUNT++;

                    if(COUNT == 16){

                        COUNT = 0;

                        new GetDailyForecastTask().execute();

                    }

                    else
                        new GetHourlyForecastTask().execute();

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(WeatherForecast.this, "Error, Cannot process JSON results", Toast.LENGTH_LONG).show();

            }
        }
    }


    private class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.DataHolder> {

        private ArrayList<LocationHourlyForecastValues> mDataset;

        class DataHolder extends RecyclerView.ViewHolder {

            TextView WF_H_TIME, WF_H_ICON, WF_H_TEMPERATURE, WF_H_HUMIDITY, WF_H_PRESSURE;

            DataHolder(final View itemView) {
                super(itemView);

                WF_H_TIME = itemView.findViewById(R.id.wf_i_h_time);
                WF_H_ICON = itemView.findViewById(R.id.wf_i_h_icon);
                WF_H_TEMPERATURE = itemView.findViewById(R.id.wf_i_h_temperature);
                WF_H_HUMIDITY = itemView.findViewById(R.id.wf_i_h_humidity);
                WF_H_PRESSURE = itemView.findViewById(R.id.wf_i_h_pressure);

                WF_H_ICON.setTypeface(BasicFunctions.weatherFont);

            }

        }

        HourlyForecastAdapter(ArrayList<LocationHourlyForecastValues> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_wf_item_hourly, parent, false);

            mHourlyRecyclerView.setMinimumHeight(mHourlyRecyclerView.getHeight() + view.getHeight());

            return new DataHolder(view);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(@NonNull DataHolder holder, int position) {

            holder.WF_H_TIME.setText(mDataset.get(position).getWF_H_TIME());
            holder.WF_H_ICON.setText(Html.fromHtml(mDataset.get(position).getWF_H_ICON()));
            holder.WF_H_TEMPERATURE.setText(mDataset.get(position).getWF_H_TEMPERATURE());
            holder.WF_H_HUMIDITY.setText(mDataset.get(position).getWF_H_HUMIDITY());
            holder.WF_H_PRESSURE.setText(mDataset.get(position).getWF_H_PRESSURE());

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }


    private class LocationHourlyForecastValues {

        private String WF_H_TIME;
        private String WF_H_ICON;
        private String WF_H_TEMPERATURE;
        private String WF_H_HUMIDITY;
        private String WF_H_PRESSURE;

        LocationHourlyForecastValues(String time, String icon, String temperature, String humidity, String pressure){
            WF_H_TIME = time;
            WF_H_ICON = icon;
            WF_H_TEMPERATURE = temperature;
            WF_H_HUMIDITY = humidity;
            WF_H_PRESSURE = pressure;
        }

        String getWF_H_TIME() {
            return WF_H_TIME;
        }

        String getWF_H_ICON() {
            return WF_H_ICON;
        }

        String getWF_H_TEMPERATURE() {
            return WF_H_TEMPERATURE;
        }

        String getWF_H_HUMIDITY() {
            return WF_H_HUMIDITY;
        }

        String getWF_H_PRESSURE() {
            return WF_H_PRESSURE;
        }

    }


    @SuppressLint("StaticFieldLeak")
    private class GetDailyForecastTask extends AsyncTask<String, Void, JSONObject> {

        private GetDailyForecastTask() {}

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;

            try {

                jsonWeather = basicFunctions.getWeatherJSON(basicFunctions.OWM_DAILY_FORECAST_URL,
                        MY_LOCATION_LATITUDE, MY_LOCATION_LONGITUDE);

            } catch (Exception e) {

                Toast.makeText(WeatherForecast.this, "Error, Cannot process JSON results", Toast.LENGTH_LONG).show();

            }

            return jsonWeather;
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                if(json != null){

                    JSONObject list = json.getJSONArray("list").getJSONObject(COUNT);
                    JSONObject weather = list.getJSONArray("weather").getJSONObject(0);
                    JSONObject temp = list.getJSONObject("temp");


                    long millis = list.getLong("dt")*1000L;

                    Date d = new Date(millis);
                    Calendar original = Calendar.getInstance();
                    original.setTimeInMillis(millis);

                    Calendar today = Calendar.getInstance();

                    String date;

                    if(today.get(Calendar.DATE) == original.get(Calendar.DATE)){

                        SimpleDateFormat sdf = new SimpleDateFormat("d / M");
                        sdf.setTimeZone(TimeZone.getDefault());
                        date = "Today, " + sdf.format(d);

                    } else{

                        SimpleDateFormat sdf = new SimpleDateFormat("EE, d / M");
                        sdf.setTimeZone(TimeZone.getDefault());
                        date = sdf.format(d);

                    }

                    com.luckycatlabs.sunrisesunset.dto.Location location = new com.luckycatlabs.sunrisesunset.dto.Location(MY_LOCATION_LATITUDE, MY_LOCATION_LONGITUDE);
                    SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, TimeZone.getDefault());

                    String sunriseForDate = calculator.getOfficialSunriseForDate(original) + ":00";
                    String sunsetForDate = calculator.getOfficialSunsetForDate(original) + ":00";

                    Time sunRise = Time.valueOf(sunriseForDate);
                    Time sunSet = Time.valueOf(sunsetForDate);

                    String iconText = basicFunctions.setWeatherIcon(weather.getInt("id"),
                            sunRise.getTime() * 1000, sunSet.getTime() * 1000);

                    String description = weather.getString("main") + ", " + weather.getString("description");

                    String temperature = temp.getInt("min") + " / " + temp.getInt("max") + " °C";

                    LocationDailyForecastValues obj = new LocationDailyForecastValues(date, iconText, description, temperature);

                    WF_DAILY_LOCATION_LIST.add(obj);

                    mDailyAdapter.notifyDataSetChanged();

                    COUNT++;

                    if(COUNT == 10)
                        progressDialog.dismiss();

                    else
                        new GetDailyForecastTask().execute();

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(WeatherForecast.this, "Error, Cannot process JSON results", Toast.LENGTH_LONG).show();

            }
        }
    }


    private class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.DataHolder> {

        private ArrayList<LocationDailyForecastValues> mDataset;

        class DataHolder extends RecyclerView.ViewHolder {

            TextView WF_D_DATE, WF_D_ICON, WF_D_DESCRIPTION, WF_D_TEMPERATURE;

            DataHolder(final View itemView) {
                super(itemView);

                WF_D_DATE = itemView.findViewById(R.id.wf_i_d_date);
                WF_D_ICON = itemView.findViewById(R.id.wf_i_d_icon);
                WF_D_DESCRIPTION = itemView.findViewById(R.id.wf_i_d_description);
                WF_D_TEMPERATURE = itemView.findViewById(R.id.wf_i_d_temperature);

                WF_D_ICON.setTypeface(BasicFunctions.weatherFont);

            }

        }

        DailyForecastAdapter(ArrayList<LocationDailyForecastValues> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_wf_item_daily, parent, false);

            mDailyRecyclerView.setMinimumHeight(mDailyRecyclerView.getHeight() + view.getHeight());

            return new DataHolder(view);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(@NonNull DataHolder holder, int position) {

            holder.WF_D_DATE.setText(mDataset.get(position).getWF_D_DATE());
            holder.WF_D_DESCRIPTION.setText(mDataset.get(position).getWF_D_DESCRIPTION());
            holder.WF_D_ICON.setText(Html.fromHtml(mDataset.get(position).getWF_D_ICON()));
            holder.WF_D_TEMPERATURE.setText(mDataset.get(position).getWF_D_TEMPERATURE());

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }


    private class LocationDailyForecastValues {

        private String WF_D_DATE;
        private String WF_D_ICON;
        private String WF_D_DESCRIPTION;
        private String WF_D_TEMPERATURE;

        LocationDailyForecastValues(String date, String icon, String description, String temperature){
            WF_D_DATE = date;
            WF_D_DESCRIPTION = description;
            WF_D_ICON = icon;
            WF_D_TEMPERATURE = temperature;
        }

        String getWF_D_DATE() {
            return WF_D_DATE;
        }

        String getWF_D_DESCRIPTION() {
            return WF_D_DESCRIPTION;
        }

        String getWF_D_ICON() {
            return WF_D_ICON;
        }

        String getWF_D_TEMPERATURE() {
            return WF_D_TEMPERATURE;
        }

    }

}