package fmt.febe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fmt.febe.helper.BasicFunctions;
import fmt.febe.model.ChatMessage;
import fmt.febe.model.ChatUser;


public class MessengerItem extends AppCompatActivity {


    private RecyclerView recyclerView;

    private BasicFunctions basicFunctions;

    ProgressDialog pDialog;

    private ChatRoomAdapter mAdapter;

    private ArrayList<ChatMessage> chatMessageArrayList = new ArrayList<>();

    private EditText inputMessage;

    ImageButton btnSend, MES_I_CANCEL;

    String n_chat_room_id, n_userid, n_username;

    String final_message_id;

    int mInterval = 5000;

    private Handler mHandler;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                getNewChatThread();

            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    ImageButton BACK_BUTTON;

    TextView MES_I_USERNAME;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_item);

        basicFunctions = new BasicFunctions(this);

        BACK_BUTTON = findViewById(R.id.mes_i_back);

        BACK_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mHandler != null)
                    stopRepeatingTask();

                finish();

            }
        });

        MES_I_USERNAME = findViewById(R.id.mes_i_username);

        inputMessage = findViewById(R.id.mes_i_message);

        MES_I_CANCEL = findViewById(R.id.mes_i_cancel);

        MES_I_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputMessage.setText("");
            }
        });

        btnSend = findViewById(R.id.mes_i_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessage();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        recyclerView = findViewById(R.id.mes_i_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selfUserId = basicFunctions.getUser_id();

        mAdapter = new ChatRoomAdapter(this, selfUserId);

        recyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();

        n_chat_room_id = intent.getStringExtra("chat_room_id");
        n_userid = intent.getStringExtra("userid");
        n_username = intent.getStringExtra("username");

        MES_I_USERNAME.setText(n_username);

        if (n_chat_room_id == null) {

            Toast.makeText(getApplicationContext(), "Chat not found !", Toast.LENGTH_LONG).show();
            finish();

        }

        if (basicFunctions.isConnectingToInternet())
            fetchChat();

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case DialogInterface.BUTTON_POSITIVE:

                            if (basicFunctions.isConnectingToInternet())
                                fetchChat();

                            else {

                                Toast.makeText(MessengerItem.this,
                                        "No Internet Connection. Try again later !",
                                        Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            Toast.makeText(MessengerItem.this,
                                    "No Internet Connection. Try again later !",
                                    Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MessengerItem.this);
            builder.setMessage("No Internet Connection. Try again ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }


    }


    private void fetchChat() {

        String method = "readchat";

        basicFunctions.performTask(method, n_chat_room_id, n_userid);

        getInitialChatThread();


    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }


    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }


    private void getInitialChatThread() {

        pDialog = ProgressDialog.show(this, "", "Fetching Chat ... ", false, false);

        String endPoint = BasicFunctions.GET_CHAT_ROOM + n_chat_room_id + "&index=" + 0 + "&type=old";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray commentsObj = obj.getJSONArray("messages");

                    if (commentsObj.length() == 0) {

                        Toast.makeText(MessengerItem.this, "No Messages to display !", Toast.LENGTH_LONG).show();

                        final_message_id = "0";

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                mHandler = new Handler();
                                startRepeatingTask();

                            }
                        }, 0);

                        pDialog.dismiss();

                    }

                    for (int i = 0; i < commentsObj.length(); i++) {

                        JSONObject commentObj = (JSONObject) commentsObj.get(i);

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = commentObj.getJSONObject("user");
                        String userId = userObj.getString("user_id");

                        final_message_id = obj.getJSONObject("final_message_id").getString("final_message_id");

                        ChatUser chatUser = new ChatUser(userId);

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setId(commentId);
                        chatMessage.setMessage(commentText);
                        chatMessage.setCreatedAt(createdAt);
                        chatMessage.setChatUser(chatUser);

                        chatMessageArrayList.add(chatMessage);

                        if (i == (commentsObj.length() - 1)) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    mHandler = new Handler();
                                    startRepeatingTask();

                                }
                            }, mInterval);

                            pDialog.dismiss();

                        }

                        mAdapter.notifyDataSetChanged();

                        if (mAdapter.getItemCount() > 1)
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Json parse error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401)
                    Toast.makeText(getApplicationContext(), "Volley error : " + error.getMessage() + ", Code : " + networkResponse, Toast.LENGTH_LONG).show();
            }
        });

        strReq.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        basicFunctions.addToRequestQueue(strReq);

    }


    private void getNewChatThread() {

        String endPoint = BasicFunctions.GET_CHAT_ROOM + n_chat_room_id + "&index=" + final_message_id + "&type=new";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray commentsObj = obj.getJSONArray("messages");

                    for (int i = 0; i < commentsObj.length(); i++) {

                        JSONObject commentObj = (JSONObject) commentsObj.get(i);

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = commentObj.getJSONObject("user");
                        String userId = userObj.getString("user_id");

                        final_message_id = obj.getJSONObject("final_message_id").getString("final_message_id");

                        ChatUser chatUser = new ChatUser(userId);

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setId(commentId);
                        chatMessage.setMessage(commentText);
                        chatMessage.setCreatedAt(createdAt);
                        chatMessage.setChatUser(chatUser);

                        if (commentsObj.length() == 1 && !userId.equals(basicFunctions.getUser_id()))
                            playMessengerSound();

                        chatMessageArrayList.add(chatMessage);

                        mAdapter.notifyDataSetChanged();

                        if (mAdapter.getItemCount() > 1)
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Json parse error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401)
                    Toast.makeText(getApplicationContext(), "Volley error : " + error.getMessage() + ", Code : " + networkResponse, Toast.LENGTH_LONG).show();
            }
        });

        strReq.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        basicFunctions.addToRequestQueue(strReq);

    }


    private void sendMessage() throws ParseException {

        String message = this.inputMessage.getText().toString().trim();

        final String mess = message;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        String timeStamp = dateFormat.format(new Date());

        Date date = dateFormat.parse(timeStamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        message = messageEncrypt(message, hours, minutes, seconds);

        message = URLEncoder.encode(message);

        if (TextUtils.isEmpty(message)) {

            Toast.makeText(getApplicationContext(), "Enter your Message !", Toast.LENGTH_LONG).show();
            return;

        }

        String method = "notification";

        basicFunctions.performTask(method, n_chat_room_id, n_userid);

        String endPoint = BasicFunctions.SEND_MESSAGE + n_chat_room_id + "&user_id=" + basicFunctions.getUser_id() + "&message=" + message + "&timestamp=" + timeStamp;

        endPoint = endPoint.replaceAll(" ", "%20");

        this.inputMessage.setText("");

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONObject commentObj = obj.getJSONObject("message");

                    String commentId = commentObj.getString("message_id");
                    String commentText = commentObj.getString("message");
                    String createdAt = commentObj.getString("created_at");

                    JSONObject userObj = obj.getJSONObject("user");
                    String userId = userObj.getString("user_id");

                    final_message_id = obj.getJSONObject("final_message_id").getString("final_message_id");

                    ChatUser chatUser = new ChatUser(userId);

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(commentId);
                    chatMessage.setMessage(commentText);
                    chatMessage.setCreatedAt(createdAt);
                    chatMessage.setChatUser(chatUser);

                    chatMessageArrayList.add(chatMessage);

                    mAdapter.notifyDataSetChanged();

                    if (mAdapter.getItemCount() > 1)
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Json parse error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage() + ", Code: " + networkResponse, Toast.LENGTH_LONG).show();
                inputMessage.setText(mess);
            }
        });

        strReq.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        basicFunctions.addToRequestQueue(strReq);

    }


    private String messageEncrypt(String message, int hours, int minutes, int seconds) {

        int count;

        StringBuilder encrypted_message = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {

            if (Character.isLetter(message.charAt(i))) {

                if (i % 2 == 0)
                    count = hours % 26;

                else if (i % 3 == 0)
                    count = minutes % 26;

                else
                    count = seconds % 26;

                if (Character.isUpperCase(message.charAt(i)))
                    encrypted_message.append((char) (((int) message.charAt(i) + count - 65) % 26 + 65));

                else
                    encrypted_message.append((char) (((int) message.charAt(i) + count - 97) % 26 + 97));

            } else
                encrypted_message.append(message.charAt(i));

        }

        return encrypted_message.toString();

    }


    private String messageDecrypt(String message, int hours, int minutes, int seconds) {

        int count;

        StringBuilder decrypted_message = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {

            if (Character.isLetter(message.charAt(i))) {

                if (i % 2 == 0)
                    count = 26 - (hours % 26);

                else if (i % 3 == 0)
                    count = 26 - (minutes % 26);

                else
                    count = 26 - (seconds % 26);

                if (Character.isUpperCase(message.charAt(i)))
                    decrypted_message.append((char) (((int) message.charAt(i) + count - 65) % 26 + 65));

                else
                    decrypted_message.append((char) (((int) message.charAt(i) + count - 97) % 26 + 97));

            } else
                decrypted_message.append(message.charAt(i));

        }

        return decrypted_message.toString();

    }


    private void playMessengerSound() {

        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getApplicationContext().getPackageName() + "/raw/messenger_sound");
            Ringtone r = RingtoneManager.getRingtone(this.getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getOldChatThread() {

        pDialog = ProgressDialog.show(this, "", "Fetching Chat ... ", false, false);

        String endPoint = BasicFunctions.GET_CHAT_ROOM + n_chat_room_id + "&index=" + chatMessageArrayList.size() + "&type=old";

        final ArrayList<ChatMessage> chatTempMessageArrayList = new ArrayList<>();

        chatTempMessageArrayList.addAll(chatMessageArrayList);

        chatMessageArrayList.clear();


        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray commentsObj = obj.getJSONArray("messages");

                    if (commentsObj.length() == 0) {

                        Toast.makeText(MessengerItem.this, "No More Old Messages to display !", Toast.LENGTH_LONG).show();

                        chatMessageArrayList.addAll(chatTempMessageArrayList);

                        mAdapter.notifyDataSetChanged();

                        pDialog.dismiss();
                    }

                    for (int i = 0; i < commentsObj.length(); i++) {

                        JSONObject commentObj = (JSONObject) commentsObj.get(i);

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = commentObj.getJSONObject("user");
                        String userId = userObj.getString("user_id");

                        final_message_id = obj.getJSONObject("final_message_id").getString("final_message_id");

                        ChatUser chatUser = new ChatUser(userId);

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setId(commentId);
                        chatMessage.setMessage(commentText);
                        chatMessage.setCreatedAt(createdAt);
                        chatMessage.setChatUser(chatUser);

                        chatMessageArrayList.add(chatMessage);

                        if (i == (commentsObj.length() - 1)) {

                            chatMessageArrayList.addAll(chatTempMessageArrayList);

                            mAdapter.notifyDataSetChanged();

                            pDialog.dismiss();

                        }

                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Json parse error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401)
                    Toast.makeText(getApplicationContext(), "Volley error : " + error.getMessage() + ", Code : " + networkResponse, Toast.LENGTH_LONG).show();
            }
        });

        strReq.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        basicFunctions.addToRequestQueue(strReq);

    }


    private class ChatRoomAdapter extends RecyclerView.Adapter<ViewHolder> {

        private String userId;

        private int SELF = 100;

        Context mContext;

        private ChatRoomAdapter(Context mContext, String userId) {

            this.mContext = mContext;
            this.userId = userId;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView;

            if (viewType == SELF) {

                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_messenger_item_self, parent, false);

            } else {

                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_messenger_item_other, parent, false);

            }

            return new ViewHolder(itemView);
        }

        @Override
        public int getItemViewType(int position) {

            ChatMessage chatMessage = chatMessageArrayList.get(position);

            if (chatMessage.getChatUser().getId().equals(userId)) {

                return SELF;

            }

            return position;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

            ChatMessage chatMessage = chatMessageArrayList.get(position);

            String timestamp = BasicFunctions.getTimeStamp(chatMessage.getCreatedAt());

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date date = null;

            try {
                date = dateFormat.parse(chatMessage.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            int seconds = calendar.get(Calendar.SECOND);

            holder.message.setText(messageDecrypt(chatMessage.getMessage(), hours, minutes, seconds));

            if (position == 0)
                holder.fetch_old.setVisibility(View.VISIBLE);

            else
                holder.fetch_old.setVisibility(View.GONE);

            holder.fetch_old.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getOldChatThread();

                }
            });

            holder.timestamp.setText(timestamp);

        }

        @Override
        public int getItemCount() {
            return chatMessageArrayList.size();
        }

    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, timestamp;
        Button fetch_old;

        private ViewHolder(View view) {
            super(view);

            message = itemView.findViewById(R.id.mes_i_message);
            timestamp = itemView.findViewById(R.id.mes_i_timestamp);
            fetch_old = view.findViewById(R.id.fetch_old);
        }
    }


}