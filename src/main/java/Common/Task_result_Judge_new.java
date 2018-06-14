package Common;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

public class Task_result_Judge_new {
	 static String[] database_paras=new String[4];
	 static  Map<String,String> mac_result_map=new HashMap<String, String>();
	 static Map<String,String> mac_JudgeResult_map=new HashMap<String, String>();
	 static String test_str="";

	 
	/**
	 * 查询指定taskid下面所有Mac的研判结果,需要传入pagesize,mysqlObject
	 * 把每一个taskid分开，不同的taskid下面可能包含相同的mac，所以如果多次调用这个方法，每次传入不同的taskid，并将每次结果拼接的话，最后可能产生重复的Mac
	 */
	public static String getAll_JudgeData(int taskid,int pagesize,mysqlObject obj,String limitstr,int totals){
		String alljudge_str="";		
		Print.println("alljudge_str="+alljudge_str);		
		return alljudge_str;
	}
		
	/**
	 * 请求参数中Mac不为空的情况下，根据单个taskid，单个mac获取研判结果,单个taskid，（单个Mac也可能对应多条结果，非常规的时候会出现！！），
	 * 需要传入pagesize,mysqlObject
	 */
	public static String getJudgeResult_by_taskid_mac(int taskid,String mac,int pagesize,mysqlObject obj,String limitstr,int totals){
		obj.clearObject();
		String judge="";
		//String selec_result_by_takid_mac="select result from  taskresult where taskid="+taskid +" and name=\'"+mac+"\'"+limitstr; 
		//Print.println("selec_result_by_takid_mac:"+selec_result_by_takid_mac);	
		//List<mysqlRow> rows=obj.ExeSqlQuery(selec_result_by_takid_mac);
		if(pagesize>totals) pagesize=totals;
		String result=obj.toJosn(0,pagesize, 0);
		result=result.split(",\"rows\"")[1];
		result=result.substring(2,result.length()-2);
		//Print.println("result:"+result);
		String[] resultarr=result.split("\"result\"");
		Map<String,String> result_map_by_taskid_mac=new HashMap<String, String>();		
		for(int i=0;i<resultarr.length;i++){
			resultarr[i]=resultarr[i].replace(":\"","").replace("},{","").replace("{","" ).replace("}","" ).replace("\"","");
			if(resultarr[i].length()>0){
				result_map_by_taskid_mac.put(resultarr[i],"");
			}
		}
		for(Map.Entry<String,String> entry:result_map_by_taskid_mac.entrySet()){
			//String each_result_str=getChineseWords_by_resultstr(entry.getKey());
			String each_result_str=entry.getKey();
			if(each_result_str.length()>0){
				judge+=each_result_str+";";
			}
				
		}
		String resultstr="";
		if(judge.length()>1){
			judge=judge.substring(0,judge.length()-1);
			resultstr=judge;
		}	
		//Print.println("resultstr:"+resultstr);
		String judge_result="";
		if(resultstr.contains(";")){
			String[] result_arr=resultstr.split(";");			
			for(int i=0;i<result_arr.length;i++){
				judge_result+=Task_result_Judge_new.trans_result(result_arr[i])+";";
			}
			judge_result=judge_result.substring(0,judge_result.length()-1);
			Print.println(judge_result);	
		}else{
			judge_result=Task_result_Judge_new.trans_result(resultstr);
		}			
		return judge_result;
	}

	/**
	 * 根据lable,day获取去重后的mac及相关依据(day写在了limitstr里面，不使用limit 2,3这样的分页),
	 * 格式是{"mac1":"","record1":""}={"mac2":"","record2":""}={"mac3":"","record3":""}
	 * 用等号=分开是为了方便后面的取第m到第n条数据，如果想全部取在获取值之后就把等号=用逗号代替
	 */
	public static String  get_Mac_JudgeResult_by_lable_day(String lable,mysqlObject obj,String limitstr){
		obj.clearObject();
		String result="";
		Set<String> macset=getMac_by_lable(lable,obj,limitstr);
		for(String mac:macset){
			String sql="select result from taskresult where name=\'"+mac+"\'"+limitstr;
			int totals=10000;
			obj.clearObject();
			List<mysqlRow> rows=obj.ExeSqlQuery(sql);		
			if(rows.size()>0){		
				String res=obj.toJosn(0, totals, 0);
				res=res.split(",\"rows\"")[1];
				res=res.substring(2,res.length()-2);
				Print.println(res);
				String resultstr=res.replace("\"result\":","").replace("\"","").replace("},{","=").replace("{","").replace("}","");
				String[] res_arr=resultstr.split("=");
				String judge_str="";
				for(int i=0;i<res_arr.length;i++){
					if((res_arr[i].contains(":"))&&(res_arr[i].contains(","))&&(res_arr[i].contains("~"))){
						judge_str+=trans_result(res_arr[i])+";";
					}
				}
				if(judge_str.length()>0) judge_str=judge_str.substring(0,judge_str.length()-1);				
				result+="{\"mac\":\""+mac+"\",\"record\":\""+judge_str+"\"}=";
				//Print.println("result:"+result);
			}
		}
        if(result.length()>0) result=result.substring(0,result.length()-1);
		return result;
	} 
	
