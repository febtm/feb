package fmt.febe;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import fmt.febe.helper.ANRAlReceiver;
import fmt.febe.helper.BasicFunctions;
import fmt.febe.helper.Menu;


public class ANR extends AppCompatActivity {


    Switch ALA_VIB_SWITCH;

    ImageButton MENU_BUTTON, ALA_ADD, ALA_LAB_SET, ALA_DUR_MINUS, ALA_DUR_PLUS,
                ALA_SNO_MINUS, ALA_SNO_PLUS, ALA_SET, ALA_CANCEL, NO_ADD, NO_CANCEL, NO_SET, RE_ADD, RE_SET, RE_CANCEL;

    EditText ALA_LAB_TEXT, NO_NOTE, RE_TEXT;

    TimePicker ALA_TIMEPICKER, RE_TIMEPICKER;

    DatePicker RE_DATEPICKER;

    TextView ALA_DUR_TEXT, ALA_SNO_TEXT;

    CheckBox ALA_SUNDAY, ALA_MONDAY, ALA_TUESDAY, ALA_WEDNESDAY, ALA_THURSDAY, ALA_FRIDAY, ALA_SATURDAY;

    LinearLayout LL_AL_SETTINGS, LL_NO_SETTINGS, LL_RE_SETTINGS;

    BasicFunctions.DatabaseHelper mOpenHelper;
    SQLiteDatabase SQL_DB;
    Cursor DB_CURSOR;

    ArrayList<AlarmListValues> ANR_AL_ALARM_LIST;
    RecyclerView mAlarmRecyclerView;
    RecyclerView.Adapter mAlarmAdapter;
    RecyclerView.LayoutManager mAlarmLayoutManager;

    ArrayList<NotesListValues> ANR_NO_NOTES_LIST;
    RecyclerView mNotesRecyclerView;
    RecyclerView.Adapter mNotesAdapter;
    RecyclerView.LayoutManager mNotesLayoutManager;

    ArrayList<RemindersListValues> ANR_RE_REMINDERS_LIST;
    RecyclerView mRemindersRecyclerView;
    RecyclerView.Adapter mRemindersAdapter;
    RecyclerView.LayoutManager mRemindersLayoutManager;

    BasicFunctions basicFunctions;

