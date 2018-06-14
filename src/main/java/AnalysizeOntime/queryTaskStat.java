/**
 * 
 */
package AnalysizeOntime;

import java.util.List;

import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.TimeDate;
import probd.hbase.common.MyLog;
import servlet.baseServlet;


public class queryTaskStat  extends baseServlet {
	
	static String[] database_paras=new String[4];	
	static String taskid=null;	
    static long TASKID=0;
    static String ENDRESULT;
    static String status="2";
	@Override
	public String handle() {
		MyLog.AddLog("actual_data_analyzer.log", "step === start handle querystatus ==="+System.currentTimeMillis());
		taskid=getObject("taskid");
		ENDRESULT="{\"status\":\"2\",\"taskid\":\""+taskid+"\"}";
		if(init()) ENDRESULT=getStatus();		
		return ENDRESULT;
	}
	
	public static String getStatus(){
		String str="";
		mysqlObject sqlobj=new mysqlObject();
		sqlobj.clearObject();
		MyLog.AddLog("actual_data_analyzer.log", "before query status============="+System.currentTimeMillis());
		String sql="select status from actual_taskname where taskid="+TASKID;
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		MyLog.AddLog("actual_data_analyzer.log", "after query status============="+System.currentTimeMillis());
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0,10,10);				 
			 String tmp=result.split("\"rows\"")[1];
			 str=tmp.replace("\"status\":","").replace("\"","").replace(":[{","").replace("}]}","");
		}
		String returnstr="{\"status\":\""+str+"\",\"taskid\":\""+taskid+"\"}";
		MyLog.AddLog("actual_data_analyzer.log", "over handle querystatus======"+str+"======="+System.currentTimeMillis());
		return returnstr;
	}
	
	
	public boolean init(){
		if(!TimeDate.isnum(taskid)) return false;
		else TASKID=Long.parseLong(taskid);		
		return true;
	}
}
