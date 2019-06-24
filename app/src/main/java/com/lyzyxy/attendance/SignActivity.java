package com.lyzyxy.attendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.lyzyxy.attendance.camera.CameraPreview;

public class SignActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        Button buttonStartPreview = findViewById(R.id.button_start_preview);
        buttonStartPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreview();
            }
        });
        Button buttonStopPreview = findViewById(R.id.button_stop_preview);
        buttonStopPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPreview();
            }
        });
    }

    public void startPreview() {
        CameraPreview mPreview = new CameraPreview(this);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    public void stopPreview() {
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.removeAllViews();
    }

    public void onPause() {
        finish();
        super.onPause();
    }


}
