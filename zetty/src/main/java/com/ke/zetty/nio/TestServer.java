package com.ke.zetty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * nio测试
 *
 * */
public class TestServer {
    public static void main(String[] args) throws Exception {

        //创建 ServerSocketChannel -> ServerSocket //绑定一个端口 6666, 在服务器端监听 //并设置为非阻塞（怎么理解？）
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));//InetAddress:类的主要作用是封装IP及DNS,InetSocketAddress类主要作用是封装端口 他是在在InetAddress基础上加端口
        serverSocketChannel.configureBlocking(false);

        //创建一个 Selecor 对象
        Selector selector = Selector.open();

        //把 serverSocketChannel 注册到 selector 关心 事件为 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true){
            if(selector.select(60000) == 0){//返回的是有事件发生的通道个数
                System.out.println("服务60秒未链接");
                continue;
            }

            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while(keyIterator.hasNext()){
                SelectionKey selectionKey = keyIterator.next();

                if(selectionKey.isAcceptable()){
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功 socketChannel:" + socketChannel.hashCode());
                    socketChannel.configureBlocking(false);
                    //将 socketChannel 注册到 selector, 关注事件为 OP_READ，同时给 socketChannel //关联一个 Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(10));
                }

                if(selectionKey.isReadable()){
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                    ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                    socketChannel.read(byteBuffer);
                    System.out.println("从客户端获取：" + new String(byteBuffer.array()));
                    //不加这一行会死循环
//                    socketChannel.register(selector, SelectionKey.OP_WRITE, ByteBuffer.allocate(10));
                }
                //移除本次遍历的SelectionKey 防止重复操作
                //如果不移除 39行会报npe，因为selectionKey会一直有监听到accept事件，但没有新的客户端连接socketChannel就是null
                keyIterator.remove();
            }
        }
    }
}
