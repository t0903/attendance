package com.lyzyxy.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyzyxy.attendance.model.dto.SignResult;
import com.lyzyxy.attendance.util.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SignResultAdapter extends ResultAdapter{

    public SignResultAdapter(Context context, List<SignResult> list){
        super(context,list);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SignResult bean = list.get(position);

        holder.tv_unsign.setVisibility(View.GONE);

        if(bean.getDistance() == -1)
            holder.tv_juli.setText("未开启位置信息");
        else
            holder.tv_juli.setText("距老师 "+bean.getDistance() +" 米");

        holder.tv_name.setText(bean.getName());
        holder.tv_no.setText(bean.getNo());

        Picasso.with(context)
                .load(Constant.URL_BASE + bean.getPhoto())
                .centerCrop()
                .resizeDimen(R.dimen.min_image_width,R.dimen.min_image_height)
                .into(holder.iv_head);
    }
}
