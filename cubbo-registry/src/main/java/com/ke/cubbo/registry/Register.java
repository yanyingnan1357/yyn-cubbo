package com.ke.cubbo.registry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Register {
    public static final Logger LOGGER = LoggerFactory.getLogger(Register.class);
    private String registryAddress;
    private ZooKeeper zooKeeper;

    public void createNode(String serverAddress) throws IOException {
        zooKeeper = new ZooKeeper(registryAddress, Constant.SESSION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                return;//注册端我就不监听事件了
            }
        });

        if (zooKeeper != null) {
            try {
                Stat stat = zooKeeper.exists(Constant.REGISTRY_PATH, false);
                if (stat == null)
                    zooKeeper.create(Constant.REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                zooKeeper.create(Constant.DATA_PATH, serverAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            } catch (KeeperException e) {
                LOGGER.error("register is false!!!");
                e.printStackTrace();
                System.exit(-1);
            } catch (InterruptedException e) {
                LOGGER.error("register is false!!!");
                e.printStackTrace();
                System.exit(-1);
            }
        } else {
            LOGGER.info("zooKeeper connect is false!!!");
            System.exit(-1);
        }
    }
}
