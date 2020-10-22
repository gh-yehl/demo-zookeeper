package com.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKUpdate {

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
     * 同步更新
     * @throws Exception
     */
    @Test
    public void set1() throws Exception{
        //arg1: 节点路径
        //arg2: 节点数据
        //arg3: 版本号 -1 代表不指定版本号更新
        zooKeeper.setData("/set/node1", "setnode1111".getBytes(),-1);

        //通过Stat 对象 捕获更新node 数据
//        Stat stat = zooKeeper.setData("/set/node1", "setnode1111".getBytes(),-1);
//        System.out.println(stat.getAversion());
    }

    /**
     * 异步更新
     * @throws Exception
     */
    @Test
    public void set2() throws Exception{
        //arg1: 节点路径
        //arg2: 节点数据
        //arg3: 版本号 -1 代表不指定版本号更新
        zooKeeper.setData("/set/node1", "setnode3333".getBytes(), -1, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                //0 代表创建成功
                System.out.println("rc ===>"+ rc);
                System.out.println("path ===>"+ path);
                //上下文参数
                System.out.println("ctx ===>"+ ctx);
                //属性描述信息
                System.out.println("version:" + stat.getAversion() + ";   update time"+stat.getMtime());
            }
        },"I am context");

        Thread.sleep(5);
        System.out.println("异步更新结束");
    }
}
