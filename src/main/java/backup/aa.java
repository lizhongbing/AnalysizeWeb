/**
 * 
 */
package backup;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;

import com.proweb.common.conf.proProperties;
import com.proweb.common.file.MyLog;

import Common.Constant;
import Common.TASK_DEFINITION;


public class aa  {
	public  static Table hbasetable=null;
	public  static String hbase_qualifieres="danger,permanent,work";

    

    public static void main(String[] args){
		String cnfname=null;
		if(debug.isDebug.islocal){
			proProperties.InitDbFile(Constant.LOCAL_DEBUG_DIR + "db.properties");
			cnfname = Constant.LOCAL_DEBUG_DIR + "task-analyzer.xml";
		}
		else{
			proProperties.InitDbFile(Constant.SERVER_DB_PROPERTIES_PATH);
			cnfname=System.getenv("HADOOP_HOME") + Constant.HADOOP_TASKANALYZER_PATH;
		}
		MyLog.AddLog("actual_data_analyzer.log","cnfname=="+cnfname);
	   	/*pro.InitDbFile(cnfname);*/
		Configuration hbaseConf = HBaseConfiguration.create();
        String quorum = proProperties.getValue("quorum");
        String zkPort = proProperties.getValue("HBaseport");
        hbaseConf.set(TableInputFormat.INPUT_TABLE, "pro_portrait");
        hbaseConf.set("hbase.zookeeper.quorum", quorum);
        hbaseConf.set("hbase.zookeeper.property.clientPort", zkPort);
        Connection conn;		
        try {
        	conn = ConnectionFactory.createConnection(hbaseConf);
			hbasetable= conn.getTable(TableName.valueOf(TASK_DEFINITION.hbase_table_name));
		} catch (IOException e) {			
			e.printStackTrace();
		}
	   	
        String str=getLableFromHbase_byMac(hbasetable,"pro_portrait","mac0","info","danger","1");
        System.out.println(str);
	   	
	}
	 //get mac's hbase lable
	  public static String getLableFromHbase_byMac(Table hbasetable,String hbaseTableName,String mac,String family,String qualifieres,String value){
	    String lable="";
	    Get g = new Get(Bytes.toBytes(mac));
	    try {
			if(hbasetable.exists(g)) {
			  Result res = hbasetable.get(g);
			  if(qualifieres.contains(",")){
			    String[] quaarr=qualifieres.split(",");
			    for(String qulifier: quaarr){
			      String strvalue = Bytes.toString(res.getValue(Bytes.toBytes(family), Bytes.toBytes(qulifier)));
			      if(strvalue!=null){
			        if(strvalue.equals(value)&&(!lable.contains(qulifier))) lable+=qulifier+";";
			      }
			    }
			  }else{
				  String strvalue = Bytes.toString(res.getValue(Bytes.toBytes(family), Bytes.toBytes(qualifieres)));
			      if(strvalue!=null){
			        if(strvalue.equals(value)&&(!lable.contains(qualifieres))) lable+=qualifieres+";";
			      }
			  }
			  if(lable.endsWith(";"))lable=lable.substring(0,lable.length()-1);
			}
		} catch (IOException e) {
			MyLog.AddLog("actual_data_analyzer.log","habse get label error=="+e.toString());
		}
	    return lable;
	}
}
