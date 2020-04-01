package com.ke.cubbo.server;

import com.ke.cubbo.common.RequestDTO;
import com.ke.cubbo.common.ResponseDTO;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;


@Setter
@Getter
@NoArgsConstructor
/**
 * 处理RPC客户端请求,调用服务提供者的具体方法,响应执行结果
 **/
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Object> serviceBeanMap;

    public ServerHandler(Map<String, Object> serviceBeanMap) {
        this.serviceBeanMap = serviceBeanMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ServerHandler进行channelRead中...");
        System.out.println(msg);
        RequestDTO request = (RequestDTO) msg;
        ResponseDTO response = handler(request);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);//告诉客户端,关闭socket连接 完成一次通信
    }

    private ResponseDTO handler(RequestDTO request) {
        ResponseDTO response = new ResponseDTO();
        response.setResponseId(UUID.randomUUID().toString());
        response.setRequestId(request.getRequestId());

        try {
            String className = request.getClassName();
            String methodName = request.getMethodName();
            Object[] parameters = request.getParameters();
            Class<?>[] parameterTypes = request.getParameterTypes();

            Class clz = Class.forName(className);
            Object serviceBean = serviceBeanMap.get(className);//先获取到实现类，全限定名
            if (serviceBean == null) {
                throw new RuntimeException("没有找到对应的serviceBean:" + className + ":beanMap:" + serviceBeanMap);
            }
            Method method = clz.getMethod(methodName, parameterTypes);//再反射调用方法
            if (method == null) {
                throw new RuntimeException("没有找到对应的方法");
            }
            Object result = method.invoke(serviceBean, parameters);//反射调用方法得到调用结果
            response.setSuccess(true);
            response.setResult(result);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setThrowable(e);
            e.printStackTrace();
        }
        return response;
    }
}
