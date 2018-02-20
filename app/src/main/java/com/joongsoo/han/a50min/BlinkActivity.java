package com.joongsoo.han.a50min;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class BlinkActivity extends Activity {

    Boolean D = false;
    String TAG = "50_ACTIVITY_BLINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D)
            Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_blink);

        final Handler handler = new Handler();
        final Runnable doNextActivity = new Runnable() {
            @Override
            public void run() {
                // Intent to jump to the next activity
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish(); // so the splash activity goes away
            }
        };

        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(1010);
                handler.post(doNextActivity);
            }
        }.start();

    }
}
