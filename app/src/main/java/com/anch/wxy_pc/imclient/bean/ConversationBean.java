package com.anch.wxy_pc.imclient.bean;

/**
 * Created by wxy-pc on 2015/6/16.
 */
public class ConversationBean {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String who_id;
    private String who_name;
    private String who_avatar;
    private String session_id;
    private String session_name;
    private String body;
    private String type;
    private String time;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;
    private String status;
    private int count;
    private String current_user;

    public String getCurrent_user() {
        return current_user;
    }

    public void setCurrent_user(String current_user) {
        this.current_user = current_user;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSession_name() {
        return session_name;
    }

    public void setSession_name(String session_name) {
        this.session_name = session_name;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getWho_avatar() {
        return who_avatar;
    }

    public void setWho_avatar(String who_avatar) {
        this.who_avatar = who_avatar;
    }

    public String getWho_name() {
        return who_name;
    }

    public void setWho_name(String who_name) {
        this.who_name = who_name;
    }

    public String getWho_id() {
        return who_id;
    }

    public void setWho_id(String who_id) {
        this.who_id = who_id;
    }

    private String unread;

}
