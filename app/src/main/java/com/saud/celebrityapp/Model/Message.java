package com.saud.celebrityapp.Model;

public class Message {
    private String user_id;
    private String msg;
    private String type;
    private String date;

    public Message() {
    }

    public Message(String user_id, String msg, String type, String date) {
        this.user_id = user_id;
        this.msg = msg;
        this.type = type;
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
