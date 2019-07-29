package com.lyzyxy.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyzyxy.attendance.model.dto.RecordDto;
import com.lyzyxy.attendance.model.dto.SignDto;
import com.lyzyxy.attendance.util.DateUtil;

import java.util.List;

public class SignAdapter extends RecyclerView.Adapter<SignAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView tv_title,tv_time,tv_summary;
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            tv_title = itemView.findViewById(R.id.title);
            tv_time = itemView.findViewById(R.id.time);
            tv_summary = itemView.findViewById(R.id.summary);
        }
    }

    private Context context;
    private List<SignDto> list;

    public SignAdapter(Context context,List<SignDto> list){
        this.context = context;
        this.list = list;
    }

    public void setData(List<SignDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.record_list_view, parent, false);
        final SignAdapter.ViewHolder viewHolder = new SignAdapter.ViewHolder(view);

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
        final SignDto bean = list.get(position);

        //2019-07-25 星期四 签到
        holder.tv_title.setText(DateUtil.convert(bean.getEnd(),DateUtil.DATE_FORMAT) + " "
                + DateUtil.getWeekOfDate(bean.getEnd()) + " 签到");
        holder.tv_time.setText(DateUtil.convert(bean.getEnd(),DateUtil.FORMAT_HH_MM));
        //36人 / 38人
        holder.tv_summary.setText(bean.getCount() +"人 / "+bean.getSum()+"人");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
