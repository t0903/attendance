package com.lyzyxy.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lyzyxy.attendance.model.Course;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseListActivity extends BaseActivity {
    CourseAdapter adapter;
    List<Course> courseList;
    RecyclerView rv_course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        //toolbar.setNavigationIcon(R.drawable.extend);
        setSupportActionBar(toolbar);

        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.extend));

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AddActivity.startActivity(CourseListActivity.this,AddActivity.class);
//            }
//        });

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");

        rv_course = findViewById(R.id.rv_course);
        LinearLayoutManager llm = new LinearLayoutManager(CourseListActivity.this);
        rv_course.setLayoutManager(llm);

        //updateCourses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(AuthUtil.user != null && AuthUtil.user.getIsTeacher() == 1)
            getMenuInflater().inflate(R.menu.menu_left,menu);
        else
            getMenuInflater().inflate(R.menu.menu_right,menu);
        setIconsVisible(menu,true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.create:
                AddActivity.startActivity(CourseListActivity.this,AddActivity.class);
                break;

            case R.id.join:
                ScanActivity.startActivity(CourseListActivity.this,ScanActivity.class);
                break;
        }
        return true;
    }

    /**
     * 解决不显示menu icon的问题
     * @param menu
     * @param flag
     */
    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if (menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCourses(){
        //TODO 当用户为学生时，显示学生加入的班课
        String url = Constant.URL_BASE + "course/getCourses";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("id", AuthUtil.user.getId());
        RetrofitRequest.sendPostRequest(url, params, AuthUtil.user.getToken(), Course.class, true,
                new RetrofitRequest.ResultHandler<List<Course>>(CourseListActivity.this) {
                    @Override
                    public void onBeforeResult() {
                        // 这里可以放关闭loading
                    }

                    @Override
                    public void onResult(RequestResult<List<Course>> r) {
                        if(r.getCode() == Constant.SUCCESS){
                            adapter = new CourseAdapter(CourseListActivity.this,r.getData());
                            rv_course.setAdapter(adapter);
                        }else if(!r.getMsg().equals("")){
                            MsgUtil.msg(CourseListActivity.this,r.getMsg());
                        }else{
                            MsgUtil.msg(CourseListActivity.this,R.string.net_server_error);
                        }
                    }

                    @Override
                    public void onAfterFailure() {
                        // 这里可以放关闭loading
                    }
                });
    }

}
