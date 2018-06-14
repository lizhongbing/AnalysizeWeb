/**
 * 
 */
package Common;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;

import com.proweb.common.conf.proProperties;
import com.proweb.common.file.MyLog;



public class DatabaseParas {
	
	//public  static String [] database_paras=new String[4];	
	public  static HTable hbasetable=null;
	public  static String hbase_qualifieres="danger,permanent,work";
	
	@SuppressWarnings("deprecation")
	public static void init(){
		ConnectToDatabase.connect();
		System.out.println("======mysql连接初始化成功========");
		
		String cnfname=null;
		if(debug.isDebug.islocal) cnfname = Constant.LOCAL_DEBUG_DIR + "db.properties";
		else{
			proProperties.InitDbFile(Constant.SERVER_DB_PROPERTIES_PATH);
			cnfname=System.getenv("HADOOP_HOME") + Constant.HADOOP_TASKANALYZER_PATH;
		}
	   	proProperties.InitDbFile(cnfname);
		Configuration hbaseConf = HBaseConfiguration.create();
        String quorum = proProperties.getValue("quorum");
        String zkPort = proProperties.getValue("HBaseport");
        hbaseConf.set(TableInputFormat.INPUT_TABLE, "pro_portrait");
        hbaseConf.set("hbase.zookeeper.quorum", quorum);
        hbaseConf.set("hbase.zookeeper.property.clientPort", zkPort);
        
        System.out.println("quorum=="+quorum+" zkPort="+zkPort);
        try {
        	hbasetable=new HTable(hbaseConf, "pro_portrait");
			System.out.println("hbasetable=="+hbasetable);
			MyLog.AddLog("actual_data_analyzer.log"," hbasetable=="+hbasetable+"");
		} catch (IOException e) {			
			e.printStackTrace();
		}
        hbase_qualifieres=TASK_DEFINITION.hbase__danger+","+TASK_DEFINITION.hbase_permanent+","+TASK_DEFINITION.hbase_work;
        System.out.println("======hbase连接初始化成功========");
	}
	
	
}
