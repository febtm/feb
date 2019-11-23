package fmt.febe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import fmt.febe.helper.BasicFunctions;
import fmt.febe.helper.LSRMenu;
import fmt.febe.helper.Menu;


public class ContactUs extends AppCompatActivity {


    String cu_email, cu_name, cu_subject, cu_message;

    EditText CU_NAME, CU_EMAIL, CU_SUBJECT, CU_MESSAGE;

    Button CU_SEND_MESSAGE;

    ImageButton MENU_BUTTON, CU_NAME_CANCEL, CU_EMAIL_CANCEL, CU_SUBJECT_CANCEL, CU_MESSAGE_CANCEL;

    private BasicFunctions basicFunctions;

    private Menu menu;

    private LSRMenu lsrMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        menu = new Menu(ContactUs.this);

        lsrMenu = new LSRMenu(ContactUs.this);

        basicFunctions = new BasicFunctions(ContactUs.this);

        AdView mAdView = findViewById(R.id.cu_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        CU_NAME = findViewById(R.id.cu_name);
        CU_EMAIL = findViewById(R.id.cu_email);
        CU_SUBJECT = findViewById(R.id.cu_subject);
        CU_MESSAGE = findViewById(R.id.cu_message);
        CU_SEND_MESSAGE = findViewById(R.id.cu_send_message);

        MENU_BUTTON = findViewById(R.id.cu_menu);

        if (basicFunctions.isLoggedIn()) {

            MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu.LeftDrawer.toggleLeftDrawer();

                }
            });

        } else {

            MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lsrMenu.LeftDrawer.toggleLeftDrawer();

                }
            });
        }

        CU_NAME_CANCEL = findViewById(R.id.cu_name_cancel);

        CU_NAME_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CU_NAME.setText("");

            }
        });

        CU_EMAIL_CANCEL = findViewById(R.id.cu_email_cancel);

        CU_EMAIL_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CU_EMAIL.setText("");

            }
        });

        CU_SUBJECT_CANCEL = findViewById(R.id.cu_subject_cancel);

        CU_SUBJECT_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CU_SUBJECT.setText("");

            }
        });

        CU_MESSAGE_CANCEL = findViewById(R.id.cu_message_cancel);

        CU_MESSAGE_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CU_MESSAGE.setText("");

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

                    if (basicFunctions.isConnectingToInternet())
                        basicFunctions.sendEmail("ContactUs", cu_email, cu_name, cu_subject, cu_message);

                    else {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                    case DialogInterface.BUTTON_POSITIVE:

                                        if (basicFunctions.isConnectingToInternet())
                                            basicFunctions.sendEmail("ContactUs", cu_email, cu_name, cu_subject, cu_message);

                                        else {

                                            Toast.makeText(ContactUs.this,
                                                    "No Internet Connection. Try again later !",
                                                    Toast.LENGTH_LONG).show();

                                            dialog.dismiss();

                                        }

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

    }

}