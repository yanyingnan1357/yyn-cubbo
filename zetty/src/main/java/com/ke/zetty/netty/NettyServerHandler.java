package com.ke.zetty.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //定义服务端处理客户端msg后要返回给客户端的msg
    private StringBuilder msg;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("客户端" + ctx.channel().remoteAddress() + "发送：" + msg);
        StringBuilder sb = new StringBuilder(String.valueOf(msg));
        //完成客户端输入数据反转
        this.msg = sb.reverse();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //数据写入缓冲并刷新
        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}
