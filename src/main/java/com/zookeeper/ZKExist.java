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

public class ZKExist {

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
     * 通过查看 节点 是否存在
     * @throws Exception
     * return Stat: 节点不存在  返回 null
     */
    @Test
    public void exists1() throws Exception{
        Stat stat = new Stat();
        stat = zooKeeper.exists("/getnode/node1", false);
        System.out.println("stat is ======================>" + stat);
    }

    @Test
    public void exist2() throws Exception {
        zooKeeper.exists("/getnode/node1", false, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                System.out.println("this is rc =============>" + rc);
                if (stat != null) {
                    System.out.println("node version is =============>" + stat.getVersion());
                }
            }
        },"I am context");

        Thread.sleep(5000);
        System.out.println("节点查看结束");
    }
}
