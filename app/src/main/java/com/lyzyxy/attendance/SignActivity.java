package com.lyzyxy.attendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lyzyxy.attendance.camera.CameraPreview;
import com.lyzyxy.attendance.util.MsgUtil;

import java.io.IOException;

public class SignActivity extends BaseActivity {
    public static int recordId;
    public static String location;

    CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        Intent intent = this.getIntent();
        location = intent.getStringExtra("location");
        recordId = intent.getIntExtra("recordId",-1);

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

    public static void startActivity(Context context,int recordId,String location, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("recordId",recordId);
        intent.putExtra("location",location);
        context.startActivity(intent);
    }

    public void setCameraPreviewSize(int viewWidth, int viewHeight, int cameraWidth, int cameraHeight) {
        boolean bIsPortrait = (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT);    // 判断水平/垂直状态

        if (bIsPortrait) {
            int tmp = cameraWidth;
            cameraWidth = cameraHeight;
            cameraHeight = tmp;
        }

        int destWidth = viewWidth;
        int destHeight = viewHeight;

        if (bIsPortrait) {
            destHeight = (int)(((double)cameraHeight / cameraWidth) * destWidth);
        } else {
            destWidth = (int)(((double)cameraWidth / cameraHeight) * destHeight);
        }

        FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(destWidth, destHeight, Gravity.TOP); // set size
        cameraFL.setMargins(0, 0, 0, 0);  // set position
        mPreview.setLayoutParams(cameraFL);
    }

    public void startPreview() {
        mPreview = new CameraPreview(this);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    public void stopPreview() {
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.removeAllViews();
        finish();
    }

    public void onPause() {
        finish();
        super.onPause();
    }


}
