package fmt.febe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

import fmt.febe.helper.BasicFunctions;
import fmt.febe.helper.Menu;


public class SocialMedia extends AppCompatActivity {


    LinearLayout SM_SEND_EMAIL, SM_FACEBOOK_SHARE, SM_TWITTER_SHARE, SM_SEND_EMAIL_DETAILS, SM_FB_AND_TW_SHARE_DETAILS;

    String sm_from_email, sm_to_email, sm_subject, sm_message, sm_title, sm_description;

    EditText SM_FROM_EMAIL, SM_TO_EMAIL, SM_SUBJECT, SM_MESSAGE, SM_TITLE, SM_DESCRIPTION;

    ImageButton SM_FROM_EMAIL_CANCEL, SM_TO_EMAIL_CANCEL, SM_SUBJECT_CANCEL,
            SM_MESSAGE_CANCEL, SM_TITLE_CANCEL, SM_DESCRIPTION_CANCEL;

    ImageButton MENU_BUTTON;

    Button SM_SEND, SM_SHARE;

    private BasicFunctions basicFunctions;

    private Menu menu;

    private CallbackManager fb_callbackManager;

    int FB_AND_TW = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        menu = new Menu(SocialMedia.this);

        basicFunctions = new BasicFunctions(SocialMedia.this);

        MENU_BUTTON = findViewById(R.id.sm_menu);

        fb_callbackManager = CallbackManager.Factory.create();

        SM_SEND_EMAIL = findViewById(R.id.sm_send_email);
        SM_FACEBOOK_SHARE = findViewById(R.id.sm_facebook_share);
        SM_TWITTER_SHARE = findViewById(R.id.sm_twitter_share);
        SM_SEND_EMAIL_DETAILS = findViewById(R.id.sm_send_email_details);
        SM_FB_AND_TW_SHARE_DETAILS = findViewById(R.id.sm_facebook_share_details);

        SM_FROM_EMAIL = findViewById(R.id.sm_from_email);
        SM_TO_EMAIL = findViewById(R.id.sm_to_email);
        SM_SUBJECT = findViewById(R.id.sm_subject);
        SM_MESSAGE = findViewById(R.id.sm_message);
        SM_TITLE = findViewById(R.id.sm_title);
        SM_DESCRIPTION = findViewById(R.id.sm_description);

        SM_FROM_EMAIL_CANCEL = findViewById(R.id.sm_from_email_cancel);
        SM_TO_EMAIL_CANCEL = findViewById(R.id.sm_to_email_cancel);
        SM_SUBJECT_CANCEL = findViewById(R.id.sm_subject_cancel);
        SM_MESSAGE_CANCEL = findViewById(R.id.sm_message_cancel);
        SM_TITLE_CANCEL = findViewById(R.id.sm_title_cancel);
        SM_DESCRIPTION_CANCEL = findViewById(R.id.sm_description_cancel);

        SM_SEND = findViewById(R.id.sm_send);
        SM_SHARE = findViewById(R.id.sm_share);


