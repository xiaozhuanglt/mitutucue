package com.xiaozhuanglt.mitutucue.controller;

/**
 * @description: 多线程实践类
 * @author: hxz
 * @create: 2019-06-30 12:26
 **/
public class ThreadDemoController {

  public static void main(String[] args) throws InterruptedException {
   //1、join使用
    /* // TODO Auto-generated method stub
    ThreadDemoController td1 = new ThreadDemoController();
    ThreadTest1 t1=td1.new ThreadTest1("A");
    ThreadTest1 t2=td1.new ThreadTest1("B");
    t1.start();
    t1.join();
    t2.start();*/

    //2、死锁
    final Object a = new Object();
    final Object b = new Object();
    Thread threadA = new Thread(new Runnable() {
      @Override
      public void run() {
        synchronized (a) {
          try {
            System.out.println("now i in threadA-locka");
            Thread.sleep(1000L);
            synchronized (b) {
              System.out.println("now i in threadA-lockb");
            }
          } catch (Exception e) {
            // ignore
          }
        }
      }
    });

    Thread threadB = new Thread(new Runnable() {
      @Override
      public void run() {
        synchronized (b) {
          try {
            System.out.println("now i in threadB-lockb");
            Thread.sleep(1000L);
            synchronized (a) {
              System.out.println("now i in threadB-locka");
            }
          } catch (Exception e) {
            // ignore
          }
        }
      }
    });
    threadA.start();
    threadB.start();

  }

  //顺序执行
  class ThreadTest1 extends Thread {
    private String name;

    public ThreadTest1(String name) {
      this.name = name;
    }
    @Override
    public void run() {
      for (int i = 1; i <= 5; i++) {
        System.out.println(name + "-" + i);
      }
    }
  }


}
