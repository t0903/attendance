package com.lyzyxy.attendance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ActivityCollector.finishAll();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "再次点击退出程序", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }

    public static void startActivity(Context context,Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Context context,Class<?> cls,int requestCode) {
        Intent intent = new Intent(context, cls);
        ((AppCompatActivity)context).startActivityForResult(intent,requestCode);
    }
}
