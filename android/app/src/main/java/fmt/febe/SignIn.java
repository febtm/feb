package fmt.febe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import fmt.febe.helper.BasicFunctions;
import fmt.febe.helper.LSRMenu;


public class SignIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{


    EditText ET_USERNAME, ET_EMAIL, ET_PASSWORD;

    CheckBox CB_SHOW;

    ImageButton B_GOOGLE, B_FACEBOOK;

    Button B_SIGNIN, B_SIGNIN_TERMS;

    private CallbackManager callbackManager;
    private LoginButton fb_login_Button;

    private ProgressDialog pDialog;

    String si_email, si_username, si_password, si_device_token;

    GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;

    private BasicFunctions basicFunctions;

    private String TANDC_URL = "https://febtech.000webhostapp.com/FebTechT&C.pdf";

    private LSRMenu lsrMenu;

    ImageButton MENU_BUTTON, SI_USERNAME_CANCEL, SI_EMAIL_CANCEL, SI_PASSWORD_CANCEL;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        basicFunctions = new BasicFunctions(this);

        ET_USERNAME = findViewById(R.id.si_username);
        ET_EMAIL = findViewById(R.id.si_email);
        ET_PASSWORD = findViewById(R.id.si_password);

        CB_SHOW = findViewById(R.id.si_show_password);

        B_SIGNIN = findViewById(R.id.si_sign_in);
        B_SIGNIN_TERMS = findViewById(R.id.si_tandc);
        B_GOOGLE = findViewById(R.id.si_sign_in_google);
        B_FACEBOOK = findViewById(R.id.si_sign_in_fb);

        lsrMenu = new LSRMenu(SignIn.this);

