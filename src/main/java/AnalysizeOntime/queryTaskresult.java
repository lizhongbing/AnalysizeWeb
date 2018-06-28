package AnalysizeOntime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.proweb.common.timeop;
import com.proweb.job.libObject;
import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.Collection;
import Common.ConnectToDatabase;
import Common.DatabaseParas;
import Common.TASK_DEFINITION;
import Common.Table_Task;
import Common.TimeDate;
import Model.task_spacetime_lib;
import Model.task_tracepegging_lib;
import datamanage.AnalysizeDataCacheManager;
import domain.NumsAndMacCount;
import jsonparse.QueryJsonParse;
import probd.hbase.common.MyLog;
import servlet.baseServlet;

public class queryTaskresult extends baseServlet {
	
	private String taskid;	
	private long TASKID=0;  
	private String mac;	
	private String type;
	private String ENDRESULT;
	private String limit;
	private String page;
	private int  pagesize=10;
	private int  pageno=1;	 
	private String conditionnum="1";
	private int ConditionNum=1;
	private int total=0;
	private String tablename;
	private mysqlObject sqlobj;
    
	@Override
	public String handle() {
		MyLog.AddLog("actual_data_analyzer.log", "step===start to handle======");
		taskid=getObject("taskid");	
		mac=getObject("mac");	
		type=getObject("type");	
		page=getObject("page");	
		limit=getObject("limit");	
		conditionnum=getObject("conditionnum");	
		ENDRESULT="{\"total\":\""+total+"\",\"data\":[]}";
		MyLog.AddLog("actual_data_analyzer.log", "step===handle-taskid======"+taskid);

		if(init()){
			MyLog.AddLog("actual_data_analyzer.log", "step===getResult======");
			ENDRESULT=getResult();		
		}
		return ENDRESULT;		
	}
	
	public boolean init(){
		if(!timeop.isNumeric(taskid)){
			return false;
		}else{
			TASKID=Long.parseLong(taskid);	
		}
		if(StringUtils.isEmpty(type)) return false;
        if(TimeDate.isnum(page)) pageno=Integer.parseInt(page);
		if(TimeDate.isnum(limit)) pagesize=Integer.parseInt(limit);
		if(TimeDate.isnum(conditionnum)) ConditionNum=Integer.parseInt(conditionnum);
		return true;
	}
	
	public String getResult(){
		MyLog.AddLog("actual_data_analyzer.log", "step===start to getResult===time===" + System.currentTimeMillis()+"===taskid===" + taskid);
		String result=ENDRESULT;
		ConnectToDatabase.connect();
		sqlobj = new mysqlObject();
		if(pageno>0) pageno = pageno-1;
		String tasktype = Table_Task.getActual_Tasktype_by_taskid(sqlobj,taskid);
		tablename = Table_Task.getActual_taskname_by_tasktype(tasktype);
		
		String str = "";
		if(type.contains("result")) str=getMacsInNums();
		if(type.contains("mac"))  str=getMacList("nums"); 
		if(type.contains("trace")) str=getMacTrace();		
		
		result="{\"total\":\""+total+"\",\"data\":["+str+"]}";
		MyLog.AddLog("actual_data_analyzer.log", "step===end to getResult===time==="+ System.currentTimeMillis() +"===total===" + total);
		return result;
	}
	
	/**
	 * 获取trace数据
	 */
	private String getMacTrace() {		
		//从本地缓存中获取，如果没有，则从数据库中获取
		String data = getTraceFromCache();
		if(data == null){
			data = getTraceFromDB();
		}
		return data;
	}
	
	/**
	 * 获取MAC数据
	 */
	private String getMacList(String columnname) {
		//从本地缓存中获取，如果没有，则从数据库中获取
		String result = getMacFromCache(columnname);
		if(result == null){
			result = getMacFromDB(columnname);
		}
		return result;
	}
	
	/**
	 * 获取碰撞或采集信息对应的MAC数
	 */
	public String getMacsInNums(){
		//从本地缓存中获取，如果没有，则从数据库中获取
		String result = getNumsAndMacsFromCache();
		if(result == null){
			result = getNumsAndMacsFromDB();
		}
		return result;
	}
	
	private String getTraceFromCache() {
		MyLog.AddLog("actual_data_analyzer.log", "step===getTraceFromCache");
		String data = null;
		ArrayList<libObject> list = AnalysizeDataCacheManager.getDataByTaskidAndTableName(taskid, tablename);
		if(list != null){
			sortList(list,"etime");
			data = getTraceData(list);
		}
		return data;
	}

