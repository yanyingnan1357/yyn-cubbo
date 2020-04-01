package com.ke.cubbo.registry;

import lombok.Getter;
import lombok.Setter;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Setter
@Getter
public class Discover {
    public static final Logger LOGGER = LoggerFactory.getLogger(Register.class);

    private String registryAddress;
    private volatile List<String> serviceList = new ArrayList<String>();//加上volatile
    private ZooKeeper zooKeeper;

    public Discover(String registryAddress) throws IOException {
        this.registryAddress = registryAddress;
        zooKeeper = new ZooKeeper(registryAddress, Constant.SESSION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println(".........llll....." + watchedEvent.getType());
                if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged)
                    watchNode();
            }
        });
        System.out.println(".........llll");
        watchNode();//监听，更新serviceList
    }

    private void watchNode() {
        try {
            List<String> nodeList = zooKeeper.getChildren(Constant.REGISTRY_PATH, true);
            List<String> tempList = new ArrayList<String>();
            for (String node : nodeList) {
                byte[] bytes = zooKeeper.getData(Constant.REGISTRY_PATH + "/" + node, true, null);
                tempList.add(new String(bytes));
            }
            this.serviceList = tempList;
        } catch (KeeperException e) {//如果不存在具有给定路径的节点，则将抛出带有错误代码KeeperException.NoNode的KeeperException
            LOGGER.error("{}", e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            LOGGER.error("{}", e);
            e.printStackTrace();
        }
    }

    public String discover() {
        int size = serviceList.size();
        if (size > 0) {
            int index = new Random().nextInt(size);
            return serviceList.get(index);//serviceList存放的是所有server端的地址及端口号，在这里只有一个就是本机10.33.132.150:9090
        }
        throw new RuntimeException("没有找到对应的服务器");
    }
}
