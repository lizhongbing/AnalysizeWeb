/**
 * 
 */
package Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proweb.common.conf.confParam;
import com.proweb.common.conf.proProperties;

import debug.isDebug;

public class ZookeeperMethod {
   
	/**
	 * 获取zookeeper配置节点及对应端口，返回List(Map(Probd01,2181),Map(Probd02,2181),Map(Probd03,2181))
	 */
	public static List<Map<String,Integer>> get_zk_quorum(){
		Map<String,Integer> zk_quorum_map  =new HashMap<String,Integer>();
		List<Map<String,Integer>> zk_nodes=new ArrayList<Map<String,Integer>>();
		String file="";
		if(isDebug.islocal){
			 file = Constant.LOCAL_DEBUG_DIR + "task-analyzer.xml";			 
		}else{
			proProperties.InitDbFile(Constant.SERVER_DB_PROPERTIES_PATH);
			file=System.getenv("HADOOP_HOME") + Constant.HADOOP_TASKANALYZER_PATH;
		}
		if(file!=null&&file.length()>0){
			 String quorums=confParam.GetComConfValue(file,"ha.zookeeper.quorum","zkquorum");
			 if(quorums.contains(",")){
				 String[] list=quorums.split(",");
				 if(list!=null){
					 for(int i=0;i<list.length;i++){
						 String str=list[i];
						 if(str.contains(":")){
							 String name=str.split(":")[0];
							 int port=Integer.parseInt(str.split(":")[1]);
							 if(!zk_quorum_map.containsKey(name)){
								 zk_quorum_map.put(name,port);								 
							 }
						 } 
					 }
				 }
			 }
		}		
		if(zk_quorum_map.size()>0)	zk_nodes.add(zk_quorum_map);
		
		return zk_nodes;
	}
	
}
