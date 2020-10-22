package com.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZKConnectionWatcher implements  Watcher{
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    static ZooKeeper zooKeeper;


    public static void main(String[] args) {
        try{
            zooKeeper = new ZooKeeper("106.54.242.3:2181", 5000, new ZKConnectionWatcher());
            System.out.println("get node value ============>" + new String(zooKeeper.getData("/getnode", false,new Stat())));
            //阻塞连接
            countDownLatch.await();
            zooKeeper.close();
            System.out.println("connection closed...........");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void process(WatchedEvent event) {
        try{
            if(event.getType() == Event.EventType.None) {
                if(event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功");
                    //继续执行
                    countDownLatch.countDown();
                }else if(event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("连接断开");
                }else if(event.getState() == Event.KeeperState.Expired) {
                    System.out.println("连接超时");
                }else if(event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
