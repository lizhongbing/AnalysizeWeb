package Service;

import com.proweb.common.timeop;
import com.proweb.common.file.MyLog;

import Common.ConnectToDatabase;
import Common.TASK_DEFINITION;
import Common.Table_Task;
import Dao.TaskNameDaoImpl;
import Model.TaskName;
//服务层，taskname增删改查实现接口
public class TaskNameServiceImpl implements TaskNameService{
	private long taskid;
	private int tasktype;
	private int status = 0;
	private int userid;
	private long timeval;
	private String name;
	private String taskdesc;	
	private String lable;
	private String taskmd5;
	private String content;
	private TaskName taskname;

	//插入任务
	@Override
	public boolean insert(boolean islocal, Object taskname_object) {
		taskname=(TaskName)taskname_object;
		boolean flag=false;
		taskid=taskname.getTaskid();
		tasktype=taskname.getTasktype();
		status=taskname.getStatus();
		userid=taskname.getUserid();
		timeval=taskname.getTimeval();
		name=taskname.getTaskname();
		taskdesc=taskname.getTaskdesc();
		lable=taskname.getLable();
		taskmd5=taskname.getTaskmd5();
		content=taskname.getContent();
		String sql="";
		long tasktype_basenum=TASK_DEFINITION.tasktype_BaseNum;
		if(taskid!=0){
			boolean taskid_exists_flag=Table_Task.exists(islocal,"taskname","taskid",String.valueOf(taskid));
			if(taskid_exists_flag==false){
				//实时分析任务的tasktype>1000,实时分析入actual_taskname库，离线分析入taskname库
				if(tasktype>=tasktype_basenum){
					tasktype=(int) (tasktype-tasktype_basenum);
					long timeval=System.currentTimeMillis()/1000;
					taskid=(long) (timeval+tasktype);
					taskname.setTaskid(taskid);
					sql="insert into actual_taskname(taskid,taskname,tasktype,taskdesc,lable,taskmd5,status,userid,timeval,content) " +"values("+"\'"+taskid+"\'"+","+"\'"+name+"\'"+","+"\'"+tasktype+"\'"+","+"\'"+taskdesc+"\'"+","+"\'"+lable+"\'"+","+"\'"+taskmd5+"\'"+","+"\'"+status+"\'"+","+"\'"+userid+"\'"+","+timeval+","+"\'"+content+"\'"+")";
		       }
				else sql="insert into taskname(taskid,taskname,tasktype,taskdesc,lable,taskmd5,status,userid,timeval,content) " +
		   		        "values("+"\'"+taskid+"\'"+","+"\'"+name+"\'"+","+"\'"+tasktype+"\'"+","+"\'"+taskdesc+"\'"+","+"\'"+lable+"\'"+","+"\'"+taskmd5+"\'"+","+"\'"+status+"\'"+","+"\'"+userid+"\'"+","+timeval+","+"\'"+content+"\'"+")";
			}
		}else{
			if(tasktype>=tasktype_basenum){
				tasktype=(int) (tasktype-tasktype_basenum);
				long timeval=System.currentTimeMillis()/1000;
				taskid=(long) (timeval+tasktype);
				taskname.setTaskid(taskid);
				sql="insert into actual_taskname(taskid,taskname,tasktype,taskdesc,lable,taskmd5,status,userid,timeval,content) " + "values("+"\'"+taskid+"\'"+","+"\'"+name+"\'"+","+"\'"+tasktype+"\'"+","+"\'"+taskdesc+"\'"+","+"\'"+lable+"\'"+","+"\'"+taskmd5+"\'"+","+"\'"+status+"\'"+","+"\'"+userid+"\'"+","+timeval+","+"\'"+content+"\'"+")";
			}
			else sql="insert into taskname(taskname,tasktype,taskdesc,lable,taskmd5,status,userid,timeval,content) " +
			   		"values("+"\'"+name+"\'"+","+"\'"+tasktype+"\'"+","+"\'"+taskdesc+"\'"+","+"\'"+lable+"\'"+","+"\'"+taskmd5+"\'"+","+"\'"+status+"\'"+","+"\'"+userid+"\'"+","+timeval+","+"\'"+content+"\'"+")";
		} 
		System.out.println("insert sql="+sql);
		ConnectToDatabase.connect();	
		MyLog.AddNormalLog("actual_data_analyzer.log",sql, "insert sql");
		TaskNameDaoImpl taskname_DaoImpl=new TaskNameDaoImpl();
		if(sql.length()>0) flag=taskname_DaoImpl.insert(null, sql);		
		return flag;
	}

    //修改任务
	@Override
	public boolean update(boolean islocal, Object taskname_object) {
		taskname=(TaskName)taskname_object;
		boolean flag=false;
		taskid=taskname.getTaskid();
		tasktype=taskname.getTasktype();
		status=taskname.getStatus();
		userid=taskname.getUserid();
		timeval=timeop.GetCurrentTime();
		name=taskname.getTaskname();
		taskdesc=taskname.getTaskdesc();
		lable=taskname.getLable();
		taskmd5=taskname.getTaskmd5();
		content=taskname.getContent();
		String sql="";
		boolean taskid_exists_flag=Table_Task.exists(islocal,"taskname","taskid",String.valueOf(taskid));
		MyLog.AddNormalLog("web_server.log","delete","\n taskid_exists_flag="+taskid_exists_flag+"\n");
		if(taskid_exists_flag){
			sql="update taskname  set tasktype="+"\'"+tasktype+"\'"
					   +",taskdesc="+"\'"+taskdesc+"\'" 
					   +", lable="+"\'"+lable+"\'"
					   +",taskmd5="+"\'"+taskmd5+"\'" 
					   +", userid="+"\'"+userid+"\'" 
					   +", content="+"\'"+content+"\'"
					   +", taskname="+"\'"+name
					   +"\',timeval="+"\'"+timeval+"\'"
					   +" where  taskid="+taskid
			           +" and status!="+1000 +" and status !="+2+" and status !="+3+" and status !="+4;
			ConnectToDatabase.connect();	
			TaskNameDaoImpl taskname_DaoImpl=new TaskNameDaoImpl();
			flag=taskname_DaoImpl.update(null, sql);
		}		
		return flag;
	}


	//删除任务
	@Override
	public boolean delete(boolean islocal,Object taskname_object) {
		taskname=(TaskName)taskname_object;
        boolean flag=false;
        String taskidstr=taskname.getTaskidstr();
		if(taskidstr.length()>0){
			//支持根据taskid批量删除任务
			String[] taskid_arr=taskidstr.split(",");
			for(int i=0;i<taskid_arr.length;i++){
				boolean taskid_exists_flag=Table_Task.exists(islocal,"taskname","taskid",taskid_arr[i]);
				if(taskid_exists_flag) {
					String sql="update taskname set status=1000 where taskid="+taskid_arr[i];
					MyLog.AddNormalLog("web_server.log","delete","\n islocal="+islocal+" sql="+sql+"\n");
					ConnectToDatabase.connect();	
					TaskNameDaoImpl taskname_DaoImpl=new TaskNameDaoImpl();
					flag=taskname_DaoImpl.delete(null, sql);
				}
			}			
		}
		return flag;
	}
	
	
	@Override
	public void findList(boolean islocal, Object taskname) {
		// TODO Auto-generated method stub
	}


		
}
