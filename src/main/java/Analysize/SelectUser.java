package Analysize;

import java.util.List;

import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.ConnectToDatabase;
import servlet.baseServlet;

public class SelectUser  extends baseServlet{
	
	public String handle() {	
		 String result="{\"status\":\"1\"}";
		 ConnectToDatabase.connect();	
		 mysqlObject obj= new mysqlObject();
		 int count=count();
		 String sql="select distinct(userid) from taskname order by timeval desc";
		 System.out.println(sql);
		 List<mysqlRow> rows=obj.ExeSqlQuery(sql);	
		 if(rows.size()>0) result=obj.toJosn(0,count,count);
		 
	return result;
 }
	//获取记录数
	public int count(){
		int totalnums=0;		
		ConnectToDatabase.connect();	
		mysqlObject obj= new mysqlObject();		
		String sql2="select count(distinct(userid)) as count from taskname";	
		List<mysqlRow> rows=obj.ExeSqlQuery(sql2);
		String result2=obj.toJosn(0,10,10);	
		if(rows.size()==1){
			String str=result2.split("count")[1];
			totalnums= Integer.parseInt(str.substring(3,str.length()-4));
			//totalnums= type_trans.getLong(rows.get(0).collist.get(0));
		}	
		return totalnums;
	}
}
