package Model;

import com.proweb.job.libObject;

import Common.TASK_DEFINITION;
public class task_tracepegging_lib extends libObject{
	
	private String taskid;
	private String mac;
	private String nums;
	private String svc;
	private String stime;
	private String etime;
	
	
	@Override
	public boolean addRec(String recs) {
		String[] strlist=recs.split(TASK_DEFINITION.task_jobname_cutmark);
		if(strlist.length!=6)return false;
		taskid=strlist[0];
		mac=strlist[1];
		nums=strlist[2];
		svc=strlist[3];
		stime=strlist[4];
		etime=strlist[5];
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
	
	public String getValueByName(String fieldName){
		String value = "";
		switch (fieldName) {
		case "taskid":
			value = getTaskid();
			break;
		case "mac":
			value = getMac();
			break;
		case "nums":
			value = getNums();
			break;
		case "svc":
			value = getSvc();
			break;
		case "stime":
			value = getStime();
			break;
		case "etime":
			value = getEtime();
			break;
		}
		return value;
	}
	
	
	
	
}
