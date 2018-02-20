package com.joongsoo.han.a50min;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.joongsoo.han.a50min.Database.DatabaseOpener;
import com.joongsoo.han.a50min.Worker.TimerDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/*
 * Created by joongsoo on 2016-12-04.
 */
public class MainActivity extends Activity {

    private final int VERSION = 50;
    Boolean D = false;
    String TAG = "50_ACTIVITY_MAIN";

    private static ListView LV_ITEM;
    public static TextView TV_NAME;

    ImageButton IB_NEW, IB_LOAD;
    ImageView IV_YEAR0, IV_YEAR1, IV_YEAR2, IV_YEAR3, IV_SPLIT0, IV_SPLIT1, IV_MONTH0, IV_MONTH1, IV_DAY0, IV_DAY1;

    /*
    MODE SELECT
     */
    private final int MODE_5MIN_10ITEM = 10;
    private final int MODE_10MIN_5ITEM = 5;
    private final int MODE_25MIN_2ITEM = 2;
    private final int MODE_50MIN_1ITEM = 1;
    private final int MODE_CUSTOM = 25;

    private static int MAXIMUM_ID = -1;
    private static int ID = -1;
    public static String NAME = "";
    public static int MODE = -1;
    private static String TODAY = "";
    private static String EXTRA = "";

    private static int CUR_ID = -1;
    private static String CUR_NAME ="";
    private static int CUR_MODE = -1;
    private static String CUR_TODAY = "";
    private static String CUR_EXTRA ="";


    public static ArrayList<mITEM> list = new ArrayList<mITEM>();
    public static MyListAdapter mListAdapter;
    public static TimerDialog mTimerDialog;
    private static boolean TIMER_RUNNING = false;
    private static int TIMER_SOUND_MODE = 0;

    public static DatabaseOpener dbOpenner;
    public static SQLiteDatabase dbSQLite;
    public static Vibrator vibe;
    public static Ringtone r;
    private static InputMethodManager imm;
    private static Uri notification;

    private static PowerManager pm;
    private static PowerManager.WakeLock wl;


    // singleton design
    private static MainActivity instance = new MainActivity();
    public MainActivity() {}

    public static MainActivity getInstance() {
        if ( instance == null )
            instance = new MainActivity();
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        //dropAllSheets();

        initComponentsView();

        setComponents();

        getToday();

        if( loadSheet() )
            LV_ITEM.setBackgroundResource(R.drawable.blank);
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        releaseLock();
    }

    private void initComponentsView(){
        Log.d(TAG, "initComponentsView()");
        setContentView(R.layout.activity_main);

        IV_YEAR0 = (ImageView)findViewById(R.id.iv_main_year0);
        IV_YEAR1 = (ImageView)findViewById(R.id.iv_main_year1);
        IV_YEAR2 = (ImageView)findViewById(R.id.iv_main_year2);
        IV_YEAR3 = (ImageView)findViewById(R.id.iv_main_year3);

        IV_MONTH0 = (ImageView)findViewById(R.id.iv_main_month0);
        IV_MONTH1 = (ImageView)findViewById(R.id.iv_main_month1);

        IV_DAY0 = (ImageView)findViewById(R.id.iv_main_day0);
        IV_DAY1 = (ImageView)findViewById(R.id.iv_main_day1);

        IV_SPLIT0 = (ImageView)findViewById(R.id.iv_main_split0);
        IV_SPLIT1 = (ImageView)findViewById(R.id.iv_main_split1);

        IB_NEW = (ImageButton)findViewById(R.id.btn_main_new);
        IB_LOAD = (ImageButton)findViewById(R.id.btn_main_load);

        TV_NAME = (TextView) findViewById(R.id.tv_main_name);
        LV_ITEM = (ListView) findViewById(R.id.lv_item_menu);

        mTimerDialog = new TimerDialog(MainActivity.this, "TIMER",
                leftClickListener, rightClickListener,
                alarmClickListener);
    }

