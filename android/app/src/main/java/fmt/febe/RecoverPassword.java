package fmt.febe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RecoverPassword extends AppCompatActivity {


    EditText ET_EMAIL;
    Button B_RESET;

    private String to_email;

    String subject;

    private BasicFunctions basicFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        basicFunctions = new BasicFunctions(this);

        ET_EMAIL = findViewById(R.id.fp_email);
        B_RESET = findViewById(R.id.fp_reset);

        B_RESET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                to_email = ET_EMAIL.getText().toString();

                if (TextUtils.isEmpty(to_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(to_email).matches()) {
                    ET_EMAIL.setError("Invalid Email-Id !");

                } else {

                    if(basicFunctions.isConnectingToInternet()) {

                        subject = "Did you forget your password ?";

                        basicFunctions.sendEmail("RecoverPassword", "BATMAN", to_email, subject, "JOKER");

                    } else {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:

                                        Intent intent = new Intent(RecoverPassword.this, RecoverPassword.class);
                                        startActivity(intent);

                                        break;

                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(RecoverPassword.this);
                        builder.setMessage("Network Failure : Please check your Internet Connection !")
                                .setPositiveButton("Try Again ... ", dialogClickListener).show();

                    }
                }
            }
        });
    }


}