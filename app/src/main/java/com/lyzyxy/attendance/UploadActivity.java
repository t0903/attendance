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
    //调取系统摄像头的请求码
    private static final int MY_ADD_CASE_CALL_PHONE = 6;
    //打开相册的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;
    private ImageView imageView;
    private EditText et_name;

    private Uri uri;
    private String outfile;

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

        Button b_ok = findViewById(R.id.b_ok);
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();

                if(name.trim().equals("")){
                    MsgUtil.msg(UploadActivity.this,"姓名不能为空");
                    et_name.requestFocus();
                }else{
                    saveImageToServer(AuthUtil.user.getId(),name);
                }
            }
        });
    }



    public void UpdatePhoto(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_select_photo, null);//获取自定义布局
        builder.setView(layout);
        final AlertDialog dlg = builder.create();
        Window window = dlg.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //设置点击外围消散
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dlg.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕

        window.setBackgroundDrawable(new ColorDrawable(0));

        TextView button1 = layout.findViewById(R.id.photograph);
        TextView button2 = layout.findViewById(R.id.photo);
        TextView button3 = layout.findViewById(R.id.cancel);


        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //"点击了照相";
                //  6.0之后动态申请权限 摄像头调取权限,SD卡写入权限
                if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        ) {
                    ActivityCompat.requestPermissions(UploadActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_ADD_CASE_CALL_PHONE);
                } else {
                    try {
                        //有权限,去打开摄像头
                        takePhoto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                dlg.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //"点击了相册");
                //  6.0之后动态申请权限 SD卡写入权限
                if (ContextCompat.checkSelfPermission(UploadActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UploadActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_ADD_CASE_CALL_PHONE2);

                } else {
                    choosePhoto();
                }
                dlg.dismiss();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dlg.dismiss();
            }
        });
    }

    private void takePhoto() throws IOException {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(getExternalCacheDir(),"output.jpg");

        try{
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            /**
             * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
             * 并且这样可以解决MIUI系统上拍照返回size为0的情况
             */
            uri = FileProvider.getUriForFile(this, "com.lyzyxy.attendance.fileprovider", file);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 1);
    }

    /**
     * 打开相册
     */
    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, 2);
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

        if (requestCode == MY_ADD_CASE_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    takePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //"权限拒绝");
                // TODO: 2018/12/4 这里可以给用户一个提示,请求权限被拒绝了
                MsgUtil.msg(UploadActivity.this, "您拒绝了权限请求");
            }
        }


        if (requestCode == MY_ADD_CASE_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                //"权限拒绝");
                // TODO: 2018/12/4 这里可以给用户一个提示,请求权限被拒绝了
                MsgUtil.msg(UploadActivity.this, "您拒绝了权限请求");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != Activity.RESULT_CANCELED) {
            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                Tiny.getInstance().source(bitmap).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                    @Override
                    public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                        UploadActivity.this.outfile = outfile;
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK
                && null != data) {
            try {
                Uri selectedImage = data.getData();
                Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                Tiny.getInstance().source(selectedImage).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                    @Override
                    public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                        UploadActivity.this.outfile = outfile;
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } catch (Exception e) {
                //"上传失败");
            }
        }
    }

    private void saveImageToServer(int id,String name) {
        if(outfile == null || outfile.equals("")) return ;

        File file = new File(outfile);

        String url = Constant.URL_BASE + "user/upload";

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("id",id);
        requestMap.put("name",name);

        RetrofitRequest.fileUpload(url, file,requestMap, null, String.class, false,
                new RetrofitRequest.ResultHandler<String>(UploadActivity.this) {

                    @Override
                    public void onBeforeResult() {

                    }

                    @Override
                    public void onResult(RequestResult<String> t) {
                        if(t.getCode() == Constant.SUCCESS){
                            String path = t.getData();
                            MsgUtil.msg(UploadActivity.this,t.getMsg());

                            CourseListActivity.startActivity(UploadActivity.this,CourseListActivity.class);
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
