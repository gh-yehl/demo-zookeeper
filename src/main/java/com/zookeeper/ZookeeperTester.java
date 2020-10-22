package com.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZookeeperTester {
    public static void main(String[] args) {
        try{
            //计数器对象
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            ZooKeeper zooKeeper = new ZooKeeper("106.54.242.3:2181", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(event.getState() == Event.KeeperState.SyncConnected) {
                        System.out.println("创建链接成功");
                        //通知主线程 向下执行
                        countDownLatch.countDown();
                    }

                }
            });

            //阻塞主线程，等待连接对象创建成功
            countDownLatch.await();
            //
            System.out.println("打印会话ID=====>" + zooKeeper.getSessionId());
            System.out.println("打印节点/hongli的所有子节点数量====>"+zooKeeper.getAllChildrenNumber("/hongli"));

            //资源释放
            zooKeeper.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
