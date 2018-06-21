/**
 * 
 */
package AnalysizeOntime;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;

import com.proweb.common.file.MyLog;
import com.proweb.job.libObject;
import com.proweb.job.objManager;
import com.proweb.mysqlobject.mysqlObject;

import Common.ConnectToDatabase;
import Common.TASK_DEFINITION;
import Model.TaskName;
import Model.task_actual_taskname_lib;
import Model.task_danger_result_lib;
import Model.task_fellow_ad_lib;
import Model.task_fellow_lib;
import Model.task_permanent_ad_lib;
import Model.task_permanent_lib;
import Model.task_spacetime_lib;
import Model.task_tasklog_lib;
import Model.task_taskresult_lib;
import Model.task_temp_mac_lib;
import Model.task_tracepegging_lib;
import Model.task_vip_svc_lib;
import Model.task_vip_trace_lib;
import Model.task_work_person_ad_lib;
import Model.task_work_person_lib;
import datamanage.AnalysizeDataCache;
import datamanage.AnalysizeDataSqlManager;
 
public class taskObjManager extends objManager{
	private DatagramSocket socket;
	private TaskName taskname;
	private static final int STATUS_RUNNING = 2;//任务运行中
	private static final int STATUS_SUCCESS = 3;//任务成功完成
	private static final int STATUS_FAILD = 4;//任务失败
	private int status;//任务状态
	
	taskObjManager(TaskName taskname){
		this.status = STATUS_RUNNING;
		this.taskname = taskname;
	}
	
	/**
	 * 根据jobname获取lib类
	 */
	@Override
	public libObject getObject(String objname) {
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_viptrace))       return new task_vip_trace_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_vipsvc))   	   	  return new task_vip_svc_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.task_danger_jobname))      return new task_danger_result_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_permanent))      return new task_permanent_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_permanent_ad))   return new task_permanent_ad_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_work_person))    return new task_work_person_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_work_person_ad)) return new task_work_person_ad_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_fellow))         return new task_fellow_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_fellow_ad))      return new task_fellow_ad_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_tasklog))        return new task_tasklog_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_actual_taskname))return new task_actual_taskname_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_temp_mac))       return new task_temp_mac_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_taskresult))     return new task_taskresult_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_trace_pegging))  return new task_tracepegging_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_spacetime))      return new task_spacetime_lib();
		return null;
	}
	
	@Override
	public void setActiveTime(long time) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSocket(DatagramSocket socket) {
		this.socket= socket;
	}

	@Override
	public void setStatus(String status) {
		MyLog.AddLog("actual_data_analyzer.log","setStatus==="+status);

		if(status == null) return;
		if(status.contains("end")){
			//当状态为end时，表示所有数据都接收完成，需要保存数据，同时改变任务的状态
			this.status = STATUS_SUCCESS;
			saveDataToMySql();
			changeTaskStatus();
		}
		if(status.contains("error")){
			//当状态为status:failed时，表示任务失败，需要改变数据库的状态
			this.status = STATUS_FAILD;
			changeTaskStatus();
		}
		if(status.contains("exit")){
			//当状态为status:exit时，表示超时、发生异常、所有数据都接收完成,需要关闭socket
			if(this.status == STATUS_RUNNING){
				this.status = STATUS_FAILD;
				changeTaskStatus();
			}
			closeSocket();
		}
	}

	/**
	 * 将结果数据保存到数据库
	 */
	private void saveDataToMySql() {
		String taskid = String.valueOf(taskname.getTaskid());
		HashMap<String, ArrayList<libObject>> job = AnalysizeDataCache.getJobByTaskid(taskid);
		AnalysizeDataSqlManager.addDataToQueue(job,taskid);
	}

	/**
	 * 关闭socket
	 */
	private void closeSocket() {
		if(socket != null){
			MyLog.AddLog("actual_data_analyzer.log","closeSocket===");
			socket.close();
		}
	}

	/**
	 * 改变任务的完成状态
	 */
	private void changeTaskStatus() {
		if(taskname == null) return;
		String taskid = String.valueOf(taskname.getTaskid());
		MyLog.AddLog("actual_data_analyzer.log","changeTaskStatus===taskid==="+taskid+"===status==="+status);
		
		AnalysizeDataCache.setTaskStatusInCache(taskid, status);
		
		ConnectToDatabase.connect();
		mysqlObject obj = new mysqlObject();
		obj.clearObject();
		String sql="update "+TASK_DEFINITION.tablename_actual_taskname+" set status="+status+" where taskid="+taskid;
		mysqlObject.ExeSql(sql);
	}
	
	

}
