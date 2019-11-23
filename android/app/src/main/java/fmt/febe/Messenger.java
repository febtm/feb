package fmt.febe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.android.volley.toolbox.Volley;

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
import java.util.Locale;

import fmt.febe.model.ChatRoom;


public class Messenger extends AppCompatActivity {


    JSONArray SearchProfileArray = null;

    EditText ET_SEARCH;

    String searchText;
    ListView search_list;
    ListViewAdapter SearchListAdapter;
    ArrayList<ItemList> searcharraylist = new ArrayList<>();


    private ArrayList<ChatRoom> chatRoomArrayList = new ArrayList<>();

    private ChatMainAdapter mAdapter;

    RecyclerView recyclerView;

    private BasicFunctions basicFunctions;

    private ProgressDialog pDialog;

    private int load_over = 0;


    ImageButton MENU_BUTTON, MES_CANCEL;

    private fmt.febe.Menu menu;

    private String chat_room_id, mp_userid, mp_username;

    LinearLayout MES_NOT;

    TextView MES_NOT_COUNT;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messenger);

        menu = new fmt.febe.Menu(Messenger.this);

        basicFunctions = new BasicFunctions(this);

        MES_NOT = findViewById(R.id.mes_not);

        MES_NOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Messenger.this, Notifications.class);
                startActivity(intent);

            }
        });

        MES_NOT_COUNT = findViewById(R.id.mes_not_count);
        Intent intent = getIntent();
        int not_count = intent.getIntExtra("unreadNotificationCount", 0);

        if(not_count > 0)
            MES_NOT_COUNT.setText("" + not_count);

        search_list = findViewById(R.id.mes_search_list);
        search_list.setVisibility(View.GONE);

        ET_SEARCH = findViewById(R.id.mes_search_bar);

        ET_SEARCH.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                searchText = ET_SEARCH.getText().toString().toLowerCase(Locale.getDefault());

                if (TextUtils.isEmpty(searchText))
                    search_list.setVisibility(View.GONE);

                else {

                    searcharraylist.clear();
                    searchData(searchText, 0);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        MES_CANCEL = findViewById(R.id.mes_cancel);

        MES_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_SEARCH.setText("");
                search_list.setVisibility(View.GONE);

            }
        });



        /*
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        */


        MENU_BUTTON = findViewById(R.id.mes_menu);

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.LeftDrawer.toggleLeftDrawer();

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.mes_recycler_view);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ChatMainAdapter(this);

        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    int index = chatRoomArrayList.size();

                    getMoreChatRooms(index);

                    mAdapter.notifyDataSetChanged();
                    mAdapter.setLoaded();

                    }
                }, 1000);
            }
        });

        if (basicFunctions.isConnectingToInternet()) {

            pDialog = ProgressDialog.show(Messenger.this, "", "Fetching Chat List ... ", false, false);

            getInitialChatRooms();


        } else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(Messenger.this, Messenger.class);
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


    private void getInitialChatRooms() {

        StringRequest strReq = new StringRequest(Request.Method.GET,
                BasicFunctions.GET_ALL_CHAT_ROOMS + basicFunctions.getUser_id() + "&index=" + 0, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");

                    System.out.println("HI come" + response);

                    if(chatRoomsArray.length() <= 0) {

                        Toast.makeText(Messenger.this, "Your Chat List is empty !", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();

                    }

                    for (int i = 0; i < chatRoomsArray.length(); i++) {

                        JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                        ChatRoom cr = new ChatRoom();

                        cr.setId(chatRoomsObj.getString("chat_room_id"));
                        cr.setUser1_id(chatRoomsObj.getString("user1_id"));
                        cr.setUser1_name(chatRoomsObj.getString("user1_name"));
                        cr.setUser1_online(chatRoomsObj.getString("user1_online"));
                        cr.setUser1_read(chatRoomsObj.getString("user1_read"));

                        cr.setUser2_id(chatRoomsObj.getString("user2_id"));
                        cr.setUser2_name(chatRoomsObj.getString("user2_name"));
                        cr.setUser2_online(chatRoomsObj.getString("user2_online"));
                        cr.setUser2_read(chatRoomsObj.getString("user2_read"));

                        cr.setTimestamp(chatRoomsObj.getString("created_at"));

                        chatRoomArrayList.add(cr);

                        if(i == chatRoomsArray.length() - 1)
                            pDialog.dismiss();

                    }

                    mAdapter.notifyDataSetChanged();

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


    private interface OnLoadMoreListener {
        void onLoadMore();
    }


    private class ChatMainAdapter extends RecyclerView.Adapter<ViewHolder> {

        Context mContext;

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;


        private ChatMainAdapter(Context mContext) {

            this.mContext = mContext;

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

        private void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        private void setLoaded() {
            isLoading = false;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_messenger_list_row, parent, false);

            return new ViewHolder(itemView);
        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            ChatRoom chatRoom = chatRoomArrayList.get(position);

            if(basicFunctions.getUser_name().equals(chatRoom.getUser1_name())) {

                holder.username.setText(chatRoom.getUser2_name());

                if(chatRoom.getUser2_online().equals("1"))
                    holder.useronline.setVisibility(View.VISIBLE);

                else
                    holder.useronline.setVisibility(View.GONE);

                if(chatRoom.getUser1_read().equals("1"))
                    holder.userread.setVisibility(View.VISIBLE);

                else
                    holder.userread.setVisibility(View.GONE);

            }

            else {

                holder.username.setText(chatRoom.getUser1_name());

                if(chatRoom.getUser1_online().equals("1"))
                    holder.useronline.setVisibility(View.VISIBLE);

                else
                    holder.useronline.setVisibility(View.GONE);

                if(chatRoom.getUser2_read().equals("1"))
                    holder.userread.setVisibility(View.VISIBLE);

                else
                    holder.userread.setVisibility(View.GONE);

            }

            holder.timestamp.setText(BasicFunctions.getTimeStamp(chatRoom.getTimestamp()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ChatRoom chatRoom = chatRoomArrayList.get(position);

                    Intent intent = new Intent(Messenger.this, MessengerItem.class);

                    intent.putExtra("chat_room_id", chatRoom.getId());

                    if (chatRoom.getUser1_name().equals(basicFunctions.getUser_name())) {

                        intent.putExtra("username", chatRoom.getUser2_name());
                        intent.putExtra("userid", chatRoom.getUser2_id());

                    } else {

                        intent.putExtra("username", chatRoom.getUser1_name());
                        intent.putExtra("userid", chatRoom.getUser1_id());

                    }

                    startActivity(intent);

                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ChatRoom chatRoom = chatRoomArrayList.get(position);

                    String method = "chat";
                    DeleteChatBackgroundTask deleteChatBackgroundTask = new DeleteChatBackgroundTask(Messenger.this);
                    deleteChatBackgroundTask.execute(method, chatRoom.getId(), basicFunctions.getUser_id());

                    chatRoomArrayList.remove(chatRoom);

                    Toast.makeText(Messenger.this, "Chat removed from Chat List !", Toast.LENGTH_LONG).show();

                    mAdapter.notifyDataSetChanged();

                }
            });

        }


        @Override
        public int getItemCount() {
            return chatRoomArrayList.size();
        }


    }


    private void getMoreChatRooms(int index) {

        pDialog = ProgressDialog.show(Messenger.this, "", "Fetching Chat List ... ", false, false);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                BasicFunctions.GET_ALL_CHAT_ROOMS + basicFunctions.getUser_id() + "&index=" + index, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");

                    if(chatRoomsArray.length() == 0) {

                        Toast.makeText(Messenger.this, "No more Chats to display !", Toast.LENGTH_LONG).show();
                        load_over = 1;
                        pDialog.dismiss();

                    }

                    for (int i = 0; i < chatRoomsArray.length(); i++) {

                        JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                        ChatRoom cr = new ChatRoom();

                        cr.setId(chatRoomsObj.getString("chat_room_id"));
                        cr.setUser1_id(chatRoomsObj.getString("user1_id"));
                        cr.setUser1_name(chatRoomsObj.getString("user1_name"));
                        cr.setUser1_online(chatRoomsObj.getString("user1_online"));
                        cr.setUser1_read(chatRoomsObj.getString("user1_read"));

                        cr.setUser2_id(chatRoomsObj.getString("user2_id"));
                        cr.setUser2_name(chatRoomsObj.getString("user2_name"));
                        cr.setUser2_online(chatRoomsObj.getString("user2_online"));
                        cr.setUser2_read(chatRoomsObj.getString("user2_read"));

                        cr.setTimestamp(chatRoomsObj.getString("created_at"));

                        chatRoomArrayList.add(cr);

                        if(i == chatRoomsArray.length() - 1)
                            pDialog.dismiss();

                    }

                    mAdapter.notifyDataSetChanged();

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


    @SuppressLint("StaticFieldLeak")
    private class DeleteChatBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;

        private ProgressDialog pDialog;

        DeleteChatBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }


        @Override
        public void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Messenger.this);
            pDialog.setMessage("Removing Chat ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("chat")) {

                String chat_room_id = params[1];
                String user_id = params[2];

                try {

                    URL url = new URL(BasicFunctions.DELETE_CHAT);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));


                    String data = URLEncoder.encode("chat_room_id", "UTF-8") + "=" + URLEncoder.encode(chat_room_id, "UTF-8")
                            + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");


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

            switch(result){

                case "Chat removed from Chat List !":

                    pDialog.dismiss();

                    if(chatRoomArrayList.size() == 0)
                        Toast.makeText(Messenger.this, "Your Chat List is empty !", Toast.LENGTH_LONG).show();

                    break;

                default:

                    Toast.makeText(Messenger.this, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                    break;

            }

        }
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, timestamp;
        private ImageButton delete;
        private ImageView useronline, userread;

        private ViewHolder(View view) {
            super(view);

            username = view.findViewById(R.id.mes_i_username);
            timestamp = view.findViewById(R.id.mes_i_timestamp);
            userread = view.findViewById(R.id.mes_i_userread);
            useronline = view.findViewById(R.id.mes_i_useronline);
            delete = view.findViewById(R.id.mes_i_deletebutton);

        }
    }

    private void searchData(String searchText, final int index) {

        pDialog = ProgressDialog.show(this, "", "Fetching Profiles ... ", false, false);

        String url = BasicFunctions.SEARCH_FOR_PROFILES + index + "&searchText=" + searchText;

        url = url.replaceAll(" ", "%20");

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObj = new JSONObject(response);
                    SearchProfileArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

                    if (index == 0 && SearchProfileArray.length() == 0) {

                        search_list.setVisibility(View.GONE);
                        Toast.makeText(Messenger.this, "No Profiles Found !", Toast.LENGTH_LONG).show();

                    }

                    else if(SearchProfileArray.length() == 0)
                        Toast.makeText(Messenger.this, "No More Profiles Found !", Toast.LENGTH_LONG).show();

                    else
                        search_list.setVisibility(View.VISIBLE);


                    for (int i = 0; i < SearchProfileArray.length(); i++) {

                        JSONObject jsonObject = SearchProfileArray.getJSONObject(i);

                        ItemList itemList = new ItemList(jsonObject.getString(basicFunctions.KEY_USER_ID),
                                jsonObject.getString(basicFunctions.KEY_USER_USERNAME));

                        if(!itemList.getUserid().equals(basicFunctions.getUser_id()))
                            searcharraylist.add(itemList);


                    }

                    SearchListAdapter = new ListViewAdapter(Messenger.this);
                    search_list.setAdapter(SearchListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Messenger.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Messenger.this);
        requestQueue.add(stringRequest);

        pDialog.dismiss();

    }


    private class ListViewAdapter extends BaseAdapter {

        Context mContext;
        LayoutInflater inflater;

        private ListViewAdapter(Context context) {

            mContext = context;
            inflater = LayoutInflater.from(mContext);

        }

        private class ViewHolder {

            TextView TV_USERNAME;
            Button B_SHOW_MORE;

        }

        @Override
        public int getCount() {
            return searcharraylist.size();
        }

        @Override
        public ItemList getItem(int position) {
            return searcharraylist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public View getView(final int position, View view, ViewGroup parent) {

            final ViewHolder holder;

            if (view == null) {

                holder = new ViewHolder();
                view = inflater.inflate(R.layout.activity_messenger_search_list_item, parent, false);

                holder.TV_USERNAME = view.findViewById(R.id.mes_search_list_title);
                holder.B_SHOW_MORE = view.findViewById(R.id.mes_search_list_more);

                view.setTag(holder);

            }

            else holder = (ViewHolder) view.getTag();

            holder.TV_USERNAME.setText(searcharraylist.get(position).getUsername());

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    mp_userid = searcharraylist.get(position).getUserid();
                    mp_username = searcharraylist.get(position).getUsername();

                    dbchat();


                }
            });

            if(position == (searcharraylist.size() - 1))
                holder.B_SHOW_MORE.setVisibility(View.VISIBLE);

            holder.B_SHOW_MORE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    searchData(searchText, searcharraylist.size());
                }
            });


            return view;
        }

    }


    private void dbchat() {

        String method = "chat_request";

        ChatBackgroundTask backgroundTask = new ChatBackgroundTask(this);

        backgroundTask.execute(method, basicFunctions.getUser_id(), mp_userid);

    }


    @SuppressLint("StaticFieldLeak")
    private class ChatBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;
        String data;

        ChatBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];
            String current_user_id = params[1];
            String user_id = params[2];


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

            switch (result){

                case "A Chat request has been sent to this User !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    break;

                case "A Chat request has already been sent to this User !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    break;

                default:

                    chat_room_id = result;
                    login();

                    break;

            }
        }
    }


    private void login() {

        Intent intent = new Intent(Messenger.this, MessengerItem.class);

        intent.putExtra("chat_room_id", chat_room_id);
        intent.putExtra("userid", mp_userid);
        intent.putExtra("username", mp_username);

        startActivity(intent);

    }


    private class ItemList {

        private String userid;
        private String username;

        private ItemList(String userid, String username) {

            this.userid = userid;
            this.username = username;

        }

        private String getUserid() {
            return this.userid;
        }

        private String getUsername() {
            return this.username;
        }

    }

}