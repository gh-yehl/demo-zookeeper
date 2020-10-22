package com.zookeeper.lock;

import org.junit.Test;

public class TicketSeller {
    public void sellTickets() {
        System.out.println("售票开始");
        int sleepMiils = 3000;
        try{
            Thread.sleep(sleepMiils);
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("售票开始");
    }

    public void sellTicketWithLock() throws Exception{
        MyLock myLock = new MyLock();
        myLock.acquireLock();
        sellTickets();
        myLock.releaseLock();
    }

    @Test
    public void testMyLock() throws Exception{
        TicketSeller ticketSeller = new TicketSeller();
        for (int i = 0; i < 10; i++) {
            ticketSeller.sellTicketWithLock();
        }
    }
}
