package com.lyzyxy.attendance;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyzyxy.attendance.model.dto.SignResult;
import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView iv_head;
        TextView tv_name,tv_no,tv_juli,tv_unsign;
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            iv_head = itemView.findViewById(R.id.iv_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_no = itemView.findViewById(R.id.tv_no);
            tv_juli = itemView.findViewById(R.id.tv_juli);
            tv_unsign = itemView.findViewById(R.id.tv_unsign);
        }
    }
    protected Context context;
    protected List<SignResult> list;

    public ResultAdapter(Context context, List<SignResult> list){
        this.context = context;
        this.list = list;
    }

    public void setData(List<SignResult> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setSign(SignResult signResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_select_sign, null);//获取自定义布局
        builder.setView(layout);
        final AlertDialog dlg = builder.create();
        Window window = dlg.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //设置点击外围消散
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();

        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dlg.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕

        window.setBackgroundDrawable(new ColorDrawable(0));

        TextView tv_queqin = layout.findViewById(R.id.tv_queqin);
        TextView tv_shijia = layout.findViewById(R.id.tv_shijia);
        TextView tv_chidao = layout.findViewById(R.id.tv_chidao);
        TextView tv_zaotui = layout.findViewById(R.id.tv_zaotui);
        TextView tv_yiqiandao = layout.findViewById(R.id.tv_yiqiandao);
        TextView cancel = layout.findViewById(R.id.cancel);

        tv_queqin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setSingResult(signResult,"缺勤",dlg);
            }
        });

        tv_shijia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setSingResult(signResult,"请假",dlg);
            }
        });

        tv_chidao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setSingResult(signResult,"迟到",dlg);
            }
        });

        tv_zaotui.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setSingResult(signResult,"早退",dlg);
            }
        });

        tv_yiqiandao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setSingResult(signResult,"已签到",dlg);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dlg.dismiss();
            }
        });
    }

    private void setSingResult(SignResult signResult,String msg,AlertDialog dlg){
        String url = Constant.URL_BASE + "user/setSign";
        int recordId = ((ResultActivity)context).recordId;
        int signId = -1;

        if(signResult.getSignId() != null){
            signId = signResult.getSignId();
        }

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordId", recordId);
        params.put("signId", signId);
        params.put("studentId", signResult.getId());
        params.put("msg", msg);

        RetrofitRequest.sendPostRequest(url, params, null, false,
                new RetrofitRequest.ResultHandler(context) {
                    @Override
                    public void onBeforeResult() {
                        // 这里可以放关闭loading
                    }

                    @Override
                    public void onResult(RequestResult r) {
                        if(r.getCode() == Constant.SUCCESS){
                            ((ResultActivity)context).loadData();
                        }else{
                            MsgUtil.msg(context,R.string.net_server_error);
                        }
                        dlg.dismiss();
                    }

                    @Override
                    public void onAfterFailure() {
                        // 这里可以放关闭loading
                    }
                });
    }

    @NonNull
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sign_list_view, parent, false);
        final ResultAdapter.ViewHolder viewHolder = new ResultAdapter.ViewHolder(view);

        if(AuthUtil.user.getIsTeacher() == 1) {
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = viewHolder.getAdapterPosition();
                    SignResult signResult = list.get(position);
                    setSign(signResult);
                }
            });
        }

        return  viewHolder;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
