package com.lyzyxy.attendance;

import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lyzyxy.attendance.model.User;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;

import java.util.HashMap;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanActivity extends BaseActivity implements QRCodeView.Delegate, View.OnClickListener{
    private static final String TAG = "ScanActivity";
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private ZXingView mZXingView;
    private TextView open_close_flashlight;

    private boolean flashligthopend = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.icon_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanActivity.this.finish();
            }
        });

        mZXingView = findViewById(R.id.zbarview);
        mZXingView.setDelegate(this);

        open_close_flashlight = findViewById(R.id.open_close_flashlight);
        open_close_flashlight.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别

        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);

        //Toast.makeText(ScanActivity.this,"扫描结果为：" + result,Toast.LENGTH_SHORT).show();
        if(result.startsWith("content:id:")){
            int id = Integer.parseInt(result.substring(11));
            joinCourse(id);
        }else{
            Toast.makeText(ScanActivity.this,"二维码不正确！",Toast.LENGTH_SHORT).show();
            vibrate();

            mZXingView.startSpot(); // 重新开始识别
        }
    }

    private void joinCourse(int id){
        String url = Constant.URL_BASE + "user/join";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("userId",AuthUtil.user.getId());
        params.put("classId",id);
        RetrofitRequest.sendPostRequest(url, params,null, false,
                new RetrofitRequest.ResultHandler(ScanActivity.this) {
                    @Override
                    public void onBeforeResult() {
                        // 这里可以放关闭loading
                    }

                    @Override
                    public void onResult(RequestResult r) {
                        if(r.getCode() == Constant.SUCCESS){
                            vibrate();

                            Intent intent=new Intent();
                            setResult(RESULT_OK, intent);

                            finish();
                        }else if(!r.getMsg().equals("")){
                            MsgUtil.msg(ScanActivity.this,r.getMsg());
                        }else{
                            MsgUtil.msg(ScanActivity.this,R.string.net_server_error);
                        }
                    }

                    @Override
                    public void onAfterFailure() {
                        // 这里可以放关闭loading
                    }
                });
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_close_flashlight:
                if(flashligthopend){
                    mZXingView.closeFlashlight();
                    flashligthopend = false;
                }else{
                    mZXingView.openFlashlight();
                    flashligthopend = true;
                }
                break;
            default:

        }
    }
}
