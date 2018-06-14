/**
 * 
 */
package AnalysizeOntime;

import com.proweb.common.timeop;

import Common.EncodeSet;
import Common.TASK_DEFINITION;
import Common.TimeDate;
import Model.TaskName;
import debug.isDebug;
import servlet.baseServlet;


public class ActivePlace_EachdayServlet  extends baseServlet  {	
	 static boolean islocal=true;
	 static String[] database_paras=new String[4];		
	 static String timerang=null;
	 static String mac=null;
	 static String tasktype=null;
	 static String svc=null;
	 static long timeval;
	 static long taskid;
	 static String pageno=null;
	 static String pagesize=null;
	 static int pno=0;
	 static int psize=10;
	 String ENDRESULT;
	 static long starttime=System.currentTimeMillis()/1000;
	 //baseServlet实现方法，方法返回前台
	@Override
	public String handle() {			
		islocal=isDebug.islocal;
		tasktype=EncodeSet.str_toUTF8(getObject("tasktype"));
		timerang=getObject("timerang");	
		mac=getObject("mac");
		svc=getObject("svc");	
		pageno=getObject("pageno");	
		pagesize=getObject("pagesize");	
		if(init())	ENDRESULT=analyse();		 
		else  ENDRESULT="{\"status\":\"1\",\"taskid\":\"\",\"data\":[]}";	
		
		return ENDRESULT;
	}
	
	//主程序
	public static String analyse(){		
		  String result="{\"status\":\"1\",\"taskid\":\"null\"}";
		  TaskName taskname=new TaskName();
		  taskname.setTasktype(Integer.parseInt(tasktype));
		  taskname.setTimeval(timeop.GetCurrentTime());
		  long tasktype_basenum=TASK_DEFINITION.tasktype_BaseNum;
		  //tasktype>1000说明是实时分析，这时才处理
		  if(taskname.getTasktype()>tasktype_basenum){		
 			  int typenum=(int) (Integer.parseInt(tasktype)-tasktype_basenum);
 			  taskname.setTasktype(typenum);
 			  taskname.setTaskid(System.currentTimeMillis()/1000+taskname.getTasktype());
 			 OnlineAnalyse actual=new OnlineAnalyse();
			  actual.putTaskName(taskname);
			  String data=null;
			  //根据mac,svc,返回天次json数据
			  data=actual.execute_getSpecialSvc_Daydata(taskname,timerang,mac,svc);
			  result="{\"status\":\"0\","+data+"}";				  
		}   
		return result;
	}
  
	//参数处理
	public static Boolean init(){
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
		if(TimeDate.isnum(pageno)) pno=Integer.parseInt(pageno);
		if(TimeDate.isnum(pagesize)) psize=Integer.parseInt(pagesize);
		return true;
	}
		
}

