package com.lyzyxy.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lyzyxy.attendance.model.Course;
import com.lyzyxy.attendance.util.AuthUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends BaseActivity {
    public static final int COURSE_INDEX = 0;

    public static Course course;

    List<Fragment> mFragments;
    private int lastIndex=0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragmentPosition(COURSE_INDEX);
                    return true;
                case R.id.navigation_dashboard:
                    setFragmentPosition(1);
                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            switch (requestCode) {
                case 1://创建班课返回
                    if(resultCode == RESULT_OK) {
                        Course course = (Course) data.getSerializableExtra("data");

                        if (course != null) {
                            ((CourseFragment)mFragments.get(0)).addCourse(course);
                        }
                    }
                    break;
                case 2://扫描二维码加入班课返回
                    if(resultCode == RESULT_OK) {
                        ((CourseFragment)mFragments.get(0)).updateCourses();
                    }
                    break;
                default:
            }
        }
    }

    private void init(){
        mFragments = new ArrayList<>();
        mFragments.add(new CourseFragment());

        setFragmentPosition(COURSE_INDEX);
    }

    public void setFragmentPosition(int position) {
        if(position >= mFragments.size()) return;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = mFragments.get(position);
        Fragment lastFragment = mFragments.get(lastIndex);
        lastIndex = position;

        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.ll_frameLayout, currentFragment);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }

}
