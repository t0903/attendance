package com.lyzyxy.attendance.network.result;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.List;

import ikidou.reflect.TypeBuilder;

public class RequestResult<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> RequestResult<List<T>> fromJsonArray(String json, Class<T> clazz) {
        Gson gson = new Gson();

        Type type = TypeBuilder
                .newInstance(RequestResult.class)
                .beginSubType(List.class)
                .addTypeParam(clazz)
                .endSubType()
                .build();
        return gson.fromJson(json, type);
    }

    public static <T> RequestResult<T> fromJsonObject(String json, Class<T> clazz) {
        Gson gson = new Gson();

        Type type = null;

        if(clazz != null)
            type = TypeBuilder
                .newInstance(RequestResult.class)
                .addTypeParam(clazz)
                .build();
        else
            type = TypeBuilder
                    .newInstance(RequestResult.class)
                    .addTypeParam(String.class)
                    .build();
        return gson.fromJson(json, type);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
