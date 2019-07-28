package com.lyzyxy.attendance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyzyxy.attendance.model.Course;
import com.lyzyxy.attendance.model.dto.RecordDto;
import com.lyzyxy.attendance.util.Constant;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView iv_head;
        TextView tv_name,tv_no,tv_rate,tv_loss;
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            iv_head = itemView.findViewById(R.id.iv_head);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_no = itemView.findViewById(R.id.tv_no);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            tv_loss = itemView.findViewById(R.id.tv_loss);
        }
    }

    private Context context;
    private List<RecordDto> list;

    public MemberAdapter(Context context,List<RecordDto> list){
        this.context = context;
        this.list = list;
    }

    public void setData(List<RecordDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_list_view, parent, false);
        final MemberAdapter.ViewHolder viewHolder = new MemberAdapter.ViewHolder(view);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();


            }
        });

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RecordDto bean = list.get(position);

        holder.tv_name.setText(bean.getName());
        holder.tv_no.setText(bean.getNo());
        String r = (int)(bean.getRate() * 100) + "%";
        holder.tv_rate.setText(r);
        holder.tv_loss.setText("缺勤"+bean.getLoss()+"次");

        //Picasso为图片加载开源库
        Picasso.with(context)
                .load(Constant.URL_BASE + bean.getPhoto())
                .centerCrop()
                .resizeDimen(R.dimen.image_width,R.dimen.image_height)
                .into(holder.iv_head);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
