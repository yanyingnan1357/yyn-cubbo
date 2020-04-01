package com.ke.cubbo.server;

import com.ke.cubbo.common.Decoder;
import com.ke.cubbo.common.Encoder;
import com.ke.cubbo.common.RequestDTO;
import com.ke.cubbo.common.ResponseDTO;
import com.ke.cubbo.registry.Register;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * 扫描所有标记了@Service的类,启动RPC服务
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Server implements ApplicationContextAware, InitializingBean {

    private final Map<String, Object> serviceBeanMap = new HashMap<String, Object>();//保存提供服务的方法, 其中key为类的全路径名, value是所有的实现类
    private Register register;
    private String serverAddress;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {//Spring容器启动完成后会执行该方法
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(Service.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object object : serviceBeanMap.values()) {
                String servicePath = object.getClass().getAnnotation(Service.class).value().getName();
                this.serviceBeanMap.put(servicePath, object);
            }
        }
        System.out.println("服务器: " + serverAddress + " 提供的服务列表: " + serviceBeanMap);
        //打印如下：
        //服务器: 10.33.132.150:9090 提供的服务列表: {houseServiceFacadeImpl=com.ke.yyn.server.HouseServiceFacadeImpl@564718df}
        //试试全局的serviceBeanMap 也就是 this.serviceBeanMap 打印如下：(保存了全限定名)
        //服务器: 10.33.132.150:9090 提供的服务列表: {com.ke.yyn.common.HouseServiceFacade=com.ke.yyn.server.HouseServiceFacadeImpl@564718df}
    }

    public void afterPropertiesSet() throws Exception {// 初始化完成后执行
        ServerBootstrap serverBootstrap = new ServerBootstrap();//创建服务端的通信对象
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();// 创建异步通信的事件组 用于建立TCP连接
        NioEventLoopGroup workGroup = new NioEventLoopGroup();// 创建异步通信的事件组 用于处理Channel(通道)的I/O事件

        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new Decoder(RequestDTO.class))
                                    .addLast(new Encoder(ResponseDTO.class))
                                    .addLast(new ServerHandler(serviceBeanMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            String host = serverAddress.split(":")[0];
            int port = Integer.valueOf(serverAddress.split(":")[1]);
            ChannelFuture future = serverBootstrap.bind(host, port).sync();//开启异步通信服务
            System.out.println("服务启动成功：" + future.channel().localAddress());
            System.out.println("请等待,向zk注册服务中...");
            register.createNode(serverAddress);//连接zk
            System.out.println("向zk注册服务地址信息完成^_^");
            future.channel().closeFuture().sync();//等待通信完成
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
