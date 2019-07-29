package com.lyzyxy.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyzyxy.attendance.model.Course;
import com.lyzyxy.attendance.util.Constant;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder>{
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
    private Context context;

    public ResultAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sign_list_view, parent, false);
        final ResultAdapter.ViewHolder viewHolder = new ResultAdapter.ViewHolder(view);

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

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
