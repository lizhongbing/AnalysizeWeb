package Analysize;


import Common.TimeDate;
import Model.TaskName;
import Service.TaskNameServiceImpl;
import debug.isDebug;
import servlet.baseServlet;

//删除任务接口
public class DropServlet extends baseServlet {
 
	static boolean islocal=true;
	static String[] database_paras=new String[4];
	static String ENDRESULT;
	static String taskid=null;
	
	//baseServlet实现方法
	@Override
	public String handle() {		
		islocal=isDebug.islocal;		
		taskid=getObject("taskid");	
		//如果输入参数符合规范，就执行删除操作
		if(init()) ENDRESULT=delete();
		else ENDRESULT="{\"status\":\"1\"}";		
		return ENDRESULT;
	}
	
	
	
	//主程序
	public static String delete(){
		boolean flag=false;
	    String result="{\"status\":\"1\"}";
	    TaskNameServiceImpl tasknameServiceImpl=new TaskNameServiceImpl();		
	    TaskName taskname=new TaskName();
	    taskname.setTaskidstr(taskid);
		flag=tasknameServiceImpl.delete(islocal, taskname);			
		if(flag) result="{\"status\":\"0\"}";
		return result;
	}


	//参数处理
	public static Boolean init(){
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
