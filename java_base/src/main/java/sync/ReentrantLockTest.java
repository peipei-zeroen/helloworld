package sync;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Outputter1 {    
    private Lock lock = new ReentrantLock();// ������    
  
    public void output(String name) {           
        lock.lock();      // �õ���    
  
        try {    
            for(int i = 0; i < name.length(); i++) {    
                System.out.print(name.charAt(i));    
            }    
        } finally {    
            lock.unlock();// �ͷ���    
        }    
    }    
}  
public class ReentrantLockTest {

	public static void main(String[] args) {
		final Outputter1 o = new Outputter1();

		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				o.output("xxxxxxxxxxx");
			}
			
		});
		t.start();
		
		Thread t2 = new Thread(new Runnable(){

			@Override
			public void run() {
				o.output("yyyyyyyyyyyyyyyyyy");
			}
			
		});
		t2.start();
	}

}
