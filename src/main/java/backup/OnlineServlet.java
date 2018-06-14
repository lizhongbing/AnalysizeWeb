/**
 * 
 */
package backup;

import com.proweb.common.timeop;

import AnalysizeOntime.Online_analyse_old;
import Common.EncodeSet;
import Common.TASK_DEFINITION;
import Common.TimeDate;
import Model.TaskName;
import debug.isDebug;
import servlet.baseServlet;


public class OnlineServlet extends baseServlet  {	
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
		  
		  if(taskname.getTasktype()>tasktype_basenum){		
  			  int typenum=(int) (Integer.parseInt(tasktype)-tasktype_basenum);
  			  taskname.setTasktype(typenum);
  			  taskname.setTaskid(System.currentTimeMillis()/1000+taskname.getTasktype());
  			  Online_analyse_old actual=new Online_analyse_old();
			  actual.putTaskName(taskname);
			  String data=null;
			  data=actual.execute_getAlldata(taskname,timerang,mac,svc,pno,psize);
			  result="{\"status\":\"0\",\"taskid\":\""+taskname.getTaskid()+"\","+data+"}";				  
		}   
		/*long endtime=System.currentTimeMillis()/1000;
		String sql="insert into tasklog(taskid,starttime,endtime,timelen,day) values"
				+ "("+taskname.getTaskid()+","+starttime+","+endtime+","+(endtime-starttime)+","+timeop.getDayFromTime(endtime).replace("-","")+")";
		Print.println("tasklog sql="+sql);
		database_paras=ConnectToDatabase.dbconnect();
		mysqlObject.SetConnectInfo(database_paras[0], database_paras[1], database_paras[2], database_paras[3]);
		mysqlObject.ExeSql(sql); */ 
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
