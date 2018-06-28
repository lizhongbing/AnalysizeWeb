/**
 * 
 */
package AnalysizeOntime;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.proweb.common.conf.proProperties;
import com.proweb.common.file.MyLog;
import com.proweb.common.zk.zkManager;
import com.proweb.job.jobCacheManage;
import com.proweb.job.jobDataRecv;
import com.proweb.job.libObject;

import Common.Constant;
import Common.HostPortManager;
import Common.REDIS;
import Common.TASK_DEFINITION;
import Common.Task_result_Judge_new;
import Common.ThreadManager;
import Common.ZookeeperMethod;
import Model.TaskName;
import datamanage.AnalysizeDataCacheManager;
import debug.isDebug;
import redis.clients.jedis.Jedis;


public class OnlineAnalyse {
	
	private TaskName taskname;
	private String spacetimeArgs;
    private int listernport;
    
    public OnlineAnalyse(TaskName taskName){
    		this.taskname = taskName;
    		listernport = HostPortManager.getListernPort();
    }
    public OnlineAnalyse(TaskName taskName,String spacetimeArgs){
    		this.taskname = taskName;
    		this.spacetimeArgs = spacetimeArgs;
    		listernport = HostPortManager.getListernPort();
    }


	/**
	 * 根据Mac 获取所有的场所，频次，时长
	 * String jobname="vip^192.168.218.2^8000^/probd/probd-3.0.1/hadoop-2.6.3/etc/hadoop/task-analyzer.xml&1521536909&C8-F2-30-5B-5C-A4,1C-48-CE-CD-9E-C3,8C-18-D9-10-A7-78,44-C3-46-73-13-57,C4-66-99-53-B5-6A,CC-08-8D-3E-1C-D9,5C-CF-7F-DA-09-69,98-E7-F5-48-FB-7A&20180324~20180327&es";
	 * @param timerang
	 * @param mac
	 * @param svc
	 * @param pno
	 * @param psize
	 * @return
	 */
	public String getAlldata(String timerang, String mac, String svc, int pno, int psize) {
		String jobname=getJobName(timerang,mac,svc,false);	
		String res="";
		String result="";
		boolean jobflag=startStreamProcess(jobname);
		MyLog.AddLog("actual_data_analyzer.log", "now jobname="+jobname+" \r\n jobflag="+jobflag);	
		if(jobflag==true){
			String str=formatObjectData(jobname,mac);
		   	if(str!=null){
		   		 if(str.contains("},{")){		   			
		   			str=str.replace("},{", "}={");
		   			//totalnums=str.split("=").length;
		   			result=Task_result_Judge_new.getdata_fromMtoN_likepage_instr(str,pno,psize);
		   		 }else{
		   			 result=str;
		   		 } 
		   	}
		}
		res="\"data\":["+result+"]";		
		return res;
	}
	
	
	/**
	 * 根据Mac，svc ,返回天次json格式字符串
	 * @param timerang
	 * @param mac
	 * @param svc
	 * @return
	 */
	public String getSpecialSvcDaydata(String timerang, String mac, String svc) {
		String daystr=getRedisDayStr(mac,svc);
		if(daystr==null || daystr.length()<0){
			String jobname=getJobName(timerang,mac,svc,false);		
			MyLog.AddLog("actual_data_analyzer.log", "now jobname="+jobname);	
			boolean jobflag=startStreamProcess(jobname);
			if(jobflag){
				daystr=getRedisDayStr(mac,svc);
			}
		}
		String res="\"data\":[]";
		if(daystr!=null&&daystr.length()>0){
			daystr="{\"day\":\""+daystr+"\"}";	
			res="\"data\":["+daystr+"]";
		}				
		return res;
	}
	
