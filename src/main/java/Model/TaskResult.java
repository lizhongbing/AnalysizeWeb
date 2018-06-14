package Model;
//taskresult  get，set方法
public class TaskResult {
	
	private int id;
	private int taskid;
	private String name;	
	private String result;
	private String day;
	
	public TaskResult(){}
	
	public TaskResult(String name, String result) {
		super();
		this.name = name;
		this.result = result;
	}
	
	public TaskResult(int taskid,String result) {
		super();
		this.taskid = taskid;
		this.result = result;
	}

	public TaskResult(int taskid, String name, String result) {
		super();
		this.taskid = taskid;
		this.name = name;
		this.result = result;
	}
	
	public TaskResult(int taskid, String name, String result, String day) {
		super();
		this.taskid = taskid;
		this.name = name;
		this.result = result;
		this.day = day;
	}
	
	public TaskResult(int id, int taskid, String name, String result, String day) {
		super();
		this.id = id;
		this.taskid = taskid;
		this.name = name;
		this.result = result;
		this.day = day;
	}


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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	
	
	@Override
	public String toString() {
		return "TaskResult [id=" + id + ", taskid=" + taskid + ", name=" + name
				+ ", result=" + result + ", day=" + day + "]";
	}
	
	
}
