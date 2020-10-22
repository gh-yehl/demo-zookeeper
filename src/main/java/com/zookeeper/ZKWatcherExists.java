package com.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKWatcherExists {

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
     * 监听节点的变化 - 只能监听一次
     * @throws Exception
     */
    @Test
    public void watch1() throws Exception{
        zooKeeper.exists("/watcher", true);
        System.out.println("stat is ======================>start");
        Thread.sleep(50000);
        System.out.println("stat is ======================>end");
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
                    zooKeeper.exists("/watcher",this);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.exists("/watcher", watcher);
        Thread.sleep(80000);
    }


    /**
     * 一个节点 注册多个 监听器
     */
    @Test
    public void watch3() throws Exception{
        zooKeeper.exists("/watcher", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("event type =========>1");
                System.out.println("event type =========>" + event.getType());
                System.out.println("event path =========>" + event.getPath());
            }
        });

        zooKeeper.exists("/watcher", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("event type =========>2");
                System.out.println("event type =========>" + event.getType());
                System.out.println("event path =========>" + event.getPath());
            }
        });


        Thread.sleep(50000);
        System.out.println("程序结束");
    }
}
