package com.lyzyxy.attendance.camera;

import android.hardware.Camera;
import android.os.AsyncTask;
import com.lyzyxy.attendance.util.Base64Util;

/**
 * Created by zhantong on 16/6/15.
 */
public class ProcessWithAsyncTask extends AsyncTask<byte[], Void, String> {
    private static final String TAG = "AsyncTask";
    public Camera.Size size;

    public ProcessWithAsyncTask(Camera.Size size){
        this.size = size;
    }

    @Override
    protected String doInBackground(byte[]... params) {
        processFrame(params[0]);
        return "test";
    }

    private void processFrame(byte[] frameData) {
        if(size == null)
            return;

        Base64Util.byteToBitmap(frameData,size);

    }
}
