package fmt.febe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class HomeAssistant extends AppCompatActivity {


    TextView TEMPERATURE, DEVICE_STATUS_TEXT, DEVICE_SWITCH_TEXT;

    ImageView DEVICE_STATUS;

    ImageButton DEVICE_SWITCH, HA_MENU;

    private static int USER_VAL, NO_OF_VAL;

    private ProgressDialog progressDialog;

    private Menu menu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_assistant);

        menu = new Menu(HomeAssistant.this);

        TEMPERATURE = findViewById(R.id.ha_temperature);

        DEVICE_STATUS = findViewById(R.id.ha_device_status);
        DEVICE_STATUS_TEXT = findViewById(R.id.ha_device_status_text);

        DEVICE_SWITCH = findViewById(R.id.ha_device_switch);
        DEVICE_SWITCH_TEXT = findViewById(R.id.ha_device_switch_text);

        HA_MENU = findViewById(R.id.ha_menu);

        HA_MENU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu.LeftDrawer.toggleLeftDrawer();

            }
        });

        progressDialog = new ProgressDialog(HomeAssistant.this);

        if (USER_VAL == 0) {

            DEVICE_SWITCH.setBackgroundResource(R.drawable.ha_switch_off);
            DEVICE_SWITCH_TEXT.setText(getString(R.string.ha_off));
            DEVICE_SWITCH_TEXT.setTextColor(Color.RED);

        } else if (USER_VAL == 1){

            DEVICE_SWITCH.setBackgroundResource(R.drawable.ha_switch_on);
            DEVICE_SWITCH_TEXT.setText(getString(R.string.ha_on));
            DEVICE_SWITCH_TEXT.setTextColor(Color.GREEN);

        }

        progressDialog.setMessage("Fetching Cloud Details ... ");
        progressDialog.show();

        NO_OF_VAL = 1;

        BasicFunctions basicFunctions = new BasicFunctions(HomeAssistant.this);

        if(basicFunctions.isConnectingToInternet()) {

            Timer t = new Timer();

            t.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

                    performUpdate();

                }

            }, 0, 5000);

        }

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(HomeAssistant.this, HomeAssistant.class);
                            startActivity(intent);

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeAssistant.this);
            builder.setMessage("Network Failure : Please check your Internet Connection !")
                    .setPositiveButton("Try Again", dialogClickListener).show();
        }

        DEVICE_SWITCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (USER_VAL == 0) {

                    USER_VAL = 1;

                    DEVICE_SWITCH.setBackgroundResource(R.drawable.ha_switch_on);
                    DEVICE_SWITCH_TEXT.setText(getString(R.string.ha_on));
                    DEVICE_SWITCH_TEXT.setTextColor(Color.GREEN);

                    PostThingspeak postThingspeak = new PostThingspeak();
                    postThingspeak.execute();

                } else if (USER_VAL == 1) {

                    USER_VAL = 0;

                    DEVICE_SWITCH.setBackgroundResource(R.drawable.ha_switch_off);
                    DEVICE_SWITCH_TEXT.setText(getString(R.string.ha_off));
                    DEVICE_SWITCH_TEXT.setTextColor(Color.RED);

                    PostThingspeak postThingspeak = new PostThingspeak();
                    postThingspeak.execute();

                }

                else {

                    USER_VAL = 1;

                    DEVICE_SWITCH.setBackgroundResource(R.drawable.ha_switch_on);
                    DEVICE_SWITCH_TEXT.setText(getString(R.string.ha_on));
                    DEVICE_SWITCH_TEXT.setTextColor(Color.GREEN);

                    PostThingspeak postThingspeak = new PostThingspeak();
                    postThingspeak.execute();

                }
            }
        });


    }


    private void performUpdate() {

        GetThingSpeak getThingSpeak = new GetThingSpeak();
        getThingSpeak.execute();

    }


    @SuppressLint("StaticFieldLeak")
    class GetThingSpeak extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {}

        protected String doInBackground(Void... urls) {

            try {

                URL url = new URL("https://api.thingspeak.com/channels/439984/feeds.json?results=" + NO_OF_VAL + "&api_key=HYAUO22HJCV6E8OG");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    bufferedReader.close();
                    return stringBuilder.toString();

                }

                finally{
                    urlConnection.disconnect();
                }

            }

            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String response) {

            if(response == null) {
                Toast.makeText(HomeAssistant.this, "Fetch Error !", Toast.LENGTH_SHORT).show();
                return;
            }


            try {

                JSONObject jsonObject = new JSONObject(response);
                JSONArray feeds = jsonObject.getJSONArray("feeds");
                JSONObject feedsJSONObject = feeds.getJSONObject(0);

                if(!feedsJSONObject.isNull("field1")) {

                    NO_OF_VAL = 1;

                    TEMPERATURE.setText(String.valueOf(feedsJSONObject.getDouble("field3")) + " °C");

                    if (feedsJSONObject.getDouble("field1") == 1) {

                        DEVICE_STATUS.setBackgroundResource(R.drawable.ha_status_on);
                        DEVICE_STATUS_TEXT.setText("ON");
                        DEVICE_STATUS_TEXT.setTextColor(Color.GREEN);

                    }

                    else if (feedsJSONObject.getDouble("field1") == 0) {

                        DEVICE_STATUS.setBackgroundResource(R.drawable.ha_status_off);
                        DEVICE_STATUS_TEXT.setText("OFF");
                        DEVICE_STATUS_TEXT.setTextColor(Color.RED);

                    }

                    progressDialog.dismiss();

                }

                else {

                    NO_OF_VAL++;
                    System.out.println("*******" + NO_OF_VAL);
                    GetThingSpeak getThingSpeak = new GetThingSpeak();
                    getThingSpeak.execute();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    class PostThingspeak extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {}

        protected String doInBackground(Void... urls) {

            try {

                URL url = new URL("https://api.thingspeak.com/update?api_key=ZODS3DRYUHT2EOZ4&field2=" + USER_VAL);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();

                }

                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {

            if(response != null) {

                if(USER_VAL == 0)
                    Toast.makeText(HomeAssistant.this, "Value OFF has been posted !", Toast.LENGTH_LONG).show();

                else if(USER_VAL == 1)
                    Toast.makeText(HomeAssistant.this, "Value ON has been posted !", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(HomeAssistant.this, "Post Error !", Toast.LENGTH_LONG).show();
        }
    }

}