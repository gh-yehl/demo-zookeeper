package com.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKClusterCreate {

    ZooKeeper zooKeeper;
    @Before
    public void beforeFuc() throws Exception{
        System.out.println("before");
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper("106.54.242.3:2181,106.54.242.3:2182,106.54.242.3:2183", 5000, new Watcher() {
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
     * 创建持久化节点
     */
    @Test
    public void create1() throws Exception{
        System.out.println("Test");
        //arg1: 节点路径
        //arg2: 节点数据
        //arg3: 节点权限    world:anyone:cdrwa
        //arg4: 节点类型 - 持久化 or 非持久化
        zooKeeper.create("/create/cluster","value for cluster".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void create2() throws Exception{
        System.out.println("Test");
        //arg1: 节点路径
        //arg2: 节点数据
        //arg3: 节点权限    world:anyone:r
        //arg4: 节点类型 - 持久化 or 非持久化
        zooKeeper.create("/create/node2","value for node2".getBytes(),
                ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void create3() throws Exception{
        List<ACL> acls = new ArrayList<ACL>();
        Id id = new Id("world","anyone");
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        acls.add(new ACL(ZooDefs.Perms.WRITE, id));
        System.out.println("Test3");

        zooKeeper.create("/create/node3","value for node3".getBytes(),
                acls, CreateMode.PERSISTENT);
    }

    /**
     * IP模式 授权
     * @throws Exception
     *
     * [zk: localhost:2181(CONNECTED) 5] addauth digest super:admin
     * [zk: localhost:2181(CONNECTED) 6] getAcl /create/node4
     * 'ip,'192.168.0.100
     * : cdrwa
     */
    @Test
    public void create4() throws Exception{
        List<ACL> acls = new ArrayList<ACL>();
        Id id = new Id("ip","192.168.0.100");
        acls.add(new ACL(ZooDefs.Perms.ALL, id));
        System.out.println("Test4");

        zooKeeper.create("/create/node4","value for node4".getBytes(),
                acls, CreateMode.PERSISTENT);
    }

    /**
     * auth模式 授权
     * @throws Exception
     */
    @Test
    public void create5() throws Exception{
        //添加授权用户
        zooKeeper.addAuthInfo("digest","authUser:123456".getBytes());
        //授权
        zooKeeper.create("/create/node5","value for node5".getBytes(),
                ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
    }

    /**
     * digest模式 授权
     * @throws Exception
     */
    @Test
    public void create6() throws Exception{
        //digest 授权模式
        Id id = new Id("digest","itheima:qlzQzCLKhBROghkooLvb+Mlwv4A=");
        List<ACL> acls = new ArrayList<ACL>();

        //授权
        acls.add(new ACL(ZooDefs.Perms.ALL,id));
        zooKeeper.create("/create/node6","value for node6".getBytes(),
                acls, CreateMode.PERSISTENT);
    }

    /**
     * 创建  持久化顺序 节点
     * @throws Exception
     * @return: 顺序持久化 节点名称
     */
    @Test
    public void create7() throws Exception{
        String result = zooKeeper.create("/create/node7","node7".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(result);
    }

    /**
     * 创建临时节点
     * @throws Exception
     */
    @Test
    public void create8() throws Exception{
        String result = zooKeeper.create("/create/node8","node8".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        try {
            //阻塞10秒，查看新创建的节点 - 因为程序运行结束 ->临时节点会自动删除
            Thread.sleep(10000);
        }catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    /**
     * 创建临时 顺序 节点
     * @throws Exception
     */
    @Test
    public void create9() throws Exception{
        String result = zooKeeper.create("/create/node9","node9".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        try {
            //阻塞5秒，查看新创建的节点 - 因为程序运行结束 ->临时节点会自动删除
            Thread.sleep(5000);
        }catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }



    /**
     * 异步方式  创建节点
     * @throws Exception
     * @return:
     */
    @Test
    public void create10() throws Exception{
        zooKeeper.create("/create/node10","node10".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, String name) {
                        //0 代表创建成功
                        System.out.println("rc ===>"+ rc);
                        System.out.println("path ===>"+ path);
                        System.out.println("name ===>"+name);
                        //上下文参数
                        System.out.println("ctx ===>"+ ctx);

                    }
                }, "I am the contect");

        Thread.sleep(5000);
        System.out.println("创建节点结束");
    }
}
