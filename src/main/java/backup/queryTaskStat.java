package backup;

import java.util.List;

import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.ConnectToDatabase;
import Common.TASK_DEFINITION;
import net.sf.json.JSONObject;
import servlet.baseServlet;

public class queryTaskStat extends baseServlet{

	@Override
	public String handle() {
		String taskid=getObject("taskid");
		String res=getResult(taskid);		
		return res;
	}
	
	private String getResult(String taskid){
		String statusstr = TASK_DEFINITION.analyse_runstatus_fail;
		ConnectToDatabase.connect();	
		mysqlObject obj=new mysqlObject();
		String sql="select status from actual_taskname where taskid="+taskid;
		List<mysqlRow> list = obj.ExeSqlQuery(sql);
		if(list.size()>0){
			String josnstr=obj.toJosn(0, 1,100);
			JSONObject object  = JSONObject.fromObject(josnstr);	
			statusstr=object.getString("status");
			System.out.println("status=="+statusstr);
		}	
		String a="{\"status\":\""+statusstr+"\",\"taskid\":\""+taskid+"\"}";
		return a;
		
	}
	

}
