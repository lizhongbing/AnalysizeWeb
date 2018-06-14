/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.List;


public class taskItem {

	List<Actual_taskname> Actual_taskname_list=new ArrayList<Actual_taskname>();
	List<Tasklog> Taskloglist=new ArrayList<Tasklog>();
	List<Danger_result> Danger_result_list=new ArrayList<Danger_result>();
	List<Vip_trace> Vip_trace_list=new ArrayList<Vip_trace>();
	List<Vip_svc> Vip_svc_list=new ArrayList<Vip_svc>();
	List<Fellow> Fellow_list=new ArrayList<Fellow>();
	List<Fellow_ad> Fellow_ad_list=new ArrayList<Fellow_ad>();
	List<Peramanent> Peramanent_list=new ArrayList<Peramanent>();
	List<Peramanent_ad> Peramanent_ad_list=new ArrayList<Peramanent_ad>();
	List<Work_person> Work_person_list=new ArrayList<Work_person>();
	List<Work_person_ad> Work_person_ad_list=new ArrayList<Work_person_ad>();
	List<Taskcount> Taskcountlist=new ArrayList<Taskcount>();
	List<Temp_mac> Temp_maclist=new ArrayList<Temp_mac>();
			
	
	public class Actual_taskname{
		long taskid;
		int tasktype;
		String taskname;
		String taskdesc;
		String taskmd5;
		String lable;
		String content;	
		int userid=0;
		int status=0;
		long timeval=System.currentTimeMillis()/1000;
		public void Actual_taskname(){}
		
		/**
		 * @param taskid
		 * @param taskname
		 * @param tasktype
		 * @param taskdesc
		 */
		public void Actual_taskname(long taskid,String taskname,int tasktype,String taskdesc){
			this.taskid=taskid;
			this.tasktype=tasktype;
			this.taskname=taskname;
			this.taskdesc=taskdesc;
		}
		
		/**
		 * @param taskid
		 * @param taskname
		 * @param tasktype
		 * @param taskdesc
		 * @param taskmd5
		 * @param userid
		 * @param status
		 */
		public void Actual_taskname(int taskid, String taskname,int tasktype,String taskdesc,String taskmd5,int userid,int status) {
			this.taskid=taskid;
			this.tasktype=tasktype;
			this.taskname=taskname;
			this.taskdesc=taskdesc;
			this.taskmd5=taskmd5;
			this.userid=userid;
			this.status=status;
		}	
		/**
		 * @param taskid
		 * @param taskname
		 * @param tasktype
		 * @param taskdesc
		 * @param taskmd5
		 * @param userid
		 * @param status
		 * @param lable
		 * @param content
		 */
		public void Actual_taskname(int taskid, String taskname,int tasktype,String taskdesc,String taskmd5,int userid,int status,String lable,String content) {
			this.taskid=taskid;
			this.tasktype=tasktype;
			this.taskname=taskname;
			this.taskdesc=taskdesc;
			this.taskmd5=taskmd5;
			this.userid=userid;
			this.status=status;
			this.lable=lable;
			this.content=content;
		}	
		
	}
	
	public class Tasklog{
		long  taskid;      
        long  starttime;  
		long  endtime;     
		long  timelen;    
		long  result_nums; 
		long  add_nums;    
		long  clear_nums;  
		String  day;        
		public void Tasklog(){}
		
		/**
		 * @param taskid
		 * @param starttime
		 * @param endtime
		 * @param timelen
		 * @param result_nums
		 * @param add_nums
		 * @param day
		 * */
		public void Tasklog(long taskid,long starttime,long endtime,long timelen,long result_nums,long add_nums,String day){
			this.taskid=taskid;
			this.starttime=starttime;
			this.endtime=endtime;
			this.timelen=timelen;
			this.result_nums=result_nums;
			this.add_nums=add_nums;
			this.day=day;
		}
	}
		
	public class Vip_trace{
		long taskid;
		long counts;
		String mac;
		String day;
		String trace;
		
		public Vip_trace(){}
		
		/**
		 * @param taskid
		 * @param counts
		 * @param mac
		 * @param day
		 * @param trace
		 * */
		public Vip_trace(long taskid,long counts,String mac,String day,String trace){
			this.taskid=taskid;
			this.counts=counts;
			this.mac=mac;
			this.day=day;
			this.trace=trace;
		}
	}

	public class Vip_svc{
		long taskid;
		long timelen;
		long nums;
		String day;
		String svc;
		String mac;
		
		public void Vip_svc(){}
		
		/**
		 * @param taskid
		 * @param timelen
		 * @param nums
		 * @param day
		 * @param svc
		 * @param mac
		 * */
		public void Vip_svc(long taskid,long timelen,long nums,String mac,String svc,String day){
			this.taskid=taskid;
			this.timelen=timelen;
			this.nums=nums;
			this.day=day;
			this.svc=svc;
			this.mac=mac;
		}
				
	}
	
