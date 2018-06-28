package datamanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.proweb.job.libObject;
import com.proweb.mysqlobject.mysqlObject;

import Common.ConnectToDatabase;
import Model.task_spacetime_lib;
import Model.task_tracepegging_lib;
import probd.hbase.common.MyLog;

/**
 * @author lizb
 * @date 2018年6月13日
 * Description: ~ 分析程序执行结果入库管理类
 */
public class AnalysizeDataSqlManager {
	
	/**需要插入到数据库的数据队列，线程安全*/
	private static final ConcurrentLinkedQueue<HashMap<String,ArrayList<libObject>>> queue = new ConcurrentLinkedQueue<HashMap<String,ArrayList<libObject>>>();
	/**保证不会重复向数据库里插入taskid相同的任务数据*/
	private static final ArrayList<String> idList = new ArrayList<String>();
	
	/**
	 * 将队列提供出去
	 * @return
	 */
	public static ConcurrentLinkedQueue<HashMap<String,ArrayList<libObject>>> getQueue(){
		return queue;
	}
	
	
	/**
	 * 将需要入库的数据添加到队列
	 * @param data
	 */
	public static void addDataToQueue(HashMap<String,ArrayList<libObject>> data,String taskid){
		if(!idList.contains(taskid)){
			queue.offer(data);
			idList.add(taskid);
		}
	}
	
	/**
	 * 当缓存中的数据被清空之后，也要清空此处的taskid
	 */
	public static void removeTaskIdFromList(String taskid) {
		if(idList.contains(taskid)){
			idList.remove(taskid);
		}
	}
	
	
	
	/**
	 * 开启线程，将队列里的数据插入数据库
	 */
	public static void startInsert(){
		insertToSqlFromQueue();
	}

	private static void insertToSqlFromQueue() {
		HashMap<String,ArrayList<libObject>> hashMap = queue.poll();
		if(hashMap != null){
			insertToSQL(hashMap);
		}
	}

	/**
	 * 将数据插入到数据库中
	 * @param hashMap
	 */
	private static void insertToSQL(HashMap<String, ArrayList<libObject>> hashMap) {
		Set<Entry<String,ArrayList<libObject>>> entrySet = hashMap.entrySet();
		for (Entry<String, ArrayList<libObject>> entry : entrySet) {
			String tableName = entry.getKey();
			ArrayList<libObject> value = entry.getValue();
			insert(tableName,value);
		}
	}

	/**
	 * 根据tableName插入到相应的表中
	 * @param tableName
	 * @param value
	 */
	private static void insert(String tableName, ArrayList<libObject> value) {
		ConnectToDatabase.connect();
		switch (tableName) {
		case "trace_pegging":
			insertTracePegging(value);
			break;
		case "spacetime":
			insertSpaceTime(value);
			break;
		default:
			break;
		}
	}

	/**
	 * 插入时空碰撞
	 * @param value
	 */
	private static void insertSpaceTime(ArrayList<libObject> value) {
		for (libObject obj : value) {
			task_spacetime_lib bean = (task_spacetime_lib)obj;
			int taskid = Integer.valueOf(bean.getTaskid());
			String mac = bean.getMac();
			int nums = Integer.valueOf(bean.getNums());
			String svc = bean.getSvc();
			int stime = Integer.valueOf(bean.getStime());
			int etime = Integer.valueOf(bean.getEtime());
			String lables = bean.getLables();
			String sql = "insert into spacetime(taskid,mac,nums,svc,stime,etime,lables) values("+taskid+","+"\""+mac+"\"" +","+nums+","+"\""+svc+"\"" +","+stime+","+etime+","+"\""+lables+"\""+")";
			MyLog.AddLog("cache_to_mysql.log", "spacetime sql===" + sql);
			mysqlObject sqlObj = new mysqlObject();
			sqlObj.clearObject();
			mysqlObject.ExeSql(sql);
		}
	}


	/**
	 * 插入轨迹反查
	 * @param value
	 */
	private static void insertTracePegging(ArrayList<libObject> value) {
		for (libObject obj : value) {
			task_tracepegging_lib bean = (task_tracepegging_lib)obj;
			int taskid = Integer.valueOf(bean.getTaskid());
			String mac = bean.getMac();
			int nums = Integer.valueOf(bean.getNums());
			String svc = bean.getSvc();
			int stime = Integer.valueOf(bean.getStime());
			int etime = Integer.valueOf(bean.getEtime());
			String sql = "insert into trace_pegging(taskid,mac,nums,svc,stime,etime) values("+taskid+","+"\""+mac+"\"" +","+nums+","+"\""+svc+"\"" +","+stime+","+etime +")";
			MyLog.AddLog("cache_to_mysql.log", "trace_pegging sql===" + sql);
			mysqlObject sqlObj = new mysqlObject();
			sqlObj.clearObject();
			mysqlObject.ExeSql(sql);
		}
	}


	
}
