/**
 * 
 */
package AnalysizeOntime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.proweb.job.libObject;
import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.Collection;
import Common.ConnectToDatabase;
import Common.DatabaseParas;
import Common.TASK_DEFINITION;
import Common.Table_Task;
import Common.Task_result_Judge_new;
import Common.TimeDate;
import Model.task_spacetime_lib;
import Model.task_tracepegging_lib;
import datamanage.AnalysizeDataCache;
import jsonparse.QueryJsonParse;
import probd.hbase.common.MyLog;
import servlet.baseServlet;

public class queryTaskresult extends baseServlet {
	
	static String taskid=null;	
    static long TASKID=0;  
	static String  mac="";	
	static String  type="";
    static String ENDRESULT;
    static String limit=null;
    static String page=null;
	static int  pagesize=10;
	static int  pageno=1;	 
	static String user=null;
	static String  totalnums=null;	
	static String conditionnum="1";
	static int ConditionNum=1;
    static int total=0;
    static String tablename;
    
	@Override
	public String handle() {
		taskid=getObject("taskid");	
		mac=getObject("mac");	
		type=getObject("type");	
		page=getObject("page");	
		limit=getObject("limit");	
		conditionnum=getObject("conditionnum");	
		ENDRESULT="{\"total\":\""+total+"\",\"data\":[]}";
		if(init())	ENDRESULT=getResult();		
		return ENDRESULT;		
	}
	
	public static String getResult(){
		MyLog.AddLog("actual_data_analyzer.log", "step===start to getResult===time===" + System.currentTimeMillis()+"===taskid===" + taskid);
		String str=ENDRESULT;
		ConnectToDatabase.connect();
		mysqlObject sqlobj=new mysqlObject();
		if(pageno>0) pageno=pageno-1;
		String tasktype=Table_Task.getActual_Tasktype_by_taskid(sqlobj,taskid);
		tablename=Table_Task.getActual_taskname_by_tasktype(tasktype);
		
		
		if(type.contains("result")) str=getTotalCountNums();
		if(type.contains("mac"))  str=getMaclist_pagelimit(tablename,"nums",pageno,pagesize); 
		if(type.contains("trace")) str=getTrace_pagelimit(sqlobj,tablename,pageno,pagesize);		
				
		
		String res="";
		if(type.contains("result")) {
			str=str.replace("},{","}={");
			res=Task_result_Judge_new.getdata_fromMtoN_likepage_instr(str,pageno,pagesize);
		}
		else res=str;
		str="{\"total\":\""+total+"\",\"data\":["+res+"]}";
		MyLog.AddLog("actual_data_analyzer.log", "step===end to getResult===time==="+ System.currentTimeMillis() +"===result===" + str);
		return str;
	}
	
