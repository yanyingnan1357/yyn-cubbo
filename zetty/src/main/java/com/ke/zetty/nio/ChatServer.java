package com.ke.zetty.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * nio聊天的例子
 *
 * */
public class ChatServer {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private static final int PORT = 7777;

    //构造器完成初始化
    public ChatServer(){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听
    private void listen() {
        try {
            while (true){
                int select = selector.select();//可以设置上时间 不阻塞，做其他事儿
                if(select > 0){
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while(keyIterator.hasNext()){
                        SelectionKey selectionKey = keyIterator.next();
                        //accept事件触发监听
                        if(selectionKey.isAcceptable()){
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress() + " is onlin");
                        }
                        //read事件触发监听
                        if(selectionKey.isReadable()){
                            readFunc(selectionKey);
                        }
                        //selector不会自己删除selectedKeys()集合中的selectionKey，
                        //如果不人工remove()，将导致下次select()的时候selectedKeys()中仍有上次轮询留下来的信息，
                        //假设这次轮询时该通道并没有准备好，却又由于上次轮询未被remove()的原因被认为已经准备好了，将会重复消费
                        keyIterator.remove();
                    }
                } else {
                    System.out.println("最近无人上线...");
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void readFunc(SelectionKey selectionKey) {
        //获取SocketChannel
        SocketChannel socketChannel = null;
        try {
            socketChannel = (SocketChannel)selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
            int read = socketChannel.read(byteBuffer);
            if(read > 0){
                String msg = new String(byteBuffer.array());
                //不加trim buffer用不完，尾部就会有很多空格
                System.out.println("客户端" + msg.trim());
                //同步其它客户端
                sentToOthers(msg, socketChannel);
            }
        } catch (IOException e) {
            try {
                System.out.println(socketChannel.getRemoteAddress() + "离线了");
                selectionKey.cancel();
                socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //SocketChannel参数是用来排除本身
    private void sentToOthers(String msg, SocketChannel selfSocketChannel) throws IOException {
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            SelectableChannel channel = key.channel();
            if(channel instanceof SocketChannel && channel != selfSocketChannel) {
                SocketChannel socketChannel = (SocketChannel) channel;
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                socketChannel.write(buffer);
                System.out.println("向" + socketChannel.getRemoteAddress().toString() + "转发成功");
            }
        }
    }

    public static void main(String[] args) {
        //创建服务器对象
        ChatServer chatServer = new ChatServer();
        chatServer.listen();
    }
}