    private Menu menu;


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr);

        MENU_BUTTON = findViewById(R.id.anr_menu);

        AdView mAdView = findViewById(R.id.anr_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ALA_ADD = findViewById(R.id.anr_al_add_alarm);

        ALA_DUR_MINUS = findViewById(R.id.anr_al_dur_minus);
        ALA_DUR_PLUS = findViewById(R.id.anr_al_dur_plus);

        ALA_LAB_SET = findViewById(R.id.anr_al_set_label);
        ALA_LAB_TEXT = findViewById(R.id.anr_al_label);

        ALA_SNO_MINUS = findViewById(R.id.anr_al_sno_minus);
        ALA_SNO_PLUS = findViewById(R.id.anr_al_sno_plus);

        ALA_VIB_SWITCH = findViewById(R.id.anr_al_vibration_switch);

        ALA_DUR_TEXT = findViewById(R.id.anr_al_dur_time);
        ALA_SNO_TEXT = findViewById(R.id.anr_al_sno_time);

        LL_AL_SETTINGS = findViewById(R.id.anr_al_settings);

        ALA_SET = findViewById(R.id.anr_al_set_alarm);

        ALA_CANCEL = findViewById(R.id.anr_al_cancel_alarm);

        ALA_SUNDAY = findViewById(R.id.anr_al_set_sunday);
        ALA_MONDAY = findViewById(R.id.anr_al_set_monday);
        ALA_TUESDAY = findViewById(R.id.anr_al_set_tuesday);
        ALA_WEDNESDAY = findViewById(R.id.anr_al_set_wednesday);
        ALA_THURSDAY = findViewById(R.id.anr_al_set_thursday);
        ALA_FRIDAY = findViewById(R.id.anr_al_set_friday);
        ALA_SATURDAY = findViewById(R.id.anr_al_set_saturday);

        ALA_TIMEPICKER = findViewById(R.id.anr_al_set_timepicker);

        ALA_TIMEPICKER.setIs24HourView(true);

        NO_ADD = findViewById(R.id.anr_no_add_notes);

        NO_NOTE = findViewById(R.id.anr_no_note);

        LL_NO_SETTINGS = findViewById(R.id.anr_no_settings);

        NO_SET = findViewById(R.id.anr_no_set_note);

        NO_CANCEL = findViewById(R.id.anr_no_cancel_notes);


        RE_ADD = findViewById(R.id.anr_re_add_reminders);

        RE_TEXT = findViewById(R.id.anr_re_reminder);

        LL_RE_SETTINGS = findViewById(R.id.anr_re_settings);

        RE_SET = findViewById(R.id.anr_re_set_reminder);

        RE_CANCEL = findViewById(R.id.anr_re_cancel_reminders);

        RE_TIMEPICKER = findViewById(R.id.anr_re_set_timepicker);

        RE_TIMEPICKER.setIs24HourView(true);

        RE_DATEPICKER = findViewById(R.id.anr_re_set_datepicker);


        basicFunctions = new BasicFunctions(ANR.this);

        mOpenHelper = new BasicFunctions.DatabaseHelper(this);

        menu = new Menu(ANR.this);

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.LeftDrawer.toggleLeftDrawer();

            }
        });

        mAlarmRecyclerView = findViewById(R.id.anr_al_alarm_list);

        mAlarmRecyclerView.setHasFixedSize(true);
        mAlarmLayoutManager = new LinearLayoutManager(this);
        mAlarmRecyclerView.setLayoutManager(mAlarmLayoutManager);
        ANR_AL_ALARM_LIST = new ArrayList<>();
        mAlarmAdapter = new AlarmAdapter(ANR_AL_ALARM_LIST);
        mAlarmRecyclerView.setAdapter(mAlarmAdapter);


        mNotesRecyclerView = findViewById(R.id.anr_no_notes_list);

        mNotesRecyclerView.setHasFixedSize(true);
        mNotesLayoutManager = new LinearLayoutManager(this);
        mNotesRecyclerView.setLayoutManager(mNotesLayoutManager);
        ANR_NO_NOTES_LIST = new ArrayList<>();
        mNotesAdapter = new NotesAdapter(ANR_NO_NOTES_LIST);
        mNotesRecyclerView.setAdapter(mNotesAdapter);


        mRemindersRecyclerView = findViewById(R.id.anr_re_reminders_list);

        mRemindersRecyclerView.setHasFixedSize(true);
        mRemindersLayoutManager = new LinearLayoutManager(this);
        mRemindersRecyclerView.setLayoutManager(mRemindersLayoutManager);
        ANR_RE_REMINDERS_LIST = new ArrayList<>();
        mRemindersAdapter = new RemindersAdapter(ANR_RE_REMINDERS_LIST);
        mRemindersRecyclerView.setAdapter(mRemindersAdapter);

        SQL_DB = mOpenHelper.getWritableDatabase();

        String SQL_ALA_SET_CREATE = "CREATE TABLE IF NOT EXISTS '" + basicFunctions.MY_ALARM_SETTINGS_TABLE + "' ( '"
                + basicFunctions.ALARM_SETTING_NAME + "' TEXT NOT NULL, '"
                + basicFunctions.ALARM_SETTING_VALUE + "' TEXT NOT NULL ) ;";

        SQL_DB.execSQL(SQL_ALA_SET_CREATE);

        String SQL_ALA_SET_SELECT = "SELECT * FROM " + basicFunctions.MY_ALARM_SETTINGS_TABLE;

        DB_CURSOR = SQL_DB.rawQuery(SQL_ALA_SET_SELECT, null);

        if(DB_CURSOR.getCount() > 0) {

            DB_CURSOR.moveToFirst();

            String time;

            if(Integer.parseInt(DB_CURSOR.getString(1)) == 1)
                time = DB_CURSOR.getString(1) + " minute";

            else
                time = DB_CURSOR.getString(1) + " minutes";

            ALA_DUR_TEXT.setText(time);

            DB_CURSOR.moveToNext();

            ALA_LAB_TEXT.setText(DB_CURSOR.getString(1));

            DB_CURSOR.moveToNext();

            if(Integer.parseInt(DB_CURSOR.getString(1)) == 1)
                time = DB_CURSOR.getString(1) + " minute";

            else
                time = DB_CURSOR.getString(1) + " minutes";

            ALA_SNO_TEXT.setText(time);

            DB_CURSOR.moveToNext();

            if(Integer.parseInt(DB_CURSOR.getString(1)) == 0)
                ALA_VIB_SWITCH.setChecked(false);

            else
                ALA_VIB_SWITCH.setChecked(true);

        }

        else {

            String SQL_ALA_SET_INSERT = "INSERT INTO '"
                    + basicFunctions.MY_ALARM_SETTINGS_TABLE + "' ( '" + basicFunctions.ALARM_SETTING_NAME + "', '"
                    + basicFunctions.ALARM_SETTING_VALUE + "' ) VALUES ( 'Alarm_duration', '10' ), ( 'Label', '' )," +
                    " ( 'Snooze_duration', '5' ), ( 'Vibration', '1' ) ;";

            SQL_DB.execSQL(SQL_ALA_SET_INSERT);

            String time = "10 minutes";
            ALA_DUR_TEXT.setText(time);

            time = "5 minutes";
            ALA_SNO_TEXT.setText(time);

            ALA_VIB_SWITCH.setChecked(true);

        }

        DB_CURSOR.close();

        SQL_DB.close();


        SQL_DB = mOpenHelper.getWritableDatabase();

        String SQL_ALA_CREATE = "CREATE TABLE IF NOT EXISTS '" + basicFunctions.MY_ALARMS_TABLE + "' ( '"
                + basicFunctions.ALARM_ID + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_STATUS + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_HOUR + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_MINUTE + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_SUNDAY + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_MONDAY + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_TUESDAY + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_WEDNESDAY + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_THURSDAY + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_FRIDAY + "' INT(11) NOT NULL, '"
                + basicFunctions.ALARM_SATURDAY + "' INT(11) NOT NULL ) ;";

        SQL_DB.execSQL(SQL_ALA_CREATE);

        String SQL_ALA_SELECT = "SELECT * FROM " + basicFunctions.MY_ALARMS_TABLE;

        DB_CURSOR = SQL_DB.rawQuery(SQL_ALA_SELECT, null);

        if(DB_CURSOR.getCount() > 0) {

            DB_CURSOR.moveToFirst();

            do {

                AlarmListValues obj = new AlarmListValues(DB_CURSOR.getInt(0), DB_CURSOR.getInt(1), DB_CURSOR.getInt(2)
                        , DB_CURSOR.getInt(3), DB_CURSOR.getInt(4), DB_CURSOR.getInt(5), DB_CURSOR.getInt(6)
                        , DB_CURSOR.getInt(7), DB_CURSOR.getInt(8), DB_CURSOR.getInt(9), DB_CURSOR.getInt(10));

                ANR_AL_ALARM_LIST.add(obj);

                mAlarmAdapter.notifyDataSetChanged();

            }while(DB_CURSOR.moveToNext());

        }

        else {

            String SQL_ALA_SET_INSERT = "INSERT INTO '"
                    + basicFunctions.MY_ALARMS_TABLE + "' ( '" + basicFunctions.ALARM_ID + "', '"
                    + basicFunctions.ALARM_STATUS + "', '" + basicFunctions.ALARM_HOUR + "', '"
                    + basicFunctions.ALARM_MINUTE + "', '" + basicFunctions.ALARM_SUNDAY + "', '"
                    + basicFunctions.ALARM_MONDAY + "', '" + basicFunctions.ALARM_TUESDAY + "', '"
                    + basicFunctions.ALARM_WEDNESDAY + "', '" + basicFunctions.ALARM_THURSDAY + "', '"
                    + basicFunctions.ALARM_FRIDAY + "', '" + basicFunctions.ALARM_SATURDAY
                    + "' ) VALUES ( 0, 1, " + 6 + ", " + 0 + ", "
                    + 1 + ", " + 1 + ", " + 1 + ", " + 1 + ", " + 1 + ", "
                    + 1 + ", " + 1 + " ) ;";

            SQL_DB.execSQL(SQL_ALA_SET_INSERT);

            basicFunctions.setUpAlarm(0, 6, 0, 1);
            basicFunctions.setUpAlarm(0, 6, 0, 2);
            basicFunctions.setUpAlarm(0, 6, 0, 3);
            basicFunctions.setUpAlarm(0, 6, 0, 4);
            basicFunctions.setUpAlarm(0, 6, 0, 5);
            basicFunctions.setUpAlarm(0, 6, 0, 6);
            basicFunctions.setUpAlarm(0, 6, 0, 7);

        }

        DB_CURSOR.close();

        SQL_DB.close();


        SQL_DB = mOpenHelper.getWritableDatabase();

        String SQL_NO_CREATE = "CREATE TABLE IF NOT EXISTS '" + basicFunctions.MY_NOTES_TABLE + "' ( '"
                + basicFunctions.NOTE_ID + "' INT(11) NOT NULL, '"
                + basicFunctions.NOTE_TEXT + "' TEXT NOT NULL ) ;";

        SQL_DB.execSQL(SQL_NO_CREATE);

        String SQL_NO_SELECT = "SELECT * FROM " + basicFunctions.MY_NOTES_TABLE;

        DB_CURSOR = SQL_DB.rawQuery(SQL_NO_SELECT, null);

        if(DB_CURSOR.getCount() > 0) {

            DB_CURSOR.moveToFirst();

            do {

                NotesListValues obj = new NotesListValues(DB_CURSOR.getInt(0), DB_CURSOR.getString(1));

                ANR_NO_NOTES_LIST.add(obj);

                mNotesAdapter.notifyDataSetChanged();

            }while(DB_CURSOR.moveToNext());

        }

        DB_CURSOR.close();

        SQL_DB.close();


        SQL_DB = mOpenHelper.getWritableDatabase();

        String SQL_RE_CREATE = "CREATE TABLE IF NOT EXISTS '" + basicFunctions.MY_REMINDERS_TABLE + "' ( '"
                + basicFunctions.REMINDER_ID + "' INT(11) NOT NULL, '"
                + basicFunctions.REMINDER_HOUR + "' INT(11) NOT NULL, '"
                + basicFunctions.REMINDER_MINUTE + "' INT(11) NOT NULL, '"
                + basicFunctions.REMINDER_DAY + "' INT(11) NOT NULL, '"
                + basicFunctions.REMINDER_MONTH + "' INT(11) NOT NULL, '"
                + basicFunctions.REMINDER_YEAR + "' INT(11) NOT NULL, '"
                + basicFunctions.REMINDER_TEXT + "' TEXT NOT NULL ) ;";

        SQL_DB.execSQL(SQL_RE_CREATE);

        String SQL_RE_SELECT = "SELECT * FROM " + basicFunctions.MY_REMINDERS_TABLE;

        DB_CURSOR = SQL_DB.rawQuery(SQL_RE_SELECT, null);

        if(DB_CURSOR.getCount() > 0) {

            DB_CURSOR.moveToFirst();

            do {

                RemindersListValues obj = new RemindersListValues(DB_CURSOR.getInt(0),
                        DB_CURSOR.getInt(1), DB_CURSOR.getInt(2), DB_CURSOR.getInt(3), DB_CURSOR.getInt(4),
                        DB_CURSOR.getInt(5), DB_CURSOR.getString(6));

                ANR_RE_REMINDERS_LIST.add(obj);

                mRemindersAdapter.notifyDataSetChanged();

            }while(DB_CURSOR.moveToNext());

        }

        DB_CURSOR.close();

        SQL_DB.close();

        ALA_DUR_MINUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQL_DB = mOpenHelper.getWritableDatabase();

                String SQL_ALA_DUR_SELECT = "SELECT * FROM " + basicFunctions.MY_ALARM_SETTINGS_TABLE;

                DB_CURSOR = SQL_DB.rawQuery(SQL_ALA_DUR_SELECT, null);

                if(DB_CURSOR.getCount() > 0) {

                    DB_CURSOR.moveToFirst();

                    Integer time = Integer.parseInt(DB_CURSOR.getString(1));

                    if(time != 1){

                        if(time == 5)
                            time = 1;

                        else
                            time -= 5;

                        String SQL_ALA_DUR_UPDATE = "UPDATE " + basicFunctions.MY_ALARM_SETTINGS_TABLE
                                + " SET " + basicFunctions.ALARM_SETTING_VALUE + " = " + time
                                + " WHERE " + basicFunctions.ALARM_SETTING_NAME + " = 'Alarm_duration' ;";

                        SQL_DB.execSQL(SQL_ALA_DUR_UPDATE);

                        String ala_dur_text;

                        if(time == 1)
                            ala_dur_text = time + " minute";

                        else
                            ala_dur_text = time + " minutes";

                        ALA_DUR_TEXT.setText(ala_dur_text);

                    }

                    else
                        Toast.makeText(ANR.this, "Not within Alarm range !", Toast.LENGTH_LONG).show();

                    DB_CURSOR.close();

                    SQL_DB.close();

                }

            }
        });


        ALA_DUR_PLUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQL_DB = mOpenHelper.getWritableDatabase();

                String SQL_ALA_DUR_SELECT = "SELECT * FROM " + basicFunctions.MY_ALARM_SETTINGS_TABLE;

                DB_CURSOR = SQL_DB.rawQuery(SQL_ALA_DUR_SELECT, null);

                if(DB_CURSOR.getCount() > 0) {

                    DB_CURSOR.moveToFirst();

                    Integer time = Integer.parseInt(DB_CURSOR.getString(1));

                    if(time != 30){

                        if(time == 1)
                            time = 5;

                        else
                            time += 5;

                        String SQL_ALA_DUR_UPDATE = "UPDATE " + basicFunctions.MY_ALARM_SETTINGS_TABLE
                                + " SET " + basicFunctions.ALARM_SETTING_VALUE + " = " + time
                                + " WHERE " + basicFunctions.ALARM_SETTING_NAME + " = 'Alarm_duration' ;";

                        SQL_DB.execSQL(SQL_ALA_DUR_UPDATE);

                        String ala_dur_text = time + " minutes";

                        ALA_DUR_TEXT.setText(ala_dur_text);

                    }

                    else
                        Toast.makeText(ANR.this, "Not within Alarm range !", Toast.LENGTH_LONG).show();

                    DB_CURSOR.close();

                    SQL_DB.close();

                }

            }
        });


        ALA_LAB_SET.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                String alarm_label = ALA_LAB_TEXT.getText().toString();

                SQL_DB = mOpenHelper.getWritableDatabase();

                String SQL_ALA_DUR_UPDATE = "UPDATE " + basicFunctions.MY_ALARM_SETTINGS_TABLE
                        + " SET " + basicFunctions.ALARM_SETTING_VALUE + " = '" + alarm_label
                        + "' WHERE " + basicFunctions.ALARM_SETTING_NAME + " = 'Label' ;";

                SQL_DB.execSQL(SQL_ALA_DUR_UPDATE);

                if(TextUtils.isEmpty(alarm_label))
                    Toast.makeText(ANR.this, "Your Label has been removed !", Toast.LENGTH_LONG).show();

                else
                    Toast.makeText(ANR.this, "Your Label has been set !", Toast.LENGTH_LONG).show();

                SQL_DB.close();

            }
        });


        ALA_SNO_MINUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQL_DB = mOpenHelper.getWritableDatabase();

                String SQL_ALA_SNO_SELECT = "SELECT * FROM " + basicFunctions.MY_ALARM_SETTINGS_TABLE;

                DB_CURSOR = SQL_DB.rawQuery(SQL_ALA_SNO_SELECT, null);

                if(DB_CURSOR.getCount() > 0) {

                    DB_CURSOR.moveToFirst();

                    DB_CURSOR.moveToNext();

                    DB_CURSOR.moveToNext();

                    Integer time = Integer.parseInt(DB_CURSOR.getString(1));

                    if(time != 1){

                        if(time == 5)
                            time = 1;

                        else
                            time -= 5;

                        String SQL_ALA_SNO_UPDATE = "UPDATE " + basicFunctions.MY_ALARM_SETTINGS_TABLE
                                + " SET " + basicFunctions.ALARM_SETTING_VALUE + " = " + time
                                + " WHERE " + basicFunctions.ALARM_SETTING_NAME + " = 'Snooze_duration' ;";

                        SQL_DB.execSQL(SQL_ALA_SNO_UPDATE);

                        String ala_sno_text;

                        if(time == 1)
                            ala_sno_text = time + " minute";

                        else
                            ala_sno_text = time + " minutes";

                        ALA_SNO_TEXT.setText(ala_sno_text);

                    }

                    else
                        Toast.makeText(ANR.this, "Not within Snooze range !", Toast.LENGTH_LONG).show();

                    DB_CURSOR.close();

                    SQL_DB.close();

                }

            }
        });


        ALA_SNO_PLUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQL_DB = mOpenHelper.getWritableDatabase();

                String SQL_ALA_SNO_SELECT = "SELECT * FROM " + basicFunctions.MY_ALARM_SETTINGS_TABLE;

                DB_CURSOR = SQL_DB.rawQuery(SQL_ALA_SNO_SELECT, null);

                if(DB_CURSOR.getCount() > 0) {

                    DB_CURSOR.moveToFirst();

                    DB_CURSOR.moveToNext();

                    DB_CURSOR.moveToNext();

                    Integer time = Integer.parseInt(DB_CURSOR.getString(1));

                    if(time != 30){

                        if(time == 1)
                            time = 5;

                        else
                            time += 5;

                        String SQL_ALA_SNO_UPDATE = "UPDATE " + basicFunctions.MY_ALARM_SETTINGS_TABLE
                                + " SET " + basicFunctions.ALARM_SETTING_VALUE + " = " + time
                                + " WHERE " + basicFunctions.ALARM_SETTING_NAME + " = 'Snooze_duration' ;";

                        SQL_DB.execSQL(SQL_ALA_SNO_UPDATE);

                        String ala_sno_text = time + " minutes";

                        ALA_SNO_TEXT.setText(ala_sno_text);

                    }

                    else
                        Toast.makeText(ANR.this, "Not within Snooze range !", Toast.LENGTH_LONG).show();

                    DB_CURSOR.close();

                    SQL_DB.close();

                }

            }
        });


        ALA_VIB_SWITCH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SQL_DB = mOpenHelper.getWritableDatabase();

                String SQL_ALA_VIB_UPDATE;

                if(isChecked)
                    SQL_ALA_VIB_UPDATE = "UPDATE " + basicFunctions.MY_ALARM_SETTINGS_TABLE
                            + " SET " + basicFunctions.ALARM_SETTING_VALUE + " = 1 WHERE "
                            + basicFunctions.ALARM_SETTING_NAME + " = 'Vibration' ;";

                else
                    SQL_ALA_VIB_UPDATE = "UPDATE " + basicFunctions.MY_ALARM_SETTINGS_TABLE
                            + " SET " + basicFunctions.ALARM_SETTING_VALUE + " = 0 WHERE "
                            + basicFunctions.ALARM_SETTING_NAME + " = 'Vibration' ;";

                SQL_DB.execSQL(SQL_ALA_VIB_UPDATE);

                SQL_DB.close();

            }
        });


        ALA_ADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ALA_SUNDAY.setChecked(true);
                ALA_MONDAY.setChecked(true);
                ALA_TUESDAY.setChecked(true);
                ALA_WEDNESDAY.setChecked(true);
                ALA_THURSDAY.setChecked(true);
                ALA_FRIDAY.setChecked(true);
                ALA_SATURDAY.setChecked(true);

                LL_AL_SETTINGS.setVisibility(View.VISIBLE);

            }
        });

        ALA_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LL_AL_SETTINGS.setVisibility(View.GONE);

            }
        });


        ALA_SET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer sunday = 0, monday = 0, tuesday = 0, wednesday = 0,
                        thursday = 0, friday = 0, saturday = 0, hour, minute;

                if(ALA_SUNDAY.isChecked())
                    sunday = 1;

                if(ALA_MONDAY.isChecked())
                    monday = 1;

                if(ALA_TUESDAY.isChecked())
                    tuesday = 1;

                if(ALA_WEDNESDAY.isChecked())
                    wednesday = 1;

                if(ALA_THURSDAY.isChecked())
                    thursday = 1;

                if(ALA_FRIDAY.isChecked())
                    friday = 1;

                if(ALA_SATURDAY.isChecked())
                    saturday = 1;


                if(sunday == 0 && monday == 0 && tuesday == 0 && wednesday == 0
                        && thursday == 0 && friday == 0 && saturday == 0)
                    Toast.makeText(ANR.this, "Kindly choose the days !", Toast.LENGTH_LONG).show();

                else {

                    if (Build.VERSION.SDK_INT >= 23) {

                        hour = ALA_TIMEPICKER.getHour();

                        minute = ALA_TIMEPICKER.getMinute();

                    } else {

                        hour = ALA_TIMEPICKER.getCurrentHour();

                        minute = ALA_TIMEPICKER.getCurrentMinute();
                    }

                    SQL_DB = mOpenHelper.getWritableDatabase();

                    String SQL_ALA_COUNT_SELECT = "SELECT COUNT (*) FROM " + basicFunctions.MY_ALARMS_TABLE;

                    DB_CURSOR = SQL_DB.rawQuery(SQL_ALA_COUNT_SELECT, null);

                    int id = 0;

                    if(DB_CURSOR.getCount() > 0){

                        DB_CURSOR.moveToFirst();
                        id = DB_CURSOR.getInt(0);
                    }

                    DB_CURSOR.close();

                    String SQL_ALA_SET_INSERT = "INSERT INTO '"
                            + basicFunctions.MY_ALARMS_TABLE + "' ( '" + basicFunctions.ALARM_ID + "', '"
                            + basicFunctions.ALARM_STATUS + "', '" + basicFunctions.ALARM_HOUR + "', '"
                            + basicFunctions.ALARM_MINUTE + "', '" + basicFunctions.ALARM_SUNDAY + "', '"
                            + basicFunctions.ALARM_MONDAY + "', '" + basicFunctions.ALARM_TUESDAY + "', '"
                            + basicFunctions.ALARM_WEDNESDAY + "', '" + basicFunctions.ALARM_THURSDAY + "', '"
                            + basicFunctions.ALARM_FRIDAY + "', '" + basicFunctions.ALARM_SATURDAY
                            + "' ) VALUES ( " + id + ", 1, " + hour + ", " + minute + ", "
                            + sunday + ", " + monday + ", " + tuesday + ", " + wednesday + ", " + thursday + ", "
                            + friday + ", " + saturday + " ) ;";

                    SQL_DB.execSQL(SQL_ALA_SET_INSERT);

                    AlarmListValues obj = new AlarmListValues(id, 1, hour, minute, sunday,
                            monday, tuesday, wednesday, thursday, friday, saturday);

                    ANR_AL_ALARM_LIST.add(obj);

                    Toast.makeText(ANR.this, "Your Alarm has been added !", Toast.LENGTH_LONG).show();

                    mAlarmAdapter.notifyDataSetChanged();

                    SQL_DB.close();

                    if(sunday == 1)
                        basicFunctions.setUpAlarm(id, hour, minute, 1);

                    if(monday == 1)
                        basicFunctions.setUpAlarm(id, hour, minute, 2);

                    if(tuesday == 1)
                        basicFunctions.setUpAlarm(id, hour, minute, 3);

                    if(wednesday == 1)
                        basicFunctions.setUpAlarm(id, hour, minute, 4);

                    if(thursday == 1)
                        basicFunctions.setUpAlarm(id, hour, minute, 5);

                    if(friday == 1)
                        basicFunctions.setUpAlarm(id, hour, minute, 6);

                    if(saturday == 1)
                        basicFunctions.setUpAlarm(id, hour, minute, 7);

                    LL_AL_SETTINGS.setVisibility(View.GONE);

                }
            }
        });


        NO_ADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LL_NO_SETTINGS.setVisibility(View.VISIBLE);

            }
        });

        NO_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LL_NO_SETTINGS.setVisibility(View.GONE);

            }
        });

        NO_SET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String note_text;

                note_text = NO_NOTE.getText().toString();

                if(TextUtils.isEmpty(note_text))
                    Toast.makeText(ANR.this, "Kindly enter the Note !", Toast.LENGTH_LONG).show();

                else {

                    SQL_DB = mOpenHelper.getWritableDatabase();

                    String SQL_NO_COUNT_SELECT = "SELECT COUNT (*) FROM " + basicFunctions.MY_ALARMS_TABLE;

                    DB_CURSOR = SQL_DB.rawQuery(SQL_NO_COUNT_SELECT, null);

                    int id = 0;

                    if(DB_CURSOR.getCount() > 0){

                        DB_CURSOR.moveToFirst();
                        id = DB_CURSOR.getInt(0);
                    }

                    DB_CURSOR.close();

                    String SQL_NOTE_SET_INSERT = "INSERT INTO '"
                            + basicFunctions.MY_NOTES_TABLE + "' ( '" + basicFunctions.NOTE_ID + "', '"
                            + basicFunctions.NOTE_TEXT + "' ) VALUES ( " + id + ", '" + note_text + "' ) ;";

                    SQL_DB.execSQL(SQL_NOTE_SET_INSERT);

                    NotesListValues obj = new NotesListValues(id, note_text);

                    ANR_NO_NOTES_LIST.add(obj);

                    Toast.makeText(ANR.this, "Your Note has been added !", Toast.LENGTH_LONG).show();

                    mNotesAdapter.notifyDataSetChanged();

                    SQL_DB.close();

                    LL_NO_SETTINGS.setVisibility(View.GONE);

                }
            }
        });


        RE_ADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LL_RE_SETTINGS.setVisibility(View.VISIBLE);

            }
        });

        RE_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LL_RE_SETTINGS.setVisibility(View.GONE);

            }
        });

        RE_SET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reminder_text;

                reminder_text = RE_TEXT.getText().toString();

                Integer hour, minute, day, month, year;

                if (Build.VERSION.SDK_INT >= 23) {

                    hour = RE_TIMEPICKER.getHour();

                    minute = RE_TIMEPICKER.getMinute();

                } else {

                    hour = RE_TIMEPICKER.getCurrentHour();

                    minute = RE_TIMEPICKER.getCurrentMinute();

                }

                day = RE_DATEPICKER.getDayOfMonth();

                month = RE_DATEPICKER.getMonth();

                month = month + 1;

                year = RE_DATEPICKER.getYear();

                if(TextUtils.isEmpty(reminder_text))
                    Toast.makeText(ANR.this, "Kindly enter the Reminder !", Toast.LENGTH_LONG).show();

                else {

                    SQL_DB = mOpenHelper.getWritableDatabase();

                    String SQL_RE_COUNT_SELECT = "SELECT COUNT (*) FROM " + basicFunctions.MY_REMINDERS_TABLE;

                    DB_CURSOR = SQL_DB.rawQuery(SQL_RE_COUNT_SELECT, null);

                    int id = 0;

                    if(DB_CURSOR.getCount() > 0){

                        DB_CURSOR.moveToFirst();
                        id = DB_CURSOR.getInt(0);
                    }

                    DB_CURSOR.close();

                    String SQL_RE_SET_INSERT = "INSERT INTO '"
                            + basicFunctions.MY_REMINDERS_TABLE + "' ( '" + basicFunctions.REMINDER_ID + "', '"
                            + basicFunctions.REMINDER_HOUR + "', '" + basicFunctions.REMINDER_MINUTE + "', '"
                            + basicFunctions.REMINDER_DAY + "', '" + basicFunctions.REMINDER_MONTH + "', '"
                            + basicFunctions.REMINDER_YEAR + "', '"
                            + basicFunctions.REMINDER_TEXT + "' ) VALUES ( " + id + ", " + hour + ", " +
                            minute + ", " + day + ", " + month + ", " + year + ", '" + reminder_text + "' ) ;";

                    SQL_DB.execSQL(SQL_RE_SET_INSERT);

                    RemindersListValues obj = new RemindersListValues(id, hour, minute, day, month, year, reminder_text);

                    ANR_RE_REMINDERS_LIST.add(obj);

                    Toast.makeText(ANR.this, "Your Reminder has been added !", Toast.LENGTH_LONG).show();

                    mRemindersAdapter.notifyDataSetChanged();

                    basicFunctions.setUpReminder(id, hour, minute, day, (month - 1), year, reminder_text);

                    SQL_DB.close();

                    LL_RE_SETTINGS.setVisibility(View.GONE);

                }
            }
        });

    }


    private class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.DataHolder> {

        private ArrayList<AlarmListValues> mDataset;

        class DataHolder extends RecyclerView.ViewHolder {

            ImageView ANR_AL_STATUS;
            TextView ANR_AL_TIME, ANR_AL_DAYS;
            Switch ANR_AL_SWITCH;
            ImageButton ANR_AL_DELETE;

            DataHolder(final View itemView) {
                super(itemView);

                ANR_AL_STATUS = itemView.findViewById(R.id.anr_al_it_status);
                ANR_AL_TIME = itemView.findViewById(R.id.anr_al_it_time);
                ANR_AL_DAYS = itemView.findViewById(R.id.anr_al_it_days);
                ANR_AL_SWITCH = itemView.findViewById(R.id.anr_al_it_switch);
                ANR_AL_DELETE = itemView.findViewById(R.id.anr_al_it_delete);

            }

        }

        AlarmAdapter(ArrayList<AlarmListValues> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_anr_alarm_item, parent, false);

            mAlarmRecyclerView.setMinimumHeight(mAlarmRecyclerView.getHeight() + view.getHeight());

            return new DataHolder(view);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(@NonNull final DataHolder holder, @SuppressLint("RecyclerView") final int position) {

            if(mDataset.get(holder.getAdapterPosition()).getANR_AL_STATUS() == 0){

                holder.ANR_AL_STATUS.setBackgroundResource(R.drawable.anr_alarm_off);
                holder.ANR_AL_SWITCH.setChecked(false);

            }

            else {

                holder.ANR_AL_STATUS.setBackgroundResource(R.drawable.anr_alarm_on);
                holder.ANR_AL_SWITCH.setChecked(true);

            }

            @SuppressLint("DefaultLocale")
            String time = String.format("%02d", mDataset.get(position).getANR_AL_HOUR()) +
                            " : " + String.format("%02d", mDataset.get(position).getANR_AL_MINUTE());

            holder.ANR_AL_TIME.setText(time);

            String days = "";

            Integer sunday, monday, tuesday, wednesday, thursday, friday, saturday;

            sunday = mDataset.get(position).getANR_AL_SUNDAY();
            monday = mDataset.get(position).getANR_AL_MONDAY();
            tuesday = mDataset.get(position).getANR_AL_TUESDAY();
            wednesday = mDataset.get(position).getANR_AL_WEDNESDAY();
            thursday = mDataset.get(position).getANR_AL_THURSDAY();
            friday = mDataset.get(position).getANR_AL_FRIDAY();
            saturday = mDataset.get(position).getANR_AL_SATURDAY();

            if(sunday == 1 && monday == 1 && tuesday == 1 && wednesday == 1
                    && thursday == 1 && friday == 1 && saturday == 1)
                days = "Everyday";

            else {

                if (sunday == 1)
                    days += "Sun ";

                if (monday == 1)
                    days += "Mon ";

                if (tuesday == 1)
                    days += "Tue ";

                if (wednesday == 1)
                    days += "Wed ";

                if (thursday == 1)
                    days += "Thu ";

                if (friday == 1)
                    days += "Fri ";

                if (saturday == 1)
                    days += "Sat";

            }

            holder.ANR_AL_DAYS.setText(days);

            holder.ANR_AL_DELETE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:

                                    Integer id, identifier, hour, minute, sunday, monday, tuesday, wednesday, thursday, friday, saturday;

                                    SQL_DB = mOpenHelper.getReadableDatabase();

                                    id = mDataset.get(position).getANR_AL_ID();
                                    hour = mDataset.get(position).getANR_AL_HOUR();
                                    minute = mDataset.get(position).getANR_AL_MINUTE();

                                    sunday = mDataset.get(position).getANR_AL_SUNDAY();
                                    monday = mDataset.get(position).getANR_AL_MONDAY();
                                    tuesday = mDataset.get(position).getANR_AL_TUESDAY();
                                    wednesday = mDataset.get(position).getANR_AL_WEDNESDAY();
                                    thursday = mDataset.get(position).getANR_AL_THURSDAY();
                                    friday = mDataset.get(position).getANR_AL_FRIDAY();
                                    saturday = mDataset.get(position).getANR_AL_SATURDAY();

                                    String SQL_SELECT = "SELECT * FROM " + basicFunctions.MY_ALARMS_TABLE + " WHERE "
                                            + basicFunctions.ALARM_ID + " = " + id;

                                    DB_CURSOR = SQL_DB.rawQuery(SQL_SELECT, null);

                                    DB_CURSOR.moveToFirst();

                                    SQLiteDatabase db = mOpenHelper.getWritableDatabase();

                                    db.delete(basicFunctions.MY_ALARMS_TABLE, " " + basicFunctions.ALARM_ID + " = '" + id + "' " +
                                            "AND " + basicFunctions.ALARM_HOUR + " = '" + hour + "' " +
                                            "AND " + basicFunctions.ALARM_MINUTE + " = '" + minute + "'", null);

                                    Toast.makeText(ANR.this, "Your Alarm has been removed !", Toast.LENGTH_LONG).show();

                                    ANR_AL_ALARM_LIST.remove(position);

                                    mAlarmAdapter.notifyDataSetChanged();

                                    if(mAlarmAdapter.getItemCount() == 0)
                                        Toast.makeText(ANR.this, "Your Alarm List is empty !", Toast.LENGTH_LONG).show();

                                    if(sunday == 1) {

                                        identifier = Integer.parseInt(String.valueOf(1) + "" + String.valueOf(id));

                                        AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                                        PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                                        if (notAlarmManager != null) {
                                            notAlarmManager.cancel(notPendingIntent);
                                        }
                                        notPendingIntent.cancel();

                                    }

                                    if(monday == 1) {

                                        identifier = Integer.parseInt(String.valueOf(2) + "" + String.valueOf(id));

                                        AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                                        PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                                        if (notAlarmManager != null) {
                                            notAlarmManager.cancel(notPendingIntent);
                                        }
                                        notPendingIntent.cancel();

                                    }

                                    if(tuesday == 1) {

                                        identifier = Integer.parseInt(String.valueOf(3) + "" + String.valueOf(id));

                                        AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                                        PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                                        if (notAlarmManager != null) {
                                            notAlarmManager.cancel(notPendingIntent);
                                        }
                                        notPendingIntent.cancel();

                                    }

                                    if(wednesday == 1) {

                                        identifier = Integer.parseInt(String.valueOf(4) + "" + String.valueOf(id));

                                        AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                                        PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                                        if (notAlarmManager != null) {
                                            notAlarmManager.cancel(notPendingIntent);
                                        }
                                        notPendingIntent.cancel();

                                    }

                                    if(thursday == 1) {

                                        identifier = Integer.parseInt(String.valueOf(5) + "" + String.valueOf(id));

                                        AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                                        PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                                        if (notAlarmManager != null) {
                                            notAlarmManager.cancel(notPendingIntent);
                                        }
                                        notPendingIntent.cancel();

                                    }

                                    if(friday == 1) {

                                        identifier = Integer.parseInt(String.valueOf(6) + "" + String.valueOf(id));

                                        AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                                        PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                                        if (notAlarmManager != null) {
                                            notAlarmManager.cancel(notPendingIntent);
                                        }
                                        notPendingIntent.cancel();

                                    }

                                    if(saturday == 1) {

                                        identifier = Integer.parseInt(String.valueOf(7) + "" + String.valueOf(id));

                                        AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                                        PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                                        if (notAlarmManager != null) {
                                            notAlarmManager.cancel(notPendingIntent);
                                        }
                                        notPendingIntent.cancel();

                                    }

                                    DB_CURSOR.close();

                                    SQL_DB.close();

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    Toast.makeText(ANR.this, "Your Alarm has not been removed !", Toast.LENGTH_LONG).show();

                                    dialog.dismiss();

                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ANR.this);
                    builder.setMessage("Are you sure you want to remove your Alarm ?")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            });

            holder.ANR_AL_SWITCH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    SQL_DB = mOpenHelper.getWritableDatabase();

                    String SQL_ALA_LIST_UPDATE;

                    Integer id, identifier, hour, minute, sunday, monday, tuesday, wednesday, thursday, friday, saturday;

                    id = mDataset.get(position).getANR_AL_ID();
                    hour = mDataset.get(position).getANR_AL_HOUR();
                    minute = mDataset.get(position).getANR_AL_MINUTE();

                    sunday = mDataset.get(position).getANR_AL_SUNDAY();
                    monday = mDataset.get(position).getANR_AL_MONDAY();
                    tuesday = mDataset.get(position).getANR_AL_TUESDAY();
                    wednesday = mDataset.get(position).getANR_AL_WEDNESDAY();
                    thursday = mDataset.get(position).getANR_AL_THURSDAY();
                    friday = mDataset.get(position).getANR_AL_FRIDAY();
                    saturday = mDataset.get(position).getANR_AL_SATURDAY();

                    if(isChecked) {

                        SQL_ALA_LIST_UPDATE = "UPDATE " + basicFunctions.MY_ALARMS_TABLE
                                + " SET " + basicFunctions.ALARM_STATUS + " = 1 WHERE "
                                + basicFunctions.ALARM_ID + " = " + id + " ;";

                        holder.ANR_AL_STATUS.setBackgroundResource(R.drawable.anr_alarm_on);

                        if(sunday == 1)
                            basicFunctions.setUpAlarm(id, hour, minute, 1);

                        if(monday == 1)
                            basicFunctions.setUpAlarm(id, hour, minute, 2);

                        if(tuesday == 1)
                            basicFunctions.setUpAlarm(id, hour, minute, 3);

                        if(wednesday == 1)
                            basicFunctions.setUpAlarm(id, hour, minute, 4);

                        if(thursday == 1)
                            basicFunctions.setUpAlarm(id, hour, minute, 5);

                        if(friday == 1)
                            basicFunctions.setUpAlarm(id, hour, minute, 6);

                        if(saturday == 1)
                            basicFunctions.setUpAlarm(id, hour, minute, 7);

                    }

                    else {


                        SQL_ALA_LIST_UPDATE = "UPDATE " + basicFunctions.MY_ALARMS_TABLE
                                + " SET " + basicFunctions.ALARM_STATUS + " = 0 WHERE "
                                + basicFunctions.ALARM_ID + " = " + mDataset.get(position).getANR_AL_ID() + " ;";

                        holder.ANR_AL_STATUS.setBackgroundResource(R.drawable.anr_alarm_off);

                        if(sunday == 1) {

                            identifier = Integer.parseInt(String.valueOf(1) + "" + String.valueOf(id));

                            AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                            PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                            assert notAlarmManager != null;
                            notAlarmManager.cancel(notPendingIntent);
                            notPendingIntent.cancel();

                        }

                        if(monday == 1) {

                            identifier = Integer.parseInt(String.valueOf(2) + "" + String.valueOf(id));

                            AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                            PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                            assert notAlarmManager != null;
                            notAlarmManager.cancel(notPendingIntent);
                            notPendingIntent.cancel();

                        }

                        if(tuesday == 1) {

                            identifier = Integer.parseInt(String.valueOf(3) + "" + String.valueOf(id));

                            AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                            PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                            assert notAlarmManager != null;
                            notAlarmManager.cancel(notPendingIntent);
                            notPendingIntent.cancel();

                        }

                        if(wednesday == 1) {

                            identifier = Integer.parseInt(String.valueOf(4) + "" + String.valueOf(id));

                            AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                            PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                            assert notAlarmManager != null;
                            notAlarmManager.cancel(notPendingIntent);
                            notPendingIntent.cancel();

                        }

                        if(thursday == 1) {

                            identifier = Integer.parseInt(String.valueOf(5) + "" + String.valueOf(id));

                            AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                            PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                            assert notAlarmManager != null;
                            notAlarmManager.cancel(notPendingIntent);
                            notPendingIntent.cancel();

                        }

                        if(friday == 1) {

                            identifier = Integer.parseInt(String.valueOf(6) + "" + String.valueOf(id));

                            AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                            PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                            assert notAlarmManager != null;
                            notAlarmManager.cancel(notPendingIntent);
                            notPendingIntent.cancel();

                        }

                        if(saturday == 1) {

                            identifier = Integer.parseInt(String.valueOf(7) + "" + String.valueOf(id));

                            AlarmManager notAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent notIntent = new Intent(ANR.this, ANRAlReceiver.class);
                            PendingIntent notPendingIntent = PendingIntent.getBroadcast(ANR.this, identifier, notIntent, 0);
                            assert notAlarmManager != null;
                            notAlarmManager.cancel(notPendingIntent);
                            notPendingIntent.cancel();

                        }

                    }

                    SQL_DB.execSQL(SQL_ALA_LIST_UPDATE);

                    SQL_DB.close();


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


    private class AlarmListValues {

        private Integer ANR_AL_ID;
        private Integer ANR_AL_STATUS;
        private Integer ANR_AL_HOUR;
        private Integer ANR_AL_MINUTE;
        private Integer ANR_AL_SUNDAY;
        private Integer ANR_AL_MONDAY;
        private Integer ANR_AL_TUESDAY;
        private Integer ANR_AL_WEDNESDAY;
        private Integer ANR_AL_THURSDAY;
        private Integer ANR_AL_FRIDAY;
        private Integer ANR_AL_SATURDAY;

        AlarmListValues(Integer id, Integer status, Integer hour, Integer minute, Integer sunday,
                        Integer monday, Integer tuesday, Integer wednesday, Integer thursday, Integer friday, Integer saturday){

            ANR_AL_ID = id;
            ANR_AL_STATUS = status;
            ANR_AL_HOUR = hour;
            ANR_AL_MINUTE = minute;
            ANR_AL_SUNDAY = sunday;
            ANR_AL_MONDAY = monday;
            ANR_AL_TUESDAY = tuesday;
            ANR_AL_WEDNESDAY = wednesday;
            ANR_AL_THURSDAY = thursday;
            ANR_AL_FRIDAY = friday;
            ANR_AL_SATURDAY = saturday;

        }

        Integer getANR_AL_ID() {
            return ANR_AL_ID;
        }

        Integer getANR_AL_STATUS() {
            return ANR_AL_STATUS;
        }

        Integer getANR_AL_HOUR() {
            return ANR_AL_HOUR;
        }

        Integer getANR_AL_MINUTE() {
            return ANR_AL_MINUTE;
        }

        Integer getANR_AL_SUNDAY() {
            return ANR_AL_SUNDAY;
        }

        Integer getANR_AL_MONDAY() {
            return ANR_AL_MONDAY;
        }

        Integer getANR_AL_TUESDAY() {
            return ANR_AL_TUESDAY;
        }

        Integer getANR_AL_WEDNESDAY() {
            return ANR_AL_WEDNESDAY;
        }

        Integer getANR_AL_THURSDAY() {
            return ANR_AL_THURSDAY;
        }

        Integer getANR_AL_FRIDAY() {
            return ANR_AL_FRIDAY;
        }

        Integer getANR_AL_SATURDAY() {
            return ANR_AL_SATURDAY;
        }

    }


    private class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.DataHolder> {

        private ArrayList<NotesListValues> mDataset;

        class DataHolder extends RecyclerView.ViewHolder {

            TextView ANR_NO_TEXT;
            ImageButton ANR_NO_DELETE;

            DataHolder(final View itemView) {
                super(itemView);

                ANR_NO_TEXT = itemView.findViewById(R.id.anr_no_it_text);
                ANR_NO_DELETE = itemView.findViewById(R.id.anr_no_it_delete);

            }

        }

        NotesAdapter(ArrayList<NotesListValues> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_anr_note_item, parent, false);

            mNotesRecyclerView.setMinimumHeight(mNotesRecyclerView.getHeight() + view.getHeight());

            return new DataHolder(view);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(@NonNull final DataHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.ANR_NO_TEXT.setText(mDataset.get(position).getANR_NO_TEXT());

            holder.ANR_NO_DELETE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:

                                    Integer id;
                                    String text;

                                    id = mDataset.get(position).getANR_NO_ID();
                                    text = mDataset.get(position).getANR_NO_TEXT();

                                    SQLiteDatabase db = mOpenHelper.getWritableDatabase();

                                    db.delete(basicFunctions.MY_NOTES_TABLE, " " + basicFunctions.NOTE_ID + " = '" + id + "' " +
                                            "AND " + basicFunctions.NOTE_TEXT + " = '" + text  + "'", null);

                                    Toast.makeText(ANR.this, "Your Note has been removed !", Toast.LENGTH_LONG).show();

                                    ANR_NO_NOTES_LIST.remove(position);

                                    mNotesAdapter.notifyDataSetChanged();

                                    if(mNotesAdapter.getItemCount() == 0)
                                        Toast.makeText(ANR.this, "Your Notes List is empty !", Toast.LENGTH_LONG).show();

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    Toast.makeText(ANR.this, "Your Note has not been removed !", Toast.LENGTH_LONG).show();

                                    dialog.dismiss();

                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ANR.this);
                    builder.setMessage("Are you sure you want to remove your Note ?")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

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


    private class NotesListValues {

        private Integer ANR_NO_ID;
        private String ANR_NO_TEXT;

        NotesListValues(Integer id, String text){

            ANR_NO_ID = id;
            ANR_NO_TEXT = text;

        }

        Integer getANR_NO_ID() {
            return ANR_NO_ID;
        }

        String getANR_NO_TEXT() {
            return ANR_NO_TEXT;
        }

    }


    private class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.DataHolder> {

        private ArrayList<RemindersListValues> mDataset;

        class DataHolder extends RecyclerView.ViewHolder {

            TextView ANR_RE_TEXT;
            TextView ANR_RE_TIME;
            ImageButton ANR_RE_DELETE;

            DataHolder(final View itemView) {
                super(itemView);

                ANR_RE_TEXT = itemView.findViewById(R.id.anr_re_it_text);
                ANR_RE_TIME = itemView.findViewById(R.id.anr_re_it_time);
                ANR_RE_DELETE = itemView.findViewById(R.id.anr_re_it_delete);

            }

        }

        RemindersAdapter(ArrayList<RemindersListValues> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_anr_reminder_item, parent, false);

            mRemindersRecyclerView.setMinimumHeight(mRemindersRecyclerView.getHeight() + view.getHeight());

            return new DataHolder(view);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(@NonNull final DataHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.ANR_RE_TEXT.setText(mDataset.get(position).getANR_RE_TEXT());

            String reminder_time = mDataset.get(position).getANR_RE_DAY() + " | "
                    + mDataset.get(position).getANR_RE_MONTH() + " | "
                    + mDataset.get(position).getANR_RE_YEAR() + "\t\t"
                    + mDataset.get(position).getANR_RE_HOUR() + " : "
                    + mDataset.get(position).getANR_RE_MINUTE();

            holder.ANR_RE_TIME.setText(reminder_time);

            holder.ANR_RE_DELETE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:

                                    Integer id, hour, minute, day, month, year;
                                    String text;

                                    id = mDataset.get(position).getANR_RE_ID();
                                    hour = mDataset.get(position).getANR_RE_HOUR();
                                    minute = mDataset.get(position).getANR_RE_MINUTE();
                                    day = mDataset.get(position).getANR_RE_DAY();
                                    month = mDataset.get(position).getANR_RE_MONTH();
                                    year = mDataset.get(position).getANR_RE_YEAR();

                                    text = mDataset.get(position).getANR_RE_TEXT();

                                    SQLiteDatabase db = mOpenHelper.getWritableDatabase();

                                    db.delete(basicFunctions.MY_REMINDERS_TABLE, " " + basicFunctions.REMINDER_ID + " = '" + id + "' " +
                                            "AND " + basicFunctions.REMINDER_HOUR + " = '" + hour + "' " +
                                            "AND " + basicFunctions.REMINDER_MINUTE + " = '" + minute + "' " +
                                            "AND " + basicFunctions.REMINDER_DAY + " = '" + day + "' " +
                                            "AND " + basicFunctions.REMINDER_MONTH + " = '" + month + "' " +
                                            "AND " + basicFunctions.REMINDER_YEAR + " = '" + year + "' " +
                                            "AND " + basicFunctions.REMINDER_TEXT + " = '" + text  + "'", null);

                                    Toast.makeText(ANR.this, "Your Reminder has been removed !", Toast.LENGTH_LONG).show();

                                    ANR_RE_REMINDERS_LIST.remove(position);

                                    mRemindersAdapter.notifyDataSetChanged();

                                    if(mRemindersAdapter.getItemCount() == 0)
                                        Toast.makeText(ANR.this, "Your Reminders List is empty !", Toast.LENGTH_LONG).show();

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    Toast.makeText(ANR.this, "Your Reminder has not been removed !", Toast.LENGTH_LONG).show();

                                    dialog.dismiss();

                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ANR.this);
                    builder.setMessage("Are you sure you want to remove your Reminder ?")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

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


    private class RemindersListValues {

        private Integer ANR_RE_ID, ANR_RE_HOUR, ANR_RE_MINUTE, ANR_RE_DAY, ANR_RE_MONTH, ANR_RE_YEAR;
        private String ANR_RE_TEXT;

        RemindersListValues(Integer id, Integer hour, Integer minute, Integer day, Integer month, Integer year, String text){

            ANR_RE_ID = id;
            ANR_RE_HOUR = hour;
            ANR_RE_MINUTE = minute;
            ANR_RE_DAY = day;
            ANR_RE_MONTH = month;
            ANR_RE_YEAR = year;
            ANR_RE_TEXT = text;

        }

        Integer getANR_RE_ID() {
            return ANR_RE_ID;
        }

        Integer getANR_RE_HOUR() {
            return ANR_RE_HOUR;
        }

        Integer getANR_RE_MINUTE() {
            return ANR_RE_MINUTE;
        }

        Integer getANR_RE_DAY() {
            return ANR_RE_DAY;
        }

        Integer getANR_RE_MONTH() {
            return ANR_RE_MONTH;
        }

        Integer getANR_RE_YEAR() {
            return ANR_RE_YEAR;
        }

        String getANR_RE_TEXT() {
            return ANR_RE_TEXT;
        }

    }


}