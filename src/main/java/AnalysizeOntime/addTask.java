/**
 * 
 */
package AnalysizeOntime;

import com.proweb.mysqlobject.mysqlObject;

import Common.Base64Encode;
import Common.ConnectToDatabase;
import Common.EncodeSet;
import Common.TASK_DEFINITION;
import Common.TimeDate;
import Model.TaskName;
import datamanage.AnalysizeDataCache;
import probd.hbase.common.MyLog;
import servlet.baseServlet;

public class addTask  extends baseServlet  {

	 private String taskid;		
	 private String tasktype;
	 private String taskdesc;
	 private String taskdesc_tomysql;
	 private long stime=0;
	 private long etime=0;
	 private long TASKID=0;
	 private String ENDRESULT;
	 private String timerang="";
	 private String codes="";
	 private String spacetimeArgs;
	 
	@Override
	public String handle() {
		MyLog.AddLog("actual_data_analyzer.log", "start addtask============="+System.currentTimeMillis());
		taskid=EncodeSet.str_toUTF8(getObject("taskid"));
		tasktype=EncodeSet.str_toUTF8(getObject("tasktype"));
		taskdesc=getObject("taskdesc").trim().replace("%22", "\"");	
		ENDRESULT="{\"status\":\"4\",\"taskid\":\""+taskid+"\"}";
		MyLog.AddLog("actual_data_analyzer.log", "addTask paramas===taskid ==="+taskid + "tasktype===" + tasktype + "taskdesc ===" + taskdesc);
		if(init()) ENDRESULT=insert();	
		MyLog.AddLog("actual_data_analyzer.log", "back json============="+ENDRESULT);
		return ENDRESULT;
	}

	public String insert(){
		String str=ENDRESULT;
		try {
			insertActualTask();
			AnalysizeDataCache.setTaskStatusInCache(taskid, 2);
			startAnalyse();
			str="{\"status\":\"0\",\"taskid\":\""+taskid+"\"}";
		} catch (Exception e) {
			MyLog.AddLog("actual_data_analyzer.log", "insert sql Exception");
		}
		MyLog.AddLog("actual_data_analyzer.log", "end addtask============="+System.currentTimeMillis());
		return str;
	}
	
	/**
	 * 插入新建任务
	 */
	private void insertActualTask() {
		ConnectToDatabase.connect();
		mysqlObject sqlobj=new mysqlObject();
		sqlobj.clearObject();
		String sql="insert into actual_taskname(taskid,tasktype,taskdesc,taskname,taskmd5,status)values("+TASKID+","+tasktype+",\""+taskdesc_tomysql+"\","+"\"\""+","+"\"\","+"2"+")";
		mysqlObject.ExeSql(sql);
	}

	/**
	 * 开启分析程序
	 */
	private void startAnalyse() {
		TaskName taskname=new TaskName();
		taskname.setTaskid(TASKID);
		taskname.setTasktype(Integer.parseInt(tasktype));
		OnlineAnalyse analyse = new OnlineAnalyse(taskname,spacetimeArgs);
		String jobname=analyse.getJobName(timerang,"", codes,false);
		analyse.startStreamProcess(jobname);
	}

	public boolean init(){
		if(!TimeDate.isnum(taskid)) return false;
		else TASKID=Long.parseLong(taskid);
		if(!TimeDate.isnum(tasktype))return false;		
		try{
			String sstime="";
			String eetime="";
			//String cnum="";
			String taskdesc_in=taskdesc;		
			taskdesc=taskdesc.replace("[","").replace("]","").replace(" ", "");;
			if(Integer.parseInt(tasktype)==TASK_DEFINITION.task_type_pegg){
				taskdesc=taskdesc.replace("{","").replace("}","").replace("\"", "");
				String[] arr=taskdesc.split(",");
				codes=taskdesc.substring(taskdesc.indexOf("codes:"),taskdesc.indexOf(",cnum:")).replace("codes:", "");
				for(int i=0;i<arr.length;i++){
					String tmp=arr[i];
					if(tmp.contains("stime")) sstime=tmp.replace("stime:", "").replace("\"", "");
					if(tmp.contains("etime")) eetime=tmp.replace("etime:", "").replace("\"", "");			
					//if(tmp.contains("cnum")) cnum=tmp.replace("cnum:", "").replace("\"", "");
				}
			    stime=Long.parseLong(sstime.toString());
			    etime=Long.parseLong(eetime.toString());		
			    timerang=stime+"~"+etime;
			}else if(Integer.parseInt(tasktype)==TASK_DEFINITION.task_type_spacetime){
				String spacetimeArgs="";
				taskdesc=taskdesc.replace("\"", "");
				if(taskdesc.contains("data")){
					String str=taskdesc.split("data:")[1].replace("stime:","").replace(",etime:", "~").replace(",servicecode:",",").replace("[","").replace("]", "");
					spacetimeArgs=str.replace("},{","&").replace("{","").replace("}","");					
				}else{
					if(taskdesc.contains("},{")) {
						String[] arr=taskdesc.replace("},{", "}={").split("=");
						for(String str:arr){
							str=str.split(",hitRate:")[0].replace(",hitRate:","").replace("stime:","").replace(",etime:", "~").replace(",servicecode:",",").replace("[","").replace("]", "").replace("{", "").replace("}", "");
							System.out.println("str=="+str);
							spacetimeArgs+=str+"=";
						}
						if(spacetimeArgs.endsWith("=")) spacetimeArgs=spacetimeArgs.substring(0,spacetimeArgs.length()-1);
					}else{
						taskdesc=taskdesc.split("stime:")[1].replace("stime:","").replace(",etime:", "~").replace(",servicecode:",",").replace("[","").replace("]", "").replace("}", "");
						System.out.println("str=="+taskdesc);
						spacetimeArgs+=taskdesc;
					}
					
				}
				this.spacetimeArgs=spacetimeArgs;
			}
		    taskdesc_tomysql=Base64Encode.encoded(taskdesc_in).replace("\n","");
		}catch(Exception e){
		    return false;
		}
 		return true;
	}
	
	
}
