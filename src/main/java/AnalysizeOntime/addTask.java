/**
 * 
 */
package AnalysizeOntime;

import java.util.List;

import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.Base64Encode;
import Common.ConnectToDatabase;
import Common.EncodeSet;
import Common.TASK_DEFINITION;
import Common.TimeDate;
import Model.TaskName;
import probd.hbase.common.MyLog;
import servlet.baseServlet;

public class addTask  extends baseServlet  {

	 static String[] database_paras=new String[4];	
	 static String taskid=null;		
	 static String tasktype=null;
	 static String taskdesc=null;
	 static String taskdesc_in=null;
	 static String taskdesc_tomysql=null;
	 static long stime=0;
	 static long etime=0;
	 static long timeval=0;
	 static long TASKID=0;
	 static String ENDRESULT;
	 static String timerang="";
	 static String codes="";
	 static long starttime=System.currentTimeMillis()/1000;
	 
	@Override
	public String handle() {
		MyLog.AddLog("actual_data_analyzer.log", "开始接收请求============="+System.currentTimeMillis());
		MyLog.AddLog("actual_data_analyzer.log", "into insert method");
		taskid=EncodeSet.str_toUTF8(getObject("taskid"));
		tasktype=EncodeSet.str_toUTF8(getObject("tasktype"));
		taskdesc=getObject("taskdesc").trim().replace("%22", "\"");	
		MyLog.AddLog("actual_data_analyzer.log", "get paras taskdesc="+taskdesc);
		System.out.println("taskdesc=="+taskdesc);
		ENDRESULT="{\"status\":\"4\",\"taskid\":\""+taskid+"\"}";
		MyLog.AddLog("actual_data_analyzer.log", "addTask paramas===taskid ==="+taskid + "tasktype===" + tasktype + "taskdesc ===" + taskdesc);

		if(init()) ENDRESULT=insert();	
		MyLog.AddLog("actual_data_analyzer.log", "back json============="+ENDRESULT);

		return ENDRESULT;
	}

