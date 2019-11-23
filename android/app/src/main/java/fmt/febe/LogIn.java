package fmt.febe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

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

import fmt.febe.helper.BasicFunctions;
import fmt.febe.helper.LSRMenu;
import io.fabric.sdk.android.Fabric;


public class LogIn extends AppCompatActivity {


    Button LI_LOGIN;
    EditText ET_EMAIL, ET_PASSWORD;
    CheckBox CB_SHOW;

    String li_email, li_password, li_device_token;

    private BasicFunctions basicFunctions;

    private LSRMenu lsrMenu;

    ImageButton MENU_BUTTON, LI_EMAIL_CANCEL, LI_PASSWORD_CANCEL;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        basicFunctions = new BasicFunctions(this);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        basicFunctions.storeFCMNotDeviceToken(refreshedToken);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6196885651315287~7727581519");

        Fabric.with(this, new Crashlytics());

        AppEventsLogger.activateApp(getApplication());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(basicFunctions.TWITTER_KEY, basicFunctions.TWITTER_SECRET);
        TwitterConfig.Builder builder = new TwitterConfig.Builder(this);
        builder.twitterAuthConfig(authConfig);
        Twitter.initialize(builder.build());

        if(basicFunctions.isLoggedIn()) {

            Intent intent = new Intent(LogIn.this, HomePage.class);
            startActivity(intent);
            LogIn.this.finish();

        }

        lsrMenu = new LSRMenu(LogIn.this);

        MENU_BUTTON = findViewById(R.id.li_menu);

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsrMenu.LeftDrawer.toggleLeftDrawer();

            }
        });


        AdView mAdView = findViewById(R.id.li_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ET_EMAIL = findViewById(R.id.li_email);
        ET_PASSWORD = findViewById(R.id.li_password);
        LI_LOGIN = findViewById(R.id.li_login);
        CB_SHOW = findViewById(R.id.li_show_password);

        ET_EMAIL.setText(basicFunctions.getUser_email());
        ET_PASSWORD.setText(basicFunctions.getUser_password());


        LI_EMAIL_CANCEL = findViewById(R.id.li_email_cancel);

        LI_EMAIL_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ET_EMAIL.setText("");

            }
        });


        LI_PASSWORD_CANCEL = findViewById(R.id.li_password_cancel);

        LI_PASSWORD_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ET_PASSWORD.setText("");

            }
        });


        LI_LOGIN.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View view) {

                         li_email = ET_EMAIL.getText().toString();
                         li_password = ET_PASSWORD.getText().toString();

                         li_device_token = basicFunctions.getFCMNotDeviceToken();

                         if (TextUtils.isEmpty(li_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(li_email).matches())
                             ET_EMAIL.setError("Invalid Email-Id !");

                         else if (TextUtils.isEmpty(li_password))
                             ET_PASSWORD.setError("Type in your Password !");

                         else if (li_device_token == null)
                             Toast.makeText(LogIn.this, "Device Registration Failed ! Please try logging in again later !", Toast.LENGTH_LONG).show();

                         else {

                             if(basicFunctions.isConnectingToInternet())
                                 logIn();

                             else {

                                 DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         switch (which){

                                             case DialogInterface.BUTTON_POSITIVE:

                                                 if(basicFunctions.isConnectingToInternet())
                                                     logIn();

                                                 else {

                                                     Toast.makeText(LogIn.this,
                                                             "No Internet Connection. Try again later !",
                                                             Toast.LENGTH_LONG).show();

                                                     dialog.dismiss();

                                                 }

                                                 break;

                                             case DialogInterface.BUTTON_NEGATIVE:

                                                 Toast.makeText(LogIn.this,
                                                         "No Internet Connection. Try again later !",
                                                         Toast.LENGTH_LONG).show();

                                                 dialog.dismiss();

                                                 break;

                                         }
                                     }
                                 };

                                 AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                                 builder.setMessage("No Internet Connection. Try again ?")
                                         .setPositiveButton("Yes", dialogClickListener)
                                         .setNegativeButton("No", dialogClickListener).show();

                             }
                         }}}

        );


        CB_SHOW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ET_PASSWORD.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    ET_PASSWORD.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }


    private void logIn() {

        String method = "login";
        LoginBackgroundTask loginBackgroundTask = new LoginBackgroundTask(LogIn.this);
        loginBackgroundTask.execute(method, li_email, li_password, li_device_token);

    }

    @SuppressLint("StaticFieldLeak")
    private class LoginBackgroundTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        Context ctx;

        LoginBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LogIn.this);
            pDialog.setMessage("Logging In ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("login")) {

                String li_email = params[1];
                String li_password = params[2];
                String li_device_token = params[3];

                try {

                    URL url = new URL(BasicFunctions.LOGIN);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("li_email", "UTF-8") + "=" + URLEncoder.encode(li_email, "UTF-8")
                            + "&" + URLEncoder.encode("li_password", "UTF-8") + "=" + URLEncoder.encode(li_password, "UTF-8")
                            + "&" + URLEncoder.encode("li_device_token", "UTF-8") + "=" + URLEncoder.encode(li_device_token, "UTF-8");

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
                    IS.close();
                    httpURLConnection.disconnect();
                    return response.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
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

                case "Login Successful !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

                    basicFunctions.getCurrentUserData(li_email);

                    Intent intent = new Intent (LogIn.this, HomePage.class);
                    startActivity(intent);
                    LogIn.this.finish();

                    pDialog.dismiss();

                    break;

                case "Email-Id and Password does not match !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                default:

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

            }

        }

    }

}