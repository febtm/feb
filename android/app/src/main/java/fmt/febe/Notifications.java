package fmt.febe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.List;

import fmt.febe.model.Notification;


public class Notifications extends AppCompatActivity {


    JSONArray NotificationArray = null;

    private List<Notification> mNotifications = new ArrayList<>();

    private RecyclerView recyclerView;

    private int load_over = 0;

    private NotificationFragmentAdapter notificationFragmentAdapter;

    private ProgressDialog pDialog;

    private BasicFunctions basicFunctions;

    ImageButton NOT_BACK;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        NotificationManager notifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notifManager != null;
        notifManager.cancelAll();

        NOT_BACK = findViewById(R.id.not_back);

        NOT_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Notifications.this, Messenger.class);
                startActivity(intent);

            }
        });

        basicFunctions = new BasicFunctions(this);

        //AdView mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);


        if(basicFunctions.isConnectingToInternet())
            getInitialNotificationData();

        else{

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(Notifications.this, Notifications.class);
                            startActivity(intent);

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Network Failure : Please check your Internet Connection !")
                    .setPositiveButton("Try Again ... ", dialogClickListener).show();

        }

    }


    private void getInitialNotificationData() {

        pDialog = ProgressDialog.show(this, "", "Fetching Notifications ... ", false, false);

        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_ALL_NOTIFICATIONS + basicFunctions.getUser_id()+"&index="+0, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_InitialNotifications(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Notifications.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    protected void showList_InitialNotifications(String response) {

        String an_id;
        String an_chat_id ;
        String an_post_userread;
        String an_notify_userid ;
        String an_notify_username;
        String an_timestamp;

        try {

            JSONObject jsonObj = new JSONObject(response);
            NotificationArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            for (int i = 0; i < NotificationArray.length(); i++) {

                JSONObject jsonObject = NotificationArray.getJSONObject(i);

                an_id = jsonObject.getString(basicFunctions.KEY_POST_ID);
                an_chat_id = jsonObject.getString(basicFunctions.KEY_CHAT_ROOM_ID);
                an_post_userread = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_POST_USERREAD);
                an_notify_userid = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USER_ID);
                an_notify_username = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USERNAME);
                an_timestamp = jsonObject.getString(basicFunctions.KEY_POST_TIMESTAMP);

                Notification data = new Notification();

                data.setNotDetails(an_id, an_chat_id, an_post_userread, an_notify_userid,
                        an_notify_username, an_timestamp);

                mNotifications.add(data);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(NotificationArray.length() <= 0)
            Toast.makeText(Notifications.this, "Your Notification List is empty !", Toast.LENGTH_LONG).show();

        recyclerView = findViewById(R.id.no_frag_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationFragmentAdapter = new NotificationFragmentAdapter(this);
        recyclerView.setAdapter(notificationFragmentAdapter);


        notificationFragmentAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                mNotifications.add(null);
                notificationFragmentAdapter.notifyItemInserted(mNotifications.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pDialog = ProgressDialog.show(Notifications.this, "", "Fetching Notifications ... ", false, false);

                        mNotifications.remove(mNotifications.size() - 1);
                        notificationFragmentAdapter.notifyItemRemoved(mNotifications.size());

                        int index = mNotifications.size();

                        getMoreNotificationData(index);

                        notificationFragmentAdapter.notifyDataSetChanged();
                        notificationFragmentAdapter.setLoaded();

                    }
                }, 1000);
            }
        });

        pDialog.dismiss();

    }


    private void getMoreNotificationData(int index) {

        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_ALL_NOTIFICATIONS + basicFunctions.getUser_id()+"&index="+index, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_MoreNotifications(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Notifications.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    protected void showList_MoreNotifications(String response) {

        String an_id;
        String an_chat_id;
        String an_post_userread;
        String an_notify_userid;
        String an_notify_username;
        String an_timestamp;

        try {
            JSONObject jsonObj = new JSONObject(response);
            NotificationArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            if(NotificationArray.length() == 0) {

                Toast.makeText(this, "No more Notifications to display !", Toast.LENGTH_LONG).show();
                load_over = 1;
            }

            for (int i = 0; i < NotificationArray.length(); i++) {

                JSONObject jsonObject = NotificationArray.getJSONObject(i);

                an_id = jsonObject.getString(basicFunctions.KEY_POST_ID);
                an_chat_id = jsonObject.getString(basicFunctions.KEY_CHAT_ROOM_ID);
                an_post_userread = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_POST_USERREAD);
                an_notify_userid = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USER_ID);
                an_notify_username = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USERNAME);
                an_timestamp = jsonObject.getString(basicFunctions.KEY_POST_TIMESTAMP);

                Notification data = new Notification();

                data.setNotDetails(an_id, an_chat_id, an_post_userread, an_notify_userid,
                        an_notify_username, an_timestamp);

                mNotifications.add(data);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        notificationFragmentAdapter.notifyDataSetChanged();

        pDialog.dismiss();

    }


    private class NotificationFragmentAdapter extends RecyclerView.Adapter<ViewHolder> {

        private LayoutInflater mLayoutInflater;
        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;


        private NotificationFragmentAdapter(Context context) {

            mLayoutInflater = LayoutInflater.from(context);

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && load_over == 0) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.activity_messenger_list_row, viewGroup, false));
        }


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            final Notification data = mNotifications.get(position);

            if(data != null) {

                viewHolder.setData(data.getChat_id(), data.getPost_userread(), data.getNotify_username(),
                        data.getTimestamp(), position);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        onNotificationFragmentItemSelected(data.getId(), data.getChat_id(),
                                data.getNotify_userid(), data.getNotify_username());

                    }
                });

            }

        }

        @Override
        public int getItemCount() {

            return mNotifications.size();

        }

        private void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        private void setLoaded() {
            isLoading = false;
        }

    }


    private interface OnLoadMoreListener {
        void onLoadMore();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextTextView, mDateTextView;
        private ImageView mUserreadImageView;
        private ImageButton delete;


        private ViewHolder(View itemView) {
            super(itemView);

            mTextTextView = itemView.findViewById(R.id.mes_i_username);
            mUserreadImageView = itemView.findViewById(R.id.mes_i_userread);
            mDateTextView = itemView.findViewById(R.id.mes_i_timestamp);
            delete = itemView.findViewById(R.id.mes_i_deletebutton);

        }

        private void setData(String chat_id, String post_userread, String notify_username,
                             String timestamp, final int position) {

            if(post_userread.equals("1"))
                mUserreadImageView.setVisibility(View.VISIBLE);

            else
                mUserreadImageView.setVisibility(View.GONE);

            String temp_timestamp = BasicFunctions.getTimeStamp(timestamp);

            mDateTextView.setText(temp_timestamp);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Notification data = mNotifications.get(position);

                    String method = "deletenotification";
                    DeleteNotificationBackgroundTask nb = new DeleteNotificationBackgroundTask(Notifications.this);
                    nb.execute(method, data.getId(), data.getChat_id(),
                            basicFunctions.getUser_id(), data.getNotify_userid());

                    data.deleteNot();

                    mNotifications.remove(position);

                    notificationFragmentAdapter.notifyDataSetChanged();

                }
            });

            String text;

            switch (chat_id){

                case "55555555":

                    text = notify_username + " has sent you a Chat request !";
                    mTextTextView.setText(text);

                    break;


                case "55555551":

                    text = notify_username + " has accepted your Chat request !";
                    mTextTextView.setText(text);

                    break;

                case "55555552":

                    text = notify_username + " has rejected your Chat request !";
                    mTextTextView.setText(text);

                    break;

                default:

                    text = notify_username + " has sent you a Message !";
                    mTextTextView.setText(text);

                    break;

            }

        }

    }


    private void onNotificationFragmentItemSelected(String id, String chat_id,
                                                    String notify_user_id, String notify_username) {

        String method = "readnotification";

        basicFunctions.performTask(method, id, notify_user_id);

        switch (chat_id){

            case "55555555":

                dbchat("request", basicFunctions.getUser_id(), notify_username, notify_user_id);
                break;


            case "55555551":

                dbchat("chat", basicFunctions.getUser_id(), notify_username, notify_user_id);
                break;


            case "55555552":

                Toast.makeText(this, notify_username+" has rejected Your Chat request !", Toast.LENGTH_LONG).show();
                break;


            default:

                if(basicFunctions.isConnectingToInternet())
                    login(chat_id, notify_username, notify_user_id);

                else{

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:

                                    Intent intent = new Intent(Notifications.this, HomePage.class);
                                    startActivity(intent);
                                    startActivity(intent);

                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Network Failure : Please check Your Internet Connection !")
                            .setPositiveButton("Try Again ... ", dialogClickListener).show();

                }

                break;
        }

    }


    private void dbchat(String method, final String current_user_id, final String username, final String user_id) {

        if(basicFunctions.isConnectingToInternet())

            if(method.equals("request")) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                ChatBackgroundTask backgroundTask = new ChatBackgroundTask(Notifications.this);

                                backgroundTask.execute("chat_accept", current_user_id, username, user_id);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                ChatBackgroundTask backgroundTask_2 = new ChatBackgroundTask(Notifications.this);

                                backgroundTask_2.execute("chat_reject", current_user_id, username, user_id);

                                break;
                        }
                    }


                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Notifications.this);
                builder.setMessage("Would you like to accept this User's Chat request ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }

            else if(method.equals("chat")){

                ChatBackgroundTask backgroundTask = new ChatBackgroundTask(Notifications.this);

                backgroundTask.execute("chat_accept", current_user_id, username, user_id);
            }

            else{

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                Intent intent = new Intent(Notifications.this, HomePage.class);
                                startActivity(intent);

                                break;

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Network Failure : Please check your Internet Connection !")
                        .setPositiveButton("Try Again ... ", dialogClickListener).show();

            }

    }


    @SuppressLint("StaticFieldLeak")
    private class ChatBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;
        String data;

        private String method, current_user_id, username, user_id;

        ChatBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            method = params[0];
            current_user_id = params[1];
            username = params[2];
            user_id = params[3];

            try {

                URL url = new URL(BasicFunctions.ADD_CHAT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));


                data = URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode(method, "UTF-8")
                        + "&" + URLEncoder.encode("current_user_id", "UTF-8") + "=" + URLEncoder.encode(current_user_id, "UTF-8")
                        + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                httpURLConnection.disconnect();
                IS.close();

                return response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            if(result.equals("The Chat request has been rejected !"))
                Toast.makeText(Notifications.this, result,  Toast.LENGTH_LONG).show();

            else if(result.equals("The Chat request has been accepted !") && method.equals("chat_accept")) {

                Toast.makeText(Notifications.this, result, Toast.LENGTH_LONG).show();
                login(result, username, user_id);

            }

            else if(method.equals("chat_accept"))
                login(result, username, user_id);

        }
    }


    private void login(final String chat_id, final String username, final String user_id) {

        Intent intent = new Intent(Notifications.this, MessengerItem.class);
        intent.putExtra("chat_room_id", chat_id);
        intent.putExtra("userid", user_id);
        intent.putExtra("username", username);

        startActivity(intent);

    }


    @SuppressLint("StaticFieldLeak")
    private class DeleteNotificationBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;

        private ProgressDialog pDialog;

        DeleteNotificationBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Notifications.this);
            pDialog.setMessage("Removing Notification ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("deletenotification")) {

                String id = params[1];
                String chat_room_id = params[2];
                String user_id = params[3];
                String notify_user_id = params[4];

                try {

                    URL url = new URL(BasicFunctions.DELETE_NOTIFICATION);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));


                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                            + "&" + URLEncoder.encode("chat_room_id", "UTF-8") + "=" + URLEncoder.encode(chat_room_id, "UTF-8")
                            + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                            + "&" + URLEncoder.encode("notify_user_id", "UTF-8") + "=" + URLEncoder.encode(notify_user_id, "UTF-8");


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
                    httpURLConnection.disconnect();
                    IS.close();

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

            if(result.equals("Notification removed from Notification List !"))
                pDialog.dismiss();

        }
    }
}