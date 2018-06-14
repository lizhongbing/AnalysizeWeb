package Common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.proweb.common.file.MyLog;
import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

public class Table_Task {
	
	/** 
	 * 查询某张表中所有的去重taskid,以set形式返回
	 */
	public static List<String> get_taskid_by_limitstr(boolean islocal,String tablename,String limitstr){
		//String[] taskid_list=null;
		List<String> taskid_list  = new ArrayList<String>();
		ConnectToDatabase.connect();	
		mysqlObject obj=new mysqlObject();
		String sql="select distinct(taskid) from "+tablename+limitstr;
		Print.println(sql);
		List<mysqlRow> rows2=obj.ExeSqlQuery(sql);
		String result2=obj.toJosn(0,(int)get_totalnums_intable(islocal,tablename),0);
		String taskidstr="";
		if(rows2.size()>0&&(!rows2.equals("[]"))){
			result2=result2.split(",\"rows\"")[1];
			result2=result2.substring(2,result2.length()-2);
			taskidstr=result2.replace("\"taskid\":","").replace("\"","").replace("{","").replace("}","");		
		}
		if(taskidstr.length()>0){
			if(taskidstr.contains(",")){
				String[] taskid_arr=taskidstr.split(",");
				for(int i=0;i<taskid_arr.length;i++){
					taskid_list.add(taskid_arr[i]);
				}				 
			}
			else taskid_list.add(taskidstr);
		}
		return taskid_list;
	}
	
	/**
	 * 根据taskid查询某张表中所有的去重字段A,以list形式返回
	 */
	public static List<String> get_ditinc_pointcoulumn(mysqlObject sqlobj,String tablename,String columnname,String taskid){
		//String[] taskid_list=null;
		sqlobj.clearObject();
		List<String> taskid_list  = new ArrayList<String>();
		String taskidstr="";
		String sql="select distinct("+columnname+") from "+tablename +" where taskid="+taskid;
		System.out.println(sql);
		MyLog.AddLog("actual_data_analyzer.log", "select mac sql="+sql);
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		if(rows.size()>0&&(!rows.toString().equals("[]"))){
			 String result=sqlobj.toJosn(0,10,10);	
			 String tmp=result.split(",\"rows\"")[1];
			 taskidstr=tmp.substring(2,tmp.length()-2).replace("\""+columnname+"\":","").replace("\"","").replace("{","").replace("}","");
		}
	
		if(taskidstr.length()>0){
			if(taskidstr.contains(",")){
				String[] taskid_arr=taskidstr.split(",");
				for(int i=0;i<taskid_arr.length;i++){
					taskid_list.add(taskid_arr[i]);
				}				 
			}
			else taskid_list.add(taskidstr);
		}
		return taskid_list;
	}
	
	/**
	 * 查看某张表记录总数,即所有记录
	 */
	public static long get_totalnums_intable(boolean islocal,String tablename){
		long nums=0;
		ConnectToDatabase.connect();	
		mysqlObject sqlobj=new mysqlObject();
		String sql="select count(*) as count from "+tablename;
		 List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		 if(rows.size()>0){			
			 String result=sqlobj.toJosn(0,10,10);	
			 String str=result.split("count")[1];
			 int count= Integer.parseInt(str.substring(3,str.length()-4));
			 System.out.println("count "+count);
			 if(count>0){
				 nums=count;
			 }
		}
		return nums;
	}
	
	/**
	 * 查看某张表中列值为value的记录总数
	 */
	public static long get_toalNums_by_columnvalue(boolean islocal,String tablename,String column_name,String column_value){
		int nums=0;
		ConnectToDatabase.connect();	
		mysqlObject sqlobj=new mysqlObject();
		String sql="select count(*) as count from "+tablename+" where "+column_name+"=\""+column_value+"\"";
		if(tablename.equals("taskname")) sql+=" and status not in(2,3,1000)";
		 List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		 if(rows.size()>0){
			 String result=sqlobj.toJosn(0,10,10);	
			 String str=result.split("count")[1];
			 int count= Integer.parseInt(str.substring(3,str.length()-4));
			 System.out.println("count "+count);
			 if(count>0){
				 nums=count;
			 } 
		 }		
		return nums;
	}
	