	/**
	 * 根据taskid查询Mac,返回字符串（由单个Mac拼接而成，逗号分隔，数组形式）,需要传入pagesize,mysqlObject
	 * 这里的limistr可以传进去where day=20170827等，但不允许不传入分页
	 */	
	public static String getMac_byTaskid(int taskid,String tablename,String mac_column_name,int pagesize,mysqlObject obj,String limitstr,int totals){
		String macstr="";
		String sql="";
		String result="";
		obj.clearObject();
		sql="select  distinct("+mac_column_name+") from "+tablename+" where  taskid="+taskid+limitstr;			
		//Print.println("selec_mac_bytaskid:"+selec_mac_bytaskid);	
		List<mysqlRow> rows=obj.ExeSqlQuery(sql);
		if(pagesize>totals) pagesize=totals;
		if(rows.size()>0) {
			result=obj.toJosn(0,pagesize, 0);
			result=result.split(",\"rows\"")[1];
			result=result.substring(2,result.length()-2);
			macstr=result.replace("\""+mac_column_name+"\":","").replace("\"","").replace("{","").replace("}","");
		} 	
		//Print.println("macstr:"+macstr);
		if(macstr.endsWith(",")) macstr=macstr.substring(0,macstr.length()-1);
		String macstr_inchars="";
		if(macstr.contains(",")&&tablename.contains("danger")){
             macstr_inchars="\'"+macstr.replace(",","\','")+"\'";
             obj.clearObject();
             String mac_desc_by_nums_sql="select "+mac_column_name+" from (select "+mac_column_name+",sum(nums) as nums from "+tablename+" group by "+mac_column_name+" order by nums desc) as a where "+mac_column_name+" in ("+macstr_inchars+")";
			 List<mysqlRow> desc_mac_rows=obj.ExeSqlQuery(mac_desc_by_nums_sql);
			 if(pagesize>totals) pagesize=totals;
			 if(desc_mac_rows.size()>0) {
				result=obj.toJosn(0,pagesize, 0);
				result=result.split(",\"rows\"")[1];
				result=result.substring(2,result.length()-2);
				macstr=result.replace("\""+mac_column_name+"\":","").replace("\"","").replace("{","").replace("}","");
			 } 	
		}
		if(macstr.endsWith(",")) macstr=macstr.substring(0,macstr.length()-1);
		return  macstr;
	}

	
	/**
	 * 根据taskid查询Mac,返回mac字符串（逗号分隔，数组形式）,
	 * 如果是伴随的话返回的是Mac组,多个Mac组组成一个字符串，需要传入pagesize,mysqlObject
	 * 这里的limistr可以传进去where day=20170827等，但不允许不传入分页
	 */	
	public static String getMac_byTaskid_fellow_return_macgroup(int taskid,String tablename,String mac_column_name,int pagesize,mysqlObject obj,String limitstr,int totals){
		obj.clearObject();
		String macstr="";
		String sql="";
		String result="";
		if(!tablename.contains("fellow")){
			sql="select  distinct("+mac_column_name+") from "+tablename+" where  taskid="+taskid+limitstr;			
			//Print.println("selec_mac_bytaskid:"+selec_mac_bytaskid);	
			List<mysqlRow> rows=obj.ExeSqlQuery(sql);
			if(pagesize>totals) pagesize=totals;
			if(rows.size()>0) {
				result=obj.toJosn(0,pagesize, 0);
				result=result.split(",\"rows\"")[1];
				result=result.substring(2,result.length()-2);
				macstr=result.replace("\""+mac_column_name+"\":","").replace("\"","").replace("{","").replace("}","");
			} 	
		}else{
			obj.clearObject();
			//伴随类型的时候，先查询去重后的fellowid，然后根据fellowid查询Mac
			String fellowid_str="";
			String selec_fellowid="select distinct(fellowid) from "+tablename;
			List<mysqlRow> rows_fellowid=obj.ExeSqlQuery(selec_fellowid);
			if(pagesize>totals) pagesize=totals;
			if(rows_fellowid.size()>0) {
				fellowid_str=obj.toJosn(0,pagesize, 0);
				fellowid_str=fellowid_str.split(",\"rows\"")[1];
				fellowid_str=fellowid_str.substring(2,fellowid_str.length()-2);
				fellowid_str=fellowid_str.replace("\""+mac_column_name+"\":","").replace("\"","").replace("fellowid:","").replace("{","").replace("}","");
			} 	
			if(fellowid_str.endsWith(",")) fellowid_str=fellowid_str.substring(0,fellowid_str.length()-1);
			if(fellowid_str.contains(",")) {
				String[] fellowid_list=fellowid_str.split(",");
				obj.clearObject();
				for(int i=0;i<fellowid_list.length;i++){
					String selec_mac="select "+mac_column_name+" from "+tablename+" where fellowid =\'"+fellowid_list[i]+"\'";
					List<mysqlRow> rows_mac_byfellowid=obj.ExeSqlQuery(selec_mac);
					if(pagesize>totals) pagesize=totals;
					if(rows_mac_byfellowid.size()>0) {
						String str="";
						str=obj.toJosn(0,pagesize, 0);
						str=str.split(",\"rows\"")[1];
						str=str.substring(2,str.length()-2);
						if(str.endsWith(",")) str=str.substring(0,str.length()-1);
						str=str.replace("\""+mac_column_name+"\":","").replace("\"","").replace("{","").replace("}","");
						macstr+="\""+str+"\",";
					} 	
				}		
			}else if(fellowid_str.length()>0){
				String selec_mac="select "+mac_column_name+" from "+tablename+" where fellowid =\'"+fellowid_str+"\'";
				List<mysqlRow> rows_mac_byfellowid=obj.ExeSqlQuery(selec_mac);
				if(pagesize>totals) pagesize=totals;
				if(rows_mac_byfellowid.size()>0) {
					String str="";
					str=obj.toJosn(0,pagesize, 0);
					str=str.split(",\"rows\"")[1];
					str=str.substring(2,str.length()-2);
					if(str.endsWith(",")) str=str.substring(0,str.length()-1);
					str=str.replace("\""+mac_column_name+"\":","").replace("\"","").replace("{","").replace("}","");
					macstr+="\""+str+"\",";
				} 	
			}
				
		}
		//Print.println("macstr:"+macstr);
		if(macstr.endsWith(",")) macstr=macstr.substring(0,macstr.length()-1);
		return  macstr;
	}

