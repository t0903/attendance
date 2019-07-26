package com.lyzyxy.attendance;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyzyxy.attendance.model.User;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.LocationUtil;
import com.lyzyxy.attendance.util.MsgUtil;
import com.qiniu.util.Auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignFragment extends Fragment {
    private List list;

    Context context;
    AppCompatActivity mAppCompatActivity;

    private boolean permission = false;

    private RecyclerView recyclerView;
    private SignAdapter signAdapter;

    public SignFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign, container, false);

        context = getContext();
        mAppCompatActivity = (AppCompatActivity) getActivity();

        view.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AuthUtil.user.getIsTeacher() == 1){
                    launch();
                }else {
                    ifSign();
                }
            }
        });

        recyclerView = view.findViewById(R.id.records);
        signAdapter = new SignAdapter(context);

        recyclerView.setAdapter(signAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    private void ifSign(){
        String location = getLocation();

        if(location == null)
            return;

        String url = Constant.URL_BASE + "user/ifSign";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("courseId",CourseActivity.course.getId());
        RetrofitRequest.sendPostRequest(url, params, Integer.class, false,
                new RetrofitRequest.ResultHandler<Integer>(context) {
                    @Override
                    public void onBeforeResult() {
                        // 这里可以放关闭loading
                    }

                    @Override
                    public void onResult(RequestResult<Integer> r) {
                        if(r.getCode() == Constant.SUCCESS){
                            int recordId = r.getData();

                            SignActivity.startActivity(context,recordId,location,SignActivity.class);
                        }else{
                            MsgUtil.msg(context,r.getMsg());
                        }
                    }

                    @Override
                    public void onAfterFailure() {
                        // 这里可以放关闭loading
                    }
                });
    }

    private String getLocation(){
        checkPermission();

        if(!permission){
            return null;
        }

        Location net = LocationUtil.getNetWorkLocation(context);
        if (net != null) {
            return net.getLatitude() + "," + net.getLongitude();
        }
        return null;
    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(mAppCompatActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                permission = true;
            }
        } else {
            permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                permission = true;
            } else {
                permission = false;
                MsgUtil.msg(context, "您拒绝了权限请求! 将不能进行签到！");
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //教师发起签到任务
    private void launch(){
        String location = getLocation();

        if(location == null)
            return;


        String url = Constant.URL_BASE + "user/launch";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("userId",AuthUtil.user.getId());
        params.put("courseId",CourseActivity.course.getId());
        params.put("location",location);

        RetrofitRequest.sendPostRequest(url, params, null, false,
                new RetrofitRequest.ResultHandler(context) {
                    @Override
                    public void onBeforeResult() {
                        // 这里可以放关闭loading
                    }

                    @Override
                    public void onResult(RequestResult r) {
                        if(r.getCode() == Constant.SUCCESS){
                            MsgUtil.msg(context,"发起签到成功！");
                            //TODO 跳转到签到页面 观看签到情况
                        }else{
                            MsgUtil.msg(context,r.getMsg());
                        }
                    }

                    @Override
                    public void onAfterFailure() {
                        // 这里可以放关闭loading
                    }
                });
    }
}
