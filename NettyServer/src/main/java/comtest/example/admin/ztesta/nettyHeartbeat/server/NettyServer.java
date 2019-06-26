package comtest.example.admin.ztesta.nettyHeartbeat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ReStartLin
 * @data 2019/2/20 16:49
 * @classDesc: 功能描述:
 */
public class NettyServer {
    private int port = 20803;

    //维护设备在线的表
    private Map<String, Integer> clientMap = new HashMap<>();

    public synchronized void setClient(String name) {
        this.clientMap.put(name, 1);
    }

    public synchronized void removeClient(String name) {
        this.clientMap.remove(name);
    }

    //判断连接池里面是否有东西
    public synchronized boolean getClientMapSize() {
        return this.clientMap.size() > 0;
    }

    /*
     * 维护设备连接的map 用于推送消息     nettyServer.setChannel(o.toString(),ctx.channel());
     */
    private Map<String, Channel> channelMap = new HashMap<>();

    public synchronized void setChannel(String name, Channel channel) {
        this.channelMap.put(name, channel);
    }

    public synchronized Map<String, Channel> getChannelMap() {
        return this.channelMap;
    }

    /**
     * 发送消息给下游设备
     *
     * @param msg 消息内容
     * @return 成功 false  失败true
     */
    public boolean writeMsg(String msg) {
        boolean errorFlag = false;
        Map<String, Channel> channelMap = getChannelMap();
        if (channelMap.size() == 0) {
            return true;
        }
        Set<String> keySet = channelMap.keySet();
        for (String key : keySet) {
            try {
                Channel channel = channelMap.get(key);
                if (!channel.isActive()) {
                    errorFlag = true;
                    continue;
                }
                //真正发送数据是在这个地方
                channel.writeAndFlush(msg + System.getProperty("line.separator"));
            } catch (Exception e) {
                errorFlag = true;
            }

        }
        return errorFlag;
    }

    public void bind() {
        System.out.println("service start successful ");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
//                        pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                        //特殊分隔符 \0
                        //    pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE,
                        //            Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes())));
                        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());
                        pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast("handler", new NettyServerHandler(NettyServer.this));
                    }
                });
        try {
            ChannelFuture f = bootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        final NettyServer nettyServer = new NettyServer();
        new Thread() {
            @Override
            public void run() {
                nettyServer.bind();
            }
        }.start();
        Scanner scanner = new Scanner(System.in);
        String msg = "";
        while (!(msg = scanner.nextLine()).equals("exit")) {
            System.out.println(nettyServer.writeMsg(msg));
        }
    }
}