	/**
	 * 根据Mac，svc,获取都有哪些天
	 * @param mac
	 * @param svc
	 * @return
	 */
	public static String getRedisDayStr(String mac,String svc){
		String daystr="";
		REDIS.getRedis();
		Set<String> keys=REDIS.keys_set;
		Jedis jedis=REDIS.jedis;   
		for(String key:keys){
			if(key.contains(TASK_DEFINITION.task_vip_jobname)){					
				String value=jedis.get(key);
				if(value.contains(",")){
					String[] strval=value.split(",");
					if(strval.length>=6){						
						String tmp_mac=strval[0].trim();
						String tmp_svc=strval[1].trim();
						if(tmp_mac.equals(mac)){
							if(tmp_svc.equals(svc)){
								String tmp_day=strval[5];
							    if(!daystr.contains(tmp_day)) daystr+=tmp_day+",";
							}
							
						}
					}
				}
			}
		}
		if(daystr.endsWith(","))daystr=daystr.substring(0,daystr.length()-1);
		return daystr;
	}
	
	/**
	 * 从Redis获取数据，返回json字符串
	 * @param jobtype_name
	 * @param mac
	 * @return
	 */
	public static String formatObjectData(String jobtype_name,String mac){
		String data="";
		List<String> list=new ArrayList<String>();
		REDIS.getRedis();
		Set<String> keys=REDIS.keys_set;
		Jedis jedis=REDIS.jedis;   
		
		if(jobtype_name.contains(TASK_DEFINITION.task_vip_jobname)){
			if(keys==null) return data;
			for(String key:keys){
				if(key.contains(TASK_DEFINITION.task_vip_jobname)){					
					String value=jedis.get(key);
					jedis.psetex(key, REDIS.savetime, value);
					if(value.contains(",")){
						String[] strval=value.split(",");
						if(strval.length>=6){						
							String tmp_mac=strval[0];
							if(tmp_mac.equals(mac)){
								String tmp_svc=strval[1];
								String tmp_timelen=strval[2];
								String tmp_count=strval[3];
								//String tmp_taskid=strval[4];							
								String tmp_day=strval[5];
								list.add(tmp_count+","+tmp_timelen+","+tmp_mac+","+tmp_svc+","+tmp_day);
							}													
						}
					}
					
				}
			} 
			if(list.size()>0){
				Collections.sort(list);
				for(int i=list.size()-1;i>=0;i--){
					System.out.println(list.get(i).toString());
					String[] strarr=list.get(i).toString().split(",");
					data+="{\""+TASK_DEFINITION.tablefield_mac+"\":\""+strarr[2]+"\","
							+ "\""+TASK_DEFINITION.tablefield_day+"\":\""+strarr[4]+"\","
							+"\""+TASK_DEFINITION.tablefield_svc+"\":\""+strarr[3]+"\","
							+"\""+TASK_DEFINITION.tablefield_counts+"\":\""+strarr[0]+"\","
							+"\""+TASK_DEFINITION.tablefield_timelen+"\":\""+strarr[1]+"\","
							+"\""+TASK_DEFINITION.tablefield_totaltimelen+"\":\""+(Integer.parseInt(strarr[0]))*(Integer.parseInt(strarr[1]))+"\"},";					
					
				}
			}	
		}		
		if(data.endsWith(",")) data=data.substring(0,data.length()-1);
		data=data.replace("\n","");			
		System.out.println(" data="+data);	
		MyLog.AddLog("actual_data_analyzer.log","data=="+data);
		return data;
	}
	
