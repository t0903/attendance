package com.lyzyxy.attendance;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyzyxy.attendance.model.Course;
import com.lyzyxy.attendance.model.dto.RecordDto;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.CommonUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO 修改签到结果后刷新数据

public class MemberFragment extends Fragment {
    private List<RecordDto> list;

    Context context;

    private RecyclerView recyclerView;
    private MemberAdapter memberAdapter;
    private LinearLayout ll_student;

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

        ll_student = view.findViewById(R.id.ll_student);

        recyclerView = view.findViewById(R.id.member);
        memberAdapter = new MemberAdapter(context,list);

        recyclerView.setAdapter(memberAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        getData();

        if(AuthUtil.user.getIsTeacher() == 1)
            ll_student.setVisibility(View.GONE);

        return view;
    }

    private static class MyHandler extends Handler {
        //使该内部类持有对外部类的弱引用
        private WeakReference<MemberFragment> weakReference;
        //构造器中完成弱引用初始化
        MyHandler(MemberFragment fragment){
            weakReference=new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            //通过弱引用的get()方法获得外部类对象的引用
            MemberFragment fragment = weakReference.get();

            int[] r = CommonUtil.Rank(fragment.list,AuthUtil.user.getId());
            TextView rate = fragment.ll_student.findViewById(R.id.rate);
            rate.setText("出勤率"+r[1]+"%");
            TextView rank = fragment.ll_student.findViewById(R.id.rank);
            rank.setText("第"+r[0]+"名");
            ImageView head = fragment.ll_student.findViewById(R.id.head);

            Picasso.with(fragment.context)
                    .load(Constant.URL_BASE + AuthUtil.user.getPhoto())
                    .centerCrop()
                    .resizeDimen(R.dimen.image_width,R.dimen.image_height)
                    .into(head);
        }
    }
    //创建Handler对象
    private MyHandler handler=new MyHandler(this);

    private void getData(){
        String url = Constant.URL_BASE + "user/record";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("courseId", CourseActivity.course.getId());

        RetrofitRequest.sendPostRequest(url, params, RecordDto.class, true,
                new RetrofitRequest.ResultHandler<List<RecordDto>>(context) {
                    @Override
                    public void onBeforeResult() {
                        // 这里可以放关闭loading
                    }

                    @Override
                    public void onResult(RequestResult<List<RecordDto>> r) {
                        if(r.getCode() == Constant.SUCCESS){
                            list = r.getData();

                            if(AuthUtil.user.getIsTeacher() == 0)
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Message message=new Message();
                                        handler.sendMessage(message);
                                    }
                                }).start();

                            memberAdapter.setData(list);
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
