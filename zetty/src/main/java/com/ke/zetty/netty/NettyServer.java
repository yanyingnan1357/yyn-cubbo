package com.ke.zetty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {

        //以下两个对象会进入无限循环中
        //默认返回的线程数是：the number of processors available to the Java virtual machine * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建服务端启动对象并配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)//配置两个线程组
                    .channel(NioServerSocketChannel.class)//配置通道类型
                    .option(ChannelOption.SO_BACKLOG, 128)//配置线程队列等待链接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//配置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {//配置处理器为：workerGroup的某个EventLoop所对应的管道
                        @Override//给pipeline设置处理器
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast(new StringEncoder())
                                    .addLast(new StringDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("服务端设置完毕...");

            //绑定端口并同步
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("服务端优雅的关闭了...");
        }
    }
}
