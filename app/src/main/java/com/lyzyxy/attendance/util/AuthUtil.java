package com.lyzyxy.attendance.util;

import android.content.Context;
import android.widget.Toast;

import com.lyzyxy.attendance.LoginActivity;
import com.lyzyxy.attendance.R;
import com.lyzyxy.attendance.model.User;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;

import java.util.HashMap;
import java.util.Map;

public class AuthUtil {
    public static User user;

    public static void login(String username, String password,final RetrofitRequest.ResultHandler resultHandler) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        RetrofitRequest.sendGetRequest(Constant.URL_AUTH, params,User.class, false,resultHandler);
    }

    public static void login(final Context context){
        login(user.getUsername(),user.getPassword(),new RetrofitRequest.ResultHandler<User>(context) {
            @Override
            public void onBeforeResult() {
                // 这里可以放关闭loading
            }

            @Override
            public void onResult(RequestResult<User> r) {
                AuthUtil.user = r.getData();
                Toast.makeText(context, "请重试！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAfterFailure() {
                // 这里可以放关闭loading
            }
        });
    }
}
