package Analysize;

import java.text.ParseException;
import java.util.Map;

import com.proweb.common.timeop;
import com.proweb.mysqlobject.mysqlObject;

import Common.ConnectToDatabase;
import Common.Task_result_Judge_new;
import Common.TimeDate;
import debug.isDebug;
import servlet.baseServlet;

public class GetObjFreDetail  extends baseServlet {
	static String attr=null;
	static String taskid=null;
	static String time_rang=null;
	static String mac=null;
	static int pageno=0;
	static int pagesize=15;
	static String limit=null;
	static String page=null;
	static String totalnums="0";
	static int totals=0;
	static String nowday=timeop.getDayFromTime(timeop.GetCurrentTime()).replace("-","");		
	static String begday=TimeDate.getYesterday();
	static String endday=TimeDate.getToday();	
	static boolean isLocal=true;
	static String ENDRESULT=null;
	static String limitstr=null;
	static int TOTALS=0;
	static String teststr=null;
	
	@Override
	public String handle() {
		isLocal=isDebug.islocal;
		taskid=getObject("taskid");
		time_rang=getObject("time_rang");	
		mac=getObject("name");
		limit=getObject("limit");
		page=getObject("page");
		totalnums=getObject("totalnums");	
		try {
			ENDRESULT=Manage();
		} catch (NumberFormatException | ParseException e) {
			e.printStackTrace();
		}		
		return ENDRESULT;
	}
	
	//主处理函数
	 public static String Manage() throws NumberFormatException, ParseException{
		String  result="{\"status\":\"0\",\"pageno\":\""+pageno+"\",\"pagesize\":"+"\""+pagesize+"\",\"totalnums\":\""+0+"\",\"result\":[]}";
		if(init_paras()){	
			init_limitstr();
			ConnectToDatabase.connect();	
			mysqlObject obj= new mysqlObject();
			Map<String,String> judge_table_map=Task_result_Judge_new.judge_ownedtable_bytaskid(obj,taskid);
			String tablename="";
			String mac_columnname="";
			if(judge_table_map.keySet().size()>0) tablename=judge_table_map.keySet().toArray()[0].toString();
			if(!tablename.isEmpty())  mac_columnname=judge_table_map.get(tablename).toString();
			if(tablename.equalsIgnoreCase("fellow;fellow_ad")) tablename="fellow";
			if(tablename.equalsIgnoreCase("permanent_ad;permanent")) tablename="permanent_ad";
			if(tablename.contains("vip")) tablename="vip_svc";
			//String time_str="";
			String day_pinci_str=Task_result_Judge_new.get_daysvcpinci_by_taskidmac(Integer.parseInt(taskid), mac, tablename, mac_columnname, pageno,pagesize, obj, 100000,time_rang,isLocal);
			int nums=0;
			if(day_pinci_str.length()>0&&day_pinci_str.contains("day")) nums=day_pinci_str.split("day").length-1;
			if(day_pinci_str.length()>0&&!day_pinci_str.contains("day"))nums=1;
			day_pinci_str=Task_result_Judge_new.getdata_fromMtoN_likepage_instr(day_pinci_str.replace("},{","}={"),pageno,pagesize);
			result="{\"status\":\"0\",\"totalnums\":\""+nums+"\",\"result\":["+day_pinci_str+"]}";
			
		}else{
			result="{\"status\":\"1\",\"totalnums\":\""+0+"\",\"result\":[]}";
		}
		 return result;
	}
	
 	//参数输入判断，只做判断，不做处理
 	public static boolean init_paras(){
 		if(TimeDate.isnum(taskid)&&mac!=null&&(!mac.isEmpty())) return true;
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
		if(TimeDate.isnum(page)) pageno=Integer.parseInt(page);
		if(TimeDate.isnum(limit)) pagesize=Integer.parseInt(limit);
		/*begno=pageno*pagesize;
	    */		
		
		/*if(TimeDate.isNormaltime_rang(time_rang)) {
			int[] time_arr=TimeDate.getStartday_Endday_by_timerang(time_rang);
			begday=time_arr[0];
			endday=time_arr[1];
		}	*/		
		//if(TimeDate.isNormaltime_rang(time_rang)) limitstr =" and day>="+begday+"  and  day<"+endday;			
	}
}
