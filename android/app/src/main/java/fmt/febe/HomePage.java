package fmt.febe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import fmt.febe.helper.BasicFunctions;
import fmt.febe.helper.LocationEnabler;
import fmt.febe.helper.Menu;


public class HomePage extends AppCompatActivity {


    private static final int REQ_CODE_SPEECH_INPUT = 100;

    private EditText ET_SEARCH, ET_PH_NO, ET_MESSAGE;

    private TextToSpeech TTS;

    private Menu menu;

    private TextView TV_TIME;

    private FrameLayout HP_PHONE_MESSAGE, HP_MESS_LAYOUT;

    private ImageButton IB_SEARCH_BUTTON_PH_MES;

    private LinearLayout HP_SEARCH_LAYOUT;

    ArrayList<SearchResultValues> HP_SEARCH_RESULT_LIST;

    ArrayList<NewsItemsValues> HP_GENERAL_NEWS_LIST, HP_BUSINESS_NEWS_LIST,
            HP_TECHNOLOGY_NEWS_LIST, HP_SPORTS_NEWS_LIST, HP_ENTERTAINMENT_NEWS_LIST;

    RecyclerView mSearchResultRecyclerView, mGeneralNewsRecyclerView,
            mBusinessNewsRecyclerView, mTechnologyNewsRecyclerView, mSportsNewsRecyclerView, mEntertainmentNewsRecyclerView;

    RecyclerView.Adapter mSearchResultAdapter, mGeneralNewsAdapter,
            mBusinessNewsAdapter, mTechnologyNewsAdapter, mSportsNewsAdapter, mEntertainmentNewsAdapter;

    RecyclerView.LayoutManager mSearchResultLayoutManager, mGeneralNewsLayoutManager,
            mBusinessNewsLayoutManager, mTechnologyNewsLayoutManager, mSportsNewsLayoutManager, mEntertainmentNewsLayoutManager;

    private ProgressDialog progressDialog;

    String MY_LOCATION_COUNTRY_CODE;

    private BasicFunctions basicFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        basicFunctions = new BasicFunctions(HomePage.this);

        menu = new Menu(HomePage.this);

