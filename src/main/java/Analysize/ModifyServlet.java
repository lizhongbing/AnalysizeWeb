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
	 
	private boolean islocal=true;
	private String taskid;
	private String name;
	private String tasktype;
	private String taskdesc;
	private String lable;
	private String taskmd5;		
	private String userid;
	private String content;
	private int TASKTYPE=0;
	private int USERID=0;
	private int TASKID=0;
	private String ENDRESULT;
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
	
	
	public String update(){
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

	public Boolean init(){
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
