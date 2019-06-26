package com.example.administrator.testz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements MessageListener, NettyListener {
    private static final String HOST = "10.0.2.107";  //服务器的ip地址
    private static final int PORT = 20803;              ///指定的端口号
    private NettyClient mNettyClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNettyClient = new NettyClient(HOST, PORT);
        mNettyClient.setNettyListener(this);
        mNettyClient.setMessageListener(this);
        if (!mNettyClient.isConnected()) {
            mNettyClient.connect();
        }

        Button button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNettyClient.sendHeartBeatData();
            }
        });
    }

    @Override
    public void onGetBoxInfo(BatteryBox boxInfo) {

    }

    @Override
    public void onGetSlotStaticData(SlotStaticInfo slotInfo) {

    }

    @Override
    public void onGetSlotDynamicData(SlotDynamicInfo dynamicSlotInfo) {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisConnect() {

    }
}