        AdView mAdView = findViewById(R.id.hp_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ImageButton IB_VOICE_BUTTON = findViewById(R.id.hp_voice_button);

        HP_PHONE_MESSAGE = findViewById(R.id.hp_phone_message);
        HP_MESS_LAYOUT = findViewById(R.id.hp_mess_layout);

        ET_SEARCH = findViewById(R.id.hp_search_bar);
        ET_PH_NO = findViewById(R.id.hp_phone_number);
        ET_MESSAGE = findViewById(R.id.hp_message);

        ImageButton IB_SEARCH_BUTTON = findViewById(R.id.hp_search_button);
        IB_SEARCH_BUTTON_PH_MES = findViewById(R.id.hp_search_button_ph_mes);

        ImageButton IB_MENU = findViewById(R.id.hp_menu);

        ImageButton IB_CANCEL = findViewById(R.id.hp_cancel);
        ImageButton IB_CANCEL_PHONE = findViewById(R.id.hp_cancel_phone_number);
        ImageButton IB_CANCEL_MESSAGE = findViewById(R.id.hp_cancel_message);

        TV_TIME = findViewById(R.id.hp_time);

        HP_SEARCH_LAYOUT = findViewById(R.id.hp_search_layout);

        mSearchResultRecyclerView = findViewById(R.id.hp_search_results);
        mSearchResultRecyclerView.setHasFixedSize(true);
        mSearchResultLayoutManager = new LinearLayoutManager(this);
        mSearchResultRecyclerView.setLayoutManager(mSearchResultLayoutManager);

        mGeneralNewsRecyclerView = findViewById(R.id.hp_news_general);
        mGeneralNewsRecyclerView.setHasFixedSize(true);
        mGeneralNewsLayoutManager = new LinearLayoutManager(this);
        mGeneralNewsRecyclerView.setLayoutManager(mGeneralNewsLayoutManager);

        mBusinessNewsRecyclerView = findViewById(R.id.hp_news_business);
        mBusinessNewsRecyclerView.setHasFixedSize(true);
        mBusinessNewsLayoutManager = new LinearLayoutManager(this);
        mBusinessNewsRecyclerView.setLayoutManager(mBusinessNewsLayoutManager);

        mTechnologyNewsRecyclerView = findViewById(R.id.hp_news_techonology);
        mTechnologyNewsRecyclerView.setHasFixedSize(true);
        mTechnologyNewsLayoutManager = new LinearLayoutManager(this);
        mTechnologyNewsRecyclerView.setLayoutManager(mTechnologyNewsLayoutManager);

        mSportsNewsRecyclerView = findViewById(R.id.hp_news_sports);
        mSportsNewsRecyclerView.setHasFixedSize(true);
        mSportsNewsLayoutManager = new LinearLayoutManager(this);
        mSportsNewsRecyclerView.setLayoutManager(mSportsNewsLayoutManager);

        mEntertainmentNewsRecyclerView = findViewById(R.id.hp_news_entertainment);
        mEntertainmentNewsRecyclerView.setHasFixedSize(true);
        mEntertainmentNewsLayoutManager = new LinearLayoutManager(this);
        mEntertainmentNewsRecyclerView.setLayoutManager(mEntertainmentNewsLayoutManager);

        IB_MENU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.LeftDrawer.toggleLeftDrawer();

            }
        });

        showTime();

        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    int result = TTS.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        Toast.makeText(HomePage.this, "This Language is not supported !", Toast.LENGTH_LONG).show();

                    speak(getString(R.string.hp_tts_start));

                } else
                    Toast.makeText(HomePage.this, "Text to Speech Initialization Failed !", Toast.LENGTH_LONG).show();

            }

        });

        IB_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_SEARCH.setText("");

            }
        });

        IB_CANCEL_PHONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_PH_NO.setText("");

            }
        });

        IB_CANCEL_MESSAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_MESSAGE.setText("");

            }
        });

        IB_VOICE_BUTTON.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }

        });

        IB_SEARCH_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = ET_SEARCH.getText().toString();

                if (TextUtils.isEmpty(searchText))
                    ET_SEARCH.setError("Type in the box or click on the mic to search !");

                else
                    performSearchFunction(searchText);

            }
        });

        if (basicFunctions.isConnectingToInternet())
            this.checkLocationPermission();

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {

                        case DialogInterface.BUTTON_POSITIVE:

                            if (basicFunctions.isConnectingToInternet())
                                HomePage.this.checkLocationPermission();

                            else {

                                Toast.makeText(HomePage.this,
                                        "No Internet Connection. Try again later !",
                                        Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            Toast.makeText(HomePage.this,
                                    "No Internet Connection. Try again later !",
                                    Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
            builder.setMessage("No Internet Connection. Try again ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }

    }

    private void checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(HomePage.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(HomePage.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(HomePage.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        else
            HomePage.this.enableLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {

            case 0: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    this.enableLocation();

                else {

                    Toast.makeText(HomePage.this, "Kindly grant Location permission to continue !", Toast.LENGTH_LONG).show();
                    finish();

                }

                break;
            }

            default:
                break;

        }
    }

    private void enableLocation() {

        fetchNews();

        new LocationEnabler(this).turnGPSOn(new LocationEnabler.onGpsListener() {
            @Override
            public void gpsStatus() {
            }
        });
    }


    private void fetchNews() {

        progressDialog = new ProgressDialog(HomePage.this);
        progressDialog.setMessage("Fetching News ... ");
        progressDialog.show();

        MY_LOCATION_COUNTRY_CODE = this.getResources().getConfiguration().locale.getCountry();

        HP_GENERAL_NEWS_LIST = new ArrayList<>();
        mGeneralNewsAdapter = new NewsItemsAdapter(HP_GENERAL_NEWS_LIST);
        mGeneralNewsRecyclerView.setAdapter(mGeneralNewsAdapter);

        HP_BUSINESS_NEWS_LIST = new ArrayList<>();
        mBusinessNewsAdapter = new NewsItemsAdapter(HP_BUSINESS_NEWS_LIST);
        mBusinessNewsRecyclerView.setAdapter(mBusinessNewsAdapter);

        HP_TECHNOLOGY_NEWS_LIST = new ArrayList<>();
        mTechnologyNewsAdapter = new NewsItemsAdapter(HP_TECHNOLOGY_NEWS_LIST);
        mTechnologyNewsRecyclerView.setAdapter(mTechnologyNewsAdapter);

        HP_SPORTS_NEWS_LIST = new ArrayList<>();
        mSportsNewsAdapter = new NewsItemsAdapter(HP_SPORTS_NEWS_LIST);
        mSportsNewsRecyclerView.setAdapter(mSportsNewsAdapter);

        HP_ENTERTAINMENT_NEWS_LIST = new ArrayList<>();
        mEntertainmentNewsAdapter = new NewsItemsAdapter(HP_ENTERTAINMENT_NEWS_LIST);
        mEntertainmentNewsRecyclerView.setAdapter(mEntertainmentNewsAdapter);

        FetchNewsItems fetchGeneralNewsItems = new FetchNewsItems();
        fetchGeneralNewsItems.execute(1);

    }


    @SuppressLint("StaticFieldLeak")
    private class FetchNewsItems extends AsyncTask<Integer, Integer, String> {

        int responseCode;
        String responseMessage;
        String result;

        int NEWS_TYPE = 0;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {

            NEWS_TYPE = params[0];

            String NEWS_API_KEY = "13fad38e90404faa97795bd7d6780dd1";

            String newsURL = null;

            if (NEWS_TYPE == 1)
                newsURL = "https://newsapi.org/v2/top-headlines?category=general&pageSize=5&apiKey="
                        + NEWS_API_KEY + "&country=" + MY_LOCATION_COUNTRY_CODE;

            else if (NEWS_TYPE == 2)
                newsURL = "https://newsapi.org/v2/top-headlines?category=business&pageSize=5&apiKey="
                        + NEWS_API_KEY + "&country=" + MY_LOCATION_COUNTRY_CODE;

            else if (NEWS_TYPE == 3)
                newsURL = "https://newsapi.org/v2/top-headlines?category=technology&pageSize=5&apiKey="
                        + NEWS_API_KEY + "&country=" + MY_LOCATION_COUNTRY_CODE;

            else if (NEWS_TYPE == 4)
                newsURL = "https://newsapi.org/v2/top-headlines?category=sports&pageSize=5&apiKey="
                        + NEWS_API_KEY + "&country=" + MY_LOCATION_COUNTRY_CODE;

            else if (NEWS_TYPE == 5)
                newsURL = "https://newsapi.org/v2/top-headlines?category=entertainment&pageSize=5&apiKey="
                        + NEWS_API_KEY + "&country=" + MY_LOCATION_COUNTRY_CODE;

            URL url = null;

            try {

                url = new URL(newsURL);

            } catch (MalformedURLException e) {

                Toast.makeText(HomePage.this, "String URL Conversion Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            HttpURLConnection conn = null;

            try {

                assert url != null;
                conn = (HttpURLConnection) url.openConnection();

            } catch (IOException e) {

                Toast.makeText(HomePage.this, "HTTP Connection Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            try {

                assert conn != null;
                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();

            } catch (IOException e) {

                Toast.makeText(HomePage.this, "HTTP Response Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            try {

                if (responseCode == 200) {

                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = rd.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    rd.close();

                    conn.disconnect();

                    result = sb.toString();

                    return result;

                } else {

                    Toast.makeText(HomePage.this, "HTTP Response Error : " + responseMessage, Toast.LENGTH_LONG).show();
                    result = responseMessage;
                    return result;

                }

            } catch (IOException e) {

                Toast.makeText(HomePage.this, "HTTP Response Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            return null;

        }

        protected void onPostExecute(String result) {

            JSONObject jsonResult;

            String title, description, link, image;

            try {

                jsonResult = new JSONObject(result);

                JSONArray items = jsonResult.getJSONArray("articles");

                for (int i = 0; i < items.length(); i++) {

                    JSONObject jsonObject = items.getJSONObject(i);

                    title = jsonObject.getString("title");
                    description = jsonObject.getString("description");
                    link = jsonObject.getString("url");
                    image = jsonObject.getString("urlToImage");

                    NewsItemsValues obj = new NewsItemsValues(title, description, link, image);

                    if (NEWS_TYPE == 1) {

                        HP_GENERAL_NEWS_LIST.add(obj);

                        mGeneralNewsAdapter.notifyDataSetChanged();

                    } else if (NEWS_TYPE == 2) {

                        HP_BUSINESS_NEWS_LIST.add(obj);

                        mBusinessNewsAdapter.notifyDataSetChanged();

                    } else if (NEWS_TYPE == 3) {

                        HP_TECHNOLOGY_NEWS_LIST.add(obj);

                        mTechnologyNewsAdapter.notifyDataSetChanged();

                    } else if (NEWS_TYPE == 4) {

                        HP_SPORTS_NEWS_LIST.add(obj);

                        mSportsNewsAdapter.notifyDataSetChanged();

                    } else if (NEWS_TYPE == 5) {

                        HP_ENTERTAINMENT_NEWS_LIST.add(obj);

                        mEntertainmentNewsAdapter.notifyDataSetChanged();

                    }

                }

                FetchNewsItems fetchBusinessNewsItems = new FetchNewsItems();

                if (NEWS_TYPE == 1)
                    fetchBusinessNewsItems.execute(2);

                else if (NEWS_TYPE == 2)
                    fetchBusinessNewsItems.execute(3);

                else if (NEWS_TYPE == 3)
                    fetchBusinessNewsItems.execute(4);

                else if (NEWS_TYPE == 4)
                    fetchBusinessNewsItems.execute(5);

                progressDialog.dismiss();

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }

    }


    private class NewsItemsAdapter extends RecyclerView.Adapter<NewsItemsAdapter.DataHolder> {

        private ArrayList<NewsItemsValues> mDataset;

        class DataHolder extends RecyclerView.ViewHolder {

            TextView HP_I_N_TITLE, HP_I_N_DESCRIPTION, HP_I_N_LINK;
            ImageView HP_I_N_IMAGE;

            DataHolder(final View itemView) {
                super(itemView);

                HP_I_N_TITLE = itemView.findViewById(R.id.hp_i_n_title);
                HP_I_N_DESCRIPTION = itemView.findViewById(R.id.hp_i_n_description);
                HP_I_N_LINK = itemView.findViewById(R.id.hp_i_n_link);
                HP_I_N_IMAGE = itemView.findViewById(R.id.hp_i_n_image);

            }

        }

        NewsItemsAdapter(ArrayList<NewsItemsValues> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_hp_item_news, parent, false);

            mGeneralNewsRecyclerView.setMinimumHeight(mGeneralNewsRecyclerView.getHeight() + view.getHeight());
            mBusinessNewsRecyclerView.setMinimumHeight(mGeneralNewsRecyclerView.getHeight() + view.getHeight());
            mTechnologyNewsRecyclerView.setMinimumHeight(mGeneralNewsRecyclerView.getHeight() + view.getHeight());
            mSportsNewsRecyclerView.setMinimumHeight(mGeneralNewsRecyclerView.getHeight() + view.getHeight());
            mEntertainmentNewsRecyclerView.setMinimumHeight(mGeneralNewsRecyclerView.getHeight() + view.getHeight());

            return new DataHolder(view);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(@NonNull final DataHolder holder, @SuppressLint("RecyclerView") final int position) {

            String title, description, link, image;

            title = mDataset.get(position).getHP_I_N_TITLE();
            description = mDataset.get(position).getHP_I_N_DESCRIPTION();
            link = mDataset.get(position).getHP_I_N_LINK();
            image = mDataset.get(position).getHP_I_N_IMAGE();

            if (!title.equals("null"))
                holder.HP_I_N_TITLE.setText(title);

            else
                holder.HP_I_N_TITLE.setVisibility(View.GONE);

            if (!description.equals("null"))
                holder.HP_I_N_DESCRIPTION.setText(description);

            else
                holder.HP_I_N_DESCRIPTION.setVisibility(View.GONE);

            if (!link.equals("null")) {

                SpannableString underlinedLink = new SpannableString(link);
                underlinedLink.setSpan(new UnderlineSpan(), 0, underlinedLink.length(), 0);

                holder.HP_I_N_LINK.setText(underlinedLink);

            } else
                holder.HP_I_N_LINK.setVisibility(View.GONE);

            if (!image.equals("null")) {

                image = mDataset.get(position).getHP_I_N_IMAGE();

                URL url;

                Bitmap bmp = null;

                try {

                    url = new URL(image);

                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                } catch (MalformedURLException e) {

                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                holder.HP_I_N_IMAGE.setImageBitmap(bmp);

            } else
                holder.HP_I_N_IMAGE.setVisibility(View.GONE);

            holder.HP_I_N_TITLE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getHP_I_N_LINK()));
                    startActivity(browserIntent);

                }
            });

            holder.HP_I_N_DESCRIPTION.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getHP_I_N_LINK()));
                    startActivity(browserIntent);

                }
            });

            holder.HP_I_N_LINK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getHP_I_N_LINK()));
                    startActivity(browserIntent);

                }
            });

            holder.HP_I_N_IMAGE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getHP_I_N_LINK()));
                    startActivity(browserIntent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }


    private class NewsItemsValues {

        private String HP_I_N_TITLE;
        private String HP_I_N_DESCRIPTION;
        private String HP_I_N_LINK;
        private String HP_I_N_IMAGE;


        NewsItemsValues(String title, String description, String link, String image) {
            HP_I_N_TITLE = title;
            HP_I_N_DESCRIPTION = description;
            HP_I_N_LINK = link;
            HP_I_N_IMAGE = image;
        }

        String getHP_I_N_TITLE() {
            return HP_I_N_TITLE;
        }

        String getHP_I_N_DESCRIPTION() {
            return HP_I_N_DESCRIPTION;
        }

        String getHP_I_N_LINK() {
            return HP_I_N_LINK;
        }

        String getHP_I_N_IMAGE() {
            return HP_I_N_IMAGE;
        }

    }


    private void performSearchFunction(String searchText) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(ET_SEARCH.getWindowToken(), 0);

        ET_SEARCH.setText(searchText);

        searchText = searchText.toLowerCase();

        if (searchText.contains("post on facebook") || searchText.contains("post on twitter")
                || searchText.contains("send an email")) {

            Intent intent = new Intent(HomePage.this, SocialMedia.class);
            startActivity(intent);

            speak("Let's socialize !");

        } else if (searchText.contains("chat") || searchText.contains("message online")) {

            Intent intent = new Intent(HomePage.this, Messenger.class);
            startActivity(intent);

            speak("Let's connect with people !");

        } else if (searchText.contains("weather") || searchText.contains("forecast") || searchText.contains("climate")
                || searchText.contains("temperature") || searchText.contains("humidity")
                || searchText.contains("atmospheric pressure") || searchText.contains("rain")
                || searchText.contains("sunny")) {

            Intent intent = new Intent(HomePage.this, WeatherForecast.class);
            startActivity(intent);

            speak("Showing you the weather forecast !");

        } else if (searchText.contains("maps") || searchText.contains("location") || searchText.contains("route")) {

            Intent intent = new Intent(HomePage.this, Maps.class);
            startActivity(intent);

            speak("Let's find directions !");

        } else if (searchText.contains("call") || searchText.contains("phone") || searchText.contains("dial")) {

            HP_SEARCH_LAYOUT.setVisibility(View.GONE);

            HP_PHONE_MESSAGE.setVisibility(View.VISIBLE);

            speak("Type in the mobile number or contact name !");

            IB_SEARCH_BUTTON_PH_MES.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String phone_number = ET_PH_NO.getText().toString();

                    if (TextUtils.isEmpty(phone_number))
                        ET_PH_NO.setError("Type in the mobile number or contact name !");

                    else {

                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + phone_number));

                        speak("Dialling the number !");

                        HP_PHONE_MESSAGE.setVisibility(View.GONE);

                        ET_SEARCH.setText("");
                        ET_PH_NO.setText("");

                        startActivity(callIntent);

                    }

                }

            });

        } else if (searchText.contains("message") || searchText.contains("text")) {

            HP_SEARCH_LAYOUT.setVisibility(View.GONE);

            HP_PHONE_MESSAGE.setVisibility(View.VISIBLE);

            HP_MESS_LAYOUT.setVisibility(View.VISIBLE);

            speak("Type in the mobile number or contact name and the message !");

            IB_SEARCH_BUTTON_PH_MES.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String phone_number = ET_PH_NO.getText().toString();

                    String message = ET_MESSAGE.getText().toString();

                    if (TextUtils.isEmpty(phone_number))
                        ET_PH_NO.setError("Type in the mobile number or contact name !");

                    else if (TextUtils.isEmpty(phone_number))
                        ET_MESSAGE.setError("Type in the message !");

                    else {

                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone_number));
                        smsIntent.putExtra("sms_body", message);

                        speak("Drafting your message !");

                        HP_PHONE_MESSAGE.setVisibility(View.GONE);

                        ET_SEARCH.setText("");
                        ET_PH_NO.setText("");
                        ET_MESSAGE.setText("");

                        startActivity(smsIntent);

                    }

                }

            });

        } else if (searchText.contains("take a picture") || searchText.contains("take an image")
                || searchText.contains("take a photo")) {

            Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            startActivity(intent);

            speak("Let's take a picture !");

            ET_SEARCH.setText("");

        } else if (searchText.contains("contact developer")) {

            Intent intent = new Intent(HomePage.this, ContactUs.class);
            startActivity(intent);

            speak("Send us a message !");

        } else if (searchText.contains("log out") || searchText.contains("logout")) {

            menu.logout();

            speak("Logging out ! See you soon !");

        } else {

            HP_SEARCH_LAYOUT.setVisibility(View.VISIBLE);

            HP_SEARCH_RESULT_LIST = new ArrayList<>();
            mSearchResultAdapter = new SearchResultAdapter(HP_SEARCH_RESULT_LIST);
            mSearchResultRecyclerView.setAdapter(mSearchResultAdapter);

            speak("Showing results for " + searchText);

            searchText = searchText.replace(" ", "+");

            String GCS_API_KEY = "AIzaSyCu5tAnhBTm-YQ2E_SGP7dgBUeTibFiXIQ";

            String ENGINE_ID = "016079436097315106303:_bnhjpdfili";

            String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchText
                    + "&key=" + GCS_API_KEY + "&cx=" + ENGINE_ID + "&alt=json";

            URL url = null;

            try {

                url = new URL(urlString);

            } catch (MalformedURLException e) {

                Toast.makeText(HomePage.this, "String URL Conversion Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            SearchResultAsyncTask searchTask = new SearchResultAsyncTask();
            searchTask.execute(url);

        }

    }


    @SuppressLint("StaticFieldLeak")
    private class SearchResultAsyncTask extends AsyncTask<URL, Integer, String> {

        private ProgressDialog hp_loading;

        int responseCode;
        String responseMessage;
        String result;

        protected void onPreExecute() {
            super.onPreExecute();
            hp_loading = new ProgressDialog(HomePage.this);
            hp_loading.setMessage("Fetching Search Results ... ");
            hp_loading.setIndeterminate(false);
            hp_loading.setCancelable(true);
            hp_loading.show();

        }

        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[0];

            HttpURLConnection conn = null;

            try {

                conn = (HttpURLConnection) url.openConnection();

            } catch (IOException e) {

                Toast.makeText(HomePage.this, "HTTP Connection Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            try {

                assert conn != null;
                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();

            } catch (IOException e) {

                Toast.makeText(HomePage.this, "HTTP Response Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            try {

                if (responseCode == 200) {

                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = rd.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    rd.close();

                    conn.disconnect();

                    result = sb.toString();

                    return result;

                } else {

                    Toast.makeText(HomePage.this, "HTTP Response Error : " + responseMessage, Toast.LENGTH_LONG).show();
                    result = responseMessage;
                    return result;

                }

            } catch (IOException e) {

                Toast.makeText(HomePage.this, "HTTP Response Error : " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            return null;

        }

        protected void onPostExecute(String result) {

            JSONObject jsonResult;

            String title, description, link;

            try {

                jsonResult = new JSONObject(result);

                JSONArray items = jsonResult.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {

                    JSONObject jsonObject = items.getJSONObject(i);

                    title = jsonObject.getString("title");
                    description = jsonObject.getString("snippet");
                    link = jsonObject.getString("link");

                    SearchResultValues obj = new SearchResultValues(title, description, link);

                    HP_SEARCH_RESULT_LIST.add(obj);

                    mSearchResultAdapter.notifyDataSetChanged();

                }

            } catch (JSONException e) {

                e.printStackTrace();

            }

            hp_loading.dismiss();

        }

    }


    private class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.DataHolder> {

        private ArrayList<SearchResultValues> mDataset;

        class DataHolder extends RecyclerView.ViewHolder {

            TextView HP_I_S_TITLE, HP_I_S_DESCRIPTION, HP_I_S_LINK;

            DataHolder(final View itemView) {
                super(itemView);

                HP_I_S_TITLE = itemView.findViewById(R.id.hp_i_s_title);
                HP_I_S_DESCRIPTION = itemView.findViewById(R.id.hp_i_s_description);
                HP_I_S_LINK = itemView.findViewById(R.id.hp_i_s_link);

            }

        }

        SearchResultAdapter(ArrayList<SearchResultValues> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_hp_item_search, parent, false);

            mSearchResultRecyclerView.setMinimumHeight(mSearchResultRecyclerView.getHeight() + view.getHeight());

            return new DataHolder(view);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(@NonNull final DataHolder holder, @SuppressLint("RecyclerView") final int position) {

            String title, description, link;

            title = mDataset.get(position).getHP_I_S_TITLE();

            description = mDataset.get(position).getHP_I_S_DESCRIPTION();

            link = mDataset.get(position).getHP_I_S_LINK();

            if (!title.equals("null"))
                holder.HP_I_S_TITLE.setText(title);

            else
                holder.HP_I_S_TITLE.setVisibility(View.GONE);

            if (!description.equals("null"))
                holder.HP_I_S_DESCRIPTION.setText(description);

            else
                holder.HP_I_S_DESCRIPTION.setVisibility(View.GONE);

            if (!link.equals("null")) {

                SpannableString underlinedLink = new SpannableString(link);
                underlinedLink.setSpan(new UnderlineSpan(), 0, underlinedLink.length(), 0);

                holder.HP_I_S_LINK.setText(underlinedLink);

            } else
                holder.HP_I_S_LINK.setVisibility(View.GONE);

            holder.HP_I_S_TITLE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getHP_I_S_LINK()));
                    startActivity(browserIntent);

                }
            });

            holder.HP_I_S_DESCRIPTION.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getHP_I_S_LINK()));
                    startActivity(browserIntent);

                }
            });

            holder.HP_I_S_LINK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).getHP_I_S_LINK()));
                    startActivity(browserIntent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }


    private class SearchResultValues {

        private String HP_I_S_TITLE;
        private String HP_I_S_DESCRIPTION;
        private String HP_I_S_LINK;

        SearchResultValues(String title, String description, String link) {
            HP_I_S_TITLE = title;
            HP_I_S_DESCRIPTION = description;
            HP_I_S_LINK = link;
        }

        String getHP_I_S_TITLE() {
            return HP_I_S_TITLE;
        }

        String getHP_I_S_DESCRIPTION() {
            return HP_I_S_DESCRIPTION;
        }

        String getHP_I_S_LINK() {
            return HP_I_S_LINK;
        }

    }


    private void startVoiceInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.hp_how_can));

        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {

            Toast.makeText(HomePage.this, "Your device doesn't support Speech Recognition !", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQ_CODE_SPEECH_INPUT: {

                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    performSearchFunction(result.get(0));

                }

                break;

            }

        }
    }


    private void speak(String text) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

        } else {

            TTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        }
    }


    private void showTime() {

        CountDownTimer newtimer = new CountDownTimer(1000000000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {

                Calendar c = Calendar.getInstance();

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat df = new SimpleDateFormat("HH : mm\n\nEEE, dd MMM yyyy");
                df.setTimeZone(TimeZone.getDefault());


                String currentTime = df.format(c.getTime());

                TV_TIME.setText(currentTime);

            }

            public void onFinish() {
            }
        };

        newtimer.start();

    }


    @Override
    public void onDestroy() {

        if (TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }

        super.onDestroy();

    }

}