	/**
	 * 查看某张表中某一列是否存在某值 
	 */
	public static boolean exists(boolean islocal,String tablename,String column_name,String column_value){		
		boolean flag=false;
		ConnectToDatabase.connect();	
		mysqlObject sqlobj=new mysqlObject();
		String sql="select count(*) as count from "+tablename+" where "+column_name+"=\""+column_value+"\" and status not in(2,3)";
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		if(rows.size()>0){
			String result=sqlobj.toJosn(0,10,10);	
			 String str=result.split("count")[1];
			 int count= Integer.parseInt(str.substring(3,str.length()-4));
			 if(count>0){
				 flag=true;
			 }
		}		
		return flag;
	}
	
	
	/**
	 * 查询一个表里面某一列(整型)的最大值,可以传入id查询最近的一个id值
	 */
	public static int selectMaxid(boolean islocal,String id,String tablename){
		 int taskid=0;
		 String sql2="select max("+id+") as id from "+tablename;
		 ConnectToDatabase.connect();	
		 mysqlObject sqlobj2=new mysqlObject();
		 List<mysqlRow>	rows=sqlobj2.ExeSqlQuery(sql2);
		 if(rows.size()>=1){			
			 String result=sqlobj2.toJosn(0,10,10);	
			 String str=result.split("id")[1];
			 taskid= Integer.parseInt(str.substring(3,str.length()-4));
		 } 
		return taskid;
	 }
	
	/**
	 * 将任务类型HighRisk，Permanent，Liveness，Follow转换成数字  12,13,14,3
	 */
	public  static int Converttype_wordsto_num(String typewords){
		int tasktype=0;
		switch(typewords){
			case "Fellow":				
				tasktype=16;
				break;
			case "Permanent":
				tasktype=12;
				break;
			case "HighRisk":
				tasktype=13;
				break;
			case "Active":
				tasktype=14;
				break;
			default:
				tasktype=13;
				break;
		}
		return tasktype;		
	}
	
	
	/**
	 * 将数字任务类型12,13,14,3  转换成单词 HighRisk，Permanent，Liveness，Follow
	 */
	public  static String Converttype_numto_words(int typenum){
		String tasktype="";
		switch(typenum){
			case 16:				
				tasktype="Fellow";
				break;
			case 12:
				tasktype="Permanent";
				break;
			case 13:
				tasktype="HighRisk";
				break;
			case 14:
				tasktype="Active";
				break;
			default:
				tasktype="HighRisk";
				break;
		}
		return tasktype;
		
	}
	
	/**
	 * 根据taskid查找任务类型
	 */
	public static String getActual_Tasktype_by_taskid(mysqlObject sqlobj,String taskid){
		sqlobj.clearObject();
		String tasktype="";
		String sql="select tasktype from actual_taskname where taskid="+taskid;
		System.out.println(sql);
		List<mysqlRow>	rows=sqlobj.ExeSqlQuery(sql);
		if(rows.size()>0){
			 String result=sqlobj.toJosn(0,10,10);				 
			 String tmp=result.split("\"rows\"")[1];
			 tasktype=tmp.replace("\"tasktype\":","").replace("\"","").replace(":[{","").replace("}]}","");
		}
		return tasktype;		
	}
	
	/**
	 * get mac's hbase lable
	 */
	public static String getLableFromHbase_byMac(Table table,String hbaseTableName,String mac,String family,String qualifieres,String value){
	    String lable="";
	    Get g = new Get(Bytes.toBytes(mac));
	    try {
			if(table.exists(g)) {
			  Result res = table.get(g);
			  if(qualifieres.contains(",")){
			    String[] quaarr=qualifieres.split(",");
			    for(String qulifier: quaarr){
			      String strvalue = Bytes.toString(res.getValue(Bytes.toBytes(family), Bytes.toBytes(qulifier)));
			      if(strvalue!=null){
			        if(strvalue.equals(value)&&(!lable.contains(qulifier))) lable+=qulifier+";";
			      }
			    }
			  }
			  if(lable.endsWith(";"))lable=lable.substring(0,lable.length()-1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return lable;
	  }
		
	/**
	 * 根据任务类型查找实时表名
	 */
	public static String getActual_taskname_by_tasktype(String tasktype){
		String taskname=TASK_DEFINITION.tablename_trace_pegging;
		switch(tasktype){
			case "1":
				taskname=TASK_DEFINITION.tablename_spacetime;
				break;
			case "9":
				taskname=TASK_DEFINITION.tablename_trace_pegging;
				break;			
		}		
		return taskname;
	}
}
