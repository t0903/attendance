package com.lyzyxy.attendance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends BaseActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView iv_scan = findViewById(R.id.iv_scan);
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanActivity.startActivity(MainActivity.this,ScanActivity.class);
            }
        });

        TextView tv_teacher = findViewById(R.id.tv_teacher);
        tv_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.startActivity(MainActivity.this,LoginActivity.class);
                finish();
            }
        });

        TextView test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadActivity.startActivity(MainActivity.this,UploadActivity.class);
                finish();
            }
        });
    }


}
