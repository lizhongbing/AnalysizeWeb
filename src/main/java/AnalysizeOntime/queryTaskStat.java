/**
 * 
 */
package AnalysizeOntime;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.TimeDate;
import datamanage.AnalysizeDataCache;
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
		String result = getStatusFromCache();
		if(StringUtils.isEmpty(result)){
			result = getStatusFromDB();
		}
		return result;
	}
	
	/**
	 * 从本地缓存中获取任务状态
	 * @return
	 */
	private String getStatusFromCache() {
		int taskStatus = AnalysizeDataCache.getTaskStatusInCache(taskid);
		if(taskStatus == -1){
			return null;
		}
		String returnstr="{\"status\":\""+taskStatus+"\",\"taskid\":\""+taskid+"\"}";
		return returnstr;
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
		String returnstr="{\"status\":\""+str+"\",\"taskid\":\""+taskid+"\"}";
		return returnstr;
	}
	
	
	
}
