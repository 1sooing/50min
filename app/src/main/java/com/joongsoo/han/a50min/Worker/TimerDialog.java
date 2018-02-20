package com.joongsoo.han.a50min.Worker;

/**
 * Created by joongsoo on 2016-12-07.
 */
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joongsoo.han.a50min.MainActivity;
import com.joongsoo.han.a50min.R;

import java.util.Timer;
import java.util.TimerTask;

public class TimerDialog extends Dialog{

    private static Intent gotoHome;

    private final String TAG = "50_TIMERDIALOG";
    private final boolean D = false;

    private TextView TV_TIMER_TITLE;
    private TextView TV_TIMER_SEC;
    private ImageView mContentView;
    private ImageView mAlarmView;
    private ImageView mTrayView;
    private Button mLeftButton;
    private Button mRightButton;


    private boolean IF_NEW = true;
    private boolean ALARM_ENDED = false;
    Timer timer;
    TimerTask timerTask;

    private String TITLE;
    private int TOTAL;
    private int SEC;
    private int DIS_MIN;
    private int DIS_SEC;
    private static int POSITION;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;
    private View.OnClickListener mAlarmClickListener;
    private View.OnClickListener mTrayClickListener;


    public void setTimer(int position, int sec, String title, int total) {

        ALARM_ENDED = false;
        IF_NEW = true;

        this.TITLE = title;
        this.SEC = sec;
        this.POSITION = position;
        this.TOTAL = total;
        DIS_MIN = SEC / 60;
        DIS_SEC = SEC - 60 * DIS_MIN;

        if (D) Log.d(TAG, "setTimer(): POSITION: " +  POSITION + ", TITLE: " + TITLE + ", SEC: " + SEC);

        if(sec <= 0) {
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContentView.setImageResource(R.drawable.item_menu_timer_over);
                    TV_TIMER_TITLE.setText(TITLE);
                    TV_TIMER_SEC.setText("00  :  00");
                    mLeftButton.setText("START");
                    mLeftButton.setOnClickListener(null);
                }
            });
        } else {
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    TV_TIMER_TITLE.setText(TITLE);
                    mLeftButton.setText("START");
                    TV_TIMER_SEC.setText(String.format("%02d", DIS_MIN) + "  :  " + String.format("%02d",DIS_SEC));

                    // ALREADY DONE
                    if(SEC <= 0)
                        mContentView.setImageResource(R.drawable.item_menu_timer_over);
                        // DONE OVER 7/8
                    else if (SEC * 8 < TOTAL)
                        mContentView.setImageResource(R.drawable.item_menu_timer_7octa);
                        // DONE OVER 3/4
                    else if (SEC * 4 < TOTAL)
                        mContentView.setImageResource(R.drawable.item_menu_timer_3quarter);
                        // DONE OVER 5/8
                    else if (SEC * 8 < TOTAL * 3)
                        mContentView.setImageResource(R.drawable.item_menu_timer_5octa);
                        // DONE OVER 1/2
                    else if (SEC * 2 < TOTAL)
                        mContentView.setImageResource(R.drawable.item_menu_timer_2quarter);
                        // DONE OVER 3/8
                    else if (SEC * 8 < TOTAL * 5)
                        mContentView.setImageResource(R.drawable.item_menu_timer_3octa);
                        // DONE OVER 1/4
                    else if (SEC * 4 < TOTAL * 3)
                        mContentView.setImageResource(R.drawable.item_menu_timer_1quarter);
                        // DONE OVER 1/8
                    else if (SEC * 8 < TOTAL * 7)
                        mContentView.setImageResource(R.drawable.item_menu_timer_1octa);
                        // LESS THAN 1/8 ALMOST 0
                    else
                        mContentView.setImageResource(R.drawable.item_menu_timer);
                }
            });
        }
    }

    public void startTimer() {
        IF_NEW = false;
        timer = new Timer();

        MainActivity.getInstance().holdLock();

        timerTask = new TimerTask() {
            public void run(){

                // CALCULATE REMAIN
                DIS_MIN = SEC / 60;
                DIS_SEC = SEC - 60 * DIS_MIN;

                // if timer ended
                if(SEC < 0) {
                    if(D)Log.d(TAG, "startTimer().run().return");
                    MainActivity.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TV_TIMER_SEC.setText("00  :  00");
                            mLeftButton.setText("STOP");
                            mContentView.setImageResource(R.drawable.item_menu_timer_over);
                        }
                    });
                    timerTask.cancel();
                    completeTimer();

                    return;
                }

                // DISPLAY TIMER
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(D)Log.d(TAG,"startTimer().run()");
                        TV_TIMER_SEC.setText(String.format("%02d", DIS_MIN) + "  :  " + String.format("%02d",DIS_SEC));

                        // ALREADY DONE
                        if(SEC <= 0)
                            mContentView.setImageResource(R.drawable.item_menu_timer_over);
                            // DONE OVER 7/8
                        else if (SEC * 8 < TOTAL)
                            mContentView.setImageResource(R.drawable.item_menu_timer_7octa);
                            // DONE OVER 3/4
                        else if (SEC * 4 < TOTAL)
                            mContentView.setImageResource(R.drawable.item_menu_timer_3quarter);
                            // DONE OVER 5/8
                        else if (SEC * 8 < TOTAL * 3)
                            mContentView.setImageResource(R.drawable.item_menu_timer_5octa);
                            // DONE OVER 1/2
                        else if (SEC * 2 < TOTAL)
                            mContentView.setImageResource(R.drawable.item_menu_timer_2quarter);
                            // DONE OVER 3/8
                        else if (SEC * 8 < TOTAL * 5)
                            mContentView.setImageResource(R.drawable.item_menu_timer_3octa);
                            // DONE OVER 1/4
                        else if (SEC * 4 < TOTAL * 3)
                            mContentView.setImageResource(R.drawable.item_menu_timer_1quarter);
                            // DONE OVER 1/8
                        else if (SEC * 8 < TOTAL * 7)
                            mContentView.setImageResource(R.drawable.item_menu_timer_1octa);
                            // LESS THAN 1/8 ALMOST 0
                        else
                            mContentView.setImageResource(R.drawable.item_menu_timer);
                    }
                });
                SEC -= 1;
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public boolean isTimerEnded() {
        return (SEC <= 0);
    }

    public void stopTimer() {
        Log.d(TAG, "stopTimer(): POSITION: " + POSITION + " SEC: " + SEC);
        MainActivity.getInstance().updateDoneOnSheet(POSITION, SEC);
        timer.cancel();
        timerTask.cancel();
        MainActivity.getInstance().releaseLock();
        // sendNotification();
    }

    public void finishTimer() {
        Log.d(TAG, "finishTimer(): POSITION: " + POSITION + " SEC: " + SEC);
        if(!IF_NEW) {
            MainActivity.getInstance().updateDoneOnSheet(POSITION, SEC);
            timer.cancel();
            timerTask.cancel();
        }
        MainActivity.getInstance().releaseLock();
    }

    public void completeTimer() {
        // NOTIFY TO MAIN
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!ALARM_ENDED) {
                    MainActivity.getInstance().timerAlarm();
                    ALARM_ENDED = true;
                }
                MainActivity.getInstance().updateDoneOnSheet(POSITION, 0);
            }
        });
        MainActivity.getInstance().releaseLock();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_timer);
        setLayout();
        setTitle(TITLE);
        setClickListener(mLeftClickListener , mRightClickListener,
                mAlarmClickListener, mTrayClickListener);

        gotoHome = new Intent(Intent.ACTION_MAIN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!IF_NEW) {
            MainActivity.getInstance().updateDoneOnSheet(POSITION, SEC);
            MainActivity.getInstance().releaseLock();
            timer.cancel();
            timerTask.cancel();
        }
    }

    public TimerDialog(Context context , String title,
                       View.OnClickListener leftListener, View.OnClickListener rightListener,
                       View.OnClickListener alarmListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        Log.d(TAG, "TimerDialogConstructor()");
        this.TITLE = title;
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
        this.mAlarmClickListener = alarmListener;
        this.mTrayClickListener = trayClickListener;
    }

    private void setTitle(String title){
        TV_TIMER_TITLE.setText(title);
    }

    private void setClickListener(View.OnClickListener left , View.OnClickListener right,
                                  View.OnClickListener alarm , View.OnClickListener tray) {
        mLeftButton.setOnClickListener(left);
        mRightButton.setOnClickListener(right);
        mAlarmView.setOnClickListener(alarm);
        mTrayView.setOnClickListener(tray);
    }


    // for later implementation for notification event
    private View.OnClickListener trayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gotoHome.addCategory(Intent.CATEGORY_HOME);
            gotoHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getContext().startActivity(gotoHome);
            // sendNotification();
        }
    };

    /*
     * Layout
     */
    private void setLayout(){
        TV_TIMER_TITLE = (TextView) findViewById(R.id.dialog_tv_title);
        TV_TIMER_SEC = (TextView) findViewById(R.id.dialog_timer_tv);
        mContentView = (ImageView) findViewById(R.id.dialog_iv_timer);
        mAlarmView = (ImageView) findViewById(R.id.dialog_iv_alarm);
        mTrayView = (ImageView) findViewById(R.id.dialog_iv_tray);
        mLeftButton = (Button) findViewById(R.id.bt_left);
        mRightButton = (Button) findViewById(R.id.bt_right);
    }
/*
    public void sendNotification(){
        if(D)Log.d(TAG, "sendNotification()");

        mBuilder
                .setSmallIcon(R.drawable.ic_50)
                .setContentTitle("TEST")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
         mBuilder.setContentText("TEST")
                 .setTicker("TEST");

        notificationManager.notify(0, mBuilder.build());
    }
*/
}
