package com.ke.cubbo.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.annotation.Resource;
import java.util.List;

//解码, 接受到的数据是字节数组,需要把数组转换为对应的请求/响应消息对象
public class Decoder extends ByteToMessageDecoder {

//    @Resource
//    private SerializeKryo serializeKryo;
//
//    @Resource
//    private SerializeProtostuff serializeProtostuff;

    private Class transformClass;

    public Decoder(Class transformClass) {
        this.transformClass = transformClass;
    }

    //继承netty中的ByteToMessageDecoder类，而后重写decode方法 即可完成反序列化
    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readableBytes();
        if (length < 4) {//保证所有的消息都完全接受完成
            return;
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
//        Object object = SerializeProtostuff.deserialize(bytes, transformClass);
        Object object = SerializeKryo.deserialize(bytes, transformClass);
        out.add(object);
        chc.flush();
    }
}


//如何解决序列化过程中的拆包粘包：
//
// 1.消息定长
//
// 2.在包尾增加一个标识，通过这个标志符进行分割
//
// 这里使用>3.将消息分为两部分，也就是消息头和消息尾，消息头中写入要发送数据的总长度，通常是在消息头的第一个字段使用int值来标识发送数据的长度。