        SM_FROM_EMAIL_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SM_FROM_EMAIL.setText("");

            }
        });

        SM_TO_EMAIL_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SM_TO_EMAIL.setText("");

            }
        });

        SM_SUBJECT_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SM_SUBJECT.setText("");

            }
        });

        SM_MESSAGE_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SM_MESSAGE.setText("");

            }
        });

        SM_TITLE_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SM_TITLE.setText("");

            }
        });

        SM_DESCRIPTION_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SM_DESCRIPTION.setText("");

            }
        });


        SM_SEND_EMAIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(SM_SEND_EMAIL_DETAILS.getVisibility() == View.VISIBLE) {

                    SM_FACEBOOK_SHARE.setVisibility(View.VISIBLE);
                    SM_TWITTER_SHARE.setVisibility(View.VISIBLE);
                    SM_SEND_EMAIL_DETAILS.setVisibility(View.GONE);

                }

                else {

                    SM_FACEBOOK_SHARE.setVisibility(View.GONE);
                    SM_TWITTER_SHARE.setVisibility(View.GONE);
                    SM_SEND_EMAIL_DETAILS.setVisibility(View.VISIBLE);

                }
            }
        });


        SM_FACEBOOK_SHARE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FB_AND_TW = 0;

                if(SM_FB_AND_TW_SHARE_DETAILS.getVisibility() == View.VISIBLE) {

                    SM_SEND_EMAIL.setVisibility(View.VISIBLE);
                    SM_TWITTER_SHARE.setVisibility(View.VISIBLE);
                    SM_FB_AND_TW_SHARE_DETAILS.setVisibility(View.GONE);

                }

                else {

                    SM_SEND_EMAIL.setVisibility(View.GONE);
                    SM_TWITTER_SHARE.setVisibility(View.GONE);
                    SM_FB_AND_TW_SHARE_DETAILS.setVisibility(View.VISIBLE);

                }
            }
        });


        SM_TWITTER_SHARE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FB_AND_TW = 1;

                if(SM_FB_AND_TW_SHARE_DETAILS.getVisibility() == View.VISIBLE) {

                    SM_SEND_EMAIL.setVisibility(View.VISIBLE);
                    SM_FACEBOOK_SHARE.setVisibility(View.VISIBLE);
                    SM_FB_AND_TW_SHARE_DETAILS.setVisibility(View.GONE);

                }

                else {

                    SM_SEND_EMAIL.setVisibility(View.GONE);
                    SM_FACEBOOK_SHARE.setVisibility(View.GONE);
                    SM_FB_AND_TW_SHARE_DETAILS.setVisibility(View.VISIBLE);

                }
            }
        });


        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.LeftDrawer.toggleLeftDrawer();

            }
        });


        SM_SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sm_from_email = SM_FROM_EMAIL.getText().toString();

                sm_to_email = SM_TO_EMAIL.getText().toString();

                sm_subject = SM_SUBJECT.getText().toString();

                sm_message = SM_MESSAGE.getText().toString();

                if (TextUtils.isEmpty(sm_from_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(sm_from_email).matches()) {
                    SM_FROM_EMAIL.setError("Invalid Email-Id !");

                } else if (TextUtils.isEmpty(sm_to_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(sm_to_email).matches()) {
                    SM_TO_EMAIL.setError("Invalid Email-Id !");

                } else if (TextUtils.isEmpty(sm_subject)) {
                    SM_SUBJECT.setError("Type in the Subject !");

                } else if (TextUtils.isEmpty(sm_message)) {
                    SM_MESSAGE.setError("Type in the Message !");

                } else {

                    if(basicFunctions.isConnectingToInternet())
                       basicFunctions.sendEmail("SendEmail", sm_from_email, sm_to_email, sm_subject, sm_message);

                    else {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){

                                    case DialogInterface.BUTTON_POSITIVE:

                                        if(basicFunctions.isConnectingToInternet())
                                            basicFunctions.sendEmail("SendEmail", sm_from_email, sm_to_email, sm_subject, sm_message);

                                        else
                                            Toast.makeText(SocialMedia.this,
                                                    "No Internet Connection. Try again later !",
                                                    Toast.LENGTH_LONG).show();

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        Toast.makeText(SocialMedia.this,
                                                "No Internet Connection. Try again later !",
                                                Toast.LENGTH_LONG).show();

                                        dialog.dismiss();

                                        break;

                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(SocialMedia.this);
                        builder.setMessage("No Internet Connection. Try again ?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }

                }
            }
        });


        SM_SHARE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sm_title = SM_TITLE.getText().toString();

                sm_description = SM_DESCRIPTION.getText().toString();

                if (TextUtils.isEmpty(sm_title)) {
                    SM_TITLE.setError("Type in the Title !");

                } else if (TextUtils.isEmpty(sm_description)) {
                    SM_DESCRIPTION.setError("Type in the Description !");

                } else {

                    String app_id;

                    if(FB_AND_TW == 0){

                        app_id = "com.facebook.katana";

                        if(basicFunctions.isAppInstalled(app_id))
                            shareOnFacebook(sm_title, sm_description);

                        else {

                            try {

                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_id)));

                            } catch (android.content.ActivityNotFoundException anfe) {

                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_id)));

                            }

                            Toast.makeText(getApplicationContext(), "Kindly install the Facebook App to enable Facebook Post Sharing !", Toast.LENGTH_LONG).show();

                        }

                    }

                    else {

                        shareOnTwitter(sm_title, sm_description);

                    }

                }

            }
        });

    }


    private void shareOnFacebook(String title, String description) {

        ShareDialog shareDialog = new ShareDialog(SocialMedia.this);

        if (ShareDialog.canShow(ShareLinkContent.class)) {

            shareDialog.registerCallback(fb_callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {

                    SM_SEND_EMAIL.setVisibility(View.VISIBLE);
                    SM_TWITTER_SHARE.setVisibility(View.VISIBLE);
                    SM_FB_AND_TW_SHARE_DETAILS.setVisibility(View.GONE);

                    SM_TITLE.setText("");
                    SM_DESCRIPTION.setText("");

                    Toast.makeText(SocialMedia.this, "Facebook Share Successful !", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(SocialMedia.this, "Facebook Share Cancelled !", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(SocialMedia.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    exception.printStackTrace();
                }
            });

            ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                    .putString("og:type", "news.article")
                    .putString("og:title", title)
                    .putString("og:description", description).build();

            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                    .setActionType("news.publishes").putObject("article", object)
                    .build();

            ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                    .setPreviewPropertyName("article")
                    .setAction(action)
                    .build();

            shareDialog.show(content);

        }

    }


    private void shareOnTwitter(String title, String description){

        String app_id = "com.twitter.android";

        if(basicFunctions.isAppInstalled(app_id)) {

            TweetComposer.Builder builder = new TweetComposer.Builder(this)
                    .text("Feb - " + title + " : " + description);
            builder.show();

        } else {

            try {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_id)));

            } catch (android.content.ActivityNotFoundException anfe) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_id)));

            }

            Toast.makeText(getApplicationContext(),"Kindly install the Twitter App to enable Twitter Post Sharing !",Toast.LENGTH_LONG).show();

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        fb_callbackManager.onActivityResult(requestCode, resultCode, data);

    }

}