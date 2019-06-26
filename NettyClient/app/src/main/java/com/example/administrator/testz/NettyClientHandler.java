package com.example.administrator.testz;

/**
 * Created by wrs on 2019/6/26,11:44
 * projectName: Testz
 * packageName: com.example.administrator.testz
 */


import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

//wangnetty
public class NettyClientHandler extends SimpleChannelInboundHandler {
    private ChannelHandlerContext context;
    private MessageListener listener;
    private NettyClient nettyClinet;
    private String tenantId;
    private int attempts = 0;


    //wangnetty
    public NettyClientHandler(MessageListener messageListener, NettyClient nettyClinet) {
        this.listener = messageListener;
        this.nettyClinet = nettyClinet;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("channelRead0 service send message: " + o.toString());
        ToastUtils.showShort("消息错误");
    }

    // 建立连接就发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("output connected!!");
        this.context = ctx;
        attempts = 0;
    }

    //断开netty连接
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("offline。。。。。。");
        //使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        if (attempts < 12) {
            attempts++;
        }
        int timeout = 2 << attempts;
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                nettyClinet.start();
            }
        }, timeout, TimeUnit.SECONDS);
        ctx.fireChannelInactive();

    }

    //连接中
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("READER_IDLE");
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                /*向服务端发送心跳包,保持长连接*/
                String s = "Netty1Client" + System.getProperty("line.separator");
                ctx.channel().writeAndFlush(s);
                // System.out.println("心跳发送成功!");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("ALL_IDLE");
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        //Log.e(TAG, "channelReadComplete");
    }

    /*
    要发送新消息，我们需要分配一个包含消息的新缓冲区。
    我们要写一个32位整数，因此我们需要一个容量至少为4个字节的ByteBuf。
    通过ChannelHandlerContext.alloc（）
    获取当前的ByteBufAllocator并分配一个新的缓冲区。
     */
    public void sendData(String userJson) {
        if (null != context && context.channel().isActive()) {
            context.channel().writeAndFlush(userJson + System.getProperty("line.separator"));
        }
    }


    private void writeToFile(String s) {
        byte[] buf = s.getBytes();
    }

    private synchronized void parseData(String data) throws Exception {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(data);
        } catch (Exception e) {
            ToastUtils.showShort("数据转JSON失败:" + e.toString());
            return;
        }
        if (jsonObject.getInt("resultCode") == 0) {
            switch (jsonObject.getString("requestType")) {
                case "BOX_INFO":
                    System.out.println("BOX_INFO");
                    break;
                case "SLOT_STATIC_DATA":
                    System.out.println("SLOT_STATIC_DATA");
                    break;
                case "SLOT_DYNAMIC_DATA":
                    System.out.println("SLOT_DYNAMIC_DATA");
                    break;
            }
        }
    }

    /**
     * 解析电池柜信息
     *
     * @param jsonObject
     * @return
     */
    private void parseToBoxInfo(JSONObject jsonObject) {
        System.out.println("parseToBoxInfo");
    }

    /**
     * 解析电池静态信息
     *
     * @param jsonObject
     * @return
     */
    private void parseToStaticBatteryInfo(JSONObject jsonObject) {
        System.out.println("parseToStaticBatteryInfo");
    }

    private void parseToDynamicBatteryInfo(JSONObject jsonObject) {
        System.out.println("parseToDynamicBatteryInfo");
    }


}
