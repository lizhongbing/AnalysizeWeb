package Analysize;


import Common.TimeDate;
import Model.TaskName;
import Service.TaskNameServiceImpl;
import debug.isDebug;
import servlet.baseServlet;

//删除任务接口
public class DropServlet extends baseServlet {
 
	private String ENDRESULT;
	private String taskid;
	private boolean islocal;
	
	@Override
	public String handle() {	
		islocal = isDebug.islocal;
		taskid=getObject("taskid");	
		if(init()) ENDRESULT=delete();
		else ENDRESULT="{\"status\":\"1\"}";		
		return ENDRESULT;
	}
	
	public String delete(){
		boolean flag=false;
	    String result="{\"status\":\"1\"}";
	    TaskNameServiceImpl tasknameServiceImpl=new TaskNameServiceImpl();		
	    TaskName taskname=new TaskName();
	    taskname.setTaskidstr(taskid);
		flag=tasknameServiceImpl.delete(islocal, taskname);			
		if(flag) result="{\"status\":\"0\"}";
		return result;
	}

	public Boolean init(){
		if(taskid==null||taskid.isEmpty()) return false;
		else{
			taskid=taskid.replace("，",",").replace("，",",");
			if(taskid.contains(",")){
				if(taskid.endsWith(",")) taskid=taskid.substring(0,taskid.length()-1);
				else{
					String[] idstr=taskid.split(",");
					for(int i=0;i<idstr.length;i++){
						if((!TimeDate.isnum(idstr[i]))||(Integer.parseInt(idstr[i])<1)) return false;
					}
				}
			}
		}		
		return true;
	}
	
	
}
