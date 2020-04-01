package com.ke.zetty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * nio测试
 *
 * */
public class TestClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
        //连接服务器(需要时间)
        if(!socketChannel.connect(inetSocketAddress)){
            while (!socketChannel.finishConnect()){
                System.out.println("干点其他事情");
            }
        }
        String s = "yynnetty";
        //产生一个与字节数组一样大小的buffer
        ByteBuffer byteBuffer = ByteBuffer.wrap(s.getBytes());
        socketChannel.write(byteBuffer);
        System.in.read();
    }
}
