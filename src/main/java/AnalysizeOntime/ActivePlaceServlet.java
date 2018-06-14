/**
 * 
 */
package AnalysizeOntime;

import java.util.HashMap;
import java.util.Map;

import com.proweb.common.timeop;
import com.proweb.common.file.MyLog;

import Common.EncodeSet;
import Common.TASK_DEFINITION;
import Common.TimeDate;
import Model.TaskName;
import debug.isDebug;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import servlet.baseServlet;

public class ActivePlaceServlet  extends baseServlet  {	
	 static boolean islocal=true;
	 static String[] database_paras=new String[4];		
	 static String timerang=null;
	 static String mac=null;
	 static String tasktype=null;
	 static String svc=null;
	 static long timeval;
	 static long taskid;
	 static String pageno=null;
	 static String pagesize=null;
	 static int pno=0;
	 static int psize=10;
	 String ENDRESULT;
	 static long starttime=System.currentTimeMillis()/1000;
	@Override
	public String handle() {			
		islocal=isDebug.islocal;
		tasktype=EncodeSet.str_toUTF8(getObject("tasktype"));
		timerang=getObject("timerang");	
		mac=getObject("mac");
		svc=getObject("svc");	
		pageno=getObject("pageno");	
		pagesize=getObject("pagesize");	
		if(init())	ENDRESULT=analyse();		 
		else  ENDRESULT="{\"status\":\"1\",\"taskid\":\"\",\"data\":[]}";	
		
		return ENDRESULT;
	}
	
	//主程序
	public static String analyse(){		
		  String result="{\"status\":\"1\",\"taskid\":\"null\"}";
		  TaskName taskname=new TaskName();
		  taskname.setTasktype(Integer.parseInt(tasktype));
		  taskname.setTimeval(timeop.GetCurrentTime());
		  long tasktype_basenum=TASK_DEFINITION.tasktype_BaseNum;
		  
		  if(taskname.getTasktype()>tasktype_basenum){		
 			  int typenum=(int) (Integer.parseInt(tasktype)-tasktype_basenum);
 			  taskname.setTasktype(typenum);
 			  taskname.setTaskid(System.currentTimeMillis()/1000+taskname.getTasktype());
 			  OnlineAnalyse actual=new OnlineAnalyse();
			  actual.putTaskName(taskname);
			  String data=null;
			  //获取所有的mac,场所
			  data=actual.execute_getAlldata(taskname,timerang,mac,svc,pno,psize);
			  if(data.contains(",")){
				  if(!data.startsWith("{"))data="{"+data;
				  if(!data.endsWith("}"))data=data+"}";
				  if(!data.startsWith("{\"data\":["))data="{\"data\":["+data+"]}";
				  data=getActivePlacedata_fromJsonData(data);
			  }
			  result="{\"status\":\"0\","+data+"}";				  
		}   		
		return result;
	}
  
	//参数处理
	public static Boolean init(){
		if(!TimeDate.isnum(tasktype)) return false;
		if(Integer.parseInt(tasktype)<TASK_DEFINITION.tasktype_BaseNum) return false;
		if(!TimeDate.isNormaltime_rang(timerang)) return false; 	
		if((Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_VIP)
				||(Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_fellow)){
			if(mac==null||mac.length()==0) return false;
		}
		if(Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_highrisk){
			if(!svc.startsWith("(")) return false;
			if(!svc.endsWith(")")) return false;
		}		
		if((Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_permanent)||
				(Integer.parseInt(tasktype)-TASK_DEFINITION.tasktype_BaseNum==TASK_DEFINITION.task_type_workperson)){
			if(svc==null||svc.length()==0) return false;
		}
		if(TimeDate.isnum(pageno)) pno=Integer.parseInt(pageno);
		if(TimeDate.isnum(pagesize)) psize=Integer.parseInt(pagesize);
		return true;
	}
	
