/**
 * 
 */
package AnalysizeOntime;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proweb.common.conf.proProperties;
import com.proweb.common.file.MyLog;
import com.proweb.common.zk.zkManager;
import com.proweb.job.jobCacheManage;
import com.proweb.job.jobDataRecv;
import com.proweb.job.libObject;

import Common.Constant;
import Common.HostPortManager;
import Common.TASK_DEFINITION;
import Common.Task_result_Judge_new;
import Common.ZookeeperMethod;
import Model.TaskName;
import Model.task_danger_result_lib;
import Model.task_permanent_ad_lib;
import Model.task_vip_svc_lib;
import Model.task_work_person_lib;
import debug.isDebug;
import net.sf.json.JSONObject;

public class Online_analyse_old {
    public static int	 bindport=9000;	
	public static TaskName taskname=new TaskName();
		
	
	public static int getListernPort(){
		bindport++;
		if(bindport>10000)bindport=9000;
		return bindport;
	}
		
	
	/**
	 * 根据Mac获取svc,day,频次，活动时长
	 * @param taskname2
	 * @param timerang
	 * @param mac
	 * @param svc
	 * @param pno
	 * @param psize
	 * @return
	 */
	public String execute_getAlldata(TaskName taskname2, String timerang, String mac, String svc, int pno, int psize) {
        // String jobname="vip^192.168.218.2^8000^/probd/probd-3.0.1/hadoop-2.6.3/etc/hadoop/task-analyzer.xml&1521536909&C8-F2-30-5B-5C-A4,1C-48-CE-CD-9E-C3,8C-18-D9-10-A7-78,44-C3-46-73-13-57,C4-66-99-53-B5-6A,CC-08-8D-3E-1C-D9,5C-CF-7F-DA-09-69,98-E7-F5-48-FB-7A&20180324~20180327&es";
		int listernport=getListernPort();
		String jobname=get_jobname(taskname,timerang,mac,svc,listernport,false);		
		MyLog.AddLog("actual_data_analyzer.log", "now jobname="+jobname+"\n listernport="+listernport);	
		//int totalnums=0;
		String res="";
		String result="\"data\":[]";
		HashMap<String,ArrayList<libObject>> job=stream_process(jobname,listernport);
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_fellow){
			get_jobname(taskname,timerang,mac,svc,listernport,true);
			job=stream_process(jobname,listernport);
		}
		if(job==null||job.size()==0) return result;
		else{
		   	String task_jobtype=get_tablename_by_tasktype(taskname.getTasktype());
		   	ArrayList<libObject> libobj =job.get(task_jobtype);
		   	result=format_object_data(libobj,task_jobtype);
		   	if(result!=null){
		   		 if(result.contains("},{")){		   			
		   			result=result.replace("},{", "}={");
		   			//totalnums=result.split("=").length;
		   			result=Task_result_Judge_new.getdata_fromMtoN_likepage_instr(result,pno,psize);
		   		 } 
		   	}
		}
		//res="\"totalnums\":\""+totalnums+"\",\"data\":["+result+"]";
		res="\"data\":["+result+"]";		
		return res;
	}
		
	
	/**
	 * 根据Mac，svc 获取天次对应json数据
	 * @param taskname2
	 * @param timerang
	 * @param mac
	 * @param svc
	 * @param pno
	 * @param psize
	 * @return
	 */
	public String execute_getSpecialSvcdata(TaskName taskname2, String timerang, String mac, String svc, int pno, int psize) {
        // String jobname="vip^192.168.218.2^8000^/probd/probd-3.0.1/hadoop-2.6.3/etc/hadoop/task-analyzer.xml&1521536909&C8-F2-30-5B-5C-A4,1C-48-CE-CD-9E-C3,8C-18-D9-10-A7-78,44-C3-46-73-13-57,C4-66-99-53-B5-6A,CC-08-8D-3E-1C-D9,5C-CF-7F-DA-09-69,98-E7-F5-48-FB-7A&20180324~20180327&es";
		int listernport=getListernPort();
		String jobname=get_jobname(taskname,timerang,mac,svc,listernport,false);		
		MyLog.AddLog("actual_data_analyzer.log", "now jobname="+jobname+"\n listernport="+listernport);	
		//int totalnums=0;
		String res="";
		String result="";
		HashMap<String,ArrayList<libObject>> job=stream_process(jobname,listernport);
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_fellow){
			get_jobname(taskname,timerang,mac,svc,listernport,true);
			job=stream_process(jobname,listernport);
		}
	    MyLog.AddLog("actual_data_analyzer.log","job is null ="+(job==null||job.size()==0));
		if(job==null||job.size()==0) return result;
		else{
		 	MyLog.AddLog("actual_data_analyzer.log","taskname.getTasktype()="+taskname.getTasktype());
		   	String task_jobtype=get_tablename_by_tasktype(taskname.getTasktype());
		   	ArrayList<libObject> libobj =job.get(task_jobtype);
		   	result=format_object_data(libobj,task_jobtype);
		   	if(result!=null){
		   		 if(result.contains("},{")){		   			 
		   			result=result.replace("},{", "}={");
		   			String[] arr=result.split("=");
		   			for(int i=0;i<arr.length;i++) if(!arr[i].contains(svc))result=result.replace(arr[i],"");
					result=result.replace("==","=");		   			
		   			//totalnums=result.split("=").length;
		   			result=Task_result_Judge_new.getdata_fromMtoN_likepage_instr(result,pno,psize);
		   		 }else{
		   			 if(!result.contains(svc)) result="";
		   		 } 
		   	}
		}
		//res="\"totalnums\":\""+totalnums+"\",\"data\":["+result+"]";
		res="\"data\":["+result+"]";		
		return res;
	}
	
	
	/**
	 * @param taskname2
	 * @param timerang
	 * @param mac
	 * @param svc
	 * @param pno
	 * @param psize
	 * @return
	 */
	public String execute_getSpecialSvc_Daydata(TaskName taskname2, String timerang, String mac, String svc) {
        // String jobname="vip^192.168.218.2^8000^/probd/probd-3.0.1/hadoop-2.6.3/etc/hadoop/task-analyzer.xml&1521536909&C8-F2-30-5B-5C-A4,1C-48-CE-CD-9E-C3,8C-18-D9-10-A7-78,44-C3-46-73-13-57,C4-66-99-53-B5-6A,CC-08-8D-3E-1C-D9,5C-CF-7F-DA-09-69,98-E7-F5-48-FB-7A&20180324~20180327&es";
		int listernport=getListernPort();
		String jobname=get_jobname(taskname,timerang,mac,svc,listernport,false);		
		MyLog.AddLog("actual_data_analyzer.log", "now jobname="+jobname+"\n listernport="+listernport);	
		//int totalnums=0;
		String res="";
		String result="\"data\":[]";
		String daystr="";
		HashMap<String,ArrayList<libObject>> job=stream_process(jobname,listernport);
		if(taskname.getTasktype()==TASK_DEFINITION.task_type_fellow){
			get_jobname(taskname,timerang,mac,svc,listernport,true);
			job=stream_process(jobname,listernport);
		}	 	
		if(job==null||job.size()==0) return result;
		else{
		   	String task_jobtype=get_tablename_by_tasktype(taskname.getTasktype());
		   	ArrayList<libObject> libobj =job.get(task_jobtype);
		   	MyLog.AddLog("actual_data_analyzer.log","libobj.size="+libobj.size());
		   	result=format_object_data(libobj,task_jobtype);	
		   	MyLog.AddLog("actual_data_analyzer.log", "execute_getSpecialSvc_Daydata before svcfilter data="+result);	
		   	if(result!=null){
		   		 if(result.contains("},{")){		   			 
		   			result=result.replace("},{", "}={");
		   			String[] arr=result.split("=");
		   			for(int i=0;i<arr.length;i++) if(!arr[i].contains(svc))result=result.replace(arr[i],"");
		   			while(result.contains("=="))result=result.replace("==","=");
		   			MyLog.AddLog("actual_data_analyzer.log", "execute_getSpecialSvc_Daydata svcfilter str="+result);	
					if(result.contains("=")){
						String[] array=result.split("=");
						for(int i=0;i<array.length;i++){
							MyLog.AddLog("actual_data_analyzer.log", "execute_getSpecialSvc_Daydata array[i]="+array[i]);
							if(array[i].startsWith("{")){
								JSONObject json = JSONObject.fromObject(array[i]);
								 String day = json.getString("day");
							     if(!daystr.contains(day))daystr+=day+",";
							}					       
						}	
					}							       
		   		 }else{
		   			 if(result.contains(svc)&&result.startsWith("{")){
		   				 JSONObject json = JSONObject.fromObject(result);
				        String day = json.getString("day");
				        if(!daystr.contains(day))daystr+=day+",";
		   			 }
		   			
		   		 } 
		   		 if(daystr.endsWith(",")) daystr=daystr.substring(0,daystr.length()-1);
			     daystr="{\"day\":\""+daystr+"\"}";
		   	}
		}
		res="\"data\":["+daystr+"]";		
		return res;
	}
	
	//格式化数据，返回json数据
 	public static String format_object_data(ArrayList<libObject>  liblist,String jobtype_name){
		String data="";
		if(liblist!=null&&(jobtype_name.contains(TASK_DEFINITION.task_vip_jobname))){
			for(int i=0;i<liblist.size();i++){
				task_vip_svc_lib vip_svc=(task_vip_svc_lib) liblist.get(i);
				if(vip_svc!=null){					
					data+="{\""+TASK_DEFINITION.tablefield_mac+"\":\""+vip_svc.mac+"\","
							+ "\""+TASK_DEFINITION.tablefield_day+"\":\""+vip_svc.day+"\","
							+"\""+TASK_DEFINITION.tablefield_svc+"\":\""+vip_svc.svc+"\","
							+"\""+TASK_DEFINITION.tablefield_counts+"\":\""+vip_svc.counts+"\","
							+"\""+TASK_DEFINITION.tablefield_timelen+"\":\""+vip_svc.timelen+"\","
							+"\""+TASK_DEFINITION.tablefield_totaltimelen+"\":\""+(Integer.parseInt(vip_svc.counts))*(Integer.parseInt(vip_svc.timelen))+"\"},";					
				}
			}			
		}
		if(liblist!=null&&(jobtype_name.contains(TASK_DEFINITION.task_danger_jobname))){
			for(int i=0;i<liblist.size();i++){
				task_danger_result_lib danger_result=(task_danger_result_lib)liblist.get(i);
				if(danger_result!=null){
					data+="{\""+TASK_DEFINITION.tablefield_mac+"\":\""+danger_result.mac+"\","
							+ "\""+TASK_DEFINITION.tablefield_day+"\":\""+danger_result.day+"\","
							+"\""+TASK_DEFINITION.tablefield_svc+"\":\""+danger_result.svc+"\""
							+ "},";
				}
			}
		}	
		if(liblist!=null&&(jobtype_name.contains(TASK_DEFINITION.task_work_jobname))){
			for(int i=0;i<liblist.size();i++){
				task_work_person_lib work_person=(task_work_person_lib)liblist.get(i);
				if(work_person!=null){
					data+="{\""+TASK_DEFINITION.tablefield_mac+"\":\""+work_person.mac+"\","
							+ "\""+TASK_DEFINITION.tablefield_day+"\":\""+work_person.day+"\","
							+"\""+TASK_DEFINITION.tablefield_svc+"\":\""+work_person.svc+"\""
							+ "},";
				}
			}
		}
		if(liblist!=null&&(jobtype_name.contains(TASK_DEFINITION.task_permanent_jobname))){
			for(int i=0;i<liblist.size();i++){
				task_permanent_ad_lib permanent_ad=(task_permanent_ad_lib)liblist.get(i);
				if(permanent_ad!=null){
					data+="{\""+TASK_DEFINITION.tablefield_mac+"\":\""+permanent_ad.mac+"\","
							+ "\""+TASK_DEFINITION.tablefield_day+"\":\""+permanent_ad.day+"\","
							+"\""+TASK_DEFINITION.tablefield_svc+"\":\""+permanent_ad.svc+"\""
							+ "},";
				}
			}
		}
		if(liblist!=null&&(jobtype_name.contains(TASK_DEFINITION.task_fellow_jobname))){
			for(int i=0;i<liblist.size();i++){
				
			}
		}
		if(data.endsWith(",")) data=data.substring(0,data.length()-1);
		data=data.replace("\n","");			
		System.out.println(" data="+data);	
		MyLog.AddLog("actual_data_analyzer.log","data=="+data);
		return data;
	}
	
    //获取jobname
 	public static String get_jobname(TaskName taskname,String timerang,String mac,String svc,int listernport,boolean fellow_auto){
 		String localip = null;
		try {
			localip = HostPortManager.getLinuxOrWindowIP();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(localip==null)return null;
		
	    String cnfname=get_Stream_cnfname();
	    long taskid=taskname.getTaskid();
	    String dataSource_type=TASK_DEFINITION.task_job_datasource_es;	   
	    String stream_typename=get_streamtasktype(taskname.getTasktype());
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
 	
 	//执行jar分析程序
	public static HashMap<String,ArrayList<libObject>> stream_process(String jobname,int listernport){
		//Map中存放表名及表的数据，表名为key,数据是value		
		HashMap<String,ArrayList<libObject>> job=jobCacheManage.getJob(jobname);
		if(job==null){
			System.out.println("stream_process:"+jobname);
			job=new HashMap<String,ArrayList<libObject>>();
			jobCacheManage.addJob(jobname, job);
			//创建client的socket服务，指定目的主机和port
	        Socket s;
			try {
				List<Map<String,Integer>> zkquorum=ZookeeperMethod.get_zk_quorum();
				if(zkquorum==null||zkquorum.size()==0){
					System.out.println("zookeeper client  cannot find!");
					return null;
				}
				zkManager zm=new zkManager();
				Map<String,Integer> item=zkquorum.get(0);
				for(String subkey:item.keySet()){
					zm.openZk(subkey,item.get(subkey),5000);
					break;
				}				
				String[] str=zm.getData("/memjobhost").split(":");
				if(str.length<2) return null;
				s = new Socket(str[0],Integer.parseInt(str[1]));		
				MyLog.AddLog("actual_data_analyzer.log","Socket str="+str[0]+","+Integer.parseInt(str[1]));
				
				DataOutputStream out=new DataOutputStream(s.getOutputStream());				
				out.writeUTF(jobname);
			    s.close();
		    
			    taskObjManager om=new taskObjManager();
			    jobDataRecv jobrcv=new jobDataRecv();	
			    
			    long start_jobrcvtime=System.currentTimeMillis()/1000;
			    jobrcv.StartRecvJob(job,om,listernport);
			    long end_jobrcvtime=System.currentTimeMillis()/1000;
			    System.out.println("jobrcv use time="+(end_jobrcvtime-start_jobrcvtime)+" seconds");
			    MyLog.AddLog("actual_data_analyzer.log","job is null="+(job==null||job.size()==0));
			    MyLog.AddLog("actual_data_analyzer.log","jobrcv use time="+(end_jobrcvtime-start_jobrcvtime)+" seconds");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//为了发送数据。获取socket流中的输出流
		}
		
		return job;
	}
	
		
	//获取任务类型字符串
	public static String get_streamtasktype(int tasktype){
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
		}
		return task_jobtype;
	}

	

	public static String get_Stream_cnfname(){
		String cnfname=null;
		if(isDebug.islocal) cnfname=TASK_DEFINITION.task_analyser_xml_localpath;
		else{
			proProperties.InitDbFile(Constant.SERVER_DB_PROPERTIES_PATH);
			cnfname=System.getenv("HADOOP_HOME") + Constant.HADOOP_TASKANALYZER_PATH;
		}
		
		return cnfname;
	}



	/**
	 * 给taskname赋值
	 * @param taskname
	 */
	public void putTaskName(TaskName put_taskname) {
		taskname=put_taskname;
	}

	//根据任务类型 获取表名
	public static String get_tablename_by_tasktype(int tasktype){
		if(tasktype==TASK_DEFINITION.task_type_VIP) return TASK_DEFINITION.tablename_vipsvc;
		if(tasktype==TASK_DEFINITION.task_type_fellow) return TASK_DEFINITION.tablename_fellow+","+TASK_DEFINITION.tablename_fellow_ad;
		if(tasktype==TASK_DEFINITION.task_type_permanent) return TASK_DEFINITION.tablename_permanent+","+TASK_DEFINITION.tablename_permanent_ad;
		if(tasktype==TASK_DEFINITION.task_type_workperson) return TASK_DEFINITION.tablename_work_person+","+TASK_DEFINITION.tablename_work_person_ad;
		if(tasktype==TASK_DEFINITION.task_type_highrisk) return TASK_DEFINITION.tablename_danger_result;
		
		return null;
	}

}
