package com.lyzyxy.attendance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lyzyxy.attendance.camera.CameraPreview;
import com.lyzyxy.attendance.util.MsgUtil;

import java.io.IOException;

public class SignActivity extends BaseActivity {
    public ImageView im;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        im = findViewById(R.id.iv_im);

        if (ContextCompat.checkSelfPermission(SignActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            //有权限,去打开摄像头
            startPreview();
        }
    }

    /**
     * 申请权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPreview();
            } else {
                //"权限拒绝");
                // TODO: 2018/12/4 这里可以给用户一个提示,请求权限被拒绝了
                MsgUtil.msg(SignActivity.this, "您拒绝了权限请求");
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
