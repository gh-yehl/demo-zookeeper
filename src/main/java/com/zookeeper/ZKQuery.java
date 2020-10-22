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

public class ZKQuery {

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
     * 同步读取数据
     * @throws Exception
     */
    @Test
    public void get1() throws Exception{
        Stat stat = new Stat();
        byte[] bytes = zooKeeper.getData("/getnode", false, stat);
        System.out.println(new String(bytes));
        System.out.println("version:" + stat.getAversion() + ";   update time"+stat.getMtime());

    }

    @Test
    public void get2() throws Exception{
        zooKeeper.getData("/getnode", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] bytes, Stat stat) {
                //0 代表创建成功
                System.out.println("rc ===>"+ rc);
                System.out.println("path ===>"+ path);
                //上下文参数
                System.out.println("ctx ===>"+ ctx);
                System.out.println("version:" + stat.getAversion() + ";   update time"+stat.getMtime());
            }
        },"I am contect");
        Thread.sleep(5000);
        System.out.println("获取数据完成");
    }
}
