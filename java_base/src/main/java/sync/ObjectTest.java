package sync;

public class ObjectTest {

	private static  byte[] b = new byte[0];
	
	public static void main(String[] args) throws InterruptedException {
		final A a = new A();

		Thread aThread = new Thread(new Runnable(){

			@Override
			public void run() {
//				try {
//					a.printThreadInfo();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				synchronized (a) {
					try {
						a.printThreadInfo();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		aThread.start();
		
//		aThread.join();
		
		Thread bThread = new Thread(new Runnable(){

			@Override
			public void run() {
				synchronized (a) {
					System.err.println("B===========�������н�����׼���ͷ�A");
					a.notify();
					System.err.println("B===========����ִ����ɣ�");
				}
			}
			
		});
		bThread.start();
		
	}
}

class A {
	public void printThreadInfo() throws InterruptedException {
		Thread t = Thread.currentThread();
		System.err.println(t);
		// this.wait();//һֱ�ȴ�
		System.err.println("A==========��ʼִ�У���5�룬���ߵ�Bִ���������ǰ����������");
		this.wait(5000);// �ȴ�1000ms
		System.err.println("A==========ִ�н���������������������������������");
		// super.wait(1000);
	}
	
}