	/**
	 * 根据lable查询taskreuslt里面去重后Mac数量
	 */
	public static int getMacNum_by_lable(String lable,mysqlObject obj,String limitstr){
		obj.clearObject();
		getAll_Macnum_in_taskresult(obj);
		obj.clearObject();
		int num=0;
		Map<String,Integer> macmap_by_lable=new HashMap<String, Integer>();	
		num=macmap_by_lable.keySet().size();		
		return num;
	}

	
	/**
	 * 根据lable查询taskresult里面去重后的mac，返回Set<String>形式
	 */
	public static Set<String> getMac_by_lable(String lable,mysqlObject obj,String limitstr){
		obj.clearObject();
		getAll_Macnum_in_taskresult(obj);
		obj.clearObject();
		//int num=0;
		Map<String,Integer> macmap_by_lable=new HashMap<String, Integer>();	
		Set<String> macset=macmap_by_lable.keySet();		
		return macset;
	}
	
	/**
	 * 求taskresult里面去重后的Mac数量
	 */
	public static int getAll_Macnum_in_taskresult(mysqlObject obj){
		int num=0;
		obj.clearObject();	
		String sql1="select count(distinct(name)) as count from taskresult";
		List<mysqlRow> rows1=obj.ExeSqlQuery(sql1);
		String result1=obj.toJosn(0,10,0);	
		if(rows1.size()>0){
			String str=result1.split("count")[1];
			num= Integer.parseInt(str.substring(3,str.length()-4));
		}
		return num;
	}
	
	/**
	 * 根据Mac求taskresult里面该result记录数
	 */
	public static int countsum_bymac(mysqlObject obj,String mac){
		obj.clearObject();
		int totals=0;
		String sql2="select count(*) as count from taskresult where name=\'"+mac+"\'";					
		List<mysqlRow> rows2=obj.ExeSqlQuery(sql2);
		String result2=obj.toJosn(0,10,10);	
		if(rows2.size()>0){
			String str=result2.split("count")[1];
			totals= Integer.parseInt(str.substring(3,str.length()-4));
		}
		return totals;
	 }
		
	/**
	 * 输入FC-DB-B3-58-EC-00,20170817~20170824,total:3:31010721000003:2,100,66这样的字符串进行汉字研判解读
	 */
    public static String trans_result(String result_str){
      if(result_str.contains("total")){//高危解读
    	     if(result_str.length()>0&&result_str.contains(":")&&result_str.contains(",")){
		   String[] result_arr=result_str.split(",");
		   if(result_arr.length==5){
			   String[] times_str=result_arr[2].split(":");
			   if(times_str.length==4){
				   result_str=result_arr[0]+" 在"+result_arr[1]+"期间,敏感时段累计上网次数 "+times_str[1]+"次,其中在"+times_str[2]+"场所上网 "+times_str[3]+"次, 敏感时段上网次数占总次数比例"+result_arr[3]+"%,场所变动次数占所有上网场所次数比例 "+result_arr[4]+"%";
			   }
		    }
	      }
      }	
      if(result_str.contains("daysum")){//常驻解读
    	  if(result_str.length()>0&&result_str.contains(":")&&result_str.contains(",")){
			   String[] result_arr=result_str.split(",");
			   if(result_arr.length==3){
				   String[] day_str=result_arr[2].split(":");
				   if(day_str.length==4){
					   result_str=result_arr[0]+" 在时间范围("+result_arr[1]+") "+day_str[3]+"天内有"+day_str[1]+"天在指定场所内有上网活动";
				   }
			    }
	      }    	  
      }
	   return result_str;
   }
  	
  	/**
  	 * 根据taskid获取该taskid对应的研判表，以及mac对应的列名
  	 */
  	public static Map<String,String>  judge_ownedtable_bytaskid(mysqlObject obj,String taskid) {
		obj.clearObject();
  		int tasktype=0;
  		Map<String, String> ownedtable_map=new HashMap<String,String>();
		String selec_tasktype_bytaskid="select tasktype from taskname where taskid="+taskid;
		List<mysqlRow> rows=obj.ExeSqlQuery(selec_tasktype_bytaskid);
		if(rows.size()>0) {
			String result="";
			result=obj.toJosn(0,10, 0);
			result=result.split(",\"rows\"")[1];
			result=result.substring(2,result.length()-2);
			result=result.replace("\"tasktype\":","").replace("\"","").replace("{","").replace("}","");
			if(TimeDate.isnum(result)) tasktype=Integer.parseInt(result);
		} 	
		if(tasktype>0){
			ownedtable_map.clear();
			switch(String.valueOf(tasktype)){			
			case "16":
				ownedtable_map.put("fellow;fellow_ad", "name");
				break;
			case "12":
				ownedtable_map.put("permanent_ad;permanent", "mac");
				break;
			case "13":
				ownedtable_map.put("danger_result", "mac");
				break;	
			case "15":
				ownedtable_map.put("vip_svc;vip_trace", "mac");
				break;	
			case "17":
				ownedtable_map.put("work_person_ad", "mac");
				break;	
			default:
				break;	
			}		
		}	
		return ownedtable_map;
	}

