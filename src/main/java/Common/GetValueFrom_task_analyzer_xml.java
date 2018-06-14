package Common;

import java.util.ArrayList;
import java.util.List;

import com.proweb.common.conf.confParam;
import com.proweb.common.conf.proProperties;

public class GetValueFrom_task_analyzer_xml {
	
	//获取高危中的高危时间23:00,和5:00
	public static List<String> getAlterTime_from_task_xml(boolean islocal){
		String path="";
		String firtime="23:00";
		String entime="5:00";
		if(islocal)	 path = Constant.LOCAL_DEBUG_DIR + "task-analyzer.xml";
		else {
			proProperties.InitDbFile(Constant.SERVER_DB_PROPERTIES_PATH);
			path=System.getenv("HADOOP_HOME") + Constant.HADOOP_TASKANALYZER_PATH;
			Print.println(path);
			String altertime=confParam.GetComConfValue(path,"probd.task.type.highrisk","highrisk.altertime");		
			String arrtime[]=altertime.split(":");
			if((altertime!=null)&&(altertime.length()>0)&&(arrtime.length==3)){
				firtime=arrtime[0]+":00";
				entime=arrtime[arrtime.length-1]+":00";
			}
		}
		//加载23:24,0:5，必须是这种格式，有两个冒号，一个逗号		
		List<String> timelist=new ArrayList<String>();
		if(firtime.length()>0) timelist.add(firtime);
		if(entime.length()>0) timelist.add(entime);		
		return timelist;
	}
  
	//获取task-analyzer.xml中的 高危输出条数限制
	public static int get_danger_outlimit_from_task_xml(boolean islocal){
		String path="";
		int outlimit=1000;
		if(islocal)	 path = Constant.LOCAL_DEBUG_DIR + "task-analyzer.xml";
		else {
			proProperties.InitDbFile(Constant.SERVER_DB_PROPERTIES_PATH);
			path=System.getenv("HADOOP_HOME") + Constant.HADOOP_TASKANALYZER_PATH;
			Print.println(path);
			String out_limit=confParam.GetComConfValue(path,"probd.task.type.highrisk","highrisk.out.limit");		
			if((out_limit!=null)&&(out_limit.length()>0)){
				outlimit=Integer.parseInt(out_limit);
			}
		}
		return outlimit;
	}
	
}
