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

public class UnSignResultAdapter extends ResultAdapter{
    public UnSignResultAdapter(Context context, List<SignResult> list){
        super(context,list);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SignResult bean = list.get(position);

        holder.tv_juli.setVisibility(View.GONE);
        if(bean.getRemarks() != null){
            holder.tv_unsign.setText(bean.getRemarks());
        }else {
            holder.tv_unsign.setText("缺勤");
        }

        holder.tv_name.setText(bean.getName());
        holder.tv_no.setText(bean.getNo());

        Picasso.with(context)
                .load(Constant.URL_BASE + bean.getPhoto())
                .centerCrop()
                .resizeDimen(R.dimen.min_image_width,R.dimen.min_image_height)
                .into(holder.iv_head);
    }
}
