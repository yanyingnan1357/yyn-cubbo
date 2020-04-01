package com.ke.zetty.nio;

/**
 * Selector 能够检测多个注册的通道上是否有事件发生(注意:多个 Channel 以事件的方式可以注册到同一个 Selector)，如果有事件发生，便获取事件然后针对每个事件进行相应的处理。
 * 这样就可以只用一个单线程去管 理多个通道，也就是管理多个连接和请求。
 */
public class NIOselector {
    //Netty 的 IO 线程 NioEventLoop 聚合了 Selector(选择器，也叫多路复用器)，可以同时并发处理成百上千个客户端连接。

}
