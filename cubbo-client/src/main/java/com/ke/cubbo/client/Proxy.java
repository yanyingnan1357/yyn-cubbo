package com.ke.cubbo.client;

import com.ke.cubbo.common.RequestDTO;
import com.ke.cubbo.common.ResponseDTO;
import com.ke.cubbo.registry.Discover;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 对接口创建一个动态代理对象,在invoke方法创建rpc客户端并且发送网络通信请求，并接受响应结果
 **/
@Getter
@Setter
public class Proxy {
    private Discover discover;

    @SuppressWarnings("all")
    public <T> T getInstance(final Class<T> interfaceClass) {
        T instance = (T) java.lang.reflect.Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                RequestDTO request = new RequestDTO();
                String className = method.getDeclaringClass().getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(className);
                request.setParameters(args);
                request.setParameterTypes(parameterTypes);
                request.setMethodName(method.getName());

                ResponseDTO response = new Client(request, discover).sendAndReceive();//创建rpc客户端并且发送网络通信请求，接受返回消息
                return response.getResult();
//                return new Object();//testGet()方法将会执行不成功   因为testGet()方法要求有返回值
            }
        });
        return instance;
    }
}
