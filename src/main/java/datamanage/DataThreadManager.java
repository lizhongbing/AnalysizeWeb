package datamanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.proweb.job.libObject;

public class DataThreadManager {
	
	private static int SLEEP_TIME = 10000;
	
	private static ConcurrentLinkedQueue<HashMap<String, ArrayList<libObject>>> queue;

	static{
		initQueue();
	}
	
	/**
	 * 开启线程
	 */
	public static void start(){
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				startPollQueue();
			}
		}).start();
	}
	
	@SuppressWarnings("static-access")
	private static void startPollQueue() {
		System.out.println("查询队列...");
		if(checkQueue()){
			System.out.println("插入数据库...");
			insertDataToSql();
		}else{
			try {
				Thread.currentThread().sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		startPollQueue();
	}

	/**
	 * 开始插入数据到数据库
	 */
	protected static void insertDataToSql() {
		AnalysizeDataSqlManager.startInsert();
	}

	/**
	 * 检查队列里是否有数据
	 */
	protected static boolean checkQueue() {
		return !queue.isEmpty();
	}

	protected static void initQueue() {
		queue = AnalysizeDataSqlManager.getQueue();
	}
	
	

}
