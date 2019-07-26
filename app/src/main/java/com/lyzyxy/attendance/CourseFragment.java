package com.lyzyxy.attendance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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


public class CourseFragment extends Fragment {
    private List<Course> list;

    private RecyclerView recyclerView;
    private CourseAdapter adapter;

    private Context context;
    AppCompatActivity mAppCompatActivity;

    public CourseFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        context = getContext();

        mAppCompatActivity = (AppCompatActivity) getActivity();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        mAppCompatActivity.setSupportActionBar(toolbar);

        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.extend));

        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.course);
        adapter = new CourseAdapter(context,list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        updateCourses();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);

        if(AuthUtil.user != null && AuthUtil.user.getIsTeacher() == 1)
            inflater.inflate(R.menu.menu_left,menu);
        else
            inflater.inflate(R.menu.menu_right,menu);
        setIconsVisible(menu,true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.create:
                AddActivity.startActivityForResult(context,AddActivity.class,1);
                //AddActivity.startActivity(CourseListActivity.this,AddActivity.class);
                break;

            case R.id.join:
                ScanActivity.startActivityForResult(context,ScanActivity.class,2);
                //ScanActivity.startActivity(context,ScanActivity.class);
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

    public void addCourse(Course c){
        list.add(c);
        adapter.setData(list);
    }

    public void updateCourses(){
        String url = null;
        if(AuthUtil.user.getIsTeacher() == 1)//当为老师时，显示老师的所有课程
            url = Constant.URL_BASE + "course/getCourses";
        else//当为学生时，显示学生加入的课程
            url = Constant.URL_BASE + "course/getJoinedCourses";

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("id", AuthUtil.user.getId());
        RetrofitRequest.sendPostRequest(url, params, Course.class, true,
                new RetrofitRequest.ResultHandler<List<Course>>(context) {
                    @Override
                    public void onBeforeResult() {
                        // 这里可以放关闭loading
                    }

                    @Override
                    public void onResult(RequestResult<List<Course>> r) {
                        if(r.getCode() == Constant.SUCCESS){
                            list = r.getData();

                            adapter.setData(list);
                        }else if(!r.getMsg().equals("")){
                            MsgUtil.msg(context,r.getMsg());
                        }else{
                            MsgUtil.msg(context,R.string.net_server_error);
                        }
                    }

                    @Override
                    public void onAfterFailure() {
                        // 这里可以放关闭loading
                    }
                });
    }
}
