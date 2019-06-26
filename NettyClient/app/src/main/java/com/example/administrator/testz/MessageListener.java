package com.example.administrator.testz;

/**
 * Created by wrs on 2019/6/26,11:45
 * projectName: Testz
 * packageName: com.example.administrator.testz
 */



public interface MessageListener {
    // 获取充电柜信息
    void onGetBoxInfo(BatteryBox boxInfo);

//    // 获取充电槽静态信息
//    void onGetSlotStaticData(SlotInfo slotInfo);
//
//    // 获取充电槽动态信息
//    void onGetSlotDynamicData(SlotInfo slotInfo);

    // 获取充电槽静态信息
    void onGetSlotStaticData(SlotStaticInfo slotInfo);

    // 获取充电槽动态信息
    void onGetSlotDynamicData(SlotDynamicInfo dynamicSlotInfo);
}
