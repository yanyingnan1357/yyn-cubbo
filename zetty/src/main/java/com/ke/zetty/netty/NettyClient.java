package com.ke.zetty.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NettyClient {
    public static void main(String[] args) throws Exception {

        //客户端只需要一个时间循环器
        EventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            //创建服务端启动对象并配置参数
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)//配置一个线程组
                    .channel(NioSocketChannel.class)//配置通道类型
                    .handler(new ChannelInitializer<SocketChannel>() {//配置处理器为：workerGroup的某个EventLoop所对应的管道
                        @Override//给pipeline设置处理器
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast(new StringEncoder())
                                    .addLast(new StringDecoder())
                                    .addLast(new NettyClientHandler());
                        }
                    });
            System.out.println("客户端设置完毕...");
            //连接端口并同步
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
//            channelFuture.channel().closeFuture().sync();

            //完成控制台输入
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                channelFuture.channel().writeAndFlush(in.readLine());
            }
        }finally {
            loopGroup.shutdownGracefully();
            System.out.println("客户端优雅的关闭了...");
        }
    }
}
