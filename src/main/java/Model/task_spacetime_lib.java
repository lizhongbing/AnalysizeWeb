package Model;

import com.proweb.job.libObject;

import Common.TASK_DEFINITION;
import probd.hbase.common.MyLog;
public class task_spacetime_lib extends libObject{
	
	private String taskid;
	private String mac;
	private String stime;
	private String etime;
	private String svc;
	private String nums;
	private String lables;
	
	
	@Override
	public boolean addRec(String recs) {
		MyLog.AddLog("actual_data_analyzer.log", "step === add data to task_spacetime_lib === recs ===" + recs);
		String[] strlist=recs.split(TASK_DEFINITION.task_jobname_cutmark);
		if(strlist.length!=7)return false;
		taskid=strlist[0];
		mac=strlist[1];
		stime=strlist[2];
		etime=strlist[3];
		svc=strlist[4];
		nums=strlist[5];
		lables=strlist[6];
		return true;
	}


	public String getTaskid() {
		return taskid;
	}


	public String getMac() {
		return mac;
	}


	public String getNums() {
		return nums;
	}


	public String getSvc() {
		return svc;
	}


	public String getStime() {
		return stime;
	}


	public String getEtime() {
		return etime;
	}


	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}


	public void setMac(String mac) {
		this.mac = mac;
	}


	public void setNums(String nums) {
		this.nums = nums;
	}


	public void setSvc(String svc) {
		this.svc = svc;
	}


	public void setStime(String stime) {
		this.stime = stime;
	}


	public void setEtime(String etime) {
		this.etime = etime;
	}


	public String getLables() {
		return lables;
	}


	public void setLables(String lables) {
		this.lables = lables;
	}
	
	
	
	
	
	
}
