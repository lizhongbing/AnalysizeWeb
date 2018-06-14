package Model;

//taskname 表字段get,set方法
public class TaskName {

	private long taskid;
	private int tasktype;
	private int status = 0;
	private int userid;
	private long timeval;
	private String taskname;
	private String taskdesc;	
	private String lable;
	private String taskmd5;
	private String content;

	

	private long   stattime=0;
	private long   endtime=0;
	private String svclist;
	private String maclist;
	private long   run_time=0;					
	private long   run_cycle=0;				
	private long   run_timelen=0;	
	private long   run_validate_start=0; 	
	private long   run_validate_end=0;	
	
	/**多个taskid组成的字符串*/
	private String  taskidstr;

	public  TaskName(){}
	
	public  TaskName(int tasktype,String taskdesc,String taskmd5){
			super();
			this.tasktype=tasktype;
			this.taskdesc=taskdesc;
			this.taskmd5=taskmd5;
	}
		
	public  TaskName(long taskid,int tasktype,String taskdesc,String taskmd5){
			super();
			this.taskid=taskid;
			this.tasktype=tasktype;
			this.taskdesc=taskdesc;
			this.taskmd5=taskmd5;
	}
		
	public  TaskName(String taskname,int tasktype,String taskdesc,String taskmd5){
			super();
			this.taskname=taskname;
			this.tasktype=tasktype;
			this.taskdesc=taskdesc;
			this.taskmd5=taskmd5;
	}

	public  TaskName(long taskid,String taskname,int tasktype,String taskdesc,String taskmd5){
		super();
		this.taskid=taskid;
		this.taskname=taskname;
		this.tasktype=tasktype;
		this.taskdesc=taskdesc;
		this.taskmd5=taskmd5;
	}
	
	
	
	public String getTaskname() {
		return taskname;
	}
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}
	public String getTaskdesc() {
		return taskdesc;
	}
	public void setTaskdesc(String taskdesc) {
		this.taskdesc = taskdesc;
	}
	public String getLable() {
		return lable;
	}
	public void setLable(String lable) {
		this.lable = lable;
	}
	public String getTaskmd5() {
		return taskmd5;
	}
	public void setTaskmd5(String taskmd5) {
		this.taskmd5 = taskmd5;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getTaskid() {
		return taskid;
	}
	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}
	/**多个taskid组成的字符串*/
	public String getTaskidstr() {
		return taskidstr;
	}
	/**多个taskid组成的字符串*/
	public void setTaskidstr(String taskidstr) {
		this.taskidstr = taskidstr;
	}
	public int getTasktype() {
		return tasktype;
	}
	public void setTasktype(int tasktype) {
		this.tasktype = tasktype;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public long getTimeval() {
		return timeval;
	}
	public void setTimeval(long timeval) {
		this.timeval = timeval;
	}

	
	
	public long getStattime() {
		return stattime;
	}

	public void setStattime(long stattime) {
		this.stattime = stattime;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	
	public String getSvclist() {
		return svclist;
	}

	
	public void setSvclist(String svclist) {
		this.svclist = svclist;
	}

	
	public String getMaclist() {
		return maclist;
	}

	
	public void setMaclist(String maclist) {
		this.maclist = maclist;
	}

	
	public long getRun_time() {
		return run_time;
	}

	
	public void setRun_time(long run_time) {
		this.run_time = run_time;
	}

	public long getRun_cycle() {
		return run_cycle;
	}

	public void setRun_cycle(long run_cycle) {
		this.run_cycle = run_cycle;
	}

	public long getRun_timelen() {
		return run_timelen;
	}

	
	public void setRun_timelen(long run_timelen) {
		this.run_timelen = run_timelen;
	}

	
	public long getRun_validate_start() {
		return run_validate_start;
	}

	public void setRun_validate_start(long run_validate_start) {
		this.run_validate_start = run_validate_start;
	}

	
	public long getRun_validate_end() {
		return run_validate_end;
	}

	
	public void setRun_validate_end(long run_validate_end) {
		this.run_validate_end = run_validate_end;
	}

	@Override
	public String toString() {
		return "TaskName [taskdesc=" + taskdesc + ", lable=" + lable
				+ ", taskmd5=" + taskmd5 + ", content=" + content + ", taskid="
				+ taskid + ", tasktype=" + tasktype + ", status=" + status
				+ ", userid=" + userid + ", timeval=" + timeval + ", taskname="
				+ taskname + "]";
	}

	
}