  	/**
	 * 输入taskid,Mac,返回day(具体某日),svc,频次总和,需要传入pagesize,mysqlObject
	 * 这里的limistr可以传进去where day=20170827等，但不允许不传入分页
	 */		
	public static String get_daysvcpinci_by_taskidmac(int taskid, String mac,String tablename, String mac_columnname, int pageno,int pagesize,mysqlObject obj,int totals,String time_rang,boolean islocal) throws ParseException {
		obj.clearObject();
		String res="";
		List<Long> listday=new ArrayList<Long>();
		int begday=0;
		int endday=0;
		//int begno=pageno*pagesize;
		//int endno=begno+pagesize;
		if(TimeDate.isNormaltime_rang(time_rang)) {
			int[] time_arr=TimeDate.getStartday_Endday_by_timerang(time_rang);
			begday=time_arr[0];
			endday=time_arr[1];
			listday=TimeDate.getBetweenDates(time_rang);
		}
		if(tablename.contains("danger")){
			String endresult="";		
			if(listday.size()>0){
				for(int i=0;i<listday.size();i++){
					String svcstr="";
					List<String> listsvc=new ArrayList<String>();
					String day=String.valueOf(listday.get(i));
					String svc_sql="select  svc from "+tablename+" where  taskid="+taskid+" and "+mac_columnname+"=\'"+mac+"\'";
					svc_sql+=" and day="+day;
					svc_sql+=" and svc <> \"\"";
					List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(svc_sql);
					if(selec_reusltnums_sql_rows.size()>0){
						String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
						result=result.substring(2,result.length()-2).replace("\"svc\":", "").replace("{", "").replace("}", "").replace("\"", "");
						if(result.contains(",")){
							String[] arr=result.split(",");
							for(int j=0;j<arr.length;j++){
								if(!listsvc.contains(arr[j])) listsvc.add(arr[j]);								
							} 							
						}
						else if(result.length()>0&&result.length()>0) listsvc.add(result);	
						for(int svc=0;svc<listsvc.size();svc++) svcstr+=listsvc.get(svc)+",";
						if(svcstr.endsWith(",")) svcstr=svcstr.substring(0,svcstr.length()-1);
						String foreday=TimeDate.getSpecifiedDayBefore(day);
						List<String> danger_time_list=GetValueFrom_task_analyzer_xml.getAlterTime_from_task_xml(islocal);
						String firtime="23:00";
						String entime="05:00";
						if(danger_time_list.size()==2){
							firtime=danger_time_list.get(0).toString();
							entime=danger_time_list.get(1).toString();
						}
						endresult+="{\"day\":\""+TimeDate.formatNumsDatePattern(foreday, "-")+" "+firtime+"~"+TimeDate.formatNumsDatePattern(day, "-")+" "+entime+"\",\"mac\":\""+mac+"\",\"svc\":\""+svcstr+"\"},";
					}
				 }	
			}
			else if(listday.size()==0){
				//String svcstr="";				
				//String yesterday=TimeDate.getYesterday();				
				String svc_sql="select  svc,day from "+tablename+" where  taskid="+taskid+" and "+mac_columnname+"=\'"+mac+"\'";
				//svc_sql+=" and day="+day;
				svc_sql+=" and svc <> \"\"  order by day desc ";
				List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(svc_sql);
				if(selec_reusltnums_sql_rows.size()>0){
					String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
					result=result.substring(2,result.length()-2).replace("{", "").replace("}", "").replace("\"", "").replace(",svc", ";svc");
					 Map<String, String> day_svc_map = new TreeMap<String, String>(
				                new Comparator<String>() {
				                    public int compare(String obj1, String obj2) {
				                        // 降序排序
				                        return obj2.compareTo(obj1);
				                    }
				     });
					if(result.contains(";")){
						String arr[]=result.split(";");
						for(int a=0;a<arr.length;a++){
							String str=arr[a].replace("svc:","").replace(",day:",";");
							String svc=str.split(";")[0];
							String day=str.split(";")[1];
							if(day_svc_map.containsKey(day)){
								String value=day_svc_map.get(day).toString();
								if(!value.contains(svc)) value=value+","+svc;
								day_svc_map.put(day, value);
							}
							if(!day_svc_map.containsKey(day)) day_svc_map.put(day, svc);
						}
					}
					if(!result.contains(";")){
						String str=result.replace(",day",";day").replace("svc:","").replace("day:","");
						day_svc_map.put(str.split(";")[1], str.split(";")[0]);
					}
					List<String> danger_time_list=GetValueFrom_task_analyzer_xml.getAlterTime_from_task_xml(islocal);
					String firtime="23:00";
					String entime="05:00";
					if(danger_time_list.size()==2){
						firtime=danger_time_list.get(0).toString();
						entime=danger_time_list.get(1).toString();
					}
					for (Entry<String, String> entry: day_svc_map.entrySet()) {
						 String day = entry.getKey();
						 String svc = entry.getValue();
						 String forday=TimeDate.getSpecifiedDayNDaysBefore(day,1);
						 endresult+="{\"day\":\""+TimeDate.formatNumsDatePattern(forday, "-")+" "+firtime+"~"+TimeDate.formatNumsDatePattern(day, "-")+" "+entime+"\",\"mac\":\""+mac+"\",\"svc\":\""+svc+"\"},";
					}						
				}
			}
			if(endresult.contains(",")) endresult=endresult.substring(0,endresult.length()-1);
			return endresult;
		} 
		if(tablename.contains("permanent")){			
			/**
			 * 这种方式是统计指定Mac下前15条记录
			 * */
			String endresult="";
			obj.clearObject();	
			if(listday.size()==0){
				//for(int j=0;j<pagesize;j++){
				//String svcstr="";				
				//String yesterday=TimeDate.getYesterday();				
				String svc_sql="select  svc,day from "+tablename+" where  taskid="+taskid+" and "+mac_columnname+"=\'"+mac+"\'";
				//svc_sql+=" and day="+day;
				svc_sql+=" and svc <> \"\"  order by day desc ";
				List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(svc_sql);
				if(selec_reusltnums_sql_rows.size()>0){
					String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
					result=result.substring(2,result.length()-2).replace("{", "").replace("}", "").replace("\"", "").replace(",svc", ";svc");
					 Map<String, String> day_svc_map = new TreeMap<String, String>(
				                new Comparator<String>() {
				                    public int compare(String obj1, String obj2) {
				                        // 降序排序
				                        return obj2.compareTo(obj1);
				                    }
				     });
					if(result.contains(";")){
						String arr[]=result.split(";");
						for(int a=0;a<arr.length;a++){
							String str=arr[a].replace("svc:","").replace(",day:",";");
							String svc=str.split(";")[0];
							String day=str.split(";")[1];
							if(day_svc_map.containsKey(day)){
								String value=day_svc_map.get(day).toString();
								if(!value.contains(svc)) value=value+","+svc;
								day_svc_map.put(day, value);
							}
							if(!day_svc_map.containsKey(day)) day_svc_map.put(day, svc);
						}
					}
					if(!result.contains(";")){
						String str=result.replace("svc:","").replace("day:","");
						day_svc_map.put(str.split(",")[1], str.split(",")[0]);
					}
					for (Entry<String, String> entry: day_svc_map.entrySet()) {
						 String day = entry.getKey();
						 String svc = entry.getValue();
						 endresult+="{\"day\":\""+TimeDate.formatNumsDatePattern(day,"-")+"\",\"mac\":\""+mac+"\",\"svc\":\""+svc+"\"},";
					}						
				}
			  //}
			}
			else if(listday.size()>0){				
				for(int j=0;j<listday.size();j++){
					String svcstr="";
					List<String> listsvc=new ArrayList<String>();
					String day=String.valueOf(listday.get(j));
					String svc_sql="select  svc from "+tablename+" where  taskid="+taskid+" and "+mac_columnname+"=\'"+mac+"\'";
					svc_sql+=" and day="+day;
					svc_sql+=" and svc <> \"\"";
					List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(svc_sql);
					if(selec_reusltnums_sql_rows.size()>0){
						String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
						result=result.substring(2,result.length()-2).replace("\"svc\":", "").replace("{", "").replace("}", "").replace("\"", "");
						if(result.contains(",")){
							String[] arr=result.split(",");
							for(int m=0;m<arr.length;m++){
								if(!listsvc.contains(arr[m])) listsvc.add(arr[m]);								
							} 							
						}
						else if(result.length()>0&&result.length()>0) listsvc.add(result);	
						for(int svc=0;svc<listsvc.size();svc++) svcstr+=listsvc.get(svc)+",";
						if(svcstr.endsWith(",")) svcstr=svcstr.substring(0,svcstr.length()-1);
						endresult+="{\"day\":\""+TimeDate.formatNumsDatePattern(day, "-")+"\",\"mac\":\""+mac+"\",\"svc\":\""+svcstr+"\"},";
					}
					
				}
			}			
			if(endresult.contains(",")) endresult=endresult.substring(0,endresult.length()-1);
			return endresult;
		}	
		
		if(tablename.contains("work")){			
			/**
			 * 这种方式是统计指定Mac下前15条记录
			 * */
			String endresult="";
			obj.clearObject();	
			Print.println(listday.size());
			if(listday.size()==0){
				//for(int j=0;j<pagesize;j++){
				//String svcstr="";
				//String day=TimeDate.getSpecifiedDayNDaysBefore(yesterday,j);
				String svc_sql="select  svc,left(day,8)  as day  from work_person where  taskid="+taskid+" and "+mac_columnname+"=\'"+mac+"\'";
				//svc_sql+=" and day="+day;
				svc_sql+=" and svc <> \"\" order by day desc ";
				List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(svc_sql);
				if(selec_reusltnums_sql_rows.size()>0){
					String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
					result=result.substring(2,result.length()-2).replace("{", "").replace("}", "").replace("\"", "").replace(",svc", ";svc");
					//根据map中的key降序排序
					Map<String, String> day_svc_map = new TreeMap<String, String>(
				                new Comparator<String>() {
				                    public int compare(String obj1, String obj2) {
				                        // 降序排序
				                        return obj2.compareTo(obj1);
				                    }
				     });
					if(result.contains(";")){
						String arr[]=result.split(";");
						for(int a=0;a<arr.length;a++){
							String str=arr[a].replace("svc:","").replace(",day:",";");
							String svc=str.split(";")[0];
							String day=str.split(";")[1];
							if(day_svc_map.containsKey(day)){
								String value=day_svc_map.get(day).toString();
								if(!value.contains(svc)) value=value+","+svc;
								day_svc_map.put(day, value);
							}
							if(!day_svc_map.containsKey(day)) day_svc_map.put(day, svc);
						}
					}
					if(!result.contains(";")){
						String str=result.replace("svc:","").replace("day:","");
						day_svc_map.put(str.split(",")[1], str.split(",")[0]);
					}
					for (Entry<String, String> entry: day_svc_map.entrySet()) {
						 String day = entry.getKey();
						 String svc = entry.getValue().toString().replace(" ",",").replace(":","");
						 endresult+="{\"day\":\""+TimeDate.formatNumsDatePattern(day,"-")+"\",\"mac\":\""+mac+"\",\"svc\":\""+svc+"\"},";
					}						
				}		
			  //}
			}
			else if(listday.size()>0){				
				for(int j=0;j<listday.size();j++){
					String svcstr="";
					List<String> listsvc=new ArrayList<String>();
					String day=String.valueOf(listday.get(j));
					String svc_sql="select  svc from work_person where  taskid="+taskid+" and "+mac_columnname+"=\'"+mac+"\'";
					svc_sql+=" and (day="+day+"08 or day="+day+"19)";
					svc_sql+=" and svc <> \"\"";
					List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(svc_sql);
					if(selec_reusltnums_sql_rows.size()>0){
						String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
						result=result.substring(2,result.length()-2).replace("\"svc\":", "").replace("{", "").replace("}", "").replace("\"", "");
						if(result.contains(",")){
							String[] arr=result.split(",");
							for(int m=0;m<arr.length;m++){
								if(!listsvc.contains(arr[m])) listsvc.add(arr[m]);								
							} 							
						}
						else if(result.length()>0&&result.length()>0) listsvc.add(result);	
						for(int svc=0;svc<listsvc.size();svc++) svcstr+=listsvc.get(svc).toString().replace(" ",",").replace(":","")+",";
						if(svcstr.endsWith(",")) svcstr=svcstr.substring(0,svcstr.length()-1);
						endresult+="{\"day\":\""+TimeDate.formatNumsDatePattern(day, "-")+"\",\"mac\":\""+mac+"\",\"svc\":\""+svcstr+"\"},";
					}
					
				}
			}	
			endresult=endresult.replace(":\":",":\"");
			if(endresult.contains(",")) endresult=endresult.substring(0,endresult.length()-1);
			return endresult;
		} 
		
		/*返回的字段包括（对应多条记录，所有的伴随MAC，以伴随记录越多则优先级越高，此时不按天排序）
		*	a)	时段：2017-12-10  ~ 2017-12-11
		*	b)	主MAC：
		*	c)	伴随的MAC(单个)
		*	d)	伴随地点：该时间段的伴随MAC的伴随地点列表
		*	         主Mac是指定的，一个主Mac有多少个伴随Mac就有多少组，伴随地点可能有多个
		*/
		if(tablename.contains("fellow")){
			obj.clearObject();
			if(listday.size()==0){
					//String yesterday=TimeDate.getYesterday();
					//String macstr="";
					//int svc_nums=0;
					String fellowid_str="";
					List<String> fellowid_list=new ArrayList<String>();
					List<String> listmac=new ArrayList<String>();
					obj.clearObject();
				    String selec_fellowid_sql="select  distinct(fellowid) from fellow  where  taskid="+taskid+" and name=\'"+mac+"\' order by day desc";
			        List<mysqlRow> selec_fellowid_sql_rows=obj.ExeSqlQuery(selec_fellowid_sql);
					if(selec_fellowid_sql_rows.size()>0) {
						String result=obj.toJosn(0,10, 0);
						result=result.split(",\"rows\"")[1];
						result=result.substring(2,result.length()-2);
						result=result.replace("\"","").replace("{","").replace("}","").replace("fellowid:","");
						fellowid_str=result;
					} 	
					if(fellowid_str.contains(",")){
						String[] fellowid_arr=fellowid_str.split(",");
						for(int n=0;n<fellowid_arr.length;n++){
							fellowid_list.add(fellowid_arr[n]);
						}
					}else if(fellowid_str.length()>0&&(!fellowid_str.contains(","))) fellowid_list.add(fellowid_str);
					//一个fellowid对应一个day,但一个fellowid可能对应多个Mac
					List<String> temp_list=new ArrayList<String>();
					Map<String, Integer> temp_mac_count_map=new HashMap<String, Integer>();
					List<String> Normal_list=new ArrayList<String>();
					for(int fellowid=0;fellowid<fellowid_list.size();fellowid++){						
						//Map mac_fellows_count_map=new HashMap();
						String day="";
						obj.clearObject();
						String day_sql="select distinct(day) from fellow where fellowid=\""+fellowid_list.get(fellowid).toString()+"\"";
						List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(day_sql);
						if(selec_reusltnums_sql_rows.size()>0){							
							String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
							result=result.substring(2,result.length()-2).replace("\"day\":", "").replace("{", "").replace("}", "").replace("\"", "");
							if(result.length()>0) day=result;
							obj.clearObject();
							//String mac_sql="select distinct(name) from fellow where fellowid=\""+fellowid_list.get(fellowid).toString()+"\"";
							//List<mysqlRow> mac_sql_rows = obj.ExeSqlQuery(mac_sql);
							if(selec_reusltnums_sql_rows.size()>0){
								String result1=obj.toJosn(0,5,10).split(",\"rows\"")[1];
								result1=result1.substring(2,result1.length()-2).replace("\"name\":", "").replace("{", "").replace("}", "").replace("\"", "");
								if(result1.contains(",")){
									String[] arr=result1.split(",");
									for(int m=0;m<arr.length;m++){
										if(!arr[m].equals(mac)){
											if(!listmac.contains(arr[m])) listmac.add(arr[m]);	
											if(!temp_mac_count_map.containsKey(arr[m])) temp_mac_count_map.put(arr[m], 1);
											else if(temp_mac_count_map.containsKey(arr[m]))  temp_mac_count_map.put(arr[m], Integer.parseInt(temp_mac_count_map.get(arr[m]).toString())+1);
										}
									} 							
								}
								else if(result1.length()>0&&result1.length()>0){
									if(!result1.equals(mac)){
										if(!temp_mac_count_map.containsKey(result1)) temp_mac_count_map.put(result1, 1);
										else if(temp_mac_count_map.containsKey(result1))  temp_mac_count_map.put(result1, Integer.parseInt(temp_mac_count_map.get(result1).toString())+1);
										listmac.add(result1);
									}	
								} 				
							}
							//一个fellowid对应多个场所，这些场所是具有伴随关系的Mac都经过的场所
							obj.clearObject();
							List<String> listsvc=new ArrayList<String>();
							String svcstr="";
							String svc_sql="select distinct(svc) from fellow_ad where fellowid=\""+fellowid_list.get(fellowid).toString()+"\"";
							List<mysqlRow> selec_svc_rows = obj.ExeSqlQuery(svc_sql);
							if(selec_svc_rows.size()>0){
								String result1=obj.toJosn(0,5,10).split(",\"rows\"")[1];
								result1=result1.substring(2,result1.length()-2).replace("\"svc\":", "").replace("{", "").replace("}", "").replace("\"", "");
								if(result1.contains(",")){
									String[] arr=result1.split(",");
									for(int m=0;m<arr.length;m++){
										if(!listsvc.contains(arr[m])) listsvc.add(arr[m]);								
									} 							
								}
								else if(result1.length()>0&&result1.length()>0) listsvc.add(result1);						
							}
							for(int svc=0;svc<listsvc.size();svc++) svcstr+=listsvc.get(svc)+",";
							if(svcstr.endsWith(",")) svcstr=svcstr.substring(0,svcstr.length()-1);	
							for(int m=0;m<listmac.size();m++){
								String temp="{\"day\":\""+TimeDate.formatNumsDatePattern(day, "-")+"\",\"mac\":\""+mac+"\",\"bbmac\":\""+listmac.get(m)+"\",\"svc\":\""+svcstr+"\"}";
								if(!temp_list.contains(temp)) temp_list.add(temp);
							}
						}				
					}
					//对temp_mac_count_map里面的值降序排序
					List<Entry<String, Integer>> mac_count_list = new ArrayList<Entry<String, Integer>>(temp_mac_count_map.entrySet());  
				        Collections.sort(mac_count_list, new Comparator<Map.Entry<String, Integer>>() {  
				            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {  
				                return (o2.getValue() - o1.getValue());  
				            }  
				    });  
				    //将排序后的结果存放到   Normal_list中 
			        for(Entry<String, Integer> t:mac_count_list){  
			            String str_value=t.getKey().toString();
			            for(int str=0;str<temp_list.size();str++){
			            	String val=temp_list.get(str).toString();
							if(val.contains(str_value)) Normal_list.add(val);
						}
			        }  
			        //生成结果字符串
					for(int ii=0;ii<Normal_list.size();ii++)	res+=Normal_list.get(ii)+",";
			}
			if(listday.size()>0){
				//String yesterday=TimeDate.getYesterday();
				//String macstr="";
				//int svc_nums=0;
				String fellowid_str="";
				List<String> fellowid_list=new ArrayList<String>();
				List<String> listmac=new ArrayList<String>();
				obj.clearObject();
			    String selec_fellowid_sql="select  distinct(fellowid) from fellow  where  taskid="+taskid+" and name=\'"+mac+"\' and day>="+begday+" and day<"+endday+"  order by day desc";
		        List<mysqlRow> selec_fellowid_sql_rows=obj.ExeSqlQuery(selec_fellowid_sql);
				if(selec_fellowid_sql_rows.size()>0) {
					String result=obj.toJosn(0,10, 0);
					result=result.split(",\"rows\"")[1];
					result=result.substring(2,result.length()-2);
					result=result.replace("\"","").replace("{","").replace("}","").replace("fellowid:","");
					fellowid_str=result;
				} 	
				if(fellowid_str.contains(",")){
					String[] fellowid_arr=fellowid_str.split(",");
					for(int n=0;n<fellowid_arr.length;n++){
						fellowid_list.add(fellowid_arr[n]);
					}
				}else if(fellowid_str.length()>0&&(!fellowid_str.contains(","))) fellowid_list.add(fellowid_str);
				//一个fellowid对应一个day,但一个fellowid可能对应多个Mac
				List<String> temp_list=new ArrayList<String>();
				Map<String, Integer> temp_mac_count_map=new HashMap<String, Integer>();
				List<String> Normal_list=new ArrayList<String>();
				for(int fellowid=0;fellowid<fellowid_list.size();fellowid++){						
					//Map mac_fellows_count_map=new HashMap();
					String day="";
					obj.clearObject();
					String day_sql="select distinct(day) from fellow where fellowid=\""+fellowid_list.get(fellowid).toString()+"\"";
					List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(day_sql);
					if(selec_reusltnums_sql_rows.size()>0){							
						String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
						result=result.substring(2,result.length()-2).replace("\"day\":", "").replace("{", "").replace("}", "").replace("\"", "");
						if(result.length()>0) day=result;
						obj.clearObject();
						//String mac_sql="select distinct(name) from fellow where fellowid=\""+fellowid_list.get(fellowid).toString()+"\"";
						//List<mysqlRow> mac_sql_rows = obj.ExeSqlQuery(mac_sql);
						if(selec_reusltnums_sql_rows.size()>0){
							String result1=obj.toJosn(0,5,10).split(",\"rows\"")[1];
							result1=result1.substring(2,result1.length()-2).replace("\"name\":", "").replace("{", "").replace("}", "").replace("\"", "");
							if(result1.contains(",")){
								String[] arr=result1.split(",");
								for(int m=0;m<arr.length;m++){
									if(!arr[m].equals(mac)){
										if(!listmac.contains(arr[m])) listmac.add(arr[m]);	
										if(!temp_mac_count_map.containsKey(arr[m])) temp_mac_count_map.put(arr[m], 1);
										else if(temp_mac_count_map.containsKey(arr[m]))  temp_mac_count_map.put(arr[m], Integer.parseInt(temp_mac_count_map.get(arr[m]).toString())+1);
									}
								} 							
							}
							else if(result1.length()>0&&result1.length()>0){
								if(!result1.equals(mac)){
									if(!temp_mac_count_map.containsKey(result1)) temp_mac_count_map.put(result1, 1);
									else if(temp_mac_count_map.containsKey(result1))  temp_mac_count_map.put(result1, Integer.parseInt(temp_mac_count_map.get(result1).toString())+1);
									listmac.add(result1);
								}	
							} 				
						}
						//一个fellowid对应多个场所，这些场所是具有伴随关系的Mac都经过的场所
						obj.clearObject();
						List<String> listsvc=new ArrayList<String>();
						String svcstr="";
						String svc_sql="select distinct(svc) from fellow_ad where fellowid=\""+fellowid_list.get(fellowid).toString()+"\"";
						List<mysqlRow> selec_svc_rows = obj.ExeSqlQuery(svc_sql);
						if(selec_svc_rows.size()>0){
							String result1=obj.toJosn(0,5,10).split(",\"rows\"")[1];
							result1=result1.substring(2,result1.length()-2).replace("\"svc\":", "").replace("{", "").replace("}", "").replace("\"", "");
							if(result1.contains(",")){
								String[] arr=result1.split(",");
								for(int m=0;m<arr.length;m++){
									if(!listsvc.contains(arr[m])) listsvc.add(arr[m]);								
								} 							
							}
							else if(result1.length()>0&&result1.length()>0) listsvc.add(result1);						
						}
						for(int svc=0;svc<listsvc.size();svc++) svcstr+=listsvc.get(svc)+",";
						if(svcstr.endsWith(",")) svcstr=svcstr.substring(0,svcstr.length()-1);	
						for(int m=0;m<listmac.size();m++){
							String temp="{\"day\":\""+TimeDate.formatNumsDatePattern(day, "-")+"\",\"mac\":\""+mac+"\",\"bbmac\":\""+listmac.get(m)+"\",\"svc\":\""+svcstr+"\"}";
							if(!temp_list.contains(temp)) temp_list.add(temp);
						}
					}				
				}
				//对temp_mac_count_map里面的值降序排序
				List<Entry<String, Integer>> mac_count_list = new ArrayList<Entry<String, Integer>>(temp_mac_count_map.entrySet());  
			        Collections.sort(mac_count_list, new Comparator<Map.Entry<String, Integer>>() {  
			            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {  
			                return (o2.getValue() - o1.getValue());  
			            }  
			    });  
			    //将排序后的结果存放到   Normal_list中 
		        for(Entry<String, Integer> t:mac_count_list){  
		            String str_value=t.getKey().toString();
		            for(int str=0;str<temp_list.size();str++){
		            	String val=temp_list.get(str).toString();
						if(val.contains(str_value)) Normal_list.add(val);
					}
		        }  
		        //生成结果字符串
				for(int ii=0;ii<Normal_list.size();ii++)	res+=Normal_list.get(ii)+",";
			}	
	 }  
	 if(tablename.contains("vip")){
		    obj.clearObject();
			String svc="";
			if(listday.size()==0){
			    //String finalsvc="";
				String svc_sql="select  mac,svc,timelen,nums,DATE_FORMAT(day,'%Y-%m-%d')  as day from vip_svc where mac='"+mac+"' and taskid="+taskid+" order by day desc ";
				List<mysqlRow> rows=obj.ExeSqlQuery(svc_sql);
				if(rows.size()>0){
					String svc_str="";
					svc_str=obj.toJosn(0,pagesize, 0);
					svc_str=svc_str.split(",\"rows\"")[1];
					svc=svc_str.substring(2,svc_str.length()-2);
					Print.println(svc);
				}				
			 }	
			else if(listday.size()>0){				
				obj.clearObject();
				String select_svc="select mac,svc,timelen,nums,DATE_FORMAT(day,'%Y-%m-%d')  as day  from vip_svc where taskid="+taskid+" and "+mac_columnname+"=\""+mac+"\" and day>="+begday+" and day<"+endday+"  order by day desc  ";
				List<mysqlRow> rows=obj.ExeSqlQuery(select_svc);
				if(rows.size()>0){
					String svc_str="";
					svc_str=obj.toJosn(0,pagesize, 0);
					svc_str=svc_str.split(",\"rows\"")[1];
					svc=svc_str.substring(2,svc_str.length()-2);	
				}	
			}
			if(svc.endsWith(",")) svc=svc.substring(0,svc.length()-1);
			res=svc;
	 }		 
	if(res.endsWith(",")) res=res.substring(0,res.length()-1);
	return  res;	
   }
	