	public class Danger_result{
		long taskid;
		long nums;
		long integral;
		String svc;
		String day;
		String mac;
 		long timeval=System.currentTimeMillis()/1000;
 		public void Danger_result(){}
 		/**
		 * @param taskid
		 * @param integral
		 * @param nums
		 * @param day
		 * @param svc
		 * @param mac
		 * */
 		public void	Danger_result(long taskid,long nums,long integral,String svc,String day,String mac){
 			this.taskid=taskid;
 			this.nums=nums;
 			this.integral=integral;
 			this.svc=svc;
 			this.day=day;
 			this.mac=mac;
 		}
	}
	
	public class Fellow{
		long taskid;
		String name;
		String Fellowid;
		String day;
		public void Fellow(){}
		/**
		 * @param taskid
		 * @param Fellowid
		 * @param day
		 * @param name
		 * */
		public void Fellow(long taskid,String name,String Fellowid,String day){
			this.taskid=taskid;
			this.name=name;
			this.day=day;
			this.Fellowid=Fellowid;
		}
	}

	public class Fellow_ad{
		long no;
		String svc;
		String Fellowid;
		public Fellow_ad(){}
		/**
		 * @param no
		 * @param svc
		 * @param Fellowid
		 * */
		public Fellow_ad(long no,String svc,String Fellowid){
			this.no=no;
			this.svc=svc;
			this.Fellowid=Fellowid;
		}
	}
	
    public class Peramanent{
    	long taskid;
    	String mac;
    	String svc;
    	String day;
    	public Peramanent(){}
    	/**
		 * @param taskid
		 * @param day
		 * @param svc
		 * @param mac
		 * */
    	public Peramanent(long taskid,String svc,String day,String mac){
    		this.taskid=taskid;
 			this.svc=svc;
 			this.day=day;
 			this.mac=mac;
    	}
    }

    public class Peramanent_ad{
    	long taskid;
    	String mac;
    	String svc;
    	String day;
    	public Peramanent_ad(){}
    	/**
		 * @param taskid
		 * @param day
		 * @param svc
		 * @param mac
		 * */
    	public Peramanent_ad(long taskid,String svc,String day,String mac){
    		this.taskid=taskid;
 			this.svc=svc;
 			this.day=day;
 			this.mac=mac;
    	}
    }

    public class Work_person{
    	long taskid;
		String svc;
		String day;
		String mac;
		public void Work_person(){}
		/**
		 * @param taskid
		 * @param day
		 * @param svc
		 * @param mac
		 * */
		public void Work_person(long taskid,String svc,String day,String mac){
			this.taskid=taskid;
			this.svc=svc;
			this.day=day;
			this.mac=mac;
		}
    }
    
    public class Work_person_ad{
    	long taskid;
    	long integral;
    	long nums;
    	String svc;
		String day;
		String mac;
		String workup;
		String workdown;
		long timeval=System.currentTimeMillis()/1000;
		
		public void Work_person_ad(){}
		/**
		 * @param taskid
		 * @param day
		 * @param svc
		 * @param mac
		 * @param nums
		 * @param workdown
		 * @param workup
		 * @param integral
		 * */
		public void Work_person_ad(long taskid,String svc,String day,String mac,String workup,String workdown,long integral,long nums){
			this.taskid=taskid;
			this.svc=svc;
			this.day=day;
			this.mac=mac;
			this.nums=nums;
			this.workdown=workdown;
			this.workup=workup;
			this.integral=integral;
		}
    }
    
    public class Taskcount{
    	long taskid;
		int tasktype;
		long starttime;
		long endtime;
		long result_nums;
		String taskname;
		String lable;
		String content;			
		int userid=0;
		int status=3;
		long timeval=System.currentTimeMillis()/1000;
		public void Taskcount(){}
		/**
		 * @param taskid
		 * @param taskname
		 * @param tasktype
		 * @param starttime
		 * @param endtime
		 * @param result_nums 
		 */
		public void Taskcount(int taskid, String taskname,int tasktype,long starttime,long endtime,long result_nums){
			this.taskid=taskid;
			this.tasktype=tasktype;
			this.taskname=taskname;
			this.starttime=starttime;
			this.endtime=endtime;
			this.result_nums=result_nums;
		}
		/**
		 * @param taskid
		 * @param taskname
		 * @param tasktype
		 * @param starttime
		 * @param endtime
		 * @param result_nums 
		 * @param userid
		 * @param status
		 * @param lable
		 * @param content
		 */
		public void Taskcount(int taskid, String taskname,int tasktype,long starttime,long endtime,long result_nums,int userid,int status,String lable,String content){
			this.taskid=taskid;
			this.tasktype=tasktype;
			this.taskname=taskname;
			this.starttime=starttime;
			this.endtime=endtime;
			this.result_nums=result_nums;
			this.userid=userid;
			this.status=status;
			this.lable=lable;
			this.content=content;
		}
    }
    
    public class Temp_mac{
    	long taskid;
    	String mac;
    	public Temp_mac(){}
    	/**
		 * @param taskid
		 * @param mac
		 */
    	public Temp_mac(long taskid,String mac){
    		this.taskid=taskid;
    		this.mac=mac;
    	}
    }

    
}