    private void setComponents() {
        Log.d(TAG, "setComponents()");

        dbOpenner = new DatabaseOpener(getApplicationContext());
        dbSQLite = dbOpenner.getWritableDatabase();
        vibe = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        mListAdapter = new MyListAdapter(getApplication());
        imm = (InputMethodManager)MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

        pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PARTIAL_WAKE_LOCK");

        LV_ITEM.setAdapter(mListAdapter);
        IB_NEW.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == event.ACTION_DOWN) {
                    IB_NEW.setImageResource(R.drawable.set_new_neg);
                    LV_ITEM.setBackgroundResource(R.drawable.blank);

                    DialogFragment mSpinner = MySpinnerNEW.newInstance(0);
                    mSpinner.show(getFragmentManager(), "dialog");
                }

                else if(event.getAction() == event.ACTION_UP) {
                    IB_NEW.setImageResource(R.drawable.set_new);
                }
                return false;
            }
        });

        IB_LOAD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == event.ACTION_DOWN) {
                    IB_LOAD.setImageResource(R.drawable.set_load_neg);

                    DialogFragment mSpinner = MySpinnerModify.newInstance(0);
                    mSpinner.show(getFragmentManager(), "dialog");
                }
                else if(event.getAction() == event.ACTION_UP) {
                    IB_LOAD.setImageResource(R.drawable.set_load);
                }
                return false;
            }
        });
    }

    public void setNewProfile(String name, int mode) {
        if(D)Log.d(TAG,"getNewProfile("+name+", "+mode+")");

        ++MAXIMUM_ID;

        list.clear();
        mListAdapter.removeAllItem();

        int DONE = -1;
        // CUR_ID Only added this time

        CUR_ID = MAXIMUM_ID;
        CUR_NAME = name;
        CUR_MODE = mode;
        //CUR_EXTRA = extra;

        mListAdapter.setNewProfile(mode);

        DONE = 60 * (VERSION / CUR_MODE);
        for(int i = 0; i < CUR_MODE; i++)
            addToList(CUR_MODE, "", DONE, false);

        TV_NAME.setText("Sheet: "+CUR_NAME);
        saveSheet(name, mode);
    }

    public void addToList(int mode, String title, int done, boolean is_type_disabled) {
        list.add(new mITEM(mode, title, done, is_type_disabled));
        mListAdapter.addItem(mode, title, done, is_type_disabled);
    }

    public void updateTodayOnSheet() {
        /* UPDATE [TABLENAME] SET [COLUMN] = '[VALUE]' WHERE ID = 6;
		 *
		 */
        String VALUE_DATE = "_date";
        String VALUE_ID = "_id";
        int NUM_OF_COL = dbOpenner.NUM_COLS;
        int TARGET_VALUE = 60 * (VERSION / CUR_MODE);

        String VALUE3 = Integer.toString(CUR_ID);

        String strQuery = "";
        strQuery += "UPDATE " +  DatabaseOpener.TABLENAME +
                " SET " +
                VALUE_DATE + " = " + "'"+CUR_TODAY+ "', ";

        for(int i = 0; i < CUR_MODE - 1; i++ ) {
            String TARGET_COL = "w";
            TARGET_COL += Integer.toString(2 * i + 1);
            strQuery += (TARGET_COL + " = " + "'" + TARGET_VALUE + "', ");
        }
                strQuery += ("w"+ (2 * CUR_MODE - 1) + " = " + "'"+TARGET_VALUE+ "' " +
                "WHERE " + VALUE_ID + " = " + CUR_ID);

        Log.d(TAG, strQuery);
        dbSQLite.execSQL(strQuery);
        if(D)Log.d(TAG, "updateTodayOnSheet(): (" + CUR_ID + "," + CUR_TODAY + "," + CUR_MODE+"," + CUR_NAME +"," + CUR_EXTRA+")");
        //dbOpenner.close();
        mListAdapter.notifyDataSetChanged();

    }

    public void updateDoneOnSheet(int position, int done) {
        if(D)Log.d(TAG, "updateDoneOnSheet()");

        /* UPDATE [TABLENAME] SET [COLUMN] = '[VALUE]' WHERE ID = 6;
		 *
		 */
        String VALUE0 = "W"+ Integer.toString(2 * position + 1);
        String VALUE1 = Integer.toString(done);
        String VALUE2 = "_id";
        String VALUE3 = Integer.toString(CUR_ID);

        String strQuery = "UPDATE " +  DatabaseOpener.TABLENAME + " SET " + VALUE0 + " = " + "'"+VALUE1+ "' WHERE " + VALUE2 + " = " + VALUE3;

        list.get(position).DONE = done;
        mListAdapter.updateItemDone(position, done);

        dbSQLite.execSQL(strQuery);
        if(D)Log.d(TAG, "updateDoneOnSheet(): (" + CUR_ID + "," + CUR_TODAY + "," + CUR_MODE+"," + CUR_NAME +"," + CUR_EXTRA+")");
        //dbOpenner.close();
    }

    private void getToday() {
        if(D)Log.d(TAG, "getToday()");

        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar c = Calendar.getInstance();

        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = mFormat.format(Double.valueOf(c.get(Calendar.MONTH) + 1));
        String day = mFormat.format(Double.valueOf(c.get(Calendar.DATE)));

        int iyear = Integer.parseInt(year);
        int imonth = Integer.parseInt(month);
        int iday = Integer.parseInt(day);

        int year0, year1, year2, year3;
        int month0, month1;
        int day0, day1;

        year0 = iyear / 1000;
        year1 = (iyear - 1000 * year0) / 100;
        year2 = (iyear - 1000 * year0 - 100 * year1) / 10;
        year3 = (iyear - 1000 * year0 - 100 * year1 - 10 * year2);

        month0 = imonth / 10;
        month1 = imonth - 10 * month0;

        day0 = iday / 10;
        day1 = iday - 10 * day0;

        CUR_TODAY = year + month + day;

        // SET CALENDAR
        intToView(year0, IV_YEAR0);
        intToView(year1, IV_YEAR1);
        intToView(year2, IV_YEAR2);
        intToView(year3, IV_YEAR3);

        intToView(month0, IV_MONTH0);
        intToView(month1, IV_MONTH1);

        intToView(day0, IV_DAY0);
        intToView(day1, IV_DAY1);
    }

    /*
   if load failed, false return false
   make CUR_ID properly
    */
    public void loadOneSheet(int id) {

        list.clear();
        mListAdapter.removeAllItem();

        //dbOpenner = new DatabaseOpener(getApplicationContext());
        // dbOpenner = new DatabaseOpener(this);
        // dbSQLite = dbOpenner.getWritableDatabase();

        /* SELECT *
		 * FROM [TABLENAME] */
        String strQuery = "SELECT * " + "FROM " + DatabaseOpener.TABLENAME + " ORDER BY " + "_id";
        Cursor cur = dbSQLite.rawQuery(strQuery, null);

        if(D)Log.d(TAG, "loadOneSheet: (ID, TODAY, MODE, NAME, EXTRA)");
        while (cur.moveToNext())
        {
            ID = cur.getInt(0);
            TODAY = cur.getString(1);
            MODE = cur.getInt(2);
            NAME= cur.getString(3);
            EXTRA = cur.getString(4);

            if(ID == id)
                break;

        }

        if(CUR_TODAY.equals(TODAY)) {
            for(int i = 5; i < MODE + 5; i++) {
                String name = cur.getString(-5 + 2 * i);
                int done = cur.getInt(-4 + 2 * i);

                if(name.isEmpty())
                    addToList(MODE, name, done, false);
                else
                    addToList(MODE, name, done, true);
            }
        }
        else {
            // initialize daily
            for(int i = 5; i < MODE + 5; i++) {
                String name = cur.getString(-5 + 2 * i);
                int done = 60 * (VERSION / MODE);

                if(name.isEmpty())
                    addToList(MODE, name, done, false);
                else
                    addToList(MODE, name, done, true);
            }
            updateTodayOnSheet();
        }

        // reached ID
        CUR_ID = ID;
        CUR_MODE = MODE;
        CUR_NAME = NAME;
        CUR_EXTRA = EXTRA;

        mListAdapter.setNewProfile(CUR_MODE);
        TV_NAME.setText("Sheet: "+CUR_NAME);

        if(D)Log.d(TAG, "loadOneSheet: (" + CUR_ID + "," + TODAY + "," + CUR_MODE+"," + CUR_NAME +"," + CUR_EXTRA+")");
        cur.close();
        //dbOpenner.close();
    }

    /*
    if load failed, false return false
    make CUR_ID properly
     */
    public boolean loadSheet() {
        if(D)Log.d(TAG, "loadSheet()");

        //dbOpenner = new DatabaseOpener(getApplicationContext());
        // dbOpenner = new DatabaseOpener(this);
        // dbSQLite = dbOpenner.getWritableDatabase();

        /* SELECT *
		 * FROM [TABLENAME] */
        String strQuery = "SELECT * " + "FROM " + DatabaseOpener.TABLENAME + " ORDER BY " + "_id";
        Cursor cur = dbSQLite.rawQuery(strQuery, null);
        boolean is_already = false;
        boolean is_updated = false;

        if(D)Log.d(TAG, "loadSheet: (ID, TODAY, MODE, NAME, EXTRA)");
        while (cur.moveToNext())
        {
            is_already = true;
            list.clear();               // just for init
            mListAdapter.removeAllItem();

            ID = cur.getInt(0);
            TODAY = cur.getString(1);
            MODE = cur.getInt(2);
            NAME= cur.getString(3);
            EXTRA = cur.getString(4);

            CUR_ID = ID;
            CUR_NAME = NAME;
            CUR_MODE = MODE;

            if(D)Log.d(TAG, "loadingSheet: (" + ID + "," + TODAY + "," + MODE+"," + NAME +"," + EXTRA+")");

            if(CUR_TODAY.equals(TODAY)) {
                continue;
            }

            // initialize daily
            else {
                updateTodayOnSheet();
                is_updated = true;
            }
       }

        if(!is_already)
            return false;

        if(D)Log.d(TAG, "loadedSheet: (" + ID + "," + TODAY + "," + MODE+"," + NAME +"," + EXTRA+")");

        // reached latest ID
        // CUR_TODAY is already set
        // but need to refresh cur imformation
        cur = dbSQLite.rawQuery(strQuery, null);

        MAXIMUM_ID = ID;
        CUR_ID = ID;
        CUR_MODE = MODE;
        CUR_NAME = NAME;
        CUR_EXTRA = EXTRA;

        mListAdapter.setNewProfile(CUR_MODE);
        TV_NAME.setText("Sheet: " + CUR_NAME);
        while (cur.moveToNext()) {
            ;
        }
        cur.moveToPrevious();

        for(int i = 5; i < MODE + 5; i++) {
            String name = cur.getString(-5 + 2 * i);
            int done =  cur.getInt(-4 + 2 * i);

            if(name.isEmpty())
                addToList(MODE, name, done, false);
            else
                addToList(MODE, name, done, true);
        }

        cur.close();
        //dbOpenner.close();
        return is_already;
    }

    public void updateTitleOnSheet(int position, String title) {
        if(D)Log.d(TAG, "updateTitleOnSheet()");

        /* UPDATE [TABLENAME] SET [COLUMN] = '[VALUE]' WHERE ID = 6;
		 *
		 */
        String VALUE0 = "W"+ Integer.toString(2 * position);
        String VALUE1 = title;
        String VALUE2 = "_id";
        String VALUE3 = Integer.toString(CUR_ID);

        int num_cols = DatabaseOpener.NUM_COLS;
        String strQuery = "UPDATE " +  DatabaseOpener.TABLENAME + " SET " + VALUE0 + " = " + "'"+VALUE1+ "' WHERE " + VALUE2 + " = " + VALUE3;

        list.get(position).TITLE = title;

        dbSQLite.execSQL(strQuery);
        if(D)Log.d(TAG, "updateTitleOnSheet(): (" + CUR_ID + "," + CUR_TODAY + "," + CUR_MODE+"," + CUR_NAME +"," + CUR_EXTRA+")");
        //dbOpenner.close();
    }

    public void saveSheet(String name, int mode) {

        /* INSERT INTO [TABLENAME]
		 * VALUES([VALUE0], [VALUE1], [VALUE2], [VALUE3])
		 *
		 * _id, _date, mode, name, extra
		 *  [contents]
		 * */
        int num_cols = DatabaseOpener.NUM_COLS;
        String strQuery = "INSERT INTO " +  DatabaseOpener.TABLENAME + " " + "VALUES ('" + MAXIMUM_ID + "', '" + CUR_TODAY + "', '" + CUR_MODE  + "', '" + CUR_NAME  + "', '" + CUR_EXTRA  + "', '" ;

        String default_title= "";
        String get_title = " ";
        int get_done = -1;

        for(int i = 0; i < CUR_MODE; i++) {
            /*
            get_title = mListAdapter.getItem(i).TITLE;
            get_done = mListAdapter.getItem(i).DONE;

            // local list refresh
            list.get(i).TITLE = get_title;
            list.get(i).DONE = get_done;
*/
            strQuery += list.get(i).TITLE + "', '" + list.get(i).DONE + "', '";
        }

        for(int i = 0; i < num_cols - 2 * CUR_MODE - 6; i++)
            strQuery += default_title + "', '";

        strQuery += default_title;
        strQuery += "');";

        // Toast.makeText(this, "SAVED " + TODAY, Toast.LENGTH_SHORT).show();
        //dbOpenner = new DatabaseOpener(this);
        //dbSQLite = dbOpenner.getWritableDatabase();
        dbSQLite.execSQL(strQuery);
        if(D)Log.d(TAG, "saveSheet: (" + MAXIMUM_ID + "," + CUR_TODAY + "," + CUR_MODE+"," + CUR_NAME +"," + CUR_EXTRA+")");
        //dbOpenner.close();
    }

    private void dropAllSheets() {
        dbOpenner = new DatabaseOpener(getApplicationContext());
        dbOpenner.dropDatabase(getApplicationContext());
        dbOpenner.close();
        list.clear();
        mListAdapter.removeAllItem();
        Toast.makeText(getApplication(), "FLUSHED", Toast.LENGTH_SHORT).show();
    }

    public void deleteOneSheet(int id) {

        if(id == -92) {
            Log.d(TAG, "deleteOneSheet: No Existing Sheet");
            TV_NAME.setText("No Existing Sheet");
            LV_ITEM.setBackgroundResource(R.drawable.worksheet_not_exist);
            return;
        }

        String strQueryD = "DELETE FROM " + DatabaseOpener.TABLENAME + " WHERE _id =" + id +";";
        dbSQLite.execSQL(strQueryD);
        if(D)Log.d(TAG, "deleteOneSheet: (id: " + id +")");
        if(D)Log.d(TAG, "deleteOneSheet: (CUR_ID: " + CUR_ID+")");

        if (CUR_ID == id) {
            list.clear();
            mListAdapter.removeAllItem();
            loadSheet();
        }
    }

    private View intToView(int n, ImageView view) {
        switch(n) {
            case 0:
                view.setImageResource(R.drawable.ss0);
                break;
            case 1:
                view.setImageResource(R.drawable.ss1);
                break;
            case 2:
                view.setImageResource(R.drawable.ss2);
                break;
            case 3:
                view.setImageResource(R.drawable.ss3);
                break;
            case 4:
                view.setImageResource(R.drawable.ss4);
                break;
            case 5:
                view.setImageResource(R.drawable.ss5);
                break;
            case 6:
                view.setImageResource(R.drawable.ss6);
                break;
            case 7:
                view.setImageResource(R.drawable.ss7);
                break;
            case 8:
                view.setImageResource(R.drawable.ss8);
                break;
            case 9:
                view.setImageResource(R.drawable.ss9);
                break;
            // 10 refers to space
            case 10:
                view.setImageResource(R.drawable.ssspace);
                break;
            // 11 refers to hyphen
            case 11:
                view.setImageResource(R.drawable.ssline);
                break;
            default:
                break;
        }
        return view;
    }

    /*
    show timer
    @params : timer mode
     */
    public boolean setTimer(int position) {
        int sec = list.get(position).DONE;
        String title = list.get(position).TITLE;
        int total = 60 * (VERSION / CUR_MODE);

        if(D)Log.d(TAG, "setTimer(" + position + ", " + sec + ", " + title + ")");

        // TIMER_SOUND_MODE = 0;
        TIMER_RUNNING = false;

        mTimerDialog.setTimer(position, sec, title, total);
        mTimerDialog.show();
        return false;
    }

 /*
ALARM
0: vibration
1: ring
2: silent

VIBRATE
long[] pattern = {1000, 200, 1000, 2000, 1200};       // vibe pattern of vibration intensity
vibe.vibrate(pattern, 0);                             // pattern and times
 */
    public void timerAlarm() {

        if(TIMER_SOUND_MODE == 0) {
            vibe.vibrate(2100);
            return;
        }
        else if(TIMER_SOUND_MODE == 1) {
            r.play();
            return;
        }
        else {
            return;
        }
    }

    /*
    public void sendNotification(boolean is_doing){
        if(D)Log.d(TAG, "sendNotification()");

        mBuilder
                .setSmallIcon(R.drawable.ic_50)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if(is_doing) {
            mBuilder.setContentText(message_doing)
            .setTicker(message_doing);
        }
        else {
            mBuilder.setContentText(message_result)
                    .setTicker(message_result);
        }
        notificationManager.notify(0, mBuilder.build());
    }
*/

    public void hideSoftKeyboard(View position) {
        imm.hideSoftInputFromWindow(position.getWindowToken(), 0);
    }

    /*
    ALARM
    0: vibration
    1: ring
    2: silent
     */
    private View.OnClickListener alarmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d(TAG, "TIMER_ONCLICK: TIMER_RUNNING:" + TIMER_RUNNING);

            if(TIMER_SOUND_MODE == 0) {
                // 0: vibration
                r.play();
                ((ImageView)v).setImageResource(R.drawable.timer_ring);

            }
            else if(TIMER_SOUND_MODE == 1){
                // 1: ring
                ((ImageView)v).setImageResource(R.drawable.timer_silent);
            }
            else {
                // 2: silent
                vibe.vibrate(400);
                ((ImageView) v).setImageResource(R.drawable.timer_vib);
            }
            TIMER_SOUND_MODE = (TIMER_SOUND_MODE+1) % 3;

        }
    };

    // START / STOP
    private View.OnClickListener leftClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d(TAG, "TIMER_ONCLICK: TIMER_RUNNING:" + TIMER_RUNNING);

            if(!TIMER_RUNNING) {
                // TIMER START
                TIMER_RUNNING = true;
                ((Button)v).setText("STOP");
                mTimerDialog.startTimer();
            }
            else {
                // TIMER STOP
                TIMER_RUNNING = false;
                if(!mTimerDialog.isTimerEnded())
                    ((Button)v).setText("START");
                mTimerDialog.stopTimer();
            }
        }
    };

    private View.OnClickListener rightClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TIMER FINISHED
            ((Button)v).setText("FINISH");
            mTimerDialog.finishTimer();
            mTimerDialog.dismiss();
        }
    };

    public void holdLock(){
        Log.d(TAG, "holdLock()");
        wl.acquire();
    }
    public void releaseLock() {
        Log.d(TAG, "releaseLock()");
        if(wl.isHeld())
            wl.release();
    }
}