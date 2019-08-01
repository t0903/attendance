package com.lyzyxy.attendance.model.dto;

import java.util.Date;

public class SignDto {
    private int id;//recordId
    private int courseId;
    private Date start;
    private Date end;
    private Integer count;
    private String location;
    private Integer sum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getCount() {
        if(count == null)
            return 0;
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSum() {
        if(sum == null)
            return 0;
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
