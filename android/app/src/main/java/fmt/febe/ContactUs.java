package fmt.febe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;


public class ContactUs extends AppCompatActivity {


    LinearLayout CU_SEND_MESSAGE, CU_RATE_US, CU_FIND_US;

    String cu_email, cu_name, cu_subject, cu_message;

    EditText CU_NAME, CU_EMAIL, CU_SUBJECT, CU_MESSAGE;

    ImageButton MENU_BUTTON;

    private BasicFunctions basicFunctions;

    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        menu = new Menu(ContactUs.this);

        basicFunctions = new BasicFunctions(ContactUs.this);

        CU_NAME = findViewById(R.id.cu_full_name);
        CU_EMAIL = findViewById(R.id.cu_email_id);
        CU_SUBJECT = findViewById(R.id.cu_subject);
        CU_MESSAGE = findViewById(R.id.cu_message);
        CU_SEND_MESSAGE = findViewById(R.id.cu_send_message);
        CU_RATE_US = findViewById(R.id.cu_rate_us);
        CU_FIND_US = findViewById(R.id.cu_find_us);
        MENU_BUTTON = findViewById(R.id.cu_menu);

/*
        AdView mAdView = (AdView) findViewById(R.id.cu_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
*/

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.LeftDrawer.toggleLeftDrawer();

            }
        });


        CU_SEND_MESSAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cu_name = CU_NAME.getText().toString();

                cu_email = CU_EMAIL.getText().toString();

                cu_subject = CU_SUBJECT.getText().toString();

                cu_message = CU_MESSAGE.getText().toString();

                if (TextUtils.isEmpty(cu_name)) {
                    CU_NAME.setError("Type in your Name !");

                } else if (TextUtils.isEmpty(cu_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(cu_email).matches()) {
                    CU_EMAIL.setError("Invalid Email-ID !");

                } else if (TextUtils.isEmpty(cu_subject)) {
                    CU_SUBJECT.setError("Type in the Subject !");

                } else if (TextUtils.isEmpty(cu_message)) {
                    CU_MESSAGE.setError("Type in the Message !");

                } else {

                    if(basicFunctions.isConnectingToInternet())
                       basicFunctions.sendEmail("ContactUs", cu_email, cu_name, cu_subject, cu_message);

                    else {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){

                                    case DialogInterface.BUTTON_POSITIVE:

                                        if(basicFunctions.isConnectingToInternet())
                                            basicFunctions.sendEmail("ContactUs", cu_name, cu_email, cu_subject, cu_message);

                                        else
                                            Toast.makeText(ContactUs.this,
                                                    "No Internet Connection. Try again later !",
                                                    Toast.LENGTH_LONG).show();

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        Toast.makeText(ContactUs.this,
                                                "No Internet Connection. Try again later !",
                                                Toast.LENGTH_LONG).show();

                                        dialog.dismiss();

                                        break;

                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(ContactUs.this);
                        builder.setMessage("No Internet Connection. Try again ?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }

                }
            }
        });


        CU_RATE_US.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){

                String appPackageName = getPackageName();

                try {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));

                } catch (android.content.ActivityNotFoundException anfe) {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

                }

            }

        });


        CU_FIND_US.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){

                String appDeveloperName = "Febin+M+Thomas";

                try {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + appDeveloperName)));

                } catch (android.content.ActivityNotFoundException anfe) {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + appDeveloperName)));

                }

            }

        });

    }

}