	public static String insert(){
		String sql="";
		String str=ENDRESULT;		
		try{
			MyLog.AddLog("actual_data_analyzer.log", "before insert sql time="+System.currentTimeMillis());
			ConnectToDatabase.connect();
			mysqlObject sqlobj=new mysqlObject();
			sqlobj.clearObject();
			sql="insert into actual_taskname(taskid,tasktype,taskdesc,taskname,taskmd5,status)values("+TASKID+","+tasktype+",\""+taskdesc_tomysql+"\","+"\"\""+","+"\"\","+"0"+")";
			MyLog.AddLog("actual_data_analyzer.log", "insert sql="+sql);
			mysqlObject.ExeSql(sql);
			str="{\"status\":\"0\",\"taskid\":\""+taskid+"\"}";
			MyLog.AddLog("actual_data_analyzer.log", "after insert sql time="+System.currentTimeMillis());

			//开启分析程序
			//OnlineAnalyse analyse=new OnlineAnalyse();
			TaskName taskname=new TaskName();
			taskname.setTaskid(TASKID);
			taskname.setTasktype(Integer.parseInt(tasktype));
			String jobname=OnlineAnalyse.get_jobname(taskname, timerang, "", codes, OnlineAnalyse.getListernPort(),false);
			System.out.println("jobname="+jobname);		
			MyLog.AddLog("actual_data_analyzer.log", "jobname="+jobname);
			OnlineAnalyse.stream_process(jobname);
			MyLog.AddLog("actual_data_analyzer.log", "after stream_process time="+System.currentTimeMillis()/1000);

		}catch(Exception e){
			MyLog.AddLog("actual_data_analyzer.log", "insert sql exception sql="+sql);
		}
		MyLog.AddLog("actual_data_analyzer.log", "完成请求处理并返回============="+System.currentTimeMillis());
		return str;
	}
	
	
	public boolean init(){
		 String spacetime_args="";
		if(!TimeDate.isnum(taskid)) return false;
		else TASKID=Long.parseLong(taskid);
		if(!TimeDate.isnum(tasktype))return false;		
		try{
			String sstime="";
			String eetime="";
			String cnum="";
			String taskdesc_in=taskdesc;		
			
			taskdesc=taskdesc.replace("[","").replace("]","").replace(" ", "");;
			MyLog.AddLog("actual_data_analyzer.log", "into init taskdesc=="+taskdesc_in);
			if(Integer.parseInt(tasktype)==TASK_DEFINITION.task_type_pegg){
				taskdesc=taskdesc.replace("{","").replace("}","").replace("\"", "");
				String[] arr=taskdesc.split(",");
				codes=taskdesc.substring(taskdesc.indexOf("codes:"),taskdesc.indexOf(",cnum:")).replace("codes:", "");
				for(int i=0;i<arr.length;i++){
					String tmp=arr[i];
					if(tmp.contains("stime")) sstime=tmp.replace("stime:", "").replace("\"", "");
					if(tmp.contains("etime")) eetime=tmp.replace("etime:", "").replace("\"", "");			
					if(tmp.contains("cnum")) cnum=tmp.replace("cnum:", "").replace("\"", "");
				}
				
			    stime=Long.parseLong(sstime.toString());
			    etime=Long.parseLong(eetime.toString());		
			    
			    timerang=stime+"~"+etime;
			}else if(Integer.parseInt(tasktype)==TASK_DEFINITION.task_type_spacetime){
				//{"hitRate":"100","type":"mac","data":[{"logic":"and","stime":"1525569300","etime":"1525571160","servicecode":"31010525518519"}]}
				System.out.println(taskdesc);
				taskdesc=taskdesc.replace("\"", "");
				if(taskdesc.contains("data")){
					String str=taskdesc.split("data:")[1].replace("stime:","").replace(",etime:", "~").replace(",servicecode:",",").replace("[","").replace("]", "");
					spacetime_args=str.replace("},{","&").replace("{","").replace("}","");					
				}else{
					if(taskdesc.contains("},{")) {
						String[] arr=taskdesc.replace("},{", "}={").split("=");
						for(String str:arr){
							str=str.split(",hitRate:")[0].replace(",hitRate:","").replace("stime:","").replace(",etime:", "~").replace(",servicecode:",",").replace("[","").replace("]", "").replace("{", "").replace("}", "");
							System.out.println("str=="+str);
							spacetime_args+=str+"=";
						}
						if(spacetime_args.endsWith("=")) spacetime_args=spacetime_args.substring(0,spacetime_args.length()-1);
					}else{
						taskdesc=taskdesc.split("stime:")[1].replace("stime:","").replace(",etime:", "~").replace(",servicecode:",",").replace("[","").replace("]", "").replace("}", "");
						System.out.println("str=="+taskdesc);
						spacetime_args+=taskdesc;
					}
					
				}
				OnlineAnalyse.spacetime_args=spacetime_args;
			}
			
		    taskdesc_tomysql=Base64Encode.encoded(taskdesc_in).replace("\n","");
		    System.out.println(codes+" "+sstime+" "+eetime+" "+cnum);
		}catch(Exception e){
		    return false;
		}
 		return true;
	}
	
	/** 查看某张表中某一列是否存在某值 */
	public static boolean istaskid_exists(mysqlObject sqlobj,boolean islocal,String tablename,String column_name,String column_value){		
		boolean flag=false;		
		sqlobj.clearObject();
		String sql="select count(*) as count from "+tablename+" where "+column_name+"=\""+column_value+"\" and status not in(2,3)";
		System.out.println("check is exists sql=="+sql);
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		if(rows.size()>0){
			String result=sqlobj.toJosn(0,10,10);	
			 String str=result.split("count")[1];
			 int count= Integer.parseInt(str.substring(3,str.length()-4));
			 if(count>0){
				 flag=true;
			 }
		}		
		return flag;
	}
	
}
