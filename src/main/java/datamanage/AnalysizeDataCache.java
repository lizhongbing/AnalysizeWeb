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
	private static Map<String,HashMap<String,ArrayList<libObject>>> hashMap = new  HashMap<String, HashMap<String,ArrayList<libObject>>>();
	
	
	/**
	 * 建立任务与结果对应的集合映射
	 * @param taskid
	 * @param job
	 */
	public static void createMapRelation(String taskid,HashMap<String,ArrayList<libObject>> job) {
		if(job == null){
			job = new HashMap<String, ArrayList<libObject>>();
		}
		hashMap.put(taskid, job);
	}
	
	/**
	 * 获取任务对应的结果集
	 * @param taskid
	 * @return
	 */
	public static HashMap<String,ArrayList<libObject>> getJobByTaskid(String taskid) {
		if(!hashMap.containsKey(taskid)){
			MyLog.AddLog("actual_data_analyzer.log", "step===no data in cache taskid==="+taskid);
			return null;
		}
		HashMap<String, ArrayList<libObject>> result = hashMap.get(taskid);
		
		return result;
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
		if(hashMap == null){
			AnalysizeDataSqlManager.removeTaskIdFromList(taskid);
		}else{
			AnalysizeDataSqlManager.addDataToQueue(hashMap,taskid);
			Set<Entry<String,ArrayList<libObject>>> entrySet = hashMap.entrySet();
			for (Entry<String, ArrayList<libObject>> entry : entrySet) {
				String tableNameString = entry.getKey();
				ArrayList<libObject> value = entry.getValue();
				if(tableNameString.equals(tableName)){
					list = value;
					break;
				}
			}
		}
		return list;
	}
	
	

}
