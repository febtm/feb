package fmt.febe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.navdrawer.SimpleSideDrawer;

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


@SuppressLint("Registered")
public class Menu extends Activity {


    ListView MenuList;
    ArrayList<MenuListItem> MenuItemList;
    MenuListAdapter MenuListAdapter;

    String[] MenuTitles;
    TypedArray MenuIcons;

    public SimpleSideDrawer LeftDrawer;

    Activity menuActivity;

    private BasicFunctions basicFunctions;

    JSONArray CountArray = null;

    private Integer unReadNotificationCount = 0;
    private Integer unReadMessageCount = 0;


    @SuppressWarnings("ResourceType")
    public Menu(Activity activity) {

        menuActivity = activity;

        basicFunctions = new BasicFunctions(menuActivity);

        LeftDrawer = new SimpleSideDrawer(menuActivity);
        LeftDrawer.setLeftBehindContentView(R.layout.activity_menu);

        MenuItemList = new ArrayList<>();
        MenuTitles = menuActivity.getResources().getStringArray(R.array.menu_titles);
        MenuIcons = menuActivity.getResources().obtainTypedArray(R.array.menu_icons);

        MenuItemList.add(new MenuListItem(MenuTitles[0], MenuIcons.getResourceId(0, -1)));
        MenuItemList.add(new MenuListItem(MenuTitles[1], MenuIcons.getResourceId(1, -1)));
        MenuItemList.add(new MenuListItem(MenuTitles[2], MenuIcons.getResourceId(2, -1)));
        MenuItemList.add(new MenuListItem(MenuTitles[3], MenuIcons.getResourceId(3, -1)));
        MenuItemList.add(new MenuListItem(MenuTitles[4], MenuIcons.getResourceId(4, -1)));
        MenuItemList.add(new MenuListItem(MenuTitles[5], MenuIcons.getResourceId(5, -1)));
        MenuItemList.add(new MenuListItem(MenuTitles[6], MenuIcons.getResourceId(6, -1)));
        MenuItemList.add(new MenuListItem(MenuTitles[7], MenuIcons.getResourceId(7, -1)));
        MenuItemList.add(new MenuListItem(MenuTitles[8], MenuIcons.getResourceId(8, -1)));

        MenuList = menuActivity.findViewById(R.id.menu_list);
        MenuListAdapter = new MenuListAdapter(menuActivity, MenuItemList);
        MenuList.setAdapter(MenuListAdapter);
        MenuList.setOnItemClickListener(new SlideMenuClickListener());

        if (basicFunctions.isConnectingToInternet())
            getCount();

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(menuActivity, HomePage.class);
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


    private void getCount() {

        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_COUNT + basicFunctions.getUser_id(),
                new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                showList_Count(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(menuActivity, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(menuActivity);
        requestQueue.add(stringRequest);

    }


    @SuppressWarnings("ResourceType")
    protected void showList_Count(String response) {

        String notification_count;
        String chat_count;

        try {

            JSONObject jsonObj = new JSONObject(response);
            CountArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);
            JSONObject jsonObject = CountArray.getJSONObject(0);

            notification_count = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_COUNT);
            chat_count = jsonObject.getString(basicFunctions.KEY_CHAT_COUNT);

            unReadNotificationCount = Integer.parseInt(notification_count);

            unReadMessageCount = Integer.parseInt(chat_count);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        MenuItemList.set(1, new MenuListItem(MenuTitles[1] + "\t(" + unReadMessageCount + ")", MenuIcons.getResourceId(1, -1)));

        MenuListAdapter.notifyDataSetChanged();

        MenuIcons.recycle();

    }


    private class MenuListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<MenuListItem> menuListItems;

        private MenuListAdapter(Context context, ArrayList<MenuListItem> menuListItems){

            this.context = context;
            this.menuListItems = menuListItems;

        }

        @Override
        public int getCount() {
            return menuListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return menuListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                assert mInflater != null;
                convertView = mInflater.inflate(R.layout.activity_menu_item, parent, false);

            }

            ImageView imgIcon = convertView.findViewById(R.id.menu_list_item_icon);
            TextView txtTitle = convertView.findViewById(R.id.menu_list_item_title);

            imgIcon.setImageResource(menuListItems.get(position).getIcon());
            txtTitle.setText(menuListItems.get(position).getTitle());

            return convertView;

        }
    }


    private class MenuListItem {

        private String title;
        private int icon;

        private MenuListItem(String title, int icon){

            this.title = title;
            this.icon = icon;

        }

        private String getTitle(){
            return this.title;
        }

        private int getIcon(){
            return this.icon;
        }

    }


    private class SlideMenuClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            displayView(position);

        }

    }


    private void displayView(int position) {

        switch (position) {

            case 0:

                Intent Homepage = new Intent(menuActivity, HomePage.class);
                menuActivity.startActivity(Homepage);
                break;

            case 1:

                Intent Messenger = new Intent(menuActivity, Messenger.class);
                Messenger.putExtra("unreadNotificationCount", unReadNotificationCount);
                menuActivity.startActivity(Messenger);
                break;

            case 2:

                Intent SocialMedia = new Intent(menuActivity, SocialMedia.class);
                menuActivity.startActivity(SocialMedia);
                break;


            case 3:

                Intent HomeAssistant = new Intent(menuActivity, HomeAssistant.class);
                menuActivity.startActivity(HomeAssistant);
                break;

            case 4:

                Intent Maps = new Intent(menuActivity, Maps.class);
                menuActivity.startActivity(Maps);
                break;

            case 5:

                Intent WeatherForecast = new Intent(menuActivity, WeatherForecast.class);
                menuActivity.startActivity(WeatherForecast);

                break;

            case 6:

                Intent ANR = new Intent(menuActivity, ANR.class);
                menuActivity.startActivity(ANR);

                break;

            case 7:

                Intent ContactUs = new Intent(menuActivity, ContactUs.class);
                menuActivity.startActivity(ContactUs);
                break;

            case 8:

                logout();
                break;

            default:
                break;

        }
    }


    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    public void logout(){

        LogoutBackgroundTask lb = new LogoutBackgroundTask(menuActivity);
        lb.execute("logout", basicFunctions.getUser_id());

    }


    @SuppressLint("StaticFieldLeak")
    private class LogoutBackgroundTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        Context ctx;

        LogoutBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(menuActivity);
            pDialog.setMessage("Logging Out ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("logout")) {

                String userid = params[1];

                try {

                    URL url = new URL(BasicFunctions.LOGOUT);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");

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

            switch (result) {

                case "Logout Successful !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    basicFunctions.setLogin(false);

                    basicFunctions.setUserDataNull();

                    Intent Logout = new Intent(menuActivity, Login.class);
                    menuActivity.startActivity(Logout);

                    break;

                case "Logout Failed !":

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