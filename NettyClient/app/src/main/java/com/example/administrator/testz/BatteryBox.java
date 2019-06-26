package com.example.administrator.testz;

/**
 * Created by wrs on 2019/6/26,11:51
 * projectName: Testz
 * packageName: com.example.administrator.testz
 */

import java.util.List;

/**
 * 充电柜
 */
public class BatteryBox {
    private String requestType;
    private int resultCode;
    private String size;
    private String hwVer;
    private String fwVer;
    private int alarm;
    private String sn;
    //自己定义的多层嵌套解析类

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getHwVer() {
        return hwVer;
    }

    public void setHwVer(String hwVer) {
        this.hwVer = hwVer;
    }

    public String getFwVer() {
        return fwVer;
    }

    public void setFwVer(String fwVer) {
        this.fwVer = fwVer;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }



    @Override
    public String toString() {
        return "BatteryBox{" +
                "requestType='" + requestType + '\'' +
                ", resultCode=" + resultCode +
                ", size='" + size + '\'' +
                ", hwVer='" + hwVer + '\'' +
                ", fwVer='" + fwVer + '\'' +
                ", alarm=" + alarm +
                ", sn='" + sn + '\'' +
                '}';
    }
}
