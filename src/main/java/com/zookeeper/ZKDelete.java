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

public class ZKDelete {

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
     * 同步删除
     * @throws Exception
     */
    @Test
    public void delete1() throws Exception{
        //arg1: 节点路径
        //arg2: 版本号 -1 代表不指定版本号更新
        zooKeeper.delete("/deletenode", -1);

        System.out.println("同步删除结束");
    }

    /**
     * 异步删除
     * @throws Exception
     */
    @Test
    public void delete2() throws Exception{
        //arg1: 节点路径
        //arg2: 版本号 -1 代表不指定版本号更新
        //arg3: 异步方式
        zooKeeper.delete("/deletenode", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {

            }
        }, "I am context");

        Thread.sleep(5);
        System.out.println("异步删除结束");
    }
}
