package com.zookeeper.configcenter;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class MyConfigCenter implements Watcher {
    private String url;
    private String userName;
    private String password;

    private String zookeeperIp = "106.54.242.3:2181";
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    static ZooKeeper zooKeeper;

    //构造方法
    public MyConfigCenter() {
        init();
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
            }else if(event.getType() == Event.EventType.NodeDataChanged) {
                init();
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

            this.url = new String(zooKeeper.getData("/config/url", true,null));
            this.userName = new String(zooKeeper.getData("/config/username", true, null));
            this.password = new String(zooKeeper.getData("/config/password", true,null));

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void main(String[] args) {
        try{
            MyConfigCenter myConfigCenter = new MyConfigCenter();
            for (int i = 0; i < 30; i++) {
                System.out.println("############################################################");
                System.out.println("URL=============>" + myConfigCenter.getUrl());
                System.out.println("user name=============>" + myConfigCenter.getUserName());
                System.out.println("password=============>" + myConfigCenter.getPassword());
                System.out.println("############################################################");
                Thread.sleep(5000);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
