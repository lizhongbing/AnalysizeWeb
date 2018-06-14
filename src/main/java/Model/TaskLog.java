package Model;

//tasklog表字段get,set方法
public class TaskLog {
	
	private int id;
	private int taskid;
	private long starttime;
	private long endtime = 0;
	private long timelen;
	private int result_nums;
	private int end_nums;
	private int clear_nums;
	private int add_nums;
	private String day;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTaskid() {
		return taskid;
	}
	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	public long getStarttime() {
		return starttime;
	}
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}
	public long getEndtime() {
		return endtime;
	}
	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}
	public long getTimelen() {
		return timelen;
	}
	public void setTimelen(long timelen) {
		this.timelen = timelen;
	}
	public int getResult_nums() {
		return result_nums;
	}
	public void setResult_nums(int result_nums) {
		this.result_nums = result_nums;
	}
	public int getEnd_nums() {
		return end_nums;
	}
	public void setEnd_nums(int end_nums) {
		this.end_nums = end_nums;
	}
	public int getClear_nums() {
		return clear_nums;
	}
	public void setClear_nums(int clear_nums) {
		this.clear_nums = clear_nums;
	}
	public int getAdd_nums() {
		return add_nums;
	}
	public void setAdd_nums(int add_nums) {
		this.add_nums = add_nums;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	
	public TaskLog(){}
	
	public TaskLog(int taskid,  int result_nums, int end_nums, int clear_nums,
			int add_nums) {
		super();
		this.taskid = taskid;
		//this.starttime = starttime;
		//this.endtime = endtime;
		//this.timelen = timelen;
		this.result_nums = result_nums;
		this.end_nums = end_nums;
		this.clear_nums = clear_nums;
		this.add_nums = add_nums;
		//this.day = day;
	}
	
	public TaskLog(int taskid,int result_nums, int end_nums, int clear_nums,
			int add_nums, String day) {
		super();
		this.taskid = taskid;
		this.result_nums = result_nums;
		this.end_nums = end_nums;
		this.clear_nums = clear_nums;
		this.add_nums = add_nums;
		this.day = day;
	}
	
	public TaskLog(int taskid, long starttime, long endtime,
			long timelen) {
		super();
		this.taskid = taskid;
		this.starttime = starttime;
		this.endtime = endtime;
		this.timelen = timelen;
	}

	public TaskLog(int taskid, long starttime, long endtime,
			long timelen, String day) {
		super();
		this.taskid = taskid;
		this.starttime = starttime;
		this.endtime = endtime;
		this.timelen = timelen;
		this.day = day;
	}
	
	public TaskLog(int taskid, long starttime, long endtime,
			long timelen, int result_nums, int end_nums, int clear_nums,
			int add_nums) {
		super();
		this.taskid = taskid;
		this.starttime = starttime;
		this.endtime = endtime;
		this.timelen = timelen;
		this.result_nums = result_nums;
		this.end_nums = end_nums;
		this.clear_nums = clear_nums;
		this.add_nums = add_nums;
	}
	
	public TaskLog(int taskid, long starttime, long endtime,
			long timelen, int result_nums, int end_nums, int clear_nums,
			int add_nums, String day) {
		super();
		this.taskid = taskid;
		this.starttime = starttime;
		this.endtime = endtime;
		this.timelen = timelen;
		this.result_nums = result_nums;
		this.end_nums = end_nums;
		this.clear_nums = clear_nums;
		this.add_nums = add_nums;
		this.day = day;
	}
	
	public TaskLog(int id, int taskid, long starttime, long endtime,
			long timelen, int result_nums, int end_nums, int clear_nums,
			int add_nums, String day) {
		super();
		this.id = id;
		this.taskid = taskid;
		this.starttime = starttime;
		this.endtime = endtime;
		this.timelen = timelen;
		this.result_nums = result_nums;
		this.end_nums = end_nums;
		this.clear_nums = clear_nums;
		this.add_nums = add_nums;
		this.day = day;
	}
	
	
	@Override
	public String toString() {
		return "TaskLog [id=" + id + ", taskid=" + taskid + ", starttime="
				+ starttime + ", endtime=" + endtime + ", timelen=" + timelen
				+ ", result_nums=" + result_nums + ", end_nums=" + end_nums
				+ ", clear_nums=" + clear_nums + ", add_nums=" + add_nums
				+ ", day=" + day + "]";
	}

	
}
