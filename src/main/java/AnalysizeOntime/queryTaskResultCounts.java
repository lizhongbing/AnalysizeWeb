/**
 * 
 */
package AnalysizeOntime;

import java.util.List;

import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.Table_Task;
import Common.TimeDate;
import servlet.baseServlet;

public class queryTaskResultCounts   extends baseServlet {
	
	static String[] database_paras=new String[4];	
	static String taskid=null;	
    static long TASKID=0;
    static String result;
	static int maccount=0;
    static int total=0;
	@Override
	public String handle() {
		taskid=getObject("taskid");	
		result="{\"total\":\""+total+"\",\"data\":[{\"taskid\":\""+taskid+"\",\"maccount\":\"0\"}]}";
		if(taskid != null && taskid.length()>0){
			result = getMaccount();	
		}
		return result;		
	}
	
	public static String getMaccount(){
		mysqlObject sqlobj=new mysqlObject();;
		sqlobj.clearObject();
		int count=0;
		String[] arr=taskid.split(",");
		String datastr="";
		for(String taskidstr:arr){
			if(!TimeDate.isnum(taskidstr)) return result;
			String tasktype=Table_Task.getActual_Tasktype_by_taskid(sqlobj,taskidstr.trim());
			if(tasktype!=null&&tasktype.length()>0){
				String tablename=Table_Task.getActual_taskname_by_tasktype(tasktype);
				String tmp=getMacNums_by_taskid(sqlobj,tablename,"mac",taskidstr);
				datastr+=tmp+",";		
				count+=1;
			}
		}
		if(datastr.endsWith(",")) datastr=datastr.substring(0,datastr.length()-1);
		return "{\"total\":\""+count+"\",\"data\":["+datastr+"]}";
	}
	
	
	/**
	 * 跟据taskid，tasktype查找去重Mac总数
	 */
	public static String getMacNums_by_taskid(mysqlObject sqlobj,String tablename,String mac_columnname,String taskid){
		sqlobj.clearObject();
		String count="0";
		String sql="select count(distinct("+mac_columnname+")) as count from "+tablename+" where taskid="+taskid;
		System.out.println(sql);
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0,10,10);				 
			 String tmp=result.split("\"rows\"")[1];
			 count=tmp.replace("\"count\":","").replace("\"","").replace(":[{","").replace("}]}","");
		}
		String res="{\"taskid\":\""+taskid+"\",\"maccount\":\""+count+"\"}";
		return res;
		
	}
	
	
}
