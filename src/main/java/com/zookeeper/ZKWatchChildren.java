package com.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKWatchChildren {

    ZooKeeper zooKeeper;
    @Before
    public void beforeFuc() throws Exception{
        System.out.println("before");
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper("106.54.242.3:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if(event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("创建链接成功");
                    //通知主线程 向下执行
                    countDownLatch.countDown();
                }
                System.out.println("event type =========>" + event.getType());
                System.out.println("event path =========>" + event.getPath());

            }
        });

        //阻塞主线程，等待连接对象创建成功
        countDownLatch.await();
    }

    @After
    public void AfterFuc() throws Exception{
        System.out.println("after");
        zooKeeper.close();
    }


    /**
     * watcher 只能监听一次
     * 实现 持续监听
     * @throws Exception
     */
    @Test
    public void watch2() throws Exception{
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try{
                    System.out.println("event type =========>" + event.getType());
                    System.out.println("event path =========>" + event.getPath());
                    if(event.getType() == Event.EventType.NodeChildrenChanged) {
                        zooKeeper.getChildren("/watcher",this);
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.getChildren("/watcher", watcher);
        Thread.sleep(300000);
    }

}
