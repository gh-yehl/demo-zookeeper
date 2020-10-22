package com.zookeeper.globalid;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class GlobalUniqueId implements Watcher {
    private String zookeeperIp = "106.54.242.3:2181";
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private String defaultPath = "/uniqueid";
    static ZooKeeper zooKeeper;

    public GlobalUniqueId() {
        init();
    }


    @Override
    public void process(WatchedEvent event) {
        try{
            if(event.getType() == Watcher.Event.EventType.None) {
                if(event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功");
                    //继续执行
                    countDownLatch.countDown();
                }else if(event.getState() == Watcher.Event.KeeperState.Disconnected) {
                    System.out.println("连接断开");
                }else if(event.getState() == Watcher.Event.KeeperState.Expired) {
                    System.out.println("连接超时");
                }else if(event.getState() == Watcher.Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void init() {
        try{
            zooKeeper = new ZooKeeper(zookeeperIp,5000, this);
            //阻塞连接 - 等待创建成功 [连接创建成功后，process方法会捕获，让线程继续执行]
            countDownLatch.await();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUniqueId() {
        String path = "";
        try{
            path = zooKeeper.create(defaultPath,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }catch (Exception e) {
            e.printStackTrace();
        }
        //uniqueid0000000001
        return path.substring(8);
    }

    public static void main(String[] args) {
        MyThread thread1 = new MyThread();
        MyThread thread2 = new MyThread();
        thread1.run();
        thread2.run();
    }

    static class MyThread extends Thread{
        @Override
        public void run() {
            GlobalUniqueId globalUniqueId = new GlobalUniqueId();
            for (int i = 0; i < 20; i++) {
                System.out.println("Thread1 ====> " + Thread.currentThread().getName()+ globalUniqueId.getUniqueId());

            }
        }
    }

}
