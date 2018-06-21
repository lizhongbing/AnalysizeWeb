package datamanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.proweb.job.libObject;

import probd.hbase.common.MyLog;

/**
 * @author lizb
 * @date 2018年6月13日
 * Description: ~ 分析程序执行结果返回的数据缓存
 */
public class AnalysizeDataCache {
	
	/**taskid对应的任务结果数据*/
	private static final Map<String,HashMap<String,ArrayList<libObject>>> hashMap = new  HashMap<String, HashMap<String,ArrayList<libObject>>>();
	/**taskid对应的任务状态*/
	private static final Map<String, Integer> statusMap = new HashMap<String, Integer>(); 
	
	/**
	 * 建立任务与结果对应的集合映射
	 * @param taskid
	 * @param job
	 */
	public static void createMapRelation(String taskid,HashMap<String,ArrayList<libObject>> job) {
		if(job != null){
			hashMap.put(taskid, job);
		}
	}
	
	/**
	 * 设置本地缓存中任务状态
	 * @param taskid
	 * @param taskStatus
	 */
	public static void setTaskStatusInCache(String taskid,int taskStatus){
		statusMap.put(taskid, taskStatus);
	}
	
	/**
	 * 获取任务状态  -1表示本地缓存中没有该任务的状态
	 * @param taskid
	 * @return
	 */
	public static int getTaskStatusInCache(String taskid){
		if(statusMap.containsKey(taskid)){
			return statusMap.get(taskid);
		}
		return -1;
	}
	
	/**
	 * 移除本地缓存的任务状态
	 * @param taskid
	 */
	private static void removeTaskStatusInCache(String taskid){
		if(statusMap.containsKey(taskid)){
			statusMap.remove(taskid);
		}
	}
	
	/**
	 * 获取任务对应的结果集
	 * @param taskid
	 * @return
	 */
	public static HashMap<String,ArrayList<libObject>> getJobByTaskid(String taskid) {
		if(!hashMap.containsKey(taskid)){
			MyLog.AddLog("actual_data_analyzer.log", "step===no data in cache taskid==="+taskid);
			removeTaskStatusInCache(taskid);
			AnalysizeDataSqlManager.removeTaskIdFromList(taskid);
			//本地的缓存数据在定的时间内会自动销毁，不用再手动清除
			return null;
		}
		HashMap<String, ArrayList<libObject>> data = hashMap.get(taskid);
		return data;
	}
	
	/**
	 * 根据taskid和tableName获取对应表的缓存的数据
	 * @param taskid
	 * @param tableName
	 * @return
	 */
	public static ArrayList<libObject> getDataByTaskidAndTableName(String taskid,String tableName){
		ArrayList<libObject> list = null;
		HashMap<String, ArrayList<libObject>> hashMap = getJobByTaskid(taskid);
		if(hashMap != null){
			Set<Entry<String,ArrayList<libObject>>> entrySet = hashMap.entrySet();
			for (Entry<String, ArrayList<libObject>> entry : entrySet) {
				String tableNameString = entry.getKey();
				ArrayList<libObject> value = entry.getValue();
				if(tableNameString.equals(tableName)){
					//将缓存的数据列表复制一份提供给外面（只是一个引用列表），外面的排序业务都不会影响原有集合，保证守护线程插入到数据库的正确性
					list = new ArrayList<libObject>(value);
					break;
				}
			}
		}
		return list;
	}
	
	

}
