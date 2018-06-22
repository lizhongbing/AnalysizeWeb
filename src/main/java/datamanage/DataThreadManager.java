package datamanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.proweb.job.libObject;
/**
 * @author lizb
 * @date 2018年6月21日
 * Description: ~ 守护线程，程序启动就开启，每隔一段时间查询本地缓存中是否有分析程序的结果数据，然后插入到数据库中
 */
public class DataThreadManager {
	
	private static final int SLEEP_TIME = 10000;
	private static ConcurrentLinkedQueue<HashMap<String, ArrayList<libObject>>> queue = AnalysizeDataSqlManager.getQueue();
	
	private static Thread thread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			startPollQueue();
		}
	});
	
	
	/**
	 * 开启线程
	 */
	public static void start(){
		thread.start();
	}
	
	
	@SuppressWarnings("static-access")
	private static void startPollQueue() {
		while (true) {
			
			System.out.println("query dataQueue...");
			if(checkQueue()){
				System.out.println("insert data to mysql...");
				insertDataToSql();
			}
			
			try {
				thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
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


}
