package com.lyzyxy.attendance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lyzyxy.attendance.model.Course;

import java.lang.ref.WeakReference;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class GenerateActivity extends BaseActivity {
    private ImageView mChineseIv;

    Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.icon_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GenerateActivity.this.onBackPressed();
            }
        });

        Intent intent = getIntent();
        course = (Course)intent.getSerializableExtra("course");

        mChineseIv = findViewById(R.id.iv_chinese);

        createQRCode(course != null ? course.getId():-1);
    }

    @Override
    public void onBackPressed() {
        Log.d("GenerateActivity","test");
        Intent intent=new Intent();
        intent.putExtra("data", course);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void startActivity(Context context, Class<?> cls, Course c) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("course",c);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Context context,Class<?> cls,Course c,int requestCode) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("course",c);
        ((AppCompatActivity)context).startActivityForResult(intent,requestCode);
    }

    private void createQRCode(int id) {
        String content = "content:id:"+id;
        CreateQRCodeTask task = new CreateQRCodeTask(this,content);
        task.execute();
    }

    public void decodeChinese(View v) {
        mChineseIv.setDrawingCacheEnabled(true);
        Bitmap bitmap = mChineseIv.getDrawingCache();
        decode(bitmap, "解析二维码失败");
    }

    private void decode(final Bitmap bitmap, final String errorTip) {
        /*
        这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
        请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github
        .com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
         */
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return QRCodeDecoder.syncDecodeQRCode(bitmap);
            }

            @Override
            protected void onPostExecute(String result) {
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(GenerateActivity.this, errorTip, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GenerateActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private static class CreateQRCodeTask extends AsyncTask<Void, Void, Bitmap> {

        private WeakReference<BaseActivity> activityReference;
        private String content;

        // only retain a weak reference to the activity
        CreateQRCodeTask(BaseActivity context,String content) {
            activityReference = new WeakReference<>(context);
            this.content = content;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            BaseActivity activity = activityReference.get();

            return QRCodeEncoder.syncEncodeQRCode(content, BGAQRCodeUtil.dp2px(activity, 300));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            BaseActivity activity = activityReference.get();
            if (bitmap != null) {
                ((GenerateActivity)activity).mChineseIv.setImageBitmap(bitmap);
            } else {
                Toast.makeText(activity, "生成二维码失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
