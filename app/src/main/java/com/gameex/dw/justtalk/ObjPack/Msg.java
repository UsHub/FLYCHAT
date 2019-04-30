package com.gameex.dw.justtalk.ObjPack;

import android.net.Uri;

public class Msg {
    /**
     * 日期
     */
    private String date;
    /**
     * 时间
     */
    private String time;
    /**
     * 头像
     */
    private Uri uri;
    /**
     * 头像资源id
     */
    private int resourceId;
    /**
     * 内容
     */
    private String content;
    /**
     * 类型
     */
    private Type type;

    public enum Type {
        /**
         * 接收
         */
        RECEIVED,
        /**
         * 发送
         */
        SEND
    }

    public Msg(String content, Type type) {
        this.content = content;
        this.type = type;
    }

    public Msg(String date, String time, String content, Type type) {
        this.date = date;
        this.time = time;
        this.content = content;
        this.type = type;
    }

    public Msg(String date, String time, int resourceId, String content, Type type) {
        this.date = date;
        this.time = time;
        this.resourceId = resourceId;
        this.content = content;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", uri=" + uri +
                ", resourceId=" + resourceId +
                ", content='" + content + '\'' +
                ", type=" + type +
                '}';
    }
}
