/**
 * 
 */
package Common;

import java.util.Set;

import com.proweb.common.conf.confParam;
import com.proweb.common.conf.proProperties;
import com.proweb.common.file.MyLog;

import debug.isDebug;
import redis.clients.jedis.Jedis;

public class REDIS {
    
	public static Jedis jedis ;
	public static Set<String> keys_set ;
	public static String redis_host;
	public static String redis_port;
	public static String str="";
	public static long savetime=1000*60*5;  //save 5 minutes
	
	public static void redis_init(){
		String cnfname=null;
		if(isDebug.islocal) cnfname = Constant.LOCAL_DEBUG_DIR + "task-analyzer.xml";
		else{
			proProperties.InitDbFile(Constant.SERVER_DB_PROPERTIES_PATH);
			cnfname=System.getenv("HADOOP_HOME") + Constant.HADOOP_TASKANALYZER_PATH;
		}
		MyLog.AddLog("actual_data_analyzer.log","cnfname=="+cnfname);
	   	proProperties.InitDbFile(cnfname);
	   	redis_host=confParam.GetComConfValue(cnfname,"redis","redis.host");
	   	redis_port=confParam.GetComConfValue(cnfname,"redis","redis.port");	
	   	if(redis_host!=null&&redis_host.length()>0&&redis_port!=null&&redis_port.length()>0) 
	   	jedis = new Jedis(redis_host,Integer.parseInt(redis_port));
	    System.out.println("======redis初始化成功=======");
	}
	
    public static void getRedis(){  
    	 try{  
 	 	 keys_set=jedis.keys("*"); 	 	  
 	  }catch(Exception e){
 	    	 System.out.println("==get redis keys fail=="+e.toString());
 	    	 MyLog.AddLog("redis.log"," get redis keys exception："+e.toString()+ "\r\n  redis_host="+redis_host+" redis_port="+redis_port);
 	   }
    }
   
    
}
