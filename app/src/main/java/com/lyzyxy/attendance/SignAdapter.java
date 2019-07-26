package com.lyzyxy.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public SignAdapter(Context context){
        this.context = context;
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

    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
