package com.lyzyxy.attendance;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.upload.BaseUploadUtil;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {
    private ImageView tv_head;
    private TextView tv_name,tv_no;
    private EditText et_name,et_no,et_username;

    private Context context;

    private BaseUploadUtil uploadUtil;

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_me, container, false);

        context = getContext();

        tv_head = view.findViewById(R.id.tv_head);
        tv_name = view.findViewById(R.id.tv_name);
        tv_no = view.findViewById(R.id.tv_no);
        et_name = view.findViewById(R.id.et_name);
        et_no = view.findViewById(R.id.et_no);
        et_username = view.findViewById(R.id.et_username);

        //Picasso为图片加载开源库
        Picasso.with(context)
                .load(Constant.URL_BASE + AuthUtil.user.getPhoto())
                .centerCrop()
                .resizeDimen(R.dimen.image_width,R.dimen.image_height)
                .into(tv_head);
        tv_name.setText(AuthUtil.user.getName());
        tv_no.setText(AuthUtil.user.getNo());

        et_name.setText(AuthUtil.user.getName());
        et_no.setText(AuthUtil.user.getNo());
        et_username.setText(AuthUtil.user.getUsername());

        uploadUtil = new BaseUploadUtil(getActivity(),tv_head);

        view.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUtil.UpdatePhoto(v);
            }
        });

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToServer();
            }
        });

        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUtil.user = null;
                ActivityCollector.finishAll();
                LoginActivity.startActivity(context,LoginActivity.class);
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        uploadUtil.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadUtil.onActivityResult(requestCode, resultCode, data);
    }

    private void saveToServer(){
        int id = AuthUtil.user.getId();
        String name = et_name.getText().toString();
        String no = et_no.getText().toString();
        String username = et_username.getText().toString();

        if(username.trim().equals("")){
            MsgUtil.msg(context,"用户名不能为空");
            return;
        }

        if(name.trim().equals("")){
            MsgUtil.msg(context,"姓名不能为空");
            return;
        }

        if(no.trim().equals("")){
            MsgUtil.msg(context,"学号不能为空");
            return;
        }

        uploadUtil.saveImageToServer(id, name, no, username, new RetrofitRequest.ResultHandler<String>(context) {
            @Override
            public void onBeforeResult() {

            }

            @Override
            public void onResult(RequestResult<String> t) {
                if(t.getCode() == Constant.SUCCESS){
                    String path = t.getData();
                    MsgUtil.msg(context,t.getMsg());

                    //保存成功
                    AuthUtil.user.setName(name);
                    AuthUtil.user.setNo(no);
                    AuthUtil.user.setUsername(username);
                    AuthUtil.user.setPhoto(path);
                }else if(!t.getMsg().equals("")){
                    MsgUtil.msg(context,t.getMsg());
                }else{
                    MsgUtil.msg(context,R.string.net_server_error);
                }
            }

            @Override
            public void onAfterFailure() {

            }
        });
    }
}
