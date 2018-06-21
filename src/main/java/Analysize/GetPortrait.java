package Analysize;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.Base64Encode;
import Common.ConnectToDatabase;
import Common.Print;
import Common.Task_result_Judge_new;
import Common.TimeDate;
import servlet.baseServlet;

public class GetPortrait extends baseServlet {
	
	private String mac;
	private String lable;
	private String taskid;
	private String ENDRESULT;
	private String limitstr;
	private int pageno=0;
	private int pagesize=10;
	private String limit;
	private String page;
	private int totals=10000;
	private int return_totals=0;
	private String highrisk_altertime="23:00~5:00";
	private String work_time="06:30~10:00,16:30~20:30";
	private String fpgrowth_minConfidence="99%";
	private String permanent_limitrate ="50%";
	
	
	@Override
	public String handle() {
		mac=getObject("name");
		taskid=getObject("taskid");
		limit=getObject("limit");
		page=getObject("page");
		//屏蔽Mac，label同时为空的情况，只有在其中一个不为空的情况下才进行操作
		if(init_paras()){
			//mac,label都不为空的情况暂时不做处理
			if(mac!=null){
				clear_EndResult();
				try {
					ENDRESULT=Manage();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}		
		}else{
			ENDRESULT="{\"status\":\"1\",\"data\":[]}";
		}
		return ENDRESULT;
	}
	
	
	public String Manage() throws UnsupportedEncodingException{
		clear_EndResult();
		String result="";
		init_limitstr();
		ConnectToDatabase.connect();		
		mysqlObject sqlobj= new mysqlObject();		
		if(mac!=null){
			if(TimeDate.isnum(taskid)){				
				String res="";
				String fellow_res=Operate_mac_outlable_taskid(sqlobj,"fellow","name");
				if(fellow_res.length()>0)     res+=fellow_res+",";
				String permanent_res=Operate_mac_outlable_taskid(sqlobj,"permanent","mac");
				if(permanent_res.length()>0)  res+=permanent_res+",";
				String danger_res=Operate_mac_outlable_taskid(sqlobj,"danger_result","mac");
				if(danger_res.length()>0)     res+=danger_res+",";
				String vip_res=Operate_mac_outlable_taskid(sqlobj,"vip_svc","mac");
				if(vip_res.length()>0)     res+=vip_res+",";
				String work_person_res=Operate_mac_outlable_taskid(sqlobj,"work_person_ad","mac");
				if(work_person_res.length()>0)     res+=work_person_res+",";
				if(res.endsWith(",")) res=res.substring(0,res.length()-1);
				res=Task_result_Judge_new.getdata_fromMtoN_likepage_instr(res.replace("},{","}={"),pageno,pagesize);
				result="{\"status\":\"0\",\"totalnums\":\""+(res.split("lable").length-1)+"\",\"result\":["+res+"]}";
			}else{
				/*if((taskid.length()>0)&&(TimeDate.isnum(taskid))) TASKID=Integer.parseInt(taskid);
				else TASKID=0;
				Map judge_table_map=Task_result_Judge_new.judge_ownedtable_bytaskid(sqlobj,String.valueOf(TASKID));
				String tablename="";
				String mac_columnname="";
				if(judge_table_map.keySet().size()>0) tablename=judge_table_map.keySet().toArray()[0].toString();
				if(!tablename.isEmpty())  mac_columnname=judge_table_map.get(tablename).toString();
				if(tablename.equalsIgnoreCase("fellow;fellow_ad")) tablename="fellow";
				if(tablename.equalsIgnoreCase("permanent_ad;permanent")) tablename="permanent_ad";
				if(tablename.contains("vip")) tablename="vip_svc";
				String judge=getJudgeResult_by_taskid_mac(tablename,mac_columnname,TASKID,mac,sqlobj,100,"", 100);
				if(judge.length()<=0) judge="\"\"";*/
				result="{\"status\":\"0\",\"totalnums\":\""+0+"\",\"result\":\"\"}";
			} 			
		}	
		return result;
	}
	
	/**
	*高危研判依据的返回格式：该MAC在2017-10-09~201710-30 期间在高危时段 23:00~5:00 ，高危区域 "火车站A","火车站B" 出现 XX 次
    *伴随研判依据的返回格式：该MAC在2017-10-09~201710-30 期间 有80%的概率出现伴随MAC:XX-XX-XX,XX-XX-XX
    *常驻研判依据返回格式：在分析辖区内，该MAC每月出现频次不小于50%，近一个月出现频次为XX ,常驻地点 XX ，该地点采集次数为 XX 
    *重点人群：{"mac":"XX"},{"svc":"XX","timelen":"2","nums":"2"},{"trace":"XXX"}
    *上班族：{"mac":"XX","time":"6:30~10:00,16:30~20:30","nums":"5"} 统计最近一周的频次
    *传入研判结果表，Mac对应的列，taskid，mac,mysqlObject,pagesize,limitstr,totals
    *
    */
	public String getJudgeResult_by_taskid_mac(String tablename,String mac_columnname,int task_id,String mac,mysqlObject obj,int pagesize, String limitstr, int totals) throws UnsupportedEncodingException {
        String res="";       
		if(tablename.contains("danger"))  res=danger_judge(tablename,mac_columnname,task_id,mac,obj,pagesize,limitstr,totals);
		if(tablename.contains("fellow"))  res=fellow_judge(tablename,mac_columnname,task_id,mac,obj,pagesize,limitstr,totals);
		if(tablename.contains("permanent"))  res=permanent_judge(tablename,mac_columnname,task_id,mac,obj,pagesize,limitstr,totals);
		if(tablename.contains("vip"))  res=vip_judge(tablename,mac_columnname,task_id,mac,obj,pagesize,limitstr,totals);
		if(tablename.contains("work"))  res=workperson_judge(tablename,mac_columnname,task_id,mac,obj,pagesize,limitstr,totals);
		return res;
	}
	
	/**根据taskid，mac 获取常驻研判依据*/
	public String permanent_judge(String tablename,String mac_columnname, int task_id, String mac2, mysqlObject obj,int pagesize2, String limitstr2, int totals2) {
		String judge_str="";
		if(tablename.contains("permanent")){
			obj.clearObject(); 
			int permanent_totalnums=0;
			String maxnum_svc="";
			long maxsvc_nums=0;
			String selec_totalnums_bytaskid_mac="select count(*) as count from "+tablename+" where taskid="+task_id+" and "+mac_columnname+"=\""+mac2+"\"   and day>="+TimeDate.getOneWeekBefore();
			List<mysqlRow> selec_totalnums_bytaskid_mac_rows=obj.ExeSqlQuery(selec_totalnums_bytaskid_mac);
			if(selec_totalnums_bytaskid_mac_rows.size()>0){
				String result2=obj.toJosn(0,10,10);	
				String str=result2.split("count")[1];
				permanent_totalnums= Integer.parseInt(str.substring(3,str.length()-4));
			}
			String selec_maxnums_svc="select svc,count from (select id,svc,count(*) as count from permanent_ad where taskid="+task_id+" and "+mac_columnname+"=\""+mac+"\" and day>="+TimeDate.getOneWeekBefore()+" group by svc) as b order by count desc limit 1";
	        List<mysqlRow> selec_maxnums_svc_rows=obj.ExeSqlQuery(selec_maxnums_svc);
		    if(selec_maxnums_svc_rows.size()>0) {
				String str="";
				str=obj.toJosn(0,10, 0);
				str=str.split(",\"rows\"")[1];
				str=str.substring(2,str.length()-2);
				str=str.replace("\"svc\":\"","").replace("\"count\":\"","").replace("{","").replace("}","").replace("\"","");
				if(str.endsWith(",")) str=str.substring(0,str.length()-1);
				 maxnum_svc=str.split(",")[0];
				 maxsvc_nums=Long.parseLong(str.split(",")[1]);
		    } 
		    if(permanent_totalnums>0) judge_str="{\"day\":\"\",\"svc\":\""+maxnum_svc+"\",\"nums\":\""+maxsvc_nums+"\",\"accord\":\"在分析辖区内,该MAC每月出现频次不小于"+permanent_limitrate+",近一个月出现频次为"+permanent_totalnums+",常驻地点 "+maxnum_svc+" ,该地点采集次数为 "+maxsvc_nums+"\"}";
		}
		return judge_str;
	}


	/**根据taskid，mac 获取伴随研判依据,*/
	public  String fellow_judge(String tablename, String mac_columnname,int task_id, String mac2, mysqlObject obj, int pagesize2,String limitstr2, int totals2) {
		String judge_str="";
		if(tablename.contains("fellow")){
			 obj.clearObject();
			 //String fellowid_str="";
			 String macstr="";
			 List<String> FELLOWID_list=new ArrayList<String>();
			 Map<String, String> svc_map=new HashMap<String,String>();
			 if(mac2.contains(",")) mac2=mac2.split(",")[0];
			 if(mac2.length()>0){
				    obj.clearObject();
					String FELLOWID_STR="";
					String selec_fellowid="select distinct(fellowid) from "+tablename+" where "+mac_columnname+"=\'"+mac2+"\'   and day>="+TimeDate.getYesterday();
					List<mysqlRow> rows_fellowid_bymac=obj.ExeSqlQuery(selec_fellowid);
					if(pagesize2>totals2) pagesize2=totals2;
					if(rows_fellowid_bymac.size()>0) {
						String str="";
						str=obj.toJosn(0,pagesize2, 0);
						str=str.split(",\"rows\"")[1];
						str=str.substring(2,str.length()-2);
						if(str.endsWith(",")) str=str.substring(0,str.length()-1);
						str=str.replace("\"","").replace("{","").replace("}","").replace("fellowid:","");
						FELLOWID_STR+=str+",";
					} 
					if(FELLOWID_STR.endsWith(",")) FELLOWID_STR=FELLOWID_STR.substring(0,FELLOWID_STR.length()-1);
					if(FELLOWID_STR.contains(",")){
						String[] FELLOWID_STR_list=FELLOWID_STR.split(",");
						for(int j=0;j<FELLOWID_STR_list.length;j++){
							FELLOWID_list.add(FELLOWID_STR_list[j]);
						}
					}else if(FELLOWID_STR.length()>0&&(!FELLOWID_STR.contains(","))) FELLOWID_list.add(FELLOWID_STR);
					for(int k=0;k<FELLOWID_list.size();k++){
						obj.clearObject();
						String select_mac_byfellowid_sql="select distinct("+mac_columnname+") from "+tablename+" where fellowid=\'"+FELLOWID_list.get(k)+"\'   and day>="+TimeDate.getYesterday();
						List<mysqlRow> rows_elect_mac_byfellowid_=obj.ExeSqlQuery(select_mac_byfellowid_sql);
						if(rows_elect_mac_byfellowid_.size()>0) {
							String str="";
							str=obj.toJosn(0,pagesize2, 0);
							str=str.split(",\"rows\"")[1];
							str=str.substring(2,str.length()-2);
							if(str.endsWith(",")) str=str.substring(0,str.length()-1);
							str=str.replace("\""+mac_columnname+"\":","").replace("\"","").replace("{","").replace("}","");
							macstr+=str+",";
						} 
					}
					if(macstr.endsWith(",")) macstr=macstr.substring(0,macstr.length()-1);
					
			 }			 
			 obj.clearObject(); 
			 /*String selec_day="select min(day),max(day) from "+tablename+" where taskid="+task_id+" and "+mac_columnname+"=\""+mac2+"\"";
			 List<mysqlRow> selec_mac_byfellowid_rows=obj.ExeSqlQuery(selec_day);
			 String daystr="";	
			 if(selec_mac_byfellowid_rows.size()>0) {
				String str="";
				str=obj.toJosn(0,10, 0);
				str=str.split(",\"rows\"")[1];
				str=str.substring(2,str.length()-2);
				daystr=str.replace("\"min(day)\":\"","").replace("\"max(day)\":\"","").replace("\"","").replace(",","~").replace("{", "").replace("}","");
				if(daystr.endsWith(",")) daystr=daystr.substring(0,daystr.length()-1);
			 } 	   */ 
			 String svc="";
			 for(int i=0;i<FELLOWID_list.size();i++){
				 obj.clearObject();
				 String selec_svc_byfellowid_sql="select svc from fellow_ad where fellowid=\'"+FELLOWID_list.get(i)+"\'";
				 List<mysqlRow> selec_svc_byfellowid_sql_rows=obj.ExeSqlQuery(selec_svc_byfellowid_sql);
				 if(selec_svc_byfellowid_sql_rows.size()>0) {
					String str="";
					str=obj.toJosn(0,10, 0);
					str=str.split(",\"rows\"")[1];
					str=str.substring(2,str.length()-2);
					str=str.replace("\"svc\":\"","").replace("\"","").replace("{", "").replace("}","");
					if(str.contains(",")) {
						if(str.endsWith(",")) str=str.substring(0,str.length()-1);
						String[] strs=str.split(",");
						for(int m=0;m<strs.length;m++){
							svc_map.put(strs[m],"1");
						}
					} 	
					else if(str.length()>0&&(!str.contains(","))) svc_map.put(str,"1");
				 }
			 }
			 Set<String> set=svc_map.keySet();
			 Iterator<String> itor=set.iterator();
			 while(itor.hasNext()){
				 svc+=itor.next().toString()+",";
			 }
			 if(svc.endsWith(",")) svc=svc.substring(0,svc.length()-1);
			 /*String[] day_arr=daystr.split("~");
			 String minday_str=day_arr[0];
			 String maxday_str=day_arr[1];
			 maxday_str=TimeDate.getSpecifiedDayAfter(maxday_str);*/
			 String daystr=TimeDate.getYesterday()+"~"+TimeDate.getToday();
			 if(macstr.length()>0) judge_str="{\"day\":\""+daystr+"\",\"svc\":\""+svc+"\",\"nums\":\"0\",\"accord\":\"该MAC在"+daystr+" 期间 有"+fpgrowth_minConfidence+"的概率出现伴随MAC:"+macstr+"\"}";
		}
		return judge_str;
	}


	/**根据taskid，mac 获取高危研判依据*/
	public  String danger_judge(String tablename,String mac_columnname,int task_id,String mac,mysqlObject obj,int pagesize, String limitstr, int totals) throws UnsupportedEncodingException {
		String judge_str="";
		if(tablename.contains("danger")){
			String daystr="";
			int minday=0;
			int maxday=0;
			String areastr="";
			long dangernums=0;
			//String  dangertime="";
			obj.clearObject();
			String select_day="select distinct(day) from "+tablename+" where taskid="+task_id+" and "+mac_columnname+"=\""+mac+"\" and day>="+TimeDate.getOneWeekBefore();
			List<mysqlRow> rows_day=obj.ExeSqlQuery(select_day);
			if(pagesize>totals) pagesize=totals;
			if(rows_day.size()>0) {
				daystr=obj.toJosn(0,pagesize, 0);
				daystr=daystr.split(",\"rows\"")[1];
				daystr=daystr.substring(2,daystr.length()-2);
				daystr=daystr.replace("\""+mac_columnname+"\":","").replace("\"","").replace("day:","").replace("{","").replace("}","");
			} 	
			if(daystr.endsWith(",")) daystr=daystr.substring(0,daystr.length()-1);
			if(daystr.length()>0){
				if(daystr.contains(",")){
				     obj.clearObject();
				     String[] day_arr=daystr.split(",");
				     for(int i=0;i<day_arr.length;i++){
				    	    String selec_dangernums_byday="select nums from "+tablename+" where taskid="+task_id+" and "+mac_columnname+"=\""+mac+"\" and day=\""+day_arr[i]+"\"";
				    	    List<mysqlRow> dangernums__bytaskid_daymac=obj.ExeSqlQuery(selec_dangernums_byday);
							if(pagesize>totals) pagesize=totals;
							if(dangernums__bytaskid_daymac.size()>0) {
								String str="";
								str=obj.toJosn(0,pagesize, 0);
								str=str.split(",\"rows\"")[1];
								str=str.substring(2,str.length()-2);
								if(str.endsWith(",")) str=str.substring(0,str.length()-1);
								str=str.replace("\""+mac_columnname+"\":","").replace("\"","").replace("{","").replace("}","").replace("nums:","");
								if(TimeDate.isnum(str)) dangernums+=Long.parseLong(str);
							} 	
				     }
				     //取出最小day,最大day
				      minday=Integer.parseInt(day_arr[0]);
				      maxday=Integer.parseInt(day_arr[0]);
				      for(int i=1;i<day_arr.length;i++){
				    	 if(Integer.parseInt(day_arr[i])<minday) minday=Integer.parseInt(day_arr[i]);
				    	 if(Integer.parseInt(day_arr[i])>maxday) maxday=Integer.parseInt(day_arr[i]);
				    	 maxday=Integer.parseInt(TimeDate.getSpecifiedDayAfter(String.valueOf(maxday)));
				      }
				     
			    }else{
			    	 obj.clearObject();
			    	 minday=Integer.parseInt(daystr);
			    	 maxday=Integer.parseInt(TimeDate.getSpecifiedDayAfter(daystr));
			    	String selec_dangernums_byday="select nums from "+tablename+" where taskid="+task_id+" and "+mac_columnname+"=\""+mac+"\" and day=\""+daystr+"\"";
		    	    List<mysqlRow> dangernums__bytaskid_daymac=obj.ExeSqlQuery(selec_dangernums_byday);
					if(pagesize>totals) pagesize=totals;
					if(dangernums__bytaskid_daymac.size()>0) {
						String str="";
						str=obj.toJosn(0,pagesize, 0);
						str=str.split(",\"rows\"")[1];
						str=str.substring(2,str.length()-2);
						if(str.endsWith(",")) str=str.substring(0,str.length()-1);
						str=str.replace("\""+mac_columnname+"\":","").replace("\"","").replace("{","").replace("}","").replace("nums:","");
						if(TimeDate.isnum(str)) dangernums+=Long.parseLong(str);
					} 	
			    }
			}
			obj.clearObject();
			String selec_taskdesc_bytaskid="select taskdesc from taskname where taskid="+task_id;
			List<mysqlRow> selec_taskdesc_bytaskid_rows=obj.ExeSqlQuery(selec_taskdesc_bytaskid);
			if(pagesize>totals) pagesize=totals;
			String taskdesc_encode="";
			if(selec_taskdesc_bytaskid_rows.size()>0) {
				String str="";
				str=obj.toJosn(0,pagesize, 0);
				str=str.split(",\"rows\"")[1];
				str=str.substring(2,str.length()-2);
				if(str.endsWith(",")) str=str.substring(0,str.length()-1);
				taskdesc_encode=str.replace("\""+mac_columnname+"\":","").replace("\"","").replace("{","").replace("}","").replace("taskdesc:", "");
			} 	
			String taskdesc_decode=Base64Encode.decode(taskdesc_encode);
			String[] taskdesc_arrs=taskdesc_decode.split("\"group\":");
	        for(int i=0;i<taskdesc_arrs.length;i++){
	    	  String str=taskdesc_arrs[i];
	    	  if(str.contains("scode")){
	    		  String scode_arr=str.split(",\"scode\"")[0];
	    		  boolean flag= (scode_arr == null || scode_arr == "" || (scode_arr.length()==2));
	    		  if(!flag) areastr+=scode_arr+",";
	    	  }    	  
	        }	       
		    if(areastr.endsWith(",")) areastr=areastr.substring(0,areastr.length()-1);		    
		    areastr=areastr.replace("\"", "");		   
			daystr=TimeDate.getYesterday()+"~"+TimeDate.getToday();
			if(minday>0&&dangernums>0) judge_str="{\"day\":\""+daystr+"\",\"svc\":\""+areastr+"\",\"nums\":\""+dangernums+"\",\"accord\":\"该MAC在 "+daystr+" 期间在高危时段 "+highrisk_altertime+" 高危区域: "+areastr+" 出现 "+dangernums+" 次\"}";
		}
		return judge_str;
	}
	
	/**获取重点人群的研判json*/
	public static String vip_judge(String tablename,String mac_columnname,int task_id,String mac,mysqlObject obj,int pagesize, String limitstr, int totals) throws UnsupportedEncodingException {
		String json="{\"mac\":\"\",\"svc\":\"\",\"trace\":\"\"}";
		String finaltrace="";
        String finalsvc="";
		if(tablename.contains("vip")){
			String svc="";
			String trace="";
			obj.clearObject();
			String select_svc="select distinct(svc)  from "+tablename+" where taskid="+task_id+" and "+mac_columnname+"=\""+mac+"\"  and day>="+TimeDate.getOneWeekBefore();
			List<mysqlRow> rows_svc=obj.ExeSqlQuery(select_svc);
			if(pagesize>totals) pagesize=totals;
			if(rows_svc.size()>0) {
				String str="";
				str=obj.toJosn(0,pagesize, 0);
				str=str.split(",\"rows\"")[1];
				str=str.substring(2,str.length()-2);
				if(str.endsWith(",")) str=str.substring(0,str.length()-1);
				svc=str.replace("\""+mac_columnname+"\":","").replace("\"","").replace("{","").replace("}","").replace("svc:","");
				List<String> svclist=new ArrayList<String>();
				if(svc.contains(",")){
					String[] svc_arr=svc.split(",");
					for(int i=0;i<svc_arr.length;i++){
						svclist.add(svc_arr[i]);
					}
				}
				if((!svc.contains(","))&&svc.length()>0) svclist.add(svc);
				String svc_result="";
				obj.clearObject();
				for(int j=0;j<svclist.size();j++){
					String svc_sql="select svc,timelen,nums from vip_svc where mac='"+mac+"' and taskid="+task_id+" and svc=\""+svclist.get(j)+"\" order by day desc limit 1";
					List<mysqlRow> rows=obj.ExeSqlQuery(svc_sql);
					if(rows.size()>0){
						String svc_str="";
						svc_str=obj.toJosn(0,pagesize, 0);
						svc_str=svc_str.split(",\"rows\"")[1];
						svc_str=svc_str.substring(2,svc_str.length()-2);					
						svc_result+=svc_str;
					}
				}
				if(svc_result.endsWith(","))svc_result=svc_result.substring(0,svc_result.length()-1);	
				finalsvc=svc_result;
			} 	
			
			obj.clearObject();
			//求最符合条件的一个trace，在vip_trace中，一周内trace出现频次最多的是最符合的，如果有两个同样的最大频次，取trace长度最长的
			String select_trace="select distinct(trace)  from vip_trace where taskid="+task_id+" and "+mac_columnname+"=\""+mac+"\"  and day>="+TimeDate.getOneWeekBefore();
			List<mysqlRow> rows_trace=obj.ExeSqlQuery(select_trace);
			if(pagesize>totals) pagesize=totals;
			if(rows_trace.size()>0) {
				String str="";
				str=obj.toJosn(0,pagesize, 0);
				str=str.split(",\"rows\"")[1];
				str=str.substring(2,str.length()-2);
				if(str.endsWith(",")) str=str.substring(0,str.length()-1);
				trace=str.replace("\""+mac_columnname+"\":","").replace("\"","").replace("},{",";").replace("{","").replace("}","").replace("trace:","");
				List<String> tracelist=new ArrayList<String>();
				if(trace.contains(",")){
					String[] trace_arr=trace.split(";");
					for(int i=0;i<trace_arr.length;i++){
						tracelist.add(trace_arr[i]);
					}
				}
				if((!trace.contains(","))&&trace.length()>0) tracelist.add(trace);	
				obj.clearObject();
				Map<String, Integer> trace_map=new HashMap<String, Integer>();
				for(int tra=0;tra<tracelist.size();tra++){
					String trace_sql="select count(*) as count from vip_trace where taskid="+task_id+" and mac='"+mac+"' and trace=\""+tracelist.get(tra).toString()+"\" and day>="+TimeDate.getOneWeekBefore();
					List<mysqlRow> rows=obj.ExeSqlQuery(trace_sql);
					if(rows.size()>0){
						String result2=obj.toJosn(0,10,10);	
						if(result2.contains("count")){
							String numstr=result2.split("count")[1].split("}")[0].substring(2).replace("\"", "");
							int nums=Integer.parseInt(numstr);
							trace_map.put(tracelist.get(tra).toString(),nums);					
						} 
					}
				}
				Map<String, Integer> max_trace_map=new HashMap<String, Integer>();
				Map<String, Integer> equal_trace_map=new HashMap<String, Integer>();
			    int tempnum=1;
			    for (Map.Entry<String, Integer> entry : trace_map.entrySet()) {  
			      String trace_key=entry.getKey();
			      int nums=entry.getValue();
		       	  if(nums>tempnum){
		    		  tempnum=nums;
		    		  max_trace_map.clear();
		    		  max_trace_map.put(trace_key, tempnum);
		    	  } 
		    	  if(tempnum==nums) equal_trace_map.put(trace_key, tempnum);
			    }
			    int trace_len=0;
			    if(max_trace_map.isEmpty()){			    	
			    	 for (Map.Entry<String, Integer> entry : equal_trace_map.entrySet()) {  
			    		 int len=entry.getKey().length();
			    		 if(len>trace_len){
			    			 trace_len=len;
			    			 max_trace_map.clear();
			    			 max_trace_map.put(entry.getKey(),1);
			    		 } 
			    	 }
			    }
			    for (Entry<String, Integer> entry: max_trace_map.entrySet())  finaltrace = entry.getKey();	
			}
			json="[{\"mac\":\""+mac+"\"},"+finalsvc+",{\"trace\":\""+finaltrace+"\"}]";
		}			
		return json;
	}
	
	
	
	/*获取上班族的研判json*/
	public String workperson_judge(String tablename,String mac_columnname,int task_id,String mac,mysqlObject obj,int pagesize, String limitstr, int totals) throws UnsupportedEncodingException {
		String json="{\"mac\":\"\",\"time\":\"\",\"nums\":\"\"}";
		if(tablename.contains("work")){
			int nums=0;
			String oneweek_before=TimeDate.getOneWeekBefore();
			obj.clearObject();
			String select_day="select sum(nums) as count from "+tablename+" where taskid="+task_id+" and "+mac_columnname+"=\""+mac+"\" and day>='"+oneweek_before+"'";
			List<mysqlRow> rows_day=obj.ExeSqlQuery(select_day);			
			if(rows_day.size()>0){
				String result2=obj.toJosn(0,10,10);	
				String str="";
				if(result2.contains("count")){
					str=result2.split("count")[1];
					nums= Integer.parseInt(str.substring(3,str.length()-4));					
				} 
			}
			json="{\"mac\":\""+mac+"\",\"time\":\""+work_time+"\",\"nums\":\""+nums+"\"}";				
		}
		return json;
	}
	
	
	
	/**根据Mac输出所属标签及对应taskid*/
	public String Operate_mac_outlable_taskid(mysqlObject obj,String tablename, String mac_columnname){
		String combinestr="";
		String info="";
		limitstr="";
		return_totals=0;
		return_totals=10000;
		obj.clearObject();
		String selec_taskid_bymac="select  distinct(taskid) from "+tablename+" where "+mac_columnname+"=\'"+mac+"\'";
		Print.println("selec_taskid_bymac="+selec_taskid_bymac);		
		Map<String ,String> lable_result_map=new HashMap<String ,String>();
		List<mysqlRow> rows=obj.ExeSqlQuery(selec_taskid_bymac);
		if(rows.size()>0){
			String result=obj.toJosn(0, totals, 0);
			result=result.split(",\"rows\"")[1];
			result=result.substring(2,result.length()-2);
			String taskidstr=result.replace("\"taskid\":","").replace("\"","").replace("{","").replace("}","");
			String[] taskidarr=taskidstr.split(",");
			Map<String ,String> taskidmap=new HashMap<String ,String>();
			for(int i=0;i<taskidarr.length;i++){
				taskidmap.put(taskidarr[i],mac);
			}
			String res="";
			for (Map.Entry<String, String> entry : taskidmap.entrySet()) {  		  
			    String lab=getLable_bytaskid(Integer.parseInt(entry.getKey()),obj);		    
			    res +=lab+","+entry.getKey()+"=";
			}  
			if(res.contains("=")){ 
				String[] lable_taskid_arr=res.split("=");
			    for(int i=0;i<lable_taskid_arr.length;i++){
			    	String lable_taskid=lable_taskid_arr[i];
		    		lable_result_map.put(lable_taskid,"");
			    }	
			}
			for (Map.Entry<String, String> entry : lable_result_map.entrySet()) {  
				String lab_id=entry.getKey();
				if(lab_id.contains(",")) {
					String lab=lab_id.split(",")[0];
					String id=lab_id.split(",")[1];
					info+="{\"lable\":\""+lab+"\",\"taskid\":\""+id+"\"},";
				}
			}  
			if(info.endsWith(",")) info=info.substring(0,info.length()-1);   
			combinestr=info;
	  }
        return combinestr;
	}	


	

	//lable不为空，mac为空，输出相关Mac及依据
	public String Operate_lable(mysqlObject obj){
		String res="";
		return_totals=0;
		return_totals=Task_result_Judge_new.getMacNum_by_lable(lable,obj,"");
		obj.clearObject();
		String selec_lable_bymac="select taskid from taskname where lable="+"\'"+lable+"\'";
		List<mysqlRow> rows=obj.ExeSqlQuery(selec_lable_bymac);		
		if(rows.size()>0){			
			String result=obj.toJosn(0, totals, 0);
			result=result.split(",\"rows\"")[1];
			result=result.substring(2,result.length()-2);
			String taskidstr=result.replace("\"taskid\":","").replace("\"","").replace("{","").replace("}","");
			Print.println("taskidstr:"+taskidstr);
			String[] taskidarr=taskidstr.split(","); 
			String totalmacstr="";
			for(int i=0;i<taskidarr.length;i++){
				String all_Judge_str=Task_result_Judge_new.getAll_JudgeData(Integer.parseInt(taskidarr[i]), totals,obj,"",return_totals);
				if(all_Judge_str.length()>0){
					totalmacstr+=all_Judge_str+"=";					
				}					
			}			
			if(totalmacstr.contains("=")) totalmacstr=combine_data_str(totalmacstr);
			Print.println("输出totalmacstr "+totalmacstr);
			if(totalmacstr.length()==0) totalmacstr="{\"mac\":\"\",\"macnums\":\""+0+"\",\"record\":\"\"}";
			totalmacstr=Task_result_Judge_new.getdata_fromMtoN_likepage_instr(totalmacstr, pageno, pagesize);
			res="{\"status\":\"0\",\"pageno\":\""+pageno+"\",\"pagesize\":"+"\""+pagesize+"\",\"totalrows\":\""+return_totals+"\",\"data\":["+totalmacstr+"]}";	
			res=res.replace("},]","}]");			
			Print.println("res="+res);
		}else{
			res="{\"status\":\"0\",\"pageno\":\""+pageno+"\",\"pagesize\":"+"\""+pagesize+"\",\"totalrows\":\""+0+"\",\"data\":[{\"mac\":\"\",\"macnums\":\""+0+"\",\"record\":\"\"}]}";	
		}
		res=res.replace(",,",",");		
		return res;
	}
	
	
	//将字符串中多次出现的数据进行归类合并
	//输入字符串格式为：{"mac":"aa","macnums":"2","record":"xxx1"}={"mac":"aa","macnums":"1","record":"xxx2"}={"mac":"aa","macnums":"1","record":"xxx3"}
	//输出字符串格式为：{"mac":"aa","macnums":"4","record":"xxx1;xxx2;xxx3"}
	public static String combine_data_str(String judge_result_str){
		String res="";
		HashMap<String,String> judge_result_map = new HashMap<String, String>();  
		//首先根据Mac合并macnums,record,放入map中
		if(judge_result_str.contains("=")){
			String[] judge_result_arr=judge_result_str.split("=");
			for(int i=0;i<judge_result_arr.length;i++){
				String single_judg_result=judge_result_arr[i];
				String[] single_judg_result_arr=single_judg_result.split("\",\"");
				String mac=single_judg_result_arr[0].split(":")[1].replace("\"","").replace("{","");
				String macnums=single_judg_result_arr[1].split(":")[1].replace("\"","");
				String record=single_judg_result_arr[2].split(":")[1].replace("\"","").replace("}","");
				if(judge_result_map.containsKey(mac)){
					String macnums_and_record=judge_result_map.get(mac);
					String mac_num=macnums_and_record.split("=")[0];
					String re_cord=macnums_and_record.split("=")[1];
					int nums=0;
					if(mac_num.length()>0&&TimeDate.isnum(mac_num)) {nums=Integer.parseInt(mac_num)+Integer.parseInt(macnums);}					
					judge_result_map.put(mac,nums+"="+re_cord+";"+record);
				}else{
					judge_result_map.put(mac,macnums+"="+record);
				}
			}
		}
		//然后遍历map,以字符串形式输出
		for(Map.Entry<String,String> entry: judge_result_map.entrySet()){
			String macnums_and_record=entry.getValue();
			macnums_and_record="\"macnums\":\""+macnums_and_record.replace("=","\",\"record\":\"")+"\"";			
			res+="{\"mac\":\""+entry.getKey()+"\","+macnums_and_record+"}=";
		}
		if(res.contains("}=")) res=res.substring(0,res.length()-1);
		return res;
	}
	
	
	//根据taskid求taskname所属标签
	public String getLable_bytaskid(int taskid,mysqlObject obj){
		obj.clearObject();
		//String lable="";
		String sql="select lable from taskname where taskid="+taskid+limitstr;
		//Print.println("selec_label_bytaskid="+sql);		
		List<mysqlRow> rows=obj.ExeSqlQuery(sql);
		String lablestr="";
		if(rows.size()>0){
			String result=obj.toJosn(0, totals, 0);
			result=result.split(",\"rows\"")[1];
			result=result.substring(2,result.length()-2);
			lablestr=result.replace("\"lable\":","").replace("\"","").replace("{","").replace("}","");
		}		
		return lablestr;
	 }
	
	
	//清空ENDRESULT
	public void clear_EndResult(){
		ENDRESULT=null;		
	}
	
	//参数正确性判断,
	public Boolean init_paras(){
		if(mac==null||mac.isEmpty())return false;	
		//if(TimeDate.isnum(taskid)) TASKID=Integer.parseInt(taskid);
		return true;
	}
	
	//生成limitstr
	public void init_limitstr(){
		limitstr="";		
		Map<String, String> judge_map=new HashMap<String,String>();
		//judge_map=ConnectToDatabase.getJudge_paras(isLocal);
		if(judge_map.get("highrisk.altertime")!=null) highrisk_altertime=(String) judge_map.get("highrisk.altertime");
		if(judge_map.get("fpgrowth.minConfidence")!=null) fpgrowth_minConfidence=(String) judge_map.get("fpgrowth.minConfidence");
		if(judge_map.get("permanent.limitrate")!=null) permanent_limitrate =(String) judge_map.get("permanent.limitrate");
		if(judge_map.get("workperson.work_time")!=null) work_time=(String) judge_map.get("workperson.work_time");
				
		long begno=0;
		if(TimeDate.isnum(page)) pageno=Integer.parseInt(page);
		if(TimeDate.isnum(limit)) pagesize=Integer.parseInt(limit);
				
		limitstr ="  limit "+begno+","+pagesize;			
	}
}
