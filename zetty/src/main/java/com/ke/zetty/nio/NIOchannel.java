package com.ke.zetty.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel 在 NIO 中是一个接口：public interface Channel extends Closeable{}
 */
public class NIOchannel {
    //1、常 用 的 Channel 类 有 : FileChannel 、 DatagramChannel 、 ServerSocketChannel 和 SocketChannel 。 【ServerSocketChannel 类似 ServerSocket , SocketChannel 类似 Socket】
    //2、FileChannel 用于文件的数据读写，DatagramChannel 用于 UDP 的数据读写，ServerSocketChannel 和 SocketChannel 用于 TCP 的数据读写。

    //基于chnnel buffer进行一个文件的拷贝
    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("/Users/yanyingnan/brushCode/yyn-cubbo/zetty/src/main/resources/static/a.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("/Users/yanyingnan/brushCode/yyn-cubbo/zetty/src/main/resources/static/b.txt");
        FileChannel inputStreamChannel = fileInputStream.getChannel();
        FileChannel outputStreamChannel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        while (true){

            //每次读一部分数据拷贝完成都要清除
            byteBuffer.clear();

            int read = inputStreamChannel.read(byteBuffer);
            System.out.println("read: " + read);
            if(read == -1) {
                break;
            }

            //buffer切换写模式
            byteBuffer.flip();

            outputStreamChannel.write(byteBuffer);

        }

        //转换通道：更快的方法就一行 transferFrom底层使用了零拷贝
        /*This method is potentially much more efficient than a simple loop
        * that reads from the source channel and writes to this channel.  Many
        * operating systems can transfer bytes directly from the source channel
        * into the filesystem cache without actually copying them.*/
        outputStreamChannel.transferFrom(inputStreamChannel, 0, inputStreamChannel.size());

        inputStreamChannel.close();
        outputStreamChannel.close();

        fileInputStream.close();
        fileOutputStream.close();
    }

}
