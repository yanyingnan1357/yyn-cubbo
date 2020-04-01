package com.ke.cubbo.client;

import com.ke.cubbo.common.Decoder;
import com.ke.cubbo.common.Encoder;
import com.ke.cubbo.common.RequestDTO;
import com.ke.cubbo.common.ResponseDTO;
import com.ke.cubbo.registry.Discover;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * RPC通信客户端,启动RPC通信服务,创建TCP连接,发送请求,接受响应
 **/
public class Client extends SimpleChannelInboundHandler<ResponseDTO> {

    private RequestDTO request;
    private ResponseDTO response;
    private Object object = new Object();
    private Discover discover;//试试用Spring直接注入……^_^

    public Client(RequestDTO request, Discover discover) {
        this.request = request;
        this.discover = discover;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseDTO msg) throws Exception {
        this.response = msg;
        synchronized (object) {  //获取到response后唤醒sendAndReceive()
            ctx.flush();
            object.notifyAll();
        }
    }

    public ResponseDTO sendAndReceive() throws InterruptedException {
        Bootstrap client = new Bootstrap();
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            client.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new Encoder(RequestDTO.class))
                                    .addLast(new Decoder(ResponseDTO.class))
                                    .addLast(Client.this);
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);
            String serverAddress = discover.discover();
            String host = serverAddress.split(":")[0];
            int port = Integer.valueOf(serverAddress.split(":")[1]);
            ChannelFuture future = client.connect(host, port).sync();
            System.out.println("client发送请求完成:" + request);
            future.channel().writeAndFlush(request).sync();
            synchronized (object) {  //写入request后，等待channelRead0()获取response
                object.wait();
            }
            if (response != null)
                future.channel().closeFuture().sync();
            return response;
        } finally {
            loopGroup.shutdownGracefully();
        }
    }

    /**
     * 重写异常处理方法 若client端出现异常，直接关闭
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();//异常直接关闭
    }
}
