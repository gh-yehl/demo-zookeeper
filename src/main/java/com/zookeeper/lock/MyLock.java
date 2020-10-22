package com.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MyLock {
    private String zookeeperIp = "106.54.242.3:2181";
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final String LOCK_ROOT_PATH = "/Locks";
    private static final String LOCK_NODE_PATH = "/Lock_";
    private String lockPath;

    static ZooKeeper zooKeeper;

    public MyLock() {
        init();
    }
    public void init() {
        try{
            zooKeeper = new ZooKeeper(zookeeperIp, 300000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(event.getType() == Event.EventType.None) {
                        if(event.getState() == Event.KeeperState.SyncConnected) {
                            System.out.println("连接成功");
                            countDownLatch.countDown();
                        }
                    }
                }
            });
            //阻塞连接 - 等待创建成功 [连接创建成功后，process方法会捕获，让线程继续执行]
            countDownLatch.await();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acquireLock() throws Exception{
        //创建临时有序节点  /Locks/Lock_0000000*
        createTempNode();
        attemptLock();
    }

    //监控上一个节点
    Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if(event.getType() == Event.EventType.NodeDeleted) {
                System.out.println("Previous Node is Deleted");
                synchronized (this){
                    notifyAll();
                }
//                countDownLatch.countDown();
            }
        }
    };

    public void createTempNode() throws Exception {
        Stat stat = zooKeeper.exists(LOCK_ROOT_PATH,false);
        //判断/Locks 节点是否存在， 不存在 则创建
        if (stat == null) {
            zooKeeper.create(LOCK_ROOT_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        //创建临时有序节点
        lockPath = zooKeeper.create(LOCK_ROOT_PATH+LOCK_NODE_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println("临时有序节点创建成功 ==========》"+ lockPath);
    }

    public void attemptLock() throws Exception{
        //获取/Locks下所有的临时子节点
        List<String> list = zooKeeper.getChildren(LOCK_ROOT_PATH,false);
        //对子节点进行排序
        Collections.sort(list);

        int index = list.indexOf(lockPath.substring(LOCK_ROOT_PATH.length()+1));
        if(index == 0) {
            System.out.println(this.lockPath + "获取锁成功");
            return ;
        }else {
            String previousNodePath = LOCK_ROOT_PATH + "/"+ list.get(index -1);

            Stat stat = zooKeeper.exists(previousNodePath,watcher);
            //当代码执行到 此时时，可能 之前占用的锁  已经被释放了，所以只需要重新执行下 attemptLock方法 尝试获取锁
            if(stat == null) {
                attemptLock();
            }else {
                synchronized (watcher) {
                    watcher.wait();
                }
//                countDownLatch.await();
                attemptLock();
            }
        }
    }
    public void releaseLock() throws Exception {
        //删除临时有序节点
        zooKeeper.delete(this.lockPath, -1);
        zooKeeper.close();
        System.out.println("锁已经释放");
    }

    public static void main(String[] args) {
        try{
            MyLock myLock = new MyLock();
            //myLock.createTempNode();
            myLock.acquireLock();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
