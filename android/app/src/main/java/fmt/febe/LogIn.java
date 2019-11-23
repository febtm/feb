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
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import com.google.firebase.iid.FirebaseInstanceId;

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

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class Login extends AppCompatActivity {


    Button L_LOGIN;
    Button L_SIGNIN;
    Button L_FORGOT_PASSWORD;
    EditText ET_EMAIL;
    EditText ET_PASSWORD;
    CheckBox CB_SHOW;

    String l_email,l_password, l_device_token;

    private BasicFunctions basicFunctions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        basicFunctions = new BasicFunctions(this);

        if(basicFunctions.isLoggedIn()) {

            Intent intent = new Intent(Login.this, HomePage.class);
            startActivity(intent);

        }

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        basicFunctions.storeFCMNotDeviceToken(refreshedToken);

        /*
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        */

        CB_SHOW = findViewById(R.id.l_show_password);

        ET_EMAIL = findViewById(R.id.l_email);
        ET_PASSWORD = findViewById(R.id.l_password);
        L_SIGNIN = findViewById(R.id.l_signin);
        L_LOGIN = findViewById(R.id.l_login);
        L_FORGOT_PASSWORD = findViewById(R.id.l_recoverpassword);

        ET_EMAIL.setText(basicFunctions.getUser_email());
        ET_PASSWORD.setText(basicFunctions.getUser_password());

        L_FORGOT_PASSWORD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent myIntent = new Intent(view.getContext(), RecoverPassword.class);
                startActivityForResult(myIntent, 0);

            }
        });


        L_SIGNIN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent myIntent = new Intent(view.getContext(), SignIn.class);
                startActivityForResult(myIntent, 0);

            }
        });


        L_LOGIN.setOnClickListener(new View.OnClickListener() {
                                       public void onClick(View view) {

                         l_email = ET_EMAIL.getText().toString();
                         l_password = ET_PASSWORD.getText().toString();

                         l_device_token = basicFunctions.getFCMNotDeviceToken();

                         if (TextUtils.isEmpty(l_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(l_email).matches())
                             ET_EMAIL.setError("Invalid Email-Id !");

                         else if (TextUtils.isEmpty(l_password))
                             ET_PASSWORD.setError("Type in your Password !");

                         else if (l_device_token == null)
                             Toast.makeText(Login.this, "Device Registration Failed ! Please try logging in again later !", Toast.LENGTH_LONG).show();

                         else {

                             if(basicFunctions.isConnectingToInternet()) {

                                 String method = "login";
                                 LoginBackgroundTask loginBackgroundTask = new LoginBackgroundTask(Login.this);
                                 loginBackgroundTask.execute(method, l_email, l_password, l_device_token);
                             }

                             else {

                                 DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         switch (which){
                                             case DialogInterface.BUTTON_POSITIVE:

                                                 Intent intent = new Intent(Login.this, Login.class);
                                                 startActivity(intent);

                                                 break;

                                         }
                                     }
                                 };

                                 AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                 builder.setMessage("Network Failure : Please check your Internet Connection !")
                                         .setPositiveButton("Try Again ... ", dialogClickListener).show();

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
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Logging In ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {


            String method = params[0];


            if (method.equals("login")) {

                String login_email = params[1];
                String login_password = params[2];
                String login_device_token = params[3];

                try {

                    URL url = new URL(BasicFunctions.LOGIN);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));


                    String data = URLEncoder.encode("login_email", "UTF-8") + "=" + URLEncoder.encode(login_email, "UTF-8")
                            + "&" + URLEncoder.encode("login_password", "UTF-8") + "=" + URLEncoder.encode(login_password, "UTF-8")
                            + "&" + URLEncoder.encode("login_device_token", "UTF-8") + "=" + URLEncoder.encode(login_device_token, "UTF-8");

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

                    basicFunctions.getCurrentUserData(l_email);

                    Intent intent = new Intent(Login.this, HomePage.class);
                    startActivity(intent);

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