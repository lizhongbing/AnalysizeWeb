package Analysize;

import java.io.UnsupportedEncodingException;

import com.proweb.common.timeop;
import com.proweb.mysqlobject.mysqlObject;

import Common.Base64Encode;
import Common.ConnectToDatabase;
import Common.EncodeSet;
import Common.Print;
import Common.Table_Task;
import Common.TimeDate;
import Model.TaskName;
import Service.TaskNameServiceImpl;
import debug.isDebug;
import servlet.baseServlet;

public class InsertServlet extends baseServlet  {	
	 static boolean islocal=true;
	 static String[] database_paras=new String[4];	
	 static String taskid=null;
	 static String name=null;
	 static String tasktype=null;
	 static String taskdesc=null;
	 static String lable=null;
	 static String taskmd5=null;		
	 static String userid=null;
	 static String timerang=null;
	 static String macstr=null;
	 static long timeval;
	 static String content=null;
	 static int status=0;
	 static int USERID=0;
	 static long TASKID=0;
	 String ENDRESULT;
	 static long starttime=System.currentTimeMillis()/1000;
	@Override
	public String handle() {			
		islocal=isDebug.islocal;
		taskid=EncodeSet.str_toUTF8(getObject("taskid"));
		name =getObject("taskname");
		tasktype=EncodeSet.str_toUTF8(getObject("tasktype"));				
		taskmd5 =EncodeSet.str_toUTF8(getObject("taskmd5"));
		lable =EncodeSet.str_toUTF8(getObject("lable"));
		userid=EncodeSet.str_toUTF8(getObject("userid"));
		taskdesc=getObject("taskdesc");	
		content =EncodeSet.str_toUTF8(getObject("content"));
		if(init())	ENDRESULT=insert();		 
		else  ENDRESULT="{\"status\":\"1\",\"taskid\":\"null\"}";	
		
		return ENDRESULT;
	}
	
	//主程序
	public static String insert(){		
		  String result="{\"status\":\"1\",\"taskid\":\"null\"}";
		  TaskName taskname=new TaskName();
		  taskname.setTaskid(TASKID);
		  taskname.setTasktype(Integer.parseInt(tasktype));
		  taskname.setStatus(status);
		  taskname.setUserid(USERID);
		  taskname.setTaskname(name);
		  taskname.setTaskdesc(taskdesc);
		  taskname.setLable(lable);
		  taskname.setTaskmd5(taskmd5);
		  taskname.setContent(content);
		  taskname.setTimeval(timeop.GetCurrentTime());
		  //long tasktype_basenum=TASK_DEFINITION.tasktype_BaseNum;
		   
		  TaskNameServiceImpl tasknameServiceImpl=new TaskNameServiceImpl();
		  tasknameServiceImpl.insert(islocal,taskname);
		  
		  int id=0;
		  if(!taskid.equals("0"))  id=Integer.parseInt(taskid);
		  if(taskid.equals("0"))  id=Table_Task.selectMaxid(islocal,"taskid","taskname");
		  result="{\"status\":\"0\",\"taskid\":\""+id+"\"}";		   
		  
			long endtime=System.currentTimeMillis()/1000;
			String sql="insert into tasklog(taskid,starttime,endtime,timelen,day) values"
					+ "("+taskname.getTaskid()+","+starttime+","+endtime+","+(endtime-starttime)+","+timeop.getDayFromTime(endtime).replace("-","")+")";
			Print.println("tasklog sql="+sql);
			ConnectToDatabase.connect();
			mysqlObject.ExeSql(sql);  
			return result;
	}
    
	//参数处理
	public static Boolean init(){
		Boolean flag=true;
		if(taskid==null||(taskid.isEmpty())) taskid="0";
		if(userid==null||(userid.isEmpty())) userid="0";
		if(taskmd5!=null&&(!taskmd5.isEmpty())&&TimeDate.isnum(taskid)&&TimeDate.isnum(tasktype)&&TimeDate.isnum(userid)){
	    if(taskdesc==null)return false;
	   try {
			taskdesc=Base64Encode.encoded(taskdesc).replace("\n","");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
	   }	
	    }
	   TASKID=Integer.parseInt(taskid);
	   USERID=Integer.parseInt(userid);
		 	
		return flag;
	}
		
}
