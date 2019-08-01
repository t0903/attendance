package com.lyzyxy.attendance;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.upload.BaseUploadUtil;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends BaseActivity {
    private ImageView imageView;
    private EditText et_name;
    private EditText et_no;

    private BaseUploadUtil uploadUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        if(AuthUtil.user == null){
            LoginActivity.startActivity(UploadActivity.this, LoginActivity.class);
            finish();
        }

        imageView = findViewById(R.id.tv_face);
        et_name = findViewById(R.id.name);
        et_no = findViewById(R.id.no);

        uploadUtil = new BaseUploadUtil(this,imageView);

        findViewById(R.id.b_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUtil.UpdatePhoto(v);
            }
        });

        Button b_ok = findViewById(R.id.b_ok);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToServer();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        uploadUtil.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadUtil.onActivityResult(requestCode, resultCode, data);
    }

    private void saveToServer() {
        int id = AuthUtil.user.getId();
        String name = et_name.getText().toString();
        String no = et_no.getText().toString();

        if(name.trim().equals("")){
            MsgUtil.msg(this,"姓名不能为空");
            return;
        }

        if(no.trim().equals("")){
            MsgUtil.msg(this,"学号不能为空");
            return;
        }

        uploadUtil.saveImageToServer(id, name, no, "", new RetrofitRequest.ResultHandler<String>(this) {
            @Override
            public void onBeforeResult() {

            }

            @Override
            public void onResult(RequestResult<String> t) {
                if(t.getCode() == Constant.SUCCESS){
                    String path = t.getData();
                    MsgUtil.msg(UploadActivity.this,t.getMsg());

                    //保存成功
                    AuthUtil.user.setName(name);
                    AuthUtil.user.setNo(no);
                    AuthUtil.user.setPhoto(path);

                    //CourseListActivity.startActivity(UploadActivity.this,CourseListActivity.class);
                    CourseActivity.startActivity(UploadActivity.this,CourseActivity.class);

                    UploadActivity.this.finish();
                }else if(!t.getMsg().equals("")){
                    MsgUtil.msg(UploadActivity.this,t.getMsg());
                }else{
                    MsgUtil.msg(UploadActivity.this,R.string.net_server_error);
                }
            }

            @Override
            public void onAfterFailure() {

            }
        });
    }

}
