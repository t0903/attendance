package com.lyzyxy.attendance.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;

import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.Constant;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ProcessWithAsyncTask extends AsyncTask<byte[], Void, String> {
    private static final String TAG = "AsyncTask";
    public static int uploading = 0;//上传的帧数
    public static boolean upload = false;//当有一帧图片在上传，则不再上传其他帧
    private Context context;
    public Camera.Size size;

    public ProcessWithAsyncTask(Context context, Camera.Size size) {
        this.context = context;
        this.size = size;
    }

    @Override
    protected String doInBackground(byte[]... params) {
        processFrame(params[0]);
        return "test";
    }

    private void processFrame(byte[] frameData) {
        if (size == null)
            return;

        if (++uploading == 16) {
            upload = true;

            ByteArrayOutputStream baos = null;
            try {


                YuvImage yuvImage = new YuvImage(frameData, ImageFormat.NV21, size.width, size.height, null);

                baos = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, baos);//80--JPG图片的质量[0-100],100最高

                Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();

                Tiny.getInstance().source(baos.toByteArray()).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                    @Override
                    public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                        File file = new File(outfile);

                        String url = Constant.URL_BASE + "user/uploading";

                        Map<String, Object> requestMap = new HashMap<>();

                        RetrofitRequest.fileUpload(url, file,requestMap,String.class, false,
                                new RetrofitRequest.ResultHandler<String>(context) {

                                    @Override
                                    public void onBeforeResult() {

                                    }

                                    @Override
                                    public void onResult(RequestResult<String> t) {
                                        System.out.print("aaa");
                                    }

                                    @Override
                                    public void onAfterFailure() {

                                    }
                                });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                        baos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
