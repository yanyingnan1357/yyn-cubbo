package com.ke.cubbo.registry;

public interface Constant {

    int SESSION_TIMEOUT = 10000; //new zookeeper 客户端连接的超时时间

    String REGISTRY_PATH = "/cubbo"; //单独建一个zk节点 保存远程通信服务端的地址信息

    String DATA_PATH = REGISTRY_PATH + "/serverAddress";// 远程服务器地址数据存放的具体目录
}
