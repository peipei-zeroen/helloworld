package sync;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class syncData {        
	private int data;// ��������    
    private ReadWriteLock rwl = new ReentrantReadWriteLock();       
    public void set(int data) {    
        rwl.writeLock().lock();// ȡ��д��    
        try {    
            System.out.println(Thread.currentThread().getName() + "׼��д������");    
            try {    
                Thread.sleep(20);    
            } catch (InterruptedException e) {    
                e.printStackTrace();    
            }    
            this.data = data;    
            System.out.println(Thread.currentThread().getName() + "д��" + this.data);    
        } finally {    
            rwl.writeLock().unlock();// �ͷ�д��    
        }    
    }       
  
    public void get() {    
        rwl.readLock().lock();// ȡ������    
        try {    
            System.out.println(Thread.currentThread().getName() + "׼����ȡ����");    
            try {    
                Thread.sleep(20);    
            } catch (InterruptedException e) {    
                e.printStackTrace();    
            }    
            System.out.println(Thread.currentThread().getName() + "��ȡ" + this.data);    
        } finally {    
            rwl.readLock().unlock();// �ͷŶ���    
        }    
    }    
} 

public class ReadWriteLockTest {

	public static void main(String[] args) {
//      final Data data = new Data();    
        final syncData data = new syncData();    
//      final RwLockData data = new RwLockData();    
        
      //д��  
      for (int i = 0; i < 3; i++) {    
          Thread t = new Thread(new Runnable() {    
              @Override  
      public void run() {    
                  for (int j = 0; j < 5; j++) {    
                      data.set(new Random().nextInt(30));    
                  }    
              }    
          });  
          t.setName("Thread-W" + i);  
          t.start();  
      }    
      //��ȡ  
      for (int i = 0; i < 3; i++) {    
          Thread t = new Thread(new Runnable() {    
              @Override  
      public void run() {    
                  for (int j = 0; j < 5; j++) {    
                      data.get();    
                  }    
              }    
          });    
          t.setName("Thread-R" + i);  
          t.start();  
      }  

	}

}
