/**
 * 
 */
package AnalysizeOntime;

import com.proweb.common.timeop;

import Common.EncodeSet;
import Common.TASK_DEFINITION;
import Common.TimeDate;
import Model.TaskName;
import servlet.baseServlet;


public class ActivePlace_EachdayServlet  extends baseServlet  {	
	 private String timerang;
	 private String mac;
	 private String tasktype;
	 private String svc;
	 //private String pageno=null;
	 //private String pagesize=null;
	 String ENDRESULT;
	 
	@Override
	public String handle() {			
		tasktype=EncodeSet.str_toUTF8(getObject("tasktype"));
		timerang=getObject("timerang");	
		mac=getObject("mac");
		svc=getObject("svc");	
		//pageno=getObject("pageno");	
		//pagesize=getObject("pagesize");	
		if(init())	ENDRESULT=analyse();		 
		else  ENDRESULT="{\"status\":\"1\",\"taskid\":\"\",\"data\":[]}";	
		
		return ENDRESULT;
	}
	
	public String analyse(){		
		  String result="{\"status\":\"1\",\"taskid\":\"null\"}";
		  TaskName taskname=new TaskName();
		  taskname.setTasktype(Integer.parseInt(tasktype));
		  taskname.setTimeval(timeop.GetCurrentTime());
		  //tasktype>1000说明是实时分析，这时才处理
		  if(taskname.getTasktype() > TASK_DEFINITION.tasktype_BaseNum){		
 			  int typenum=(int) (Integer.parseInt(tasktype)- TASK_DEFINITION.tasktype_BaseNum);
 			  taskname.setTasktype(typenum);
 			  taskname.setTaskid(System.currentTimeMillis()/1000+taskname.getTasktype());
 			  OnlineAnalyse analyse=new OnlineAnalyse(taskname);
			  String data=analyse.getSpecialSvcDaydata(timerang,mac,svc);
			  result="{\"status\":\"0\","+data+"}";				  
		}   
		return result;
	}
  
	public Boolean init(){
		if(!TimeDate.isnum(tasktype)) return false;
		if(Integer.parseInt(tasktype)<TASK_DEFINITION.tasktype_BaseNum) return false;
		if(!TimeDate.isNormaltime_rang(timerang)) return false; 	
		if((Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_VIP)
				||(Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_fellow)){
			if(mac==null||mac.length()==0) return false;
		}
		if(Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_highrisk){
			if(!svc.startsWith("(")) return false;
			if(!svc.endsWith(")")) return false;
		}		
		if((Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_permanent)||
				(Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_workperson)){
			if(svc==null||svc.length()==0) return false;
		}
		//if(TimeDate.isnum(pageno)) pno=Integer.parseInt(pageno);
		//if(TimeDate.isnum(pagesize)) psize=Integer.parseInt(pagesize);
		return true;
	}
		
}

