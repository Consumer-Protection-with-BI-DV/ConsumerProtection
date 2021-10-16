package com.jachdev.consumerprotection.data;

import com.google.gson.Gson;

/**
 * Created by Asanka on 9/20/2021.
 */
public class AppResponse {

    private int code;
    private String message;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public <T> T getObjectToType(Class<T> t) {
        try{

            Gson gson = new Gson();
            String json = gson.toJson(data);
            Object value = new Gson().fromJson(json, t);
            return (T)value;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public <T> T getObjectToType(String stringJson, Class<T> t) {
        try{

            Gson gson = new Gson();
            Object value = new Gson().fromJson(stringJson, t);
            return (T)value;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
