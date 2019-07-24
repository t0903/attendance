package com.lyzyxy.attendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.lyzyxy.attendance.model.User;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends BaseActivity {
    EditText et_name, et_password,et_password2;
    RadioButton rb_teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.icon_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.finish();
            }
        });

        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
        et_password2 = findViewById(R.id.et_password2);
        rb_teacher = findViewById(R.id.rb_teacher);

        Button b_login = findViewById(R.id.b_login);
        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString();
                String password = et_password.getText().toString();
                String password2 = et_password2.getText().toString();
                boolean isteacher = rb_teacher.isChecked();

                if(name.trim().equals("")){
                    MsgUtil.msg(RegisterActivity.this,"用户名不能为空");
                    et_name.requestFocus();
                }else if(password.trim().equals("")){
                    MsgUtil.msg(RegisterActivity.this,"密码不能为空");
                    et_password.requestFocus();
                }else if (!password.equals(password2)){
                    MsgUtil.msg(RegisterActivity.this,"两次输入密码不同");
                    et_password2.requestFocus();
                }else{
                    String url = Constant.URL_BASE + "user/register";
                    Map<String,Object> params = new HashMap<String, Object>();
                    params.put("name",name);
                    params.put("password",password);
                    params.put("isTeacher",isteacher);
                    RetrofitRequest.sendPostRequest(url, params, User.class, false,
                            new RetrofitRequest.ResultHandler<User>(RegisterActivity.this) {
                                @Override
                                public void onBeforeResult() {
                                    // 这里可以放关闭loading
                                }

                                @Override
                                public void onResult(RequestResult<User> r) {
                                    if(r.getCode() == Constant.SUCCESS){
                                        //TODO 页面跳转
                                        AuthUtil.user = r.getData();
                                        UploadActivity.startActivity(RegisterActivity.this,UploadActivity.class);
                                        RegisterActivity.this.finish();
                                    }else if(!r.getMsg().equals("")){
                                        MsgUtil.msg(RegisterActivity.this,r.getMsg());
                                    }else{
                                        MsgUtil.msg(RegisterActivity.this,R.string.net_server_error);
                                    }
                                }

                                @Override
                                public void onAfterFailure() {
                                    // 这里可以放关闭loading
                                }
                            });
                }
            }
        });
    }
}
