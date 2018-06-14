package Analysize;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.proweb.common.timeop;
import com.proweb.common.type_trans;
import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.ConnectToDatabase;
import Common.Print;
import Common.Task_result_Judge_new;
import Common.TimeDate;
import servlet.baseServlet;

public class TasklogresultSelect extends baseServlet {
	
	static String[] paras=new String[4];
	static String nowday=timeop.getDayFromTime(timeop.GetCurrentTime()).replace("-","");
	static String yesterday=null;
    static String taskid=null;
    static String time_rang=null;
    static int TASKID=0;	
    static String limit=null;
    static String page=null;
    static String  totalnums=null;
	static long  pagesize=10;
    static long  pageno=0;	 	
    static long totalrows=0;
    static int  totals=0;	
    static int begday=0;
    static int endday=0;
    static String ENDRESULT=null;
    static String limitstr=null;
	@Override
	public String handle() {
		taskid=getObject("taskid");
		limit=getObject("limit");
		page=getObject("page");		
		totalnums=getObject("totalnums");
		time_rang=getObject("time_rang");
		ConnectToDatabase.connect();	
		mysqlObject sqlobj= new mysqlObject();		
		try {
			init();
			ENDRESULT=manage(sqlobj);
		} catch (ParseException e) {
			e.printStackTrace();
		}				
		return ENDRESULT;
	}
	
	//参数处理及查询结果数
	public static String manage(mysqlObject obj) throws ParseException{
        String res="";
        res=get_resultnums_by_taskid(taskid,time_rang,obj);
		totalrows=res.split("day").length-1;
		res=res.replace("},{","}={");
		res=Task_result_Judge_new.getdata_fromMtoN_likepage_instr(res,(int)pageno,(int)pagesize);
		if(res.endsWith(",")) res=res.substring(0,res.length()-1);
		res="{\"status\":\"0\",\"totalnums\":\""+totalrows+"\",\"data\":["+res+"]}";  
		Print.println("输出result "+res);		
		return res;
	}
	
	
	/** 根据taskid,时间范围查询去重后的Mac数或者组数，
	 * 返回格式：{"taskid":"165","day":"20171008","result_nums":"1987"}
	 * 时间范围为空只查询最近的一条记录
	 * @throws ParseException 
	 *  */
    public static String  get_resultnums_by_taskid(String task_id,String time,mysqlObject obj) throws ParseException{
		 obj.clearObject();
		 String res="";
		 if(TimeDate.isnum(taskid)){
            List<Long> daylist=new ArrayList<Long>();
            if(TimeDate.isNormaltime_rang(time_rang)) daylist=TimeDate.getBetweenDates(time_rang);
            else daylist.add(Long.parseLong(yesterday));
			for(int i=0;i<daylist.size();i++){
			   String day=daylist.get(i).toString();
			   String sql="select result_nums from taskcount where taskid="+taskid+" and day="+day;
			   List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(sql);
			   if(selec_reusltnums_sql_rows.size()>0){
				  String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
				  result=result.substring(2,result.length()-2).replace("\"result_nums\":", "").replace("{", "").replace("}", "").replace("\"", "");
				  res+="{\"taskid\":\""+taskid+"\",\"day\":\""+day+"\",\"result_nums\":\""+result+"\"},";						
			  }
			}           
		 }else{		
		    List<String> taskid_list=Task_result_Judge_new.get_all_taskid(obj);
		    obj.clearObject();
		    for(int i=0;i<taskid_list.size();i++){	
		    	String taskid=taskid_list.get(i).toString();
		    	String sql="select result_nums from taskcount where taskid="+taskid+" and day="+nowday;
		    	List<mysqlRow> selec_reusltnums_sql_rows = obj.ExeSqlQuery(sql);
			    if(selec_reusltnums_sql_rows.size()>0){
				  String result=obj.toJosn(0,5,10).split(",\"rows\"")[1];
				  result=result.substring(2,result.length()-2).replace("\"result_nums\":", "").replace("taskid:","").replace("{", "").replace("}", "").replace("\"", "");
				  res+="{\"taskid\":\""+taskid+"\",\"day\":\""+yesterday+"\",\"result_nums\":\""+result+"\"},";						
			    }
		    }
		}	
	    if(res.endsWith(",")) res=res.substring(0,res.length()-1);    	
		return res;
  	} 
    
    //初始化
    public static void init(){
    	Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   -1);
		yesterday = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		if(TimeDate.isnum(page)) pageno=type_trans.getLong(page);
		if(TimeDate.isnum(limit)) pagesize=type_trans.getLong(limit);	
		if(TimeDate.isNormaltime_rang(time_rang)) {
			int[] time_arr=TimeDate.getStartday_Endday_by_timerang(time_rang);
			begday=time_arr[0];
			endday=time_arr[1];
		}		
		//long begno=pageno*pagesize;
    }
    
}
