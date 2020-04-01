package com.ke.zetty.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * nio聊天的例子
 *
 * */
public class ChatClient {

    private final String HOST = "127.0.0.1";
    private final int PORT = 7777;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public ChatClient() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        username = socketChannel.getLocalAddress().toString();
        System.out.println(username + " 恭喜你上线成功");
    }

    //向服务器发送消息
    private void sendInfo(String info) {
        info = username + " 说:" + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取从服务器端回复的消息
    private void readInfo() {
        try {
            int select = selector.select();

            if (select > 0) {//有可以用的通道
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    if (selectionKey.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(100);
                        socketChannel.read(buffer);
                        //把读到的缓冲区的数据转成字符串
                        String msg = new String(buffer.array());
                        //不加trim buffer用不完，尾部就会有很多空格
                        System.out.println(msg.trim());
                    }
                    //selector不会自己删除selectedKeys()集合中的selectionKey，
                    //如果不人工remove()，将导致下次select()的时候selectedKeys()中仍有上次轮询留下来的信息，
                    //假设这次轮询时该通道并没有准备好，却又由于上次轮询未被remove()的原因被认为已经准备好了，将会重复消费
                    keyIterator.remove();
                }
            } else {
                System.out.println("当前无可用通道");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient chatClient = new ChatClient();
        new Thread(() -> {
            while (true) {
                chatClient.readInfo();
                try {
                    Thread.currentThread().sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //发送数据给服务器端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }

    }
}
