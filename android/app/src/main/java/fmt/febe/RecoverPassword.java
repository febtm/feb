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

import fmt.febe.helper.BasicFunctions;
import fmt.febe.helper.LSRMenu;


public class RecoverPassword extends AppCompatActivity {


    EditText ET_EMAIL;
    Button B_SEND_EMAIL;

    private String to_email;

    String subject;

    private BasicFunctions basicFunctions;

    private LSRMenu lsrMenu;

    ImageButton MENU_BUTTON, RP_EMAIL_CANCEL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        basicFunctions = new BasicFunctions(this);

        lsrMenu = new LSRMenu(RecoverPassword.this);

        MENU_BUTTON = findViewById(R.id.rp_menu);

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsrMenu.LeftDrawer.toggleLeftDrawer();

            }
        });

        ET_EMAIL = findViewById(R.id.rp_email);


        RP_EMAIL_CANCEL = findViewById(R.id.rp_email_cancel);

        RP_EMAIL_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ET_EMAIL.setText("");

            }
        });

        B_SEND_EMAIL = findViewById(R.id.rp_send_email);

        B_SEND_EMAIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                to_email = ET_EMAIL.getText().toString();

                if (TextUtils.isEmpty(to_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(to_email).matches()) {
                    ET_EMAIL.setError("Invalid Email-Id !");

                } else {

                    if (basicFunctions.isConnectingToInternet()) {

                        recoverPassword();

                    } else {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                    case DialogInterface.BUTTON_POSITIVE:

                                        if (basicFunctions.isConnectingToInternet())
                                            recoverPassword();

                                        else {

                                            Toast.makeText(RecoverPassword.this,
                                                    "No Internet Connection. Try again later !",
                                                    Toast.LENGTH_LONG).show();

                                            dialog.dismiss();

                                        }

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        Toast.makeText(RecoverPassword.this,
                                                "No Internet Connection. Try again later !",
                                                Toast.LENGTH_LONG).show();

                                        dialog.dismiss();

                                        break;

                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(RecoverPassword.this);
                        builder.setMessage("No Internet Connection. Try again ?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                }
            }
        });
    }


    private void recoverPassword() {

        subject = "Did you forget your password ?";

        basicFunctions.sendEmail("RecoverPassword", "BATMAN", to_email, subject, "JOKER");

    }

}