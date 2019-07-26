package com.lyzyxy.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public MemberAdapter(Context context){
        this.context = context;
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

    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
