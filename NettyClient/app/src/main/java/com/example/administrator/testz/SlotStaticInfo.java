package com.example.administrator.testz;



        import java.util.List;

/**
 * 充电槽静态信息
 */
public class SlotStaticInfo {
    private String requestType;
    private String operateType;
    private int resultCode;
    private String slotSn;
    private int slotAlarm;
    private List<Integer> existBats;

    public String getRequestType() {
        return requestType;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getSlotSn() {
        return slotSn;
    }

    public void setSlotSn(String slotSn) {
        this.slotSn = slotSn;
    }

    public int getSlotAlarm() {
        return slotAlarm;
    }

    public void setSlotAlarm(int slotAlarm) {
        this.slotAlarm = slotAlarm;
    }

    public List<Integer> getExistBats() {
        return existBats;
    }

    public void setExistBats(List<Integer> existBats) {
        this.existBats = existBats;
    }



    @Override
    public String toString() {
        return "SlotInfo{" +
                "requestType='" + requestType + '\'' +
                ", operateType='" + operateType + '\'' +
                ", resultCode=" + resultCode +
                ", slotSn='" + slotSn + '\'' +
                ", slotAlarm=" + slotAlarm +
                ", existBats=" + existBats +
                '}';
    }
}
