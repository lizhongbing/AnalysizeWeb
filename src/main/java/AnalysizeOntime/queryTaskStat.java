/**
 * 
 */
package AnalysizeOntime;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.proweb.job.libObject;
import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.Table_Task;
import Common.TimeDate;
import Model.task_spacetime_lib;
import Model.task_tracepegging_lib;
import datamanage.AnalysizeDataCacheManager;
import probd.hbase.common.MyLog;
import servlet.baseServlet;


public class queryTaskStat  extends baseServlet {
	
	private String taskid;	
	private long TASKID=0;
    
	@Override
	public String handle() {
		MyLog.AddLog("actual_data_analyzer.log", "step === start querystatus ==="+System.currentTimeMillis());
		taskid=getObject("taskid");
		String ENDRESULT="{\"status\":\"2\",\"taskid\":\""+taskid+"\"}";
		if(init()) ENDRESULT=getStatus();	
		MyLog.AddLog("actual_data_analyzer.log", "step === end querystatus ==="+System.currentTimeMillis());
		MyLog.AddLog("actual_data_analyzer.log", "step ===backjson==="+ENDRESULT);

		return ENDRESULT;
	}
	
	public boolean init(){
		if(TimeDate.isnum(taskid)){
			TASKID=Long.parseLong(taskid);	
			return true;
		}
		return false;
	}
	
	public String getStatus(){
		String count = "0";
		String status = getStatusFromCache();
		if(StringUtils.isEmpty(status)){
			status = getStatusFromDB();
		}
		if(status.equals("3")){
			count = getMaccount();
		}
		String result="{\"status\":\""+status+"\",\"total\":\""+count+"\",\"taskid\":\""+taskid+"\"}";
		return result;
	}
	
	/**
	 * 从本地缓存中获取任务状态
	 * @return
	 */
	private String getStatusFromCache() {
		int taskStatus = AnalysizeDataCacheManager.getTaskStatusInCache(taskid);
		if(taskStatus == -1){
			return null;
		}
		return String.valueOf(taskStatus);
	}

	/**
	 * 从数据库中获取任务状态
	 * @return
	 */
	private String getStatusFromDB() {
		mysqlObject sqlobj=new mysqlObject();
		sqlobj.clearObject();
		String sql="select status from actual_taskname where taskid="+TASKID;
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		String str="";
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0,10,10);				 
			 String tmp=result.split("\"rows\"")[1];
			 str=tmp.replace("\"status\":","").replace("\"","").replace(":[{","").replace("}]}","");
		}
		return str;
	}
	
	public String getMaccount(){
		mysqlObject sqlobj = new mysqlObject();;
		sqlobj.clearObject();
		String count = "0";
		String tasktype = Table_Task.getActual_Tasktype_by_taskid(sqlobj,taskid);
		if(StringUtils.isNotEmpty(tasktype)){
			String tablename = Table_Task.getActual_taskname_by_tasktype(tasktype);
			count = getMacCounFromCache(tablename,taskid);
		}
		return count;
	}
	
	/**
	 * 跟据taskid，tasktype查找去重Mac总数
	 */
	public static String getMacCounFromCache(String tablename,String taskid){
		String count="0";
		ArrayList<libObject> list = AnalysizeDataCacheManager.getDataByTaskidAndTableName(taskid, tablename);
		if(list != null){
			count = getMacCount(tablename,list);
		}
		return count;
		
	}

	private static String getMacCount(String tablename,ArrayList<libObject> list) {
		String data = "0";
		switch (tablename) {
		case "trace_pegging":
			data = getMacCountFromTracePegging(list);
			break;
		case "spacetime":
			data = getMacCountFromSpaceTime(list);
			break;
		}
		return data;
	}

	private static String getMacCountFromSpaceTime(ArrayList<libObject> list) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getMacCountFromSpaceTime");
		//获取去重的mac数
		List<String> macList = new ArrayList<String>();
		for (libObject obj : list) {
			task_spacetime_lib bean = (task_spacetime_lib)obj;
			String macValue = bean.getMac();
			if(!macList.contains(macValue)){
				macList.add(macValue);
			}
		}
		return String.valueOf(macList.size());
	}

	private static String getMacCountFromTracePegging(ArrayList<libObject> list) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getMacCountFromTracePegging");
		//获取去重的mac数
		List<String> macList = new ArrayList<String>();
		for (libObject obj : list) {
			task_tracepegging_lib bean = (task_tracepegging_lib)obj;
			String macValue = bean.getMac();
			if(!macList.contains(macValue)){
				macList.add(macValue);
			}
		}
		return String.valueOf(macList.size());
	}
	
}