        MENU_BUTTON = findViewById(R.id.si_menu);

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsrMenu.LeftDrawer.toggleLeftDrawer();

            }
        });

        SI_USERNAME_CANCEL = findViewById(R.id.si_username_cancel);

        SI_USERNAME_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ET_USERNAME.setText("");

            }
        });

        SI_EMAIL_CANCEL = findViewById(R.id.si_email_cancel);

        SI_EMAIL_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ET_EMAIL.setText("");

            }
        });

        SI_PASSWORD_CANCEL = findViewById(R.id.si_password_cancel);

        SI_PASSWORD_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ET_PASSWORD.setText("");

            }
        });

        B_SIGNIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                si_username = ET_USERNAME.getText().toString();
                si_email = ET_EMAIL.getText().toString();
                si_password = ET_PASSWORD.getText().toString();

                si_device_token = basicFunctions.getFCMNotDeviceToken();

                if (TextUtils.isEmpty(si_username))
                    ET_USERNAME.setError("Type in your Username !");

                else if (TextUtils.isEmpty(si_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(si_email).matches())
                    ET_EMAIL.setError("Invalid Email-Id !");

                else if (TextUtils.isEmpty(si_password))
                    ET_PASSWORD.setError("Type in your Password !");

                else if (si_device_token == null)
                    Toast.makeText(SignIn.this, "Device Registration Failed ! Please try Signing In again later !", Toast.LENGTH_LONG).show();

                else {

                    if(basicFunctions.isConnectingToInternet())
                        signIn();

                    else{

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){

                                    case DialogInterface.BUTTON_POSITIVE:

                                        if(basicFunctions.isConnectingToInternet())
                                            signIn();

                                        else {

                                            Toast.makeText(SignIn.this,
                                                    "No Internet Connection. Try again later !",
                                                    Toast.LENGTH_LONG).show();

                                            dialog.dismiss();

                                        }

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        Toast.makeText(SignIn.this,
                                                "No Internet Connection. Try again later !",
                                                Toast.LENGTH_LONG).show();

                                        dialog.dismiss();

                                        break;

                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                        builder.setMessage("No Internet Connection. Try again ?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }

                }
            }
        });

        B_SIGNIN_TERMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(SignIn.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(SignIn.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(SignIn.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                }

                else {

                    if(basicFunctions.isConnectingToInternet())
                        new DownloadFileFromURL().execute(TANDC_URL);

                    else {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){

                                    case DialogInterface.BUTTON_POSITIVE:

                                        if(basicFunctions.isConnectingToInternet())
                                            new DownloadFileFromURL().execute(TANDC_URL);

                                        else {

                                            Toast.makeText(SignIn.this,
                                                    "No Internet Connection. Try again later !",
                                                    Toast.LENGTH_LONG).show();

                                            dialog.dismiss();

                                        }

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        Toast.makeText(SignIn.this,
                                                "No Internet Connection. Try again later !",
                                                Toast.LENGTH_LONG).show();

                                        dialog.dismiss();

                                        break;

                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                        builder.setMessage("No Internet Connection. Try again ?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }

                }
            }
        });

        callbackManager = CallbackManager.Factory.create();

        fb_login_Button = findViewById(R.id.si_fb_button);

        fb_login_Button.setReadPermissions("public_profile", "email");

        final SignInButton signInButton = findViewById(R.id.si_google_button);

        B_FACEBOOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String app_id = "com.facebook.katana";

                if(basicFunctions.isAppInstalled(app_id)) {

                    pDialog = new ProgressDialog(SignIn.this);
                    pDialog.setMessage("Fetching Facebook Profile ... ");
                    pDialog.show();

                    fb_login_Button.performClick();

                    fb_login_Button.setPressed(true);

                    fb_login_Button.invalidate();

                    fb_login_Button.registerCallback(callbackManager, mCallBack);

                    fb_login_Button.setPressed(false);

                    fb_login_Button.invalidate();

                }

                else {

                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_id)));

                    } catch (android.content.ActivityNotFoundException anfe) {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_id)));

                    }

                    Toast.makeText(getApplicationContext(),"Kindly install the Facebook App to enable Facebook Login !", Toast.LENGTH_LONG).show();

                }

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        B_GOOGLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String app_id = "com.google.android.googlequicksearchbox";

                if(basicFunctions.isAppInstalled(app_id)){

                    pDialog = new ProgressDialog(SignIn.this);
                    pDialog.setMessage("Fetching Google Profile ... ");
                    pDialog.show();

                    signInButton.performClick();

                    signInButton.setPressed(true);

                    signInButton.invalidate();

                    googleSignIn();

                    signInButton.setPressed(false);

                    signInButton.invalidate();

                }

                else {

                    try {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_id)));

                    } catch (android.content.ActivityNotFoundException anfe) {

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_id)));

                    }

                    Toast.makeText(getApplicationContext(),"Kindly install the Google App to enable Google Login !", Toast.LENGTH_LONG).show();

                }


            }
        });

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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {

            case 0: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    new DownloadFileFromURL().execute(TANDC_URL);

                else
                    Toast.makeText(SignIn.this, "Kindly grant Storage permission to continue !", Toast.LENGTH_LONG).show();

                break;
            }

            default:
                break;

        }
    }


    private void signIn(){

        String method = "signin";
        SignInBackgroundTask signinBackgroundTask = new SignInBackgroundTask(SignIn.this);
        signinBackgroundTask.execute(method, si_username, si_email, si_password, si_device_token);

    }

    private void googleSignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account != null) {

                ET_USERNAME.setText(account.getDisplayName().replace(" ", ""));
                ET_EMAIL.setText(account.getEmail());

            }

            pDialog.dismiss();

            Toast.makeText(SignIn.this,
                    "We have accessed your details from your Google Account. Kindly edit them appropriately and Sign In !", Toast.LENGTH_LONG).show();

        } catch (ApiException e) {

            Toast.makeText(SignIn.this,
                    "Google Sign In Failed !", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,connectionResult.toString(), Toast.LENGTH_LONG).show();
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            try {

                                ET_USERNAME.setText(object.getString("name").replace(" ", ""));
                                ET_EMAIL.setText(object.getString("email"));

                                pDialog.dismiss();

                                Toast.makeText(SignIn.this,
                                        "We have accessed your details from your Facebook Account. Kindly edit them appropriately and Sign In !", Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            pDialog.dismiss();
        }

        @Override
        public void onError(FacebookException e) {
            pDialog.dismiss();
        }

    };


    @SuppressLint("StaticFieldLeak")
    private class SignInBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;

        SignInBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Signing In ... ");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("signin")) {

                String si_username= params[1];
                String si_email = params[2];
                String si_password = params[3];
                String si_device_token = params[4];

                try {
                    URL url = new URL(BasicFunctions.SIGNIN);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));


                    String data = URLEncoder.encode("si_username", "UTF-8") + "=" + URLEncoder.encode(si_username, "UTF-8")
                            + "&" + URLEncoder.encode("si_email", "UTF-8") + "=" + URLEncoder.encode(si_email, "UTF-8")
                            + "&" + URLEncoder.encode("si_password", "UTF-8") + "=" + URLEncoder.encode(si_password, "UTF-8")
                            + "&" + URLEncoder.encode("si_device_token", "UTF-8") + "=" + URLEncoder.encode(si_device_token, "UTF-8");

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

            switch (result){

                case "Sign In Successful !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

                    basicFunctions.getCurrentUserData(si_email);

                    Intent intent = new Intent(SignIn.this, HomePage.class);
                    startActivity(intent);
                    SignIn.this.finish();

                    pDialog.dismiss();

                    break;

                case "Username already exists ! Please enter a unique Username !":

                    Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();

                    break;

                case "An Account with this Email-Id already exists !":

                    Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();

                    break;

                default:

                    Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();

                    break;
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Downloading the File ... ");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                @SuppressLint("SdCardPath") File dir = new File("/sdcard/Feb");
                dir.mkdir();

                @SuppressLint("SdCardPath")
                OutputStream output = new FileOutputStream("/sdcard/Feb/" + "FebTechT&C.pdf");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {

                    total += count;

                    publishProgress("" + (int)((total*100) / fileLength));

                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

            } catch (final Exception e) {

                SignIn.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SignIn.this,"Error : "+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {

            pDialog.setProgress(Integer.parseInt(progress[0]));

        }

        @Override
        protected void onPostExecute(String file_url) {

            pDialog.dismiss();

            @SuppressLint("SdCardPath") File file = new File("/sdcard/Feb/FebTechT&C.pdf");

            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(".PDF");

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), mime);
            startActivityForResult(intent, 10);

            Toast.makeText(SignIn.this, "The File has been downloaded to /sdcard/Feb/ !", Toast.LENGTH_LONG).show();

        }
    }
}