package Analysize;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.proweb.common.timeop;
import com.proweb.common.file.MyLog;
import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.ConnectToDatabase;
import Common.Print;
import Common.TimeDate;
import servlet.baseServlet;

public class SelectMacresult extends baseServlet {
	
	static String attr=null;
	static String taskid=null;
	static String time_rang=null;
	static String mac=null;
	static int pageno=0;
	static int pagesize=10;
	static String limit=null;
	static String page=null;
	static String totalnums="0";
	static int totals=0;
	static String nowday=timeop.getDayFromTime(timeop.GetCurrentTime()).replace("-","");	
	static int begday=0;
	static int endday=0;	
	static String ENDRESULT=null;
	static String limitstr=null;
	static int TOTALS=0;
	static String teststr=null;
	static long begin_st=0;
	@Override
	public String handle() {
		begin_st=System.currentTimeMillis()/1000;
		taskid=getObject("taskid");
		time_rang=getObject("time_rang");	
		mac=getObject("mac");
		limit=getObject("limit");
		page=getObject("page");
		totalnums=getObject("totalnums");	
		ENDRESULT=Manage();		
		return ENDRESULT;
	}

	//主处理函数
	 public static String Manage(){
		String  result="{\"status\":\"0\",\"pageno\":\""+pageno+"\",\"pagesize\":"+"\""+pagesize+"\",\"totalnums\":\""+0+"\",\"time_rang\":\""+time_rang+"\",\"taskid\":\""+taskid+"\",\"data\":[]}";
		if(init_paras()){	
			init_limitstr();
			ConnectToDatabase.connect();	
			mysqlObject obj= new mysqlObject();
			MyLog.AddLog("web_server.log", "selecMacresult  before countsum use "+(System.currentTimeMillis()/1000-begin_st)+"secondes");
			countsum(obj);
			if(totals>0) {
				result=get_allMac_by_taskid(obj);
				result="{\"status\":\"0\",\"totalnums\":\""+totals+"\",\"time_rang\":\""+time_rang+"\",\"taskid\":\""+taskid+"\",\"data\":["+result+"]}";
			}
		}else{
			result="{\"status\":\"1\",\"totalnums\":\""+0+"\",\"time_rang\":\""+time_rang+"\",\"taskid\":\""+taskid+"\",\"data\":[]}";
		}
		 result=result.replace("[\"\"]","[]");
		 return result;
	}
	 
	
	
	/**
	 * 根据taskid获取该taskid下面所有mac
	 * */
	public static String get_allMac_by_taskid(mysqlObject obj){
		long st=System.currentTimeMillis()/1000;
		obj.clearObject();
		String macstr=null;
		macstr=getMac_nearly(Integer.parseInt(taskid),obj);
		MyLog.AddLog("web_server.log", "selecMacresult  getmac use "+(System.currentTimeMillis()/1000-st)+"secondes");
		return macstr;
	}
	
	
	//获取最近一次运行周期的Mac
	public static String getMac_nearly(int taskid,mysqlObject obj){
		String result="";
		String sql="";
		sql="select distinct(mac) from temp_mac where taskid="+taskid+" limit "+pageno*pagesize+","+pagesize;
		obj.clearObject();
		List<mysqlRow> mac_rows=obj.ExeSqlQuery(sql);
		if(mac_rows.size()>0){
			result=obj.toJosn(0, 100,0);
			result=result.split(",\"rows\"")[1];
			result=result.substring(2,result.length()-2);
			result=result.replace("\"mac\":","").replace("\"name\":","").replace("{","").replace("}","");
			Print.println("result=="+result);			
		}
		return result;
	}
	

	//求总数
 	public static void countsum(mysqlObject obj){
 		long st=System.currentTimeMillis()/1000;
 		String sql="";
 		if(totalnums.equals("0")||totalnums.equals("")||totalnums==null||totalnums.isEmpty()){
 			obj.clearObject();
			sql="select result_nums as count from taskcount where taskid="+taskid+"  order by day desc limit 1";
			List<mysqlRow> rows2=obj.ExeSqlQuery(sql);
			if(rows2.size()>0){
				String result2=obj.toJosn(0,10,10);	
				if(rows2.size()==1){
					String str=result2.replace("result_nums", "count").split("count")[1];
					totals= Integer.parseInt(str.substring(3,str.length()-4));
				}
			}			
 		}
 		MyLog.AddLog("web_server.log", "selecMacresult  countsum use "+(System.currentTimeMillis()/1000-st)+"secondes");
	}

 	//参数输入判断，只做判断，不做处理
 	public static boolean init_paras(){
 		if(TimeDate.isnum(taskid)) return true; 	
 		return false;
 	}
 	
 	//清空ENDRESULT
	public static void clear_EndResult(){		
		ENDRESULT=null;		
	}
 		
	//生成limitstr
	public static void init_limitstr(){
		limitstr="";
		//long begno=0;
		//long endno=0;
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   -1);
		String yesterday = new SimpleDateFormat( "yyyyMMdd").format(cal.getTime());
	
		if(TimeDate.isnum(page)) pageno=Integer.parseInt(page);
		if(TimeDate.isnum(limit)) pagesize=Integer.parseInt(limit);
		//begno=pageno*pagesize;
	
		if(TimeDate.isNormaltime_rang(time_rang)) {
			int[] time_arr=TimeDate.getStartday_Endday_by_timerang(time_rang);
			begday=time_arr[0];
			endday=time_arr[1];
		}else{
			begday=Integer.parseInt(yesterday);
			endday=Integer.parseInt(nowday);
		}	
	}
	
	
	
}
