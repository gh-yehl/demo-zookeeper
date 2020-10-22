package com.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKQueryChildren {

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
     * 同步方式获取子节点
     * @throws Exception
     */
    @Test
    public void getChildren1() throws Exception{
        List<String> list = zooKeeper.getChildren("/getnode", false);
        for(String str: list) {
            System.out.println(str);
        }
    }

    /**
     * 异步方式获取子节点
     * @throws Exception
     */
    @Test
    public void getChildren2() throws Exception{
        zooKeeper.getChildren("/getnode", false, new AsyncCallback.ChildrenCallback() {
            @Override
            public void processResult(int rc , String path, Object ctx, List<String> chlidren) {
                //0 代表创建成功
                System.out.println("rc ===>"+ rc);
                System.out.println("path ===>"+ path);
                //上下文参数
                System.out.println("ctx ===>"+ ctx);

                for(String str: chlidren) {
                    System.out.println(str);
                }
            }
        }, "I am contect");
        //sleep 5 secnods
        Thread.sleep(5000);
        System.out.println("获取子节点结束");
    }
}