	/**
	 * 生成jobname,格式如下：
	 * String jobname="vip^192.168.218.2^8000^/probd/probd-3.0.1/hadoop-2.6.3/etc/hadoop/task-analyzer.xml&1523527425&44-C3-46-73-13-57&20180304~20180327&es";
	 * @param timerang
	 * @param mac
	 * @param svc
	 * @param fellow_auto
	 * @return
	 */
 	public String getJobName(String timerang,String mac,String svc,boolean fellow_auto){
 		String localip = null;
		try {
			localip = HostPortManager.getLinuxOrWindowIP();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(localip==null)return null;
		
	    String cnfname=getStreamCnfname();
	    long taskid=taskname.getTaskid();
	    String dataSource_type=TASK_DEFINITION.task_job_datasource_es;	   
	    String stream_typename=getStreamTaskType(taskname.getTasktype());
	    if(stream_typename==null) return null;
 		String jobname_prefix=stream_typename+TASK_DEFINITION.task_jobname_startmark+
							  localip+TASK_DEFINITION.task_jobname_startmark+
							  listernport+TASK_DEFINITION.task_jobname_startmark+
							  cnfname+TASK_DEFINITION.task_jobname_cutmark+
							  taskid+TASK_DEFINITION.task_jobname_cutmark;
		
 		
 		String jobname_suffix="";
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_VIP){
			jobname_suffix=mac+TASK_DEFINITION.task_jobname_cutmark+
						   timerang+TASK_DEFINITION.task_jobname_cutmark+
						   dataSource_type;
		}
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_pegg){			
			jobname_suffix=svc+TASK_DEFINITION.task_jobname_cutmark+
						   timerang+TASK_DEFINITION.task_jobname_cutmark+
						   dataSource_type;
		}
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_spacetime){			
			jobname_suffix=spacetimeArgs+TASK_DEFINITION.task_jobname_cutmark+
						   dataSource_type;
		}
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_highrisk){
			jobname_suffix=svc+TASK_DEFINITION.task_jobname_cutmark+
						   timerang+TASK_DEFINITION.task_jobname_cutmark+
					       dataSource_type;
		}
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_workperson){
			jobname_suffix=svc+TASK_DEFINITION.task_jobname_cutmark+
						   timerang+TASK_DEFINITION.task_jobname_cutmark+
					       dataSource_type;
		}
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_permanent){
			jobname_suffix=svc+TASK_DEFINITION.task_jobname_cutmark+
						   timerang+TASK_DEFINITION.task_jobname_cutmark+
					       dataSource_type;
		}
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_fellow){
			if(fellow_auto==false){
				jobname_suffix="null"+TASK_DEFINITION.task_jobname_cutmark+
						   timerang+TASK_DEFINITION.task_jobname_cutmark+
						   mac+TASK_DEFINITION.task_jobname_cutmark+
					       dataSource_type;
			}
			else{
				jobname_suffix="null"+TASK_DEFINITION.task_jobname_cutmark+
						   timerang+TASK_DEFINITION.task_jobname_cutmark+
					       dataSource_type;
			}
		}
		return jobname_prefix+jobname_suffix;
 	}
 	
 	/**
 	 * 根据jobname执行分析程序jar包
 	 * @param jobname
 	 * @return
 	 */
	public boolean startStreamProcess(String jobname){
		//Map中存放表名及表的数据，表名为key,数据是value		
		HashMap<String,ArrayList<libObject>> job=jobCacheManage.getJob(jobname);
		if(job==null){
			System.out.println("stream_process:"+jobname);
			job=new HashMap<String,ArrayList<libObject>>();
			jobCacheManage.addJob(jobname, job);
			AnalysizeDataCacheManager.createMapRelation(String.valueOf(taskname.getTaskid()), job);
			//创建client的socket服务，指定目的主机和port
	        Socket s;
			try {
				//获取zookeeper信息并连接
				List<Map<String,Integer>> zkquorum=ZookeeperMethod.get_zk_quorum();
				if(zkquorum==null||zkquorum.size()==0){
					System.out.println("zookeeper client  cannot find!");
					return false;
				}
				zkManager zm=new zkManager();
				Map<String,Integer> item=zkquorum.get(0);
				for(String subkey:item.keySet()){
					MyLog.AddLog("actual_data_analyzer.log", "subkey="+subkey+" item.get(subkey)="+item.get(subkey));
					zm.openZk(subkey,item.get(subkey),5000);
					break;
				}				
				String[] str=zm.getData("/memjobhost").split(":");
				MyLog.AddLog("actual_data_analyzer.log", "zookeeper str="+str[0]+" "+str[1]);
				if(str.length<2) return false;
				s = new Socket(str[0],Integer.parseInt(str[1]));		
				
				DataOutputStream out=new DataOutputStream(s.getOutputStream());				
				out.writeUTF(jobname);
			    s.close();		  
			    startRecvJob(job);
			    return true;
			} catch (Exception e) {				
				MyLog.AddLog("actual_data_analyzer.log","stream_process Exception");
			}			
		}
		return false;
	}

	/**
	 * 开始接收数据
	 * @param job
	 */
	private void startRecvJob(final HashMap<String, ArrayList<libObject>> job) {
		ThreadManager.runOnFixedThreadPool(new Runnable() {
			@Override
			public void run() {
				taskObjManager om=new taskObjManager(taskname);
				jobDataRecv jobrcv=new jobDataRecv();
				MyLog.AddLog("job_recv.log","before StartRecvJob==="+System.currentTimeMillis());
				jobrcv.StartRecvJob(job,om,listernport);
				MyLog.AddLog("job_recv.log","after StartRecvJob==="+System.currentTimeMillis());
			}
		});
	}
	
	
    /**
     * 获取任务类型字符串
     * @param tasktype
     * @return
     */
	public static String getStreamTaskType(int tasktype){
		String task_jobtype=null;
		if(tasktype==TASK_DEFINITION.task_type_permanent){
			task_jobtype=TASK_DEFINITION.task_permanent_jobname;
		}else if(tasktype==TASK_DEFINITION.task_type_highrisk){
			task_jobtype=TASK_DEFINITION.task_danger_jobname;
		}else if(tasktype==TASK_DEFINITION.task_type_fellow){
			task_jobtype=TASK_DEFINITION.task_fellow_jobname;
		}else if(tasktype==TASK_DEFINITION.task_type_workperson){//上班族类型
			task_jobtype=TASK_DEFINITION.task_work_jobname;
		}else if(tasktype==TASK_DEFINITION.task_type_VIP){//重点人群分析
			task_jobtype=TASK_DEFINITION.task_vip_jobname;
		}else if(tasktype==TASK_DEFINITION.task_type_pegg){//轨迹反查
			task_jobtype=TASK_DEFINITION.task_pegg_jobname;
		}else if(tasktype==TASK_DEFINITION.task_type_spacetime){//重点人群分析
			task_jobtype=TASK_DEFINITION.task_spacetime_jobname;
		}
		return task_jobtype;
	}

	
   /**
    * 获取task-analyzer.xml路径
    * @return
    */
	public static String getStreamCnfname(){
		String cnfname=null;
		if(isDebug.islocal) cnfname=TASK_DEFINITION.task_analyser_xml_localpath;
		else{
			proProperties.InitDbFile(Constant.SERVER_DB_PROPERTIES_PATH);
			cnfname=System.getenv("HADOOP_HOME") + Constant.HADOOP_ONLINE_TASKANALYZER_PATH;
		}
		return cnfname;
	}

	/**
	 * 根据类型获取表名
	 * @param tasktype
	 * @return
	 */
	public static String getTableNameByTaskType(int tasktype){
		if(tasktype==TASK_DEFINITION.task_type_VIP) return TASK_DEFINITION.tablename_vipsvc;
		if(tasktype==TASK_DEFINITION.task_type_fellow) return TASK_DEFINITION.tablename_fellow+","+TASK_DEFINITION.tablename_fellow_ad;
		if(tasktype==TASK_DEFINITION.task_type_permanent) return TASK_DEFINITION.tablename_permanent+","+TASK_DEFINITION.tablename_permanent_ad;
		if(tasktype==TASK_DEFINITION.task_type_workperson) return TASK_DEFINITION.tablename_work_person+","+TASK_DEFINITION.tablename_work_person_ad;
		if(tasktype==TASK_DEFINITION.task_type_highrisk) return TASK_DEFINITION.tablename_danger_result;
		return null;
	}
	

}