     //intput "{\"data\":[{\"mac\":\"C8-F2-30-5B-5C-A4\",\"day\":\"20180326\",\"svc\":\"37090235990194\",\"counts\":\"1\",\"timelen\":\"1200\",\"totaltimelen\":\"1200\"},
	 //         {\"mac\":\"C8-F2-30-5B-5C-A4\",\"day\":\"20180324\",\"svc\":\"37090235990194\",\"counts\":\"2\",\"timelen\":\"1200\",\"totaltimelen\":\"2400\"}]}";
	 //return "\"data\":[{\"mac\":\"C8-F2-30-5B-5C-A4\",\"svc\":\"37090235990194\",\"counts\":\"3\",\"timelen\":\"2400\",\"totaltimelen\":\"3600\"}]";
	 public static String getActivePlacedata_fromJsonData(String jsondata){
		 MyLog.AddLog("actual_data_analyzer.log", "before active formatdata jsondata="+jsondata);
		 String result="";
		 Map<String, Integer> macsvc_counts_map=new HashMap<String,Integer>();
		 Map<String, Integer> macsvc_timelen_map=new HashMap<String,Integer>();
		 Map<String, Integer> macsvc_totaltimelen_map=new HashMap<String,Integer>();
		 if(!jsondata.contains("}")) return result;
		 else{
			    JSONObject jsonObject = JSONObject.fromObject(jsondata);
		        JSONArray family = jsonObject.getJSONArray("data");
		        for (int i = 0; i < family.size(); i++) {
		            //提取出family中的所有
		        	JSONObject array = JSONObject.fromObject(family.get(i));  
		        	String mac=array.getString(TASK_DEFINITION.tablefield_mac);
		        	String svc=array.getString(TASK_DEFINITION.tablefield_svc);
		        	int counts=Integer.parseInt(array.getString(TASK_DEFINITION.tablefield_counts));
		        	int timelen=Integer.parseInt(array.getString(TASK_DEFINITION.tablefield_timelen));
		        	int totaltimelen=Integer.parseInt(array.getString(TASK_DEFINITION.tablefield_totaltimelen));
		        	String macsvc=mac+"&"+svc;
		        	if(!macsvc_counts_map.containsKey(macsvc))macsvc_counts_map.put(macsvc,counts);
		        	else macsvc_counts_map.put(macsvc,(Integer)macsvc_counts_map.get(macsvc)+counts);
		        	if(!macsvc_timelen_map.containsKey(macsvc))macsvc_timelen_map.put(macsvc,timelen);
		        	else macsvc_timelen_map.put(macsvc, (Integer)macsvc_timelen_map.get(macsvc)+timelen);
		        	if(!macsvc_totaltimelen_map.containsKey(macsvc))macsvc_totaltimelen_map.put(macsvc,totaltimelen);
		        	else macsvc_totaltimelen_map.put(macsvc, (Integer)macsvc_totaltimelen_map.get(macsvc)+totaltimelen);		           
		        }		       
		        if(!macsvc_counts_map.isEmpty()){
		        		//String mac="";
			        String svc="";
			        String timelen="";
			        String counts="";
			        String totaltimelen="";
			        for(Object macsvc:macsvc_counts_map.keySet()){
			        	String str=macsvc.toString();
			        	mac=str.split("&")[0];
			        	svc=str.split("&")[1];
			        	counts=macsvc_counts_map.get(macsvc).toString();
			            if(macsvc_timelen_map.containsKey(macsvc)) timelen=macsvc_timelen_map.get(macsvc).toString();
			            if(macsvc_totaltimelen_map.containsKey(macsvc)) totaltimelen=macsvc_totaltimelen_map.get(macsvc).toString();
			            result+="{\"svc\":\""+svc+"\",\"counts\":\""+counts+"\",\"timelen\":\""+timelen+"\",\"totaltimelen\":\""+totaltimelen+"\"},";
			        }		           
		        }		       
		 }
		 if(result.endsWith(",")) result=result.substring(0,result.length()-1);
		 result="\"data\":["+result+"]";
		 MyLog.AddLog("actual_data_analyzer.log", "after active formatdata result="+result);
		 return result;		 
	 }
		 	
}

