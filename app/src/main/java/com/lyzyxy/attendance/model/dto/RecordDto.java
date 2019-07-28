package com.lyzyxy.attendance.model.dto;

public class RecordDto {
    private int id;
    private String name;
    private String no;
    private String photo;
    private Double rate;
    private Integer loss;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        if(no == null)
            return "";
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Double getRate() {
        if(rate == null)
            return 0.;
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getLoss() {
        if(loss == null)
            return 0;
        return loss;
    }

    public void setLoss(Integer loss) {
        this.loss = loss;
    }
}
