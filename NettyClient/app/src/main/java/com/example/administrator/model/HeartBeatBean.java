package com.example.administrator.model;


import java.util.HashMap;
import java.util.List;

/**
 * Created by wrs on 2019/6/25,17:35
 * projectName: Testz
 * packageName: com.example.administrator.testz
 */
public class HeartBeatBean {
    // 通过快捷键Alt+Insert

    String action;
    HashMap<String, String> data;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public HeartBeatBean(String mAction, HashMap<String, String> data) {
        this.action = mAction;
        this.data = data;
    }
}