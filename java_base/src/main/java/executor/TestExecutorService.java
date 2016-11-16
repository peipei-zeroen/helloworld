package executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestExecutorService {

	public static void main(String[] args) {
		List<String> data = new ArrayList<String>();

		for (int i = 0; i < 1000; i++) {
			String str = String.valueOf(i);
			data.add(str);
		}

		int size = data.size();
		int threadNum = 10;

		ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

		List<Future<String>> futures = new ArrayList<Future<String>>();

		for (int i = 0; i < threadNum; i++) {
			final List<String> subList = data.subList(size / threadNum * i, size / threadNum * (i + 1));

			Callable<String> task = new Callable<String>() {
				@Override
				public String call() throws Exception {
					StringBuffer sb = new StringBuffer();
					for (String str : subList) {
						sb.append(str + ",");
					}
					return sb.toString();
				}
			};

			Future<String> taskResult = executorService.submit(task);

			futures.add(taskResult);
		}

		long a = System.currentTimeMillis();

		// shutdown() ��������ֹǰ����ִ����ǰ�ύ������
		// ��һ�׶ε��� shutdown �ܾ���������Ȼ����� shutdownNow�����б�Ҫ��ȡ����������������
		// �ύ���������н�����ر��̳߳�
		executorService.shutdown();

		while (true) {
			/**
			 * ͨ����������ExecutorService.isTerminated()�������ȫ�����߳��Ƿ��Ѿ����н���
			 */
			if (executorService.isTerminated()) {
				System.out.println("��������ִ�����");
				System.out.println("ʱ���=" + String.valueOf(System.currentTimeMillis() - a));
				break;
			}
			try {
				// milliseconds
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		StringBuffer result = new StringBuffer();

		for (Future<String> future : futures) {
			try {
				/**
				 * V get() throws InterruptedException, ExecutionException;
				 * ���׳��쳣�����Բ����쳣���������쳣ʱ������ѡ������shutdown��������
				 */
				System.out.println("���������ִ�н����" + future.get());
				result.append(future.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				// ����shutdown��������
				executorService.shutdownNow();
				e.printStackTrace();
			}
		}

		System.out.println("���ս����" + result.toString());

	}

}
