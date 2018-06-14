package Analysize;


import java.io.UnsupportedEncodingException;

import com.proweb.common.timeop;

import Common.Base64Encode;
import Common.EncodeSet;
import Common.TimeDate;
import Model.TaskName;
import Service.TaskNameServiceImpl;
import debug.isDebug;
import servlet.baseServlet;


public  class ModifyServlet extends baseServlet {
	 
	 static boolean islocal=true;
	 static String[] database_paras=new String[4];	
	 static String taskid;
	 static String name;
	 static String tasktype;
	 static String taskdesc;
	 static String lable;
	 static String taskmd5=null;		
	 static String userid;
	 static long timeval;
	 static String content;
	 static String status;
	 static int TASKTYPE=0;
	 static int STATUS=0;
	 static int USERID=0;
	 static int TASKID=0;
	 static String ENDRESULT;
	@Override
	public String handle() {	
		islocal=isDebug.islocal;
		taskid = EncodeSet.str_toUTF8(getObject("taskid"));		
		name =getObject("taskname");
		tasktype=EncodeSet.str_toUTF8(getObject("tasktype"));	
		taskdesc=EncodeSet.str_toUTF8(getObject("taskdesc"));
		lable =EncodeSet.str_toUTF8(getObject("lable"));
		taskmd5 =EncodeSet.str_toUTF8(getObject("taskmd5"));
		userid =EncodeSet.str_toUTF8(getObject("userid"));	
		//status =EncodeSet.str_toUTF8(getObject("status"));
		content =EncodeSet.str_toUTF8(getObject("content"));
		ENDRESULT=update();
		
		return ENDRESULT;
	}
	
	
	//主程序
	public static String update(){
		 String result="{\"status\":\"1\"}";
		 if(init()){
			 TASKID=Integer.parseInt(taskid);
			 TASKTYPE=Integer.parseInt(tasktype);
			 USERID=Integer.parseInt(userid);
			 TaskName taskname=new TaskName();
			  taskname.setTaskid(TASKID);
			  taskname.setTasktype(TASKTYPE);
			  //taskname.setStatus(STATUS);
			  taskname.setUserid(USERID);
			  taskname.setTaskname(name);
			  taskname.setTaskdesc(taskdesc);
			  taskname.setLable(lable);
			  taskname.setTaskmd5(taskmd5);
			  taskname.setContent(content);
			  taskname.setTimeval(timeop.GetCurrentTime());
			  TaskNameServiceImpl tasknameServiceImpl=new TaskNameServiceImpl();
			  boolean flag=tasknameServiceImpl.update(islocal,taskname);
			  if(flag) result="{\"status\":\"0\"}";			  
		 }
		 return result;
	}

	//参数处理
	public static Boolean init(){
		if(!TimeDate.isnum(taskid)||(Integer.parseInt(taskid)<1)) return false;
		if(taskdesc==null||taskdesc.isEmpty()) return false;
		if(taskmd5==null||taskmd5.isEmpty()) return false;
		if(tasktype==null||tasktype.isEmpty()) return false;
		if(!TimeDate.isnum(userid)) return false;
		if(!TimeDate.isnum(tasktype)) return false; 		
	    try {
		   taskdesc=Base64Encode.encoded(taskdesc).replace("\n","");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
	    }
		return true;
	}
	
	
}
