package comtest.example.admin.ztesta.nettyHeartbeat.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author ReStartLin
 * @data 2019/2/20 17:00
 * @classDesc: 功能描述:
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {
    private int counter = 0;
    private NettyServer nettyServer;

    public NettyServerHandler() {
    }

    public NettyServerHandler(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    //这个地方是发送监听 和接收监听
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        System.out.println("Client say :" + o.toString());

        //这个地方是服务端处理业务逻辑
        if (o.toString() != null){
            ctx.channel().writeAndFlush("server hava get data"+o.toString() + System.getProperty("line.separator"));
        }
        //重置心跳次数
        counter = 0;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String clientName = ctx.channel().remoteAddress().toString();
        System.out.println("RemoteAddress : " + clientName +" active !");
        nettyServer.setClient(clientName);
        nettyServer.setChannel(clientName,ctx.channel());
        super.channelActive(ctx);
        counter = 0;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("if "+event.state()+"=="+IdleState.READER_IDLE);
                //空闲4s后触发
                if (counter >= 10) {
                    ctx.channel().close().sync();
                    String clientName = ctx.channel().remoteAddress().toString();
                    System.out.println("" + clientName + "offline");
                    nettyServer.removeClient(clientName);
                    //判断是否有在线的
                    if (nettyServer.getClientMapSize()) {
                        //System.out.println("还有设备在线,因此跳过离线操作");
                        return;
                    }
                   // System.out.println("没有设备在线,执行离线操作");
                } else {
                    counter++;
                    System.out.println("loss " + counter + " countHB");
                }
            }
        }
    }
}
