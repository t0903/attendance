package com.lyzyxy.attendance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lyzyxy.attendance.model.Course;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends BaseActivity {
    //ImageView iv_add;
    EditText et_class,et_course;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.icon_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddActivity.this.finish();
            }
        });

//        iv_add = findViewById(R.id.iv_add);
//        iv_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        et_class = findViewById(R.id.et_class);
        et_course = findViewById(R.id.et_course);
        Button b_create = findViewById(R.id.b_create);
        b_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_course.getText().toString();
                String className = et_class.getText().toString();

                String url = Constant.URL_BASE+"course/add";
                Map<String,Object> params = new HashMap<String, Object>();
                params.put("name",name);
                params.put("className",className);
                params.put("teacherId", AuthUtil.user.getId());

                RetrofitRequest.sendPostRequest(url, params, AuthUtil.user.getToken(), Course.class, false,
                        new RetrofitRequest.ResultHandler<Course>(AddActivity.this) {
                            @Override
                            public void onBeforeResult() {
                                // 这里可以放关闭loading
                            }

                            @Override
                            public void onResult(RequestResult<Course> r) {
                                if(r.getCode() == Constant.SUCCESS){
                                    Course c = r.getData();
                                    GenerateActivity.startActivityForResult(AddActivity.this,GenerateActivity.class,c,2);
                                    //GenerateActivity.startActivity(AddActivity.this,GenerateActivity.class,
                                    //        c);

                                    //AddActivity.this.finish();
                                }else if(!r.getMsg().equals("")){
                                    MsgUtil.msg(AddActivity.this,r.getMsg());
                                }else{
                                    MsgUtil.msg(AddActivity.this,R.string.net_server_error);
                                }
                            }

                            @Override
                            public void onAfterFailure() {
                                // 这里可以放关闭loading
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if(resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            default:
        }

    }
}
