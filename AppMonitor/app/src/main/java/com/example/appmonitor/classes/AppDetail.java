package com.example.appmonitor.classes;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017/4/11 0011.
 */
public class AppDetail extends AppInfo{

    private String name;    //名称
    private Drawable icon; // 图标
    private String describe;//提议


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

}
