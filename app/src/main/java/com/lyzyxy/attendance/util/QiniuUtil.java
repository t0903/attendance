package com.lyzyxy.attendance.util;

import android.content.Context;
import android.util.Log;

import com.qiniu.util.Auth;

public class QiniuUtil {
    private static final String TAG = "QiniuUtil";
    private static final String accessKey = "fN5cB8AUB6XYC7LRTznhKTlDSXsdM_OMHL_lzEE5";
    private static final String secretKey = "21PfGAtVophbcG-LOtD1ObhuYuKrg21P3pz3Gq0Z";
    private static final String bucket = "ucshare";
    private static Auth auth;
    private static QiniuUploadManager manager;

    static {
        auth = Auth.create(accessKey, secretKey);
    }

    public static void upload(String path, Context context,OnUploadAdapter adapter) {
        if (manager == null) {
            manager = QiniuUploadManager.getInstance(context);
        }

        String token = auth.uploadToken(bucket);

        String currentTim = String.valueOf(System.currentTimeMillis());
        String key = "files/" + currentTim + "/" + currentTim + ".jpg";
        String mimeType = "image/jpeg";
        QiniuUploadManager.QiniuUploadFile param = new QiniuUploadManager.QiniuUploadFile(path, key, mimeType, token);

        manager.upload(param, adapter);
    }

    public static abstract class OnUploadAdapter implements QiniuUploadManager.OnUploadListener{
        @Override
        public void onStartUpload() {
            Log.e(TAG, "onStartUpload");
        }

        @Override
        public void onUploadProgress(String key, double percent) {
        }

        @Override
        public abstract void onUploadFailed(String key, String err);

        @Override
        public abstract void onUploadBlockComplete(String key);

        @Override
        public void onUploadCompleted() {
            Log.e(TAG, "onUploadCompleted");
        }
        @Override
        public void onUploadCancel() {
            Log.e(TAG, "onUploadCancel");
        }
    }
}
