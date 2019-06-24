package com.lyzyxy.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyzyxy.attendance.model.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView tv_name,tv_class,tv_id;
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_class = itemView.findViewById(R.id.tv_class);
            tv_id = itemView.findViewById(R.id.tv_id);
        }
    }
    private Context context;
    private List<Course> courseList;

    public CourseAdapter(Context context,List<Course> courseList){
        this.context = context;
        this.courseList = courseList;
    }

    public void setData(List<Course> courseList) {
        this.courseList = courseList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_list_view, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();

                Course course = courseList.get(position);
            }
        });

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Course bean = courseList.get(position);

        holder.tv_name.setText(bean.getName());
        holder.tv_class.setText(bean.getClassName());
        holder.tv_id.setText(""+bean.getId());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }
}
