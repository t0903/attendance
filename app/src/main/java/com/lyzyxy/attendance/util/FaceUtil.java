package com.lyzyxy.attendance.util;

import com.baidu.aip.face.AipFace;

import org.json.JSONObject;

import java.util.HashMap;

public class FaceUtil {
    public static final String APP_ID = "16123795";
    public static final String API_KEY = "R9xy8w2L0VWkptYGqOVRQWET";
    public static final String SECRET_KEY = "NOQWyKf64FCZycrhz98TWh98Pxbx0nEV";

    public static final AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

    public static void detect(String image,String imageType) {
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "gender");
        options.put("max_face_num", "1");
        options.put("face_type", "LIVE");
        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
        System.out.println(res.toString());
    }

    public static void addUser(String image,String imageType,String groupId,String userId) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("action_type", "REPLACE");

        // 人脸注册
        JSONObject res = client.addUser(image, imageType, groupId, userId, options);
    }
}
