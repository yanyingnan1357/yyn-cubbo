package com.ke.cubbo.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.annotation.Resource;

//编码, 因为是请求/响应对象的传递,编码为字节数组,发送到服务器再解码即可
public class Encoder extends MessageToByteEncoder {

//    @Resource
//    private SerializeKryo serializeKryo;
//
//    @Resource
//    private SerializeProtostuff serializeProtostuff;

    private Class transformClass;

    public Encoder(Class transformClass) {
        this.transformClass = transformClass;
    }

    //继承netty中的MessageToByteEncoder类，而后重写encode方法 即可序列化请求消息为字节数组
    @Override
    protected void encode(ChannelHandlerContext chc, Object msg, ByteBuf out) throws Exception {
        if (transformClass.isInstance(msg)) {
//            byte[] bytes = SerializeProtostuff.serialize(msg);
            byte[] bytes = SerializeKryo.serialize(msg);
            out.writeBytes(bytes);
        }
    }
}
