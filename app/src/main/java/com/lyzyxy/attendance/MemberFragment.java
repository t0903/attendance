package com.lyzyxy.attendance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyzyxy.attendance.model.Course;
import com.lyzyxy.attendance.util.AuthUtil;
import com.qiniu.util.Auth;

import java.util.ArrayList;
import java.util.List;


public class MemberFragment extends Fragment {
    private List list;

    Context context;

    private RecyclerView recyclerView;
    private MemberAdapter memberAdapter;

    public MemberFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
        //TODO 添加数据
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member, container, false);

        Course course = CourseActivity.course;

        context = getContext();

        LinearLayout ll_student = view.findViewById(R.id.ll_student);

        if(AuthUtil.user.getIsTeacher() == 1)
            ll_student.setVisibility(View.GONE);
        else{
            
        }

        recyclerView = view.findViewById(R.id.member);
        memberAdapter = new MemberAdapter(context);

        recyclerView.setAdapter(memberAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        return view;
    }
}
