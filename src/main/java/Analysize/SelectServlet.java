package Analysize;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.proweb.common.type_trans;
import com.proweb.common.file.MyLog;
import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.ConnectToDatabase;
import Common.Print;
import Common.TimeDate;
import debug.isDebug;
import servlet.baseServlet;

public class SelectServlet extends baseServlet{
	
	     static long start_runtime=0;
		 static String taskname=null;
		 static String tasktype=null;		
		 static String status=null;	
	     static String limit=null;
	     static String page=null;
		 static long  pagesize=10;
		 static long  pageno=0;	 
		 static String user=null;
		 static String  totalnums=null;	  
		 static int  totals=0;	 
		 static String ENDRESULT=null;
		 static String limitstr=null;
		 static boolean isLocal=true;
		 static boolean local0_server1=true;
		 static String head_taskdesc=null;
		 static String taskid=null;
		 static int TASKID=0;
		 static long end_runtime=0;
	  
		//baseServlet实现方法，
		//基本查询从taskocunt表里面获取数据,(taskcount 每天随taskname，temp_mac表数据自动更新，temp_mac存放每天分析程序产生的Mac，由定时清除程序每天更新)
		@Override
	    public String handle() {
			start_runtime=System.currentTimeMillis()/1000;
			isLocal=isDebug.islocal;
			taskid=getObject("taskid");
			user=getObject("user");
			taskname=getObject("taskname");
			tasktype=getObject("tasktype");		
			status=getObject("status");
			limit=getObject("limit");
			page=getObject("page");
			totalnums=getObject("totalnums");
			try {
				ENDRESULT=Manage();
			} catch (NumberFormatException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}			
			//将utf-8转成iso8859
			//ENDRESULT=EncodeSet.utf8_to_iso885915(ENDRESULT);
			end_runtime=System.currentTimeMillis()/1000;
			if(isLocal==false) MyLog.AddNormalLog("web_server.log","selectservlet","\r\n 共用时 "+(end_runtime-start_runtime)+" 秒");
			 //MyLog.AddLog("/data/logs/web_server.log","\r\n  共用时 "+(end_runtime-start_runtime)+" 秒");
			return ENDRESULT;
		}
		
		
		//主处理程序
		public static String Manage() throws NumberFormatException, UnsupportedEncodingException{
			//String taskid_str="";
			String result="";
			limitstr=init_limitstr();
			ConnectToDatabase.connect();	
			mysqlObject obj=new mysqlObject();
			getTotals(obj);
			result=GetResult(obj);
			String finalresult=Product_ENDRESULT(result);
			ENDRESULT=finalresult;
           return ENDRESULT; 
		}
		
		//ENDRESULT拼接
	    public static String Product_ENDRESULT(String resultstr){
		    clear_EndResult();
			String rows_value_str=resultstr;
		    String beg_ENDRESULT="{\"status\":\""+0+"\",\"pageno\":\""+pageno+"\",\"pgaesize\":\""+pagesize+"\",\"totalrows\":\""+totals+"\",\"rows\":["+rows_value_str+"]}";
		    return beg_ENDRESULT;
		}
		
		//输入taskid字符串，返回结果集
		public static String GetResult(mysqlObject obj) throws NumberFormatException, UnsupportedEncodingException{
			obj.clearObject();
			String result="";			
		    String  sql="select * from taskcount "+limitstr;
		    if(taskid==null||taskid.isEmpty()||taskid.equals("0")||taskid=="0") sql+=" and day=(select max(day) from taskcount);";
		    else{
		    	long begno=pageno*pagesize;
		    	sql+=" limit "+begno+","+pagesize;
		    } 		    
		    List<mysqlRow> rows=obj.ExeSqlQuery(sql);	
			if(rows.size()>0){	
				result=obj.toJosn((int)pageno,(int)pagesize,totals);	
				result=result.split(",\"rows\"")[1];
				result=result.substring(2,result.length()-2);
				int taskname_index=result.indexOf("taskname");
		        int tasktype_index=result.indexOf("tasktype");
				String str2=result.substring(taskname_index+11,tasktype_index-3);
				result=result.replace(str2, "");
				Print.println("result=="+result);
			}
			return result;
		}
		
		

        //生成limitstr
        public static String init_limitstr(){
        	limitstr=" ";
			limitstr="  where status!=1000  and status!=1 ";
			if(taskid==null||taskid.isEmpty()) taskid="0";
			if(TimeDate.isnum(taskid)){
				TASKID=Integer.parseInt(taskid);
				if(Integer.parseInt(taskid)>0) limitstr+=" and taskid="+"\'"+TASKID+"\'";
			}
			if(TimeDate.isnum(tasktype))  limitstr+=" and tasktype="+tasktype;
	        if(TimeDate.isnum(status)) limitstr+=" and status='"+status+"'";	
	        if(TimeDate.isnum(user)) 	limitstr+=" and userid="+user;			
	       
	        if(TimeDate.isnum(page)) pageno=type_trans.getLong(page);
			if(TimeDate.isnum(limit)) pagesize=type_trans.getLong(limit);
			Print.println("输出limitstr "+limitstr);
			return limitstr;
		}
		
        //求总数
        public static void getTotals(mysqlObject obj){
			obj.clearObject();
			if(totalnums.equals("0")||totalnums==null||totalnums.isEmpty()){
				List<mysqlRow> rows = obj.ExeSqlQuery("select count(*) as count from taskcount "+limitstr);
				if(rows.size()>0){
					String result2=obj.toJosn(0,7,0).split("count")[1].split("}")[0].substring(2).replace("\"", "");	
				    totals= Integer.parseInt(result2);
				}			
			}
		}
		
        //清除ENDRESULT
        public static void clear_EndResult(){
			 ENDRESULT=null;		
		}
		
	}