	@SuppressWarnings("unused")
	private static String getTrace(mysqlObject sqlobj,String tablename) {
		sqlobj.clearObject();
		String res="";
		String sql="";
		if(mac.length()>0)sql="select mac,stime,etime,svc from "+tablename+" where taskid="+TASKID+" and  mac=\""+mac+"\" order by etime desc";
		else sql="select mac,stime,etime,svc from "+tablename+" where taskid="+TASKID+"  order by etime desc";
		
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);		
		String str="";
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0, 10, 100000);	
			 String tmp=result.split(",\"rows\":")[1];
			 str=tmp.replace("svc","servicecode").replace("[","").replace("]","").replace("}}", "}");
			 total=str.split("}").length;
			 res=str;
		}
		return res;
	}

	
	private static String getTrace_pagelimit(mysqlObject sqlobj,String tablename,int pageno,int limit) {		
		//从本地缓存中获取，如果没有，则从数据库中获取
		String data = getTraceFromCache(tablename,pageno,limit);
		if(data == null){
			data = getTraceFromDB(sqlobj,tablename,pageno,limit);
		}
		return data;
	}
	
	/**
	 * 从本地缓存中获取Trace
	 */
	private static String getTraceFromCache(String tablename,int pageno, int limit) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getTraceFromCache");
		String data = null;
		ArrayList<libObject> list = AnalysizeDataCache.getDataByTaskidAndTableName(taskid, tablename);
		if(list != null){
			data = getTraceData(tablename,list,pageno,limit);
		}
		return data;
	}

	private static String getTraceData(String tableName,ArrayList<libObject> list,int pageno, int limit) {
		String data = null;
		switch (tableName) {
		case "trace_pegging":
			data = getTraceFromTracePegging(list,pageno,limit);
			break;
		case "SpaceTime":
			data = getTraceFromSpaceTime(list,pageno,limit);
			break;
		default:
			break;
		}
		return data;
	}

	private static String getTraceFromSpaceTime(ArrayList<libObject> value,int pageno, int limit) {
		//计算total
		total = value.size();
		if(mac.length() > 0){
			int count = 0;
			for (libObject obj : value) {
				task_spacetime_lib bean = (task_spacetime_lib)obj;
				if(mac.equals(bean.getMac())){
					count++;
				}
			}
			total = count;
		}
		
		String result = getResultFromList(value,pageno,limit);
		return result;
	}

	private static String getTraceFromTracePegging(ArrayList<libObject> value,int pageno, int limit) {
		//计算total
		total = value.size();
		if(mac.length() > 0){
			int count = 0;
			for (libObject obj : value) {
				task_tracepegging_lib bean = (task_tracepegging_lib)obj;
				if(mac.equals(bean.getMac())){
					count++;
				}
			}
			total = count;
		}
		
		String result = getResultFromList(value,pageno,limit);
		return result;
	}

	/**
	 * 获取分页数据
	 */
	private static String getResultFromList(ArrayList<libObject> value,int pageno, int limit) {
		List<libObject> resultList = value.subList(pageno*limit, pageno*limit + limit);
		QueryJsonParse<libObject> parse = new QueryJsonParse<libObject>();
		parse.setRows(resultList);
		String json = JSON.toJSONString(parse);
	    String temp = json.split("\"rows\":")[1];
	    //返回格式{}，{}，{}
	    String result = temp.substring(1,temp.indexOf("]")).replace("svc","servicecode");
		MyLog.AddLog("actual_data_analyzer.log", "step===trace_pegging data in cache===" + result);
		return result;
	}
	
	/**
	 * 从数据库中获取Trace
	 */
	private static String getTraceFromDB(mysqlObject sqlobj, String tablename,int pageno,int limit) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getTraceFromDB");
		String res="";
		String sqlMac = mac.length() > 0 ? " and  mac=\""+mac+"\"" : "";
		String countSql="select count(*) as count from "+tablename+" where taskid=" + TASKID + sqlMac;
		String resSql="select mac,stime,etime,svc from "+tablename+" where taskid="+TASKID + sqlMac +" order by etime desc limit "+pageno*limit+","+limit;
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


	/**
	 * 获取Mac列表
	 */
	@SuppressWarnings("unused")
	private static String getMaclist(mysqlObject sqlobj) {
		String str="";
		String datastr="";
		Map<String,String> macmap=new HashMap<String,String>();
		List<Integer> countlist=new ArrayList<Integer>();
		if(mac==null||mac.length()==0){
			List<String> list=Table_Task.get_ditinc_pointcoulumn(sqlobj,tablename,"mac",taskid);
			if(!list.isEmpty()){
				for(int i=0;i<list.size();i++){
					String mac=list.get(i).toString();
					String count=getMacCountnums_bytaskid(sqlobj,mac);
					if(TimeDate.isnum(count)){
						int intcount=Integer.parseInt(count);
						if(!countlist.contains(intcount)) countlist.add(intcount);
					} 
					if(macmap.containsKey(count)){
						String value=macmap.get(count).toString();
						if(!value.contains(mac)) {
							String newvalue=value+","+mac;
							macmap.put(count, newvalue);
						}						
					}else{
						macmap.put(count, mac);
					}					
				}
			}
			Collections.sort(countlist);
			for(int i=countlist.size()-1;i>=0;i--){
				String count=countlist.get(i).toString();
				String macstr=macmap.get(count).toString();
				if(macstr.contains(",")){
					String arr[] =macstr.split(",");
					for(int j=0;j<arr.length;j++){
						String mac=arr[j];
						String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, mac,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
						datastr+="{\"mac\":\""+mac+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+count+"\"},";
					}
				}else{
					String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, macstr,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
					datastr+="{\"mac\":\""+macstr+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+count+"\"},";
				}
			}			
			if(datastr.endsWith(","))datastr=datastr.substring(0,datastr.length()-1);
			total=list.size();	
			str=datastr;			
		}
		else{
			String count=getMacCountnums_bytaskid(sqlobj,mac);
			String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, mac,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
			str="{\"mac\":\""+mac+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+count+"\"}";
			total=1;
		}		
		return str;
	}

	
	/**
	 * 分页获取Mac列表
	 */
	private static String getMaclist_pagelimit(String tablename,String columnname,int pageno,int pagesize) {
		//从本地缓存中获取，如果没有，则从数据库中获取
		String result = getMacFromCache(tablename,columnname,pageno,pagesize);
		if(result == null){
			result = getMacFromDB(tablename,columnname,pageno,pagesize);
		}
		return result;
	}
	
	private static String getMacFromCache(String tableName,String columnname,int pageno,int pagesize) {
		MyLog.AddLog("actual_data_analyzer.log", "step===getMacFromCache");
		String data = null;
		ArrayList<libObject> list = AnalysizeDataCache.getDataByTaskidAndTableName(taskid, tableName);
		if(list != null){
			data = getMacData(tableName,columnname,pageno,pagesize,list);
		}
		return data;
	}
	
	
	private static String getMacData(String tableName,String columnname,int pageno,int pagesize, ArrayList<libObject> list) {
		String data = "";
		switch (tableName) {
		case "trace_pegging":
			data = getMacFromTracePegging(list,columnname,pageno,pagesize);
			break;
		case "SpaceTime":
			data = getMacFromSpaceTime(list,columnname,pageno,pagesize);
			break;
		default:
			break;
		}
		return data;
	}

	private static String getMacFromSpaceTime(ArrayList<libObject> list, String columnname, int pageno2,
			int pagesize2) {
		// TODO Auto-generated method stub
		// String countsql="select count(distinct(mac)) as count from "+tablename+" where  taskid="+TASKID+" and "+columnname+" ="+ConditionNum;

		return null;
	}

	private static String getMacFromTracePegging(ArrayList<libObject> list, String columnname, int pageno2,
			int pagesize2) {
		// TODO Auto-generated method stub
		// String countsql="select count(distinct(mac)) as count from "+tablename+" where  taskid="+TASKID+" and "+columnname+" ="+ConditionNum;

		return null;
	}

	private static String getMacFromDB(String tablename,String columnname,int pageno,int pagesize) {
		String str="";
		String datastr="";
		mysqlObject sqlobj = new mysqlObject();
		List<String> maclist=new ArrayList<String>();
		if(mac==null||mac.length()==0){
			sqlobj.clearObject();
			String countsql="select count(distinct(mac)) as count from "+tablename+" where  taskid="+TASKID+" and "+columnname+" ="+ConditionNum;
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
			String sql="select distinct(mac) from "+tablename+" where  taskid="+TASKID+" and "+columnname+" ="+ConditionNum+" limit "+pageno*pagesize+","+pagesize;
			System.out.println("sql="+sql);
			MyLog.AddLog("actual_data_analyzer.log", "sql="+sql);
			List<mysqlRow>	rows2=sqlobj.ExeSqlQuery(sql);			
			if(rows2.size()>0){
				 String result=sqlobj.toJosn(0, 10, 100000);	
				 String tmp=result.split(",\"rows\"")[1];
				 str=tmp.replace("\"mac\"","").replace(":", "").replace("{", "").replace("}", "").replace("\"","").replace("[","").replace("]","");
			}	
			if(str.length()>0&&str!=null){
				if(str.contains(",")){
					String[] arr=str.split(",");
					for(String mac:arr) maclist.add(mac);
				}
				else maclist.add(str);	
			}
			if(maclist.isEmpty()) return str;	
			for(int i=0;i<maclist.size();i++){
				String mac=maclist.get(i).toString().trim();
				String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, mac,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
			    datastr+="{\"mac\":\""+mac+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+ConditionNum+"\"},";						
			}	
			if(datastr.endsWith(","))datastr=datastr.substring(0,datastr.length()-1);
			str=datastr;			
		}
		else{
			String count=getMacCountnums_bytaskid(sqlobj,mac);
			String hbaselable=Table_Task.getLableFromHbase_byMac(DatabaseParas.hbasetable,TASK_DEFINITION.hbase_table_name, mac,TASK_DEFINITION.hbase_info, DatabaseParas.hbase_qualifieres, TASK_DEFINITION.hbase_value1);
			str="{\"mac\":\""+mac+"\",\"lable\":\""+hbaselable+"\",\"count\":\""+count+"\"}";
			total=1;
		}		
		return str;
	}


	/**
	 * 根据taskid获取单个mac的碰撞次数或采集次数
	 */
	public static String getMacCountnums_bytaskid(mysqlObject sqlobj,String macstr){
		String str=ENDRESULT;
		sqlobj.clearObject();
		String sql="select nums from "+tablename+" where taskid="+TASKID+" and mac=\""+macstr+"\" limit 1";
		//System.out.println(sql);
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0,10,10);				 
			 String tmp=result.split("\"rows\"")[1];
			 str=tmp.replace("\"nums\":","").replace("\"","").replace(":[{","").replace("}]}","");
		}			
		return str;
	}
	
	
	/**
	 * 获取碰撞或采集信息
	 */
	public static String getTotalCountNums(){
		//从本地缓存中获取，如果没有，则从数据库中获取
		String result = getTotalCountNumsFromCache();
		if(result == null){
			result = getTotalCountNumsFromDB();
		}
		return result;
	}
	
	private static String getTotalCountNumsFromCache() {
		MyLog.AddLog("actual_data_analyzer.log", "step===getTotalCountNumsFromCache");
		String data = null;
		ArrayList<libObject> list = AnalysizeDataCache.getDataByTaskidAndTableName(taskid, tablename);
		if(list != null){
			data = getTotalCountNumsData(tablename,list);
		}
		return data;
	}

	private static String getTotalCountNumsData(String tableName,ArrayList<libObject> list) {
		String data = "";
		switch (tableName) {
		case "trace_pegging":
			data = getNumsDataFromTracePegging(list);
			break;
		case "SpaceTime":
			data = getNumsDataFromSpaceTime(list);
			break;
		default:
			break;
		}
		return data;
	}

	private static String getNumsDataFromSpaceTime(ArrayList<libObject> list) {
		String result = "";
		//获取去重的nums
		List<Integer> numsList = new ArrayList<Integer>();
		for (libObject obj : list) {
			task_spacetime_lib bean = (task_spacetime_lib)obj;
			String nums = bean.getNums();
			if(StringUtils.isNotEmpty(nums) && !numsList.contains(nums)){
				numsList.add(Integer.valueOf(nums));
			}
		}
		if(numsList.size() == 0)return result;
		Collections.sort(numsList);//自然正序
		
		//获取nums对应的去重的mac数
		for (Integer num : numsList) {
			ArrayList<String> macList = new ArrayList<String>();
			for (libObject obj : list) {
				task_spacetime_lib bean = (task_spacetime_lib)obj;
				String beanNum = bean.getNums();
				String mac = bean.getMac();
				if(String.valueOf(num).equals(beanNum) && !macList.contains(mac)){
					macList.add(mac);
				}
			}
			result += "{\"conditionnum\":\""+num+"\",\"countnum\":\""+macList.size()+"\",\"lablenum\":\"0\",\"falsemacnum\":\"0\",\"taskid\":\""+taskid+"\"},";			
		}
		total=numsList.size();
		return result;
	}

	private static String getNumsDataFromTracePegging(ArrayList<libObject> list) {
		String result = "";
		//获取去重的nums
		List<Integer> numsList = new ArrayList<Integer>();
		for (libObject obj : list) {
			task_tracepegging_lib bean = (task_tracepegging_lib)obj;
			String nums = bean.getNums();
			if(StringUtils.isNotEmpty(nums) && !numsList.contains(nums)){
				numsList.add(Integer.valueOf(nums));
			}
		}
		if(numsList.size() == 0)return result;
		Collections.sort(numsList);//自然正序
		
		//获取nums对应的去重的mac数
		for (Integer num : numsList) {
			ArrayList<String> macList = new ArrayList<String>();
			for (libObject obj : list) {
				task_tracepegging_lib bean = (task_tracepegging_lib)obj;
				String beanNum = bean.getNums();
				String mac = bean.getMac();
				if(String.valueOf(num).equals(beanNum) && !macList.contains(mac)){
					macList.add(mac);
				}
			}
			result += "{\"conditionnum\":\""+num+"\",\"countnum\":\""+macList.size()+"\",\"lablenum\":\"0\",\"falsemacnum\":\"0\",\"taskid\":\""+taskid+"\"},";			
		}
		total=numsList.size();
		return result;
	}

	private static String getTotalCountNumsFromDB() {
		String datastr="";
		mysqlObject sqlobj = new mysqlObject();
		int[] numslit=getDistinctCountNums(sqlobj);
		if(numslit==null||numslit.length==0) return datastr;
	    for(int i=0;i<numslit.length;i++){
	    		sqlobj.clearObject();
	    		String nums=String.valueOf(numslit[i]);
	    		String sql="select count(distinct(mac)) as count from "+tablename+" where taskid="+TASKID+" and nums="+nums;
			System.out.println(sql);
			List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
			if(rows.size()>0){
				 String result=sqlobj.toJosn(0,10,10);				 
				 String tmp=result.split("\"rows\"")[1];
				 String count=tmp.replace("\"count\":","").replace("\"","").replace(":[{","").replace("}]}","");
				 datastr+="{\"conditionnum\":\""+nums+"\",\"countnum\":\""+count+"\",\"lablenum\":\"0\",\"falsemacnum\":\"0\",\"taskid\":\""+taskid+"\"},";			
			}			
	    }
		if(datastr.endsWith(",")) datastr=datastr.substring(0,datastr.length()-1);		
		total=numslit.length;
		return datastr;
	}

	/**
	 * 获取去重碰撞次数或者采集次数，返回List
	 */
	public static int[] getDistinctCountNums(mysqlObject sqlobj){
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
		if(numsstr.length()==0||numsstr==null) return null ;
		if(numsstr.endsWith(",")) numsstr=numsstr.substring(0,numsstr.length()-1);
		if(numsstr.contains(",")){
			String[] arr=numsstr.split(",");
			for(String nums:arr){
				if(TimeDate.isnum(nums))list.add(Integer.parseInt(nums));
			}
		}
		else {
			if(TimeDate.isnum(numsstr))list.add(Integer.parseInt(numsstr));
		}
		int[] resarray = new int[list.size()];
		for(int i=0;i<list.size();i++){
			resarray[i]=Integer.parseInt(list.get(i).toString());
		}
		Collection.shell_sort(resarray);
		return resarray;
	}
	
	public boolean init(){
		if(!TimeDate.isnum(taskid)) return false;
		else TASKID=Long.parseLong(taskid);	
		if(type==null||type.length()==0) return false;
        if(TimeDate.isnum(page)) pageno=Integer.parseInt(page);
		if(TimeDate.isnum(limit)) pagesize=Integer.parseInt(limit);
		if(TimeDate.isnum(conditionnum)) ConditionNum=Integer.parseInt(conditionnum);
		return true;
	}
	

}
