package datamanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.proweb.job.libObject;

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
			return null;
		}
		HashMap<String, ArrayList<libObject>> result = hashMap.get(taskid);
		
		return result;
	}
	
	

}