	private String getTraceData(ArrayList<libObject> list) {
		String data = null;
		switch (tablename) {
		case "trace_pegging":
			data = getTraceFromTracePegging(list);
			break;
		case "spacetime":
			data = getTraceFromSpaceTime(list);
			break;
		default:
			break;
		}
		return data;
	}

	private String getTraceFromSpaceTime(ArrayList<libObject> value) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getTraceFromSpaceTime");
		List<libObject> list = null;
		if(StringUtils.isNotEmpty(mac)){
			//查找单个MAC的轨迹
			list = new ArrayList<libObject>();
			for (libObject obj : value) {
				task_spacetime_lib bean = (task_spacetime_lib)obj;
				if(mac.equals(bean.getMac())){
					list.add(bean);
				}
			}
		}else{
			//查找所有MAC的轨迹
			list = value;
		}
		total = list.size();
		String result = getPageResult(list);
		return result;
	}

	private String getTraceFromTracePegging(ArrayList<libObject> value) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getTraceFromTracePegging");
		List<libObject> list = null;
		if(StringUtils.isNotEmpty(mac)){
			//查找单个MAC的轨迹
			list = new ArrayList<libObject>();
			for (libObject obj : value) {
				task_tracepegging_lib bean = (task_tracepegging_lib)obj;
				if(mac.equals(bean.getMac())){
					list.add(bean);
				}
			}
		}else{
			//查找所有MAC的轨迹
			list = value;
		}
		total = list.size();
		String result = getPageResult(list);
		return result;
	}


	private String getPageResult(List<libObject> value) {
		int fromIndex = pageno*pagesize;
		int toIndex = fromIndex + pagesize;
		toIndex = toIndex > value.size() ? value.size() : toIndex;
		List<libObject> resultList = value.subList(fromIndex,toIndex);
		QueryJsonParse<libObject> parse = new QueryJsonParse<libObject>();
		parse.setRows(resultList);
		String json = JSON.toJSONString(parse);
	    String temp = json.split("\"rows\":")[1];
	    //返回格式{}，{}，{}
	    String result = temp.substring(1,temp.indexOf("]")).replace("svc","servicecode");
		MyLog.AddLog("actual_data_analyzer.log", "step===trace_pegging data in cache===");
		return result;
	}
	
	private String getTraceFromDB() {
		MyLog.AddLog("actual_data_analyzer.log", "step===getTraceFromDB");
		String res="";
		String sqlMac = mac.length() > 0 ? " and  mac=\""+mac+"\"" : "";
		String countSql="select count(*) as count from "+tablename+" where taskid=" + TASKID + sqlMac;
		String resSql="select mac,stime,etime,svc from "+tablename+" where taskid="+TASKID + sqlMac +" order by etime desc limit "+pageno*pagesize+","+pagesize;
		sqlobj.clearObject();
		List<mysqlRow>	crows=sqlobj.ExeSqlQuery(countSql);
		if(crows.size()>0){
			String result=sqlobj.toJosn(0,10,10);	
			String str=result.split("count")[1];
			int count= Integer.parseInt(str.substring(3,str.length()-4));
			if(count>0)total=count;
		}			
		sqlobj.clearObject();
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(resSql);		
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0, 10, 100000);	
			 String tmp=result.split(",\"rows\":")[1];
			 res=tmp.replace("svc","servicecode").replace("[","").replace("]","").replace("}}", "}");			
		}
		return res;
	}
	
	private String getMacFromCache(String columnname) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getMacFromCache");
		String data = null;
		ArrayList<libObject> list = AnalysizeDataCacheManager.getDataByTaskidAndTableName(taskid, tablename);
		if(list != null){
			sortList(list,"nums");
			data = getMacData(columnname,list);
		}
		return data;
	}
	
	private String getMacData(String columnname, ArrayList<libObject> list) {
		String data = "";
		switch (tablename) {
		case "trace_pegging":
			data = getMacFromTracePegging(list,columnname);
			break;
		case "spacetime":
			data = getMacFromSpaceTime(list,columnname);
			break;
		default:
			break;
		}
		return data;
	}

	private String getMacFromSpaceTime(ArrayList<libObject> list, String columnname) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getMacFromSpaceTime");
		String result = "";
		if(mac==null || mac.length()==0){
			boolean hasConditionNum = StringUtils.isNotEmpty(conditionnum);
			MyLog.AddLog("actual_data_analyzer.log", "step===getdistict mac");
			//获取columnname对应的去重的mac数
			List<String> macList = new ArrayList<String>();
			for (libObject obj : list) {
				task_spacetime_lib bean = (task_spacetime_lib)obj;
				String macValue = bean.getMac();
				String value = bean.getValueByName(columnname);
				if(hasConditionNum){
					if(value.equals(conditionnum) && !macList.contains(macValue)){
						macList.add(macValue);
					}
				}else{
					if(!macList.contains(macValue)){
						macList.add(macValue);
					}
				}
			}
			total = macList.size();
			//分页数据
			if(macList.size() == 0) return result;
			MyLog.AddLog("actual_data_analyzer.log", "step===macList ==="+macList.size());
			int fromIndex = pageno*pagesize;
			int toIndex = fromIndex + pagesize > macList.size() ? macList.size() : fromIndex + pagesize;
			List<String> resultList = macList.subList(fromIndex,toIndex);
			if(resultList.size() == 0) return result;
			String datastr = "";
			for(int i=0;i<resultList.size();i++){
				String macString=resultList.get(i).trim();
				String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, macString,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
			    datastr+="{\"mac\":\""+macString+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+ConditionNum+"\"},";						
			}	
			if(datastr.endsWith(","))datastr=datastr.substring(0,datastr.length()-1);
			result=datastr;	
			MyLog.AddLog("actual_data_analyzer.log", "step===result===");

		}else{
			MyLog.AddLog("actual_data_analyzer.log", "step===result===mac!= null");
			List<String> columnnameList = new ArrayList<String>();
			for (libObject obj : list) {
				task_tracepegging_lib bean = (task_tracepegging_lib)obj;
				String macValue = bean.getMac();
				if(mac.equals(macValue)){
					String value = bean.getValueByName(columnname);
					columnnameList.add(value);
				}
			}
			MyLog.AddLog("actual_data_analyzer.log", "step===columnnameList==="+columnnameList.size());
			String count = "";
			for(int i=0;i < columnnameList.size();i++){
				count += columnnameList.get(i) + ",";
			}
			if(columnnameList.size() > 0){
				count = count.substring(0,count.length() - 1);
			}else{
				count = ENDRESULT;
			}
			String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, mac,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
			result="{\"mac\":\""+mac+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+count+"\"}";
			total=1;
		}
		MyLog.AddLog("actual_data_analyzer.log", "step===result===mac!= null===result==="+result);

		return result;
	
	}

	private String getMacFromTracePegging(ArrayList<libObject> list, String columnname) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getMacFromTracePegging");
		MyLog.AddLog("actual_data_analyzer.log", "step===pageno==="+pageno+"===pagesize" +pagesize);
		String result = "";
		if(StringUtils.isEmpty(mac)){
			boolean hasConditionNum = StringUtils.isNotEmpty(conditionnum);
			MyLog.AddLog("actual_data_analyzer.log", "step===getdistict mac");
			//获取columnname对应的去重的mac数
			List<String> macList = new ArrayList<String>();
			for (libObject obj : list) {
				task_tracepegging_lib bean = (task_tracepegging_lib)obj;
				String macValue = bean.getMac();
				String value = bean.getValueByName(columnname);
				if(hasConditionNum){
					if(value.equals(conditionnum) && !macList.contains(macValue)){
						macList.add(macValue);
					}
				}else{
					if(!macList.contains(macValue)){
						macList.add(macValue);
					}
				}
			}
			total = macList.size();
			//分页数据
			if(macList.size() == 0) return result;
			
			MyLog.AddLog("actual_data_analyzer.log", "step===macList==="+macList.size());
			int fromIndex = pageno*pagesize;
			int toIndex = fromIndex + pagesize > macList.size() ? macList.size() : fromIndex + pagesize;
			List<String> resultList = macList.subList(fromIndex,toIndex);
			if(resultList.size() == 0) return result;
			String datastr = "";
			for(int i=0;i<resultList.size();i++){
				String macString=resultList.get(i).trim();
				String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, macString,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
			    datastr+="{\"mac\":\""+macString+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+ConditionNum+"\"},";						
			}	
			if(datastr.endsWith(","))datastr=datastr.substring(0,datastr.length()-1);
			result=datastr;	
			MyLog.AddLog("actual_data_analyzer.log", "step===result===");

		}else{
			MyLog.AddLog("actual_data_analyzer.log", "step===result===mac!= null");
			List<String> columnnameList = new ArrayList<String>();
			for (libObject obj : list) {
				task_tracepegging_lib bean = (task_tracepegging_lib)obj;
				String macValue = bean.getMac();
				if(mac.equals(macValue)){
					String value = bean.getValueByName(columnname);
					columnnameList.add(value);
				}
			}
			MyLog.AddLog("actual_data_analyzer.log", "step===columnnameList==="+columnnameList.size());
			String count = "";
			for(int i=0;i < columnnameList.size();i++){
				count += columnnameList.get(i) + ",";
			}
			if(columnnameList.size() > 0){
				count = count.substring(0,count.length() - 1);
			}else{
				count = ENDRESULT;
			}
			String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, mac,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
			result="{\"mac\":\""+mac+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+count+"\"}";
			total=1;
		}
		MyLog.AddLog("actual_data_analyzer.log", "step===result===mac!= null===result===");

		return result;
	}

	private String getMacFromDB(String columnname) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getMacFromDB");
		String str="";
		String datastr="";
		if(mac==null||mac.length()==0){
			List<String> maclist = getAllMacs(sqlobj,tablename,columnname);
			if(maclist.isEmpty()) return str;	
			for(int i=0;i<maclist.size();i++){
				String mac=maclist.get(i).trim();
				String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, mac,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
			    datastr+="{\"mac\":\""+mac+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+ConditionNum+"\"},";						
			}	
			if(datastr.endsWith(","))datastr=datastr.substring(0,datastr.length()-1);
			str=datastr;			
		}else{
			String count=getMacCountnums_bytaskid(sqlobj,mac);
			String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, mac,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
			str="{\"mac\":\""+mac+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+count+"\"}";
			total=1;
		}		
		return str;
	}
	
	private List<String> getAllMacs(mysqlObject sqlobj,String tablename,String columnname) {
		List<String> maclist=new ArrayList<String>();
		sqlobj.clearObject();
		String conditionNumSql = StringUtils.isEmpty(conditionnum) ? "" : " and "+columnname+"="+ConditionNum;
		String countsql="select count(distinct(mac)) as count from "+tablename+" where  taskid="+TASKID+conditionNumSql;
		System.out.println("countsql="+countsql);
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(countsql);
		if(rows.size()>0){			
			 String result=sqlobj.toJosn(0,10,10);	
			 String tmpstr=result.split("count")[1];
			 int count= Integer.parseInt(tmpstr.substring(3,tmpstr.length()-4));
			 System.out.println("count "+count);
			 if(count>0){
				 total=count;
			 }
		}		
		sqlobj.clearObject();
		String sql="select distinct(mac) from "+tablename+" where  taskid="+TASKID + conditionNumSql +" order by "+columnname+" desc limit "+pageno*pagesize+","+pagesize;
		System.out.println("sql="+sql);
		MyLog.AddLog("actual_data_analyzer.log", "sql="+sql);
		List<mysqlRow>	rows2=sqlobj.ExeSqlQuery(sql);			
		String str="";
		if(rows2.size()>0){
			 String result=sqlobj.toJosn(0, 10, 100000);	
			 String tmp=result.split(",\"rows\"")[1];
			 str=tmp.replace("\"mac\"","").replace(":", "").replace("{", "").replace("}", "").replace("\"","").replace("[","").replace("]","");
		}	
		if(str.length()>0){
			if(str.contains(",")){
				String[] arr=str.split(",");
				for(String mac:arr) maclist.add(mac);
			}
			else maclist.add(str);	
		}
		return maclist;
	}
	
	private String getNumsAndMacsFromCache() {
		MyLog.AddLog("actual_data_analyzer.log", "step===getTotalCountNumsFromCache");
		String data = null;
		ArrayList<libObject> list = AnalysizeDataCacheManager.getDataByTaskidAndTableName(taskid, tablename);
		if(list != null){
			data = getNumsAndMacsData(tablename,list);
		}
		return data;
	}

	private String getNumsAndMacsData(String tableName,ArrayList<libObject> list) {
		String data = "";
		switch (tableName) {
		case "trace_pegging":
			data = getNumsAndMacsFromTracePegging(list);
			break;
		case "spacetime":
			data = getNumsAndMacsFromSpaceTime(list);
			break;
		}
		return data;
	}

	private String getNumsAndMacsFromSpaceTime(ArrayList<libObject> list) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getNumsDataFromSpaceTime");
		String result = "";
		Map<String, Set<String>> hashMap = new HashMap<String, Set<String>>();
		for (libObject obj : list) {
			task_spacetime_lib bean = (task_spacetime_lib)obj;
			String nums = bean.getNums();
			if(hashMap.containsKey(nums)){
				Set<String> set = hashMap.get(nums);
				set.add(bean.getMac());
			}else{
				Set<String> set = new HashSet<String>();
				set.add(bean.getMac());
				hashMap.put(nums,set);
			}
		}

		if(hashMap.size() == 0) return result;
		
		Set<Entry<String, Set<String>>> entrySet = hashMap.entrySet();
		// 降序
		List<Map.Entry<String, Set<String>>> orderdList=new ArrayList<Map.Entry<String, Set<String>>>(entrySet);
        Collections.sort(orderdList, new Comparator<Map.Entry<String, Set<String>>>(){  
          @Override  
          public int compare(Entry<String, Set<String>> o1,Entry<String, Set<String>> o2) {    
              return o2.getKey().compareTo(o1.getKey());  
          }});
        //分页
	    int fromIndex = pageno*pagesize;
	    int toIndex = fromIndex + pagesize;
	    toIndex = toIndex > orderdList.size() ? orderdList.size() : toIndex;
	    List<Entry<String, Set<String>>> subList = orderdList.subList(fromIndex, toIndex);
		for (Entry<String, Set<String>> entry : subList) {
			String num = entry.getKey();
			Set<String> set = entry.getValue();
			MyLog.AddLog("actual_data_analyzer.log", "nums==="+num+"===set.size()==="+set.size());

			result += "{\"conditionnum\":\""+num+"\",\"countnum\":\""+set.size()+"\",\"lablenum\":\"0\",\"falsemacnum\":\"0\",\"taskid\":\""+taskid+"\"},";			
		}
		if(result.endsWith(",")) result=result.substring(0,result.length()-1);		

		total=hashMap.size();
		return result;
	}

	private String getNumsAndMacsFromTracePegging(ArrayList<libObject> list) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getNumsDataFromTracePegging");
		
		String result = "";
		Map<String, Set<String>> hashMap = new HashMap<String, Set<String>>();
		for (libObject obj : list) {
			task_tracepegging_lib bean = (task_tracepegging_lib)obj;
			
			String nums = bean.getNums();
			if(hashMap.containsKey(nums)){
				Set<String> set = hashMap.get(nums);
				set.add(bean.getMac());
			}else{
				Set<String> set = new HashSet<String>();
				set.add(bean.getMac());
				hashMap.put(nums,set);
			}
		}

		if(hashMap.size() == 0) return result;
		
		Set<Entry<String, Set<String>>> entrySet = hashMap.entrySet();
		// 降序
		List<Map.Entry<String, Set<String>>> orderdList=new ArrayList<Map.Entry<String, Set<String>>>(entrySet);
        Collections.sort(orderdList, new Comparator<Map.Entry<String, Set<String>>>(){  
          @Override  
          public int compare(Entry<String, Set<String>> o1,Entry<String, Set<String>> o2) {    
              return o2.getKey().compareTo(o1.getKey());  
          }}); 
        //分页
	    int fromIndex = pageno*pagesize;
	    int toIndex = fromIndex + pagesize;
	    toIndex = toIndex > orderdList.size() ? orderdList.size() : toIndex;
	    List<Entry<String, Set<String>>> subList = orderdList.subList(fromIndex, toIndex);
		for (Entry<String, Set<String>> entry : subList) {
			String num = entry.getKey();
			Set<String> set = entry.getValue();
			MyLog.AddLog("actual_data_analyzer.log", "nums="+num+"======set.size()="+set.size());

			result += "{\"conditionnum\":\""+num+"\",\"countnum\":\""+set.size()+"\",\"lablenum\":\"0\",\"falsemacnum\":\"0\",\"taskid\":\""+taskid+"\"},";			
		}
		if(result.endsWith(",")) result=result.substring(0,result.length()-1);		

		total=hashMap.size();
		return result;
	}

	private String getNumsAndMacsFromDB() {
		MyLog.AddLog("actual_data_analyzer.log", "step===getTotalCountNumsFromDB");
		String datastr="";
		sqlobj.clearObject();
		MyLog.AddLog("actual_data_analyzer.log", "step===time===" + System.currentTimeMillis());

		String sql = "select nums,count(distinct(mac)) as count from "+tablename+" where taskid="+TASKID+" group by nums";
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0,10,10);				 
			 QueryJsonParse<NumsAndMacCount> bean = JSON.parseObject(result, new TypeReference<QueryJsonParse<NumsAndMacCount>>(){});
			 ArrayList<NumsAndMacCount> rows2 = (ArrayList<NumsAndMacCount>) bean.getRows();
			 //排序
			 Collections.sort(rows2,new Comparator<NumsAndMacCount>(){
				@Override
				public int compare(NumsAndMacCount o1, NumsAndMacCount o2) {
					return o2.getNums().compareTo(o1.getNums());
				}
			 });
			 //分页
			 int fromIndex = pageno*pagesize;
			 int toIndex = fromIndex + pagesize;
			 toIndex = toIndex > rows2.size() ? rows2.size() : toIndex;
			 List<NumsAndMacCount> subList = rows2.subList(fromIndex, toIndex);
			 for (NumsAndMacCount numsAndMacCount : subList) {
				 datastr+="{\"conditionnum\":\""+numsAndMacCount.getNums()+"\",\"countnum\":\""+numsAndMacCount.getCount()+"\",\"lablenum\":\"0\",\"falsemacnum\":\"0\",\"taskid\":\""+taskid+"\"},";			
			 }
		}
		MyLog.AddLog("actual_data_analyzer.log", "step===rows===" + rows.size());
		MyLog.AddLog("actual_data_analyzer.log", "step===time===" + System.currentTimeMillis());
		if(datastr.endsWith(",")) datastr=datastr.substring(0,datastr.length()-1);		
		total=rows.size();
		return datastr;
	}

	/**
	 * 根据taskid获取单个mac的碰撞次数或采集次数
	 */
	public String getMacCountnums_bytaskid(mysqlObject sqlobj,String macstr){
		//根据taskid获取单个mac的碰撞次数或采集次数
		String str=ENDRESULT;
		sqlobj.clearObject();
		String sql="select nums from "+tablename+" where taskid="+TASKID+" and mac=\""+macstr+"\" limit 1";
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0,10,10);				 
			 String tmp=result.split("\"rows\"")[1];
			 str=tmp.replace("\"nums\":","").replace("\"","").replace(":[{","").replace("}]}","");
		}			
		return str;
	}
	
	
	/**
	 * 获取去重碰撞次数或者采集次数，返回List
	 */
	public int[] getDistinctCountNums(mysqlObject sqlobj){
		List<Integer> list=new ArrayList<Integer>();
		sqlobj.clearObject();
		String sql="select distinct(nums) as nums from "+tablename+" where taskid="+taskid;;
		System.out.println(sql);
		String numsstr="";
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0,10,10);				 
			 String tmp=result.split("\"rows\"")[1];
			 numsstr=tmp.replace("\"nums\":","").replace("\"","").replace(":","").replace("[","").replace("{","").replace("}","").replace("]","");
		}
		if(StringUtils.isEmpty(numsstr)) return null ;
		if(numsstr.endsWith(",")) numsstr=numsstr.substring(0,numsstr.length()-1);
		if(numsstr.contains(",")){
			String[] arr=numsstr.split(",");
			for(String nums:arr){
				if(TimeDate.isnum(nums))list.add(Integer.parseInt(nums));
			}
		}else {
			if(TimeDate.isnum(numsstr))list.add(Integer.parseInt(numsstr));
		}
		int[] resarray = new int[list.size()];
		for(int i=0;i<list.size();i++){
			resarray[i]=Integer.parseInt(list.get(i).toString());
		}
		Collection.shell_sort(resarray);
		return resarray;
	}
	
	/**
	 * 根据columnname排序
	 * @param list
	 */
	private void sortList(ArrayList<libObject> list,final String columnname) {
		Comparator<libObject> comparator  = null;
		switch (tablename) {
		case "trace_pegging":
			comparator = new Comparator<libObject>(){
				@Override
				public int compare(libObject o1, libObject o2) {
					task_tracepegging_lib bean1 = (task_tracepegging_lib)o1;
					task_tracepegging_lib bean2 = (task_tracepegging_lib)o2;
					String e1 = bean1.getValueByName(columnname);
					String e2 = bean2.getValueByName(columnname);
					return e2.compareTo(e1);
				}
				
			};
			break;
		case "spacetime":
			comparator = new Comparator<libObject>(){
				@Override
				public int compare(libObject o1, libObject o2) {
					task_spacetime_lib bean1 = (task_spacetime_lib)o1;
					task_spacetime_lib bean2 = (task_spacetime_lib)o2;
					String e1 = bean1.getValueByName(columnname);
					String e2 = bean2.getValueByName(columnname);
					return e2.compareTo(e1);
				}
				
			};
			break;
		}
		Collections.sort(list,comparator);
	}

	

}
