package com.ke.zetty.bio;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * bio测试
 * 只写服务端，使用telnet localhost 6666 创建多个client 与 本服务连接，并用线程池管理这些链接
 *
 * */
public class BIOTest {
    public static void main(String[] args) throws IOException {

        //初始化一个线程池
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("ThreadPoolContainer-thread-%d").build();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 3,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(4096), threadFactory,
                        new ThreadPoolExecutor.AbortPolicy());

        //创建 ServerSocket
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了...");
        while (true) {
            System.out.println("线程信息id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
            //监听，等待客户端连接
            System.out.println("等待下一个client连接...");

            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");
            //每次来一个客户端就创建一个新的线程，与客户端进行通讯
            executor.execute(() -> handler(socket));
        }

    }

    private static void handler(Socket socket) {
        try {
            byte[] bytes = new byte[1024];
            //通过 socket 获取输入流
            InputStream inputStream = socket.getInputStream();
            //循环的读取客户端发送的数据
            while (true) {
                System.out.println("线程信息id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
                System.out.println("read...");
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read)); //输出客户端发送的数据
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("关闭和client连接");
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
