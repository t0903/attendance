package com.lyzyxy.attendance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.lyzyxy.attendance.model.User;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.MsgUtil;

public class LoginActivity extends BaseActivity {
    EditText et_name, et_password;
    CheckBox cb_rember;

    SharedPreferences spf;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(AuthUtil.user != null){
            //CourseListActivity.startActivity(LoginActivity.this,CourseListActivity.class);
            CourseActivity.startActivity(LoginActivity.this,CourseActivity.class);
            finish();
        }


        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
        cb_rember = findViewById(R.id.cb_rember);

        spf = PreferenceManager.getDefaultSharedPreferences(this);

        boolean remember_password = spf.getBoolean("remember_password",false);
        if(remember_password){
            String name = spf.getString("username","");
            String password = spf.getString("password","");

            et_name.setText(name);
            et_password.setText(password);
            cb_rember.setChecked(true);
        }

        Button b_login = findViewById(R.id.b_login);
        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = et_name.getText().toString();
                final String password = et_password.getText().toString();

                if(name.equals("") || password.equals("")){
                    MsgUtil.msg(LoginActivity.this,"用户名密码不能为空！");
                }else{
                    AuthUtil.login(name,password,new RetrofitRequest.ResultHandler<User>(LoginActivity.this) {
                        @Override
                        public void onBeforeResult() {
                            // 这里可以放关闭loading
                        }

                        @Override
                        public void onResult(RequestResult<User> r) {
                            AuthUtil.user = r.getData();

                            editor = spf.edit();

                            if(cb_rember.isChecked()){
                                editor.putBoolean("remember_password",true);
                                editor.putString("username",name);
                                editor.putString("password",password);
                            }else{
                                editor.clear();
                            }
                            editor.apply();

                            //CourseListActivity.startActivity(LoginActivity.this,CourseListActivity.class);
                            CourseActivity.startActivity(LoginActivity.this,CourseActivity.class);

                            finish();
                        }

                        @Override
                        public void onAfterFailure() {
                            // 这里可以放关闭loading
                        }
                    });
                }

            }
        });

        TextView b_register = findViewById(R.id.b_register);
        b_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.startActivity(LoginActivity.this,RegisterActivity.class);
            }
        });
    }
}
