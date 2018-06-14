/**
 * 
 */
package Common;

import com.proweb.common.conf.confParam;

import debug.isDebug;

public class getTaskDataSource {
	
	/**
	 * 获取分析数据源
	 */
	public static String getDataSource(){
		String value=confParam.GetComConfValue(getTask_analyzer_xml(),"probd.TaskDataSource","TaskDataSource");
		if(value.length()>0&&(value!=null))return value;
		else return null;
	}
	
	public static String getTask_analyzer_xml(){
		String path=null;
		if(isDebug.islocal) path = Constant.LOCAL_DEBUG_DIR + "task-analyzer.xml";
		else path=System.getenv("HADOOP_HOME") + Constant.HADOOP_TASKANALYZER_PATH;		
		return path;
	}
}
