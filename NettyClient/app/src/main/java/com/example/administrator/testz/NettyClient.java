package com.example.administrator.testz;

/**
 * Created by wrs on 2019/6/26,11:43
 * projectName: Testz
 * packageName: com.example.administrator.testz
 */


import android.util.Log;


import com.example.administrator.model.HeartBeatBean;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

//wangnetty
public class NettyClient {
    private static final String TAG = NettyClient.class.getSimpleName();
    private static final long RETRY_TIME = 5 * 1000L; // 定时重新连接时间
    private static final int CONNECT_TIMEOUT = 10 * 1000; // TCP连接超时时间, 10秒
    private boolean isRunning = true;
    private String host;
    private int port;
    private boolean isConnected = false;
    private Channel channel;
    private NettyClientHandler handler;
    private NettyListener listener;
    private MessageListener messageListener;
    private Bootstrap b = null;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRunning && !isConnected) {
                connectToServer(host, port);
                try {
                    Thread.sleep(RETRY_TIME);
                } catch (Exception e) {

                }
            }
        }
    };

    public void connect() {
        Thread retryThread = new Thread(connectRunnable);
        retryThread.start();
    }


    public void connectToServer(final String host, int port) {
        Log.e(TAG, "开始连接...");
        // NioEventLoopGroup是一个处理I / O操作的多线程事件循环
        EventLoopGroup workerGroup = new NioEventLoopGroup();
       handler =  new NettyClientHandler(messageListener, NettyClient.this);
        try {
            b = new Bootstrap();
            b.group(workerGroup)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //解决TCP粘包拆包的问题，以特定的字符结尾（$_）
//                        pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                         //   pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes())));
                            //字符串解码和编码
                         pipeline.addLast("framer",new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                         pipeline.addLast("decoder", new StringDecoder());
                         pipeline.addLast("encoder", new StringEncoder());
                            //心跳检测
                            pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                            //客户端的逻辑
                            pipeline.addLast("handler",handler);

                        }
                    });

            //在这个地方进行的回调操作
            ChannelFuture f = b.connect(host, port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        Log.e(TAG, "连接成功");
                        listener.onConnected();
                    } else {
                        Log.e(TAG, "连接失败");
                        listener.onDisConnect();
                    }
                }
            }).sync();
            isConnected = true;
            channel = f.channel();
            channel.closeFuture().sync();
            listener.onDisConnect();
            isConnected = false;
        } catch (Exception e) {
            isConnected = false;
            Log.e(TAG, "Connect error : " + e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    //心跳 APP连接维持检测，每30秒上报一次。
    public void sendHeartBeatData() {
        HashMap<String, String> emplyees = new HashMap<>();
        emplyees.put("deviceSn", "113126161323");

        HeartBeatBean heartBeatBean = new HeartBeatBean("HEARTBEAT", emplyees);
        Gson gson = new Gson();
        String userJson = gson.toJson(heartBeatBean);
        System.out.println("wrs:" + userJson);
        sendData(userJson);
    }

    public void sendData(String userJson) {
        if (null != handler) {
            handler.sendData(userJson);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setNettyListener(NettyListener listener) {
        this.listener = listener;
    }

    public void start() {
        System.out.println("client start()");
        ChannelFuture f = b.connect(host, port);
        //断线重连
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    final EventLoop loop = channelFuture.channel().eventLoop();
                    loop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            System.err.println("not connect service...");
                            start();
                        }
                    }, 1L, TimeUnit.SECONDS);
                } else {
                    channel = channelFuture.channel();
                    System.err.println("connected...");
                }
            }
        });
    }
}
