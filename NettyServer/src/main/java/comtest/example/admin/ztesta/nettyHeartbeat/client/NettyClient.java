package comtest.example.admin.ztesta.nettyHeartbeat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author ReStartLin
 * @data 2019/2/20 17:34
 * @classDesc: 功能描述:
 */
public class NettyClient {
    private String host;
    private int port;

    private Channel channel;

    private Bootstrap b = null;

    public NettyClient(String host, int port) {

        this.host = host;
        this.port = port;
        init();
    }

    private void init() {
       // System.out.println("客户端启动");
        b = new Bootstrap();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //解决TCP粘包拆包的问题，以特定的字符结尾（$_）
//                        pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                       // pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes())));
                        pipeline.addLast("framer",new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                        //字符串解码和编码
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());
                        //心跳检测
                        pipeline.addLast(new IdleStateHandler(0,4,0, TimeUnit.SECONDS));
                        //客户端的逻辑
                        pipeline.addLast("handler", new NettyClientHandler(NettyClient.this));

                    }
                });
    }

    public void start() {
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

    public  Channel getChannel() {
        return channel;
    }

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 20803);
        nettyClient.start();
    }

}
