/**
 * 
 */
package AnalysizeOntime;

import com.proweb.mysqlobject.mysqlObject;

import servlet.baseServlet;

public class TestServlet extends baseServlet{

	
	@Override
	public String handle() {
		System.out.println(999);
	
		
//		List<mysqlRow> rows = obj.ExeSqlQuery("select * from actual_taskname");
//		if(rows.size() > 0){
//			out = obj.toJosn(0,10,10);//QueryJsonParse<ActualTaskName>
//			QueryJsonParse<ActualTaskName> bean = JSON.parseObject(out, new TypeReference<QueryJsonParse<ActualTaskName>>(){});
//			ArrayList<ActualTaskName> rows2 = bean.getRows();
//			for (ActualTaskName actualTaskName : rows2) {
//				System.out.println("taskid === " + actualTaskName.getTaskid());
//			}
//		}
		
		long startTime = System.currentTimeMillis()/1000;
		System.out.println("startTime==="+startTime);
		
		long id = startTime;
		String sqlString = "insert into actual_taskname(taskid,taskname,tasktype,taskdesc,taskmd5,status,userid,timeval,lable,content) values("+id+",\"\",9,\"afasdfasfafa\",\"\",0,0,0,\"\",\"\")";
		boolean b = mysqlObject.ExeSql(sqlString);
		
		long endTime = System.currentTimeMillis()/1000;
		System.out.println("endTime==="+endTime);
		
		System.out.println("b==="+b);
		
		
		return sqlString + "===" + b;
	}

}
