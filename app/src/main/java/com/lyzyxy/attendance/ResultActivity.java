package com.lyzyxy.attendance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lyzyxy.attendance.model.Course;
import com.lyzyxy.attendance.model.dto.SignResult;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends BaseActivity {
    public int courseId,recordId;

    private List<SignResult> list;
    private RecyclerView unsign;
    private RecyclerView sign;
    private SignResultAdapter signAdapter;
    private UnSignResultAdapter unSignAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = this.getIntent();
        recordId = intent.getIntExtra("recordId",-1);
        courseId = intent.getIntExtra("courseId",-1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.icon_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResultActivity.this.finish();
            }
        });

        list = new ArrayList<>();

        unsign = findViewById(R.id.unsign);
        sign = findViewById(R.id.sign);

        signAdapter = new SignResultAdapter(this,list);
        unSignAdapter = new UnSignResultAdapter(this,list);
        unsign.setAdapter(unSignAdapter);
        unsign.setLayoutManager(new LinearLayoutManager(this));
        sign.setAdapter(signAdapter);
        sign.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    public void loadData(){
        if(courseId == -1 || recordId == -1)
            return;

        String url = Constant.URL_BASE + "user/getSignResult";

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("courseId", courseId);
        params.put("recordId", recordId);

        RetrofitRequest.sendPostRequest(url, params, SignResult.class, true,
                new RetrofitRequest.ResultHandler<List<SignResult>>(this) {
                    @Override
                    public void onBeforeResult() {
                        // 这里可以放关闭loading
                    }

                    @Override
                    public void onResult(RequestResult<List<SignResult>> r) {
                        if(r.getCode() == Constant.SUCCESS){
                            list = r.getData();

                            List<SignResult> signResults = new ArrayList<>();
                            List<SignResult> unsignResults = new ArrayList<>();

                            for(SignResult bean:list){
                                if(bean.getSignId() != null && bean.getRemarks() == null)
                                    signResults.add(bean);
                                else
                                    unsignResults.add(bean);
                            }

                            signAdapter.setData(signResults);
                            unSignAdapter.setData(unsignResults);
                        }else{
                            MsgUtil.msg(ResultActivity.this,R.string.net_server_error);
                        }
                    }

                    @Override
                    public void onAfterFailure() {
                        // 这里可以放关闭loading
                    }
                });
    }

    public static void startActivity(Context context, int recordId, int courseId, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("recordId",recordId);
        intent.putExtra("courseId",courseId);
        context.startActivity(intent);
    }
}