	/**
	 * 获取在tasklog,taskname中同时存在的taskid,返回list
	 */
    public static List<String> get_all_taskid(mysqlObject obj){
    		obj.clearObject();
        String taskid_str="";
        String sql="select taskid from (select distinct(taskid) from tasklog) as a where taskid in(select distinct(taskid) from taskname  where status=3)";
        Print.println(sql);
        //List<mysqlObject.mysqlRow> rows= obj.ExeSqlQuery(sql);
        String result=obj.toJosn(0,500,1000);
        result=result.split("\"rows\"")[1].substring(2);
        result=result.replace("\"taskid\":", "").replace("{","").replace("}","").replace("]","").replace("\"","").replace("count:","");
        taskid_str=result+",";
        List<String> taskid_list=new ArrayList<String>();
        if(taskid_str.contains(",")){
            String [] arr=taskid_str.split(",");
            for(int i=0;i<arr.length;i++){
                taskid_list.add(arr[i]);
            }
        }else if(taskid_str.length()>0&&(!taskid_str.contains(","))) taskid_list.add(taskid_str);
        return taskid_list;
    }
    
    /**
     * 取出字符串中第m页，n条数据(字符串用=分割)，
     * 输入字符串中必须含有=，按=分割,例如{\"mac1\":1,\"day\":20180201}={\"mac2\":1,\"day\":20180201}={\"mac3\":1,\"day\":20180201}
     * 相当于分页取数据
     */
  	public static String getdata_fromMtoN_likepage_instr(String str,int pno,int psize){
  		if(!str.contains("=")) return str;
  		String[] result_arr=str.split("=");
  		String info="";
  		int begno=pno*psize;
  		if(psize>result_arr.length) psize=result_arr.length;
  		if(begno<=result_arr.length){
  			int end=begno+psize;
  			if(end>result_arr.length) end=result_arr.length;
  			for(int i=begno;i<end;i++){
  				info+=result_arr[i]+",";	
  		    }
  		}
  		if(info.endsWith(",")) info=info.substring(0,info.length()-1);
  		return info;
  	}

}
