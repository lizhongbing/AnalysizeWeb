/**
 * 
 */
package Common;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import debug.isDebug;

public class HDFS {
	public static FileSystem dfs;
	public static Configuration conf;
		
	//windows环境下需要初始化配置文件，集群模式下不需要
	public static void dfs_init(){
		System.setProperty("HADOOP_USER_NAME","root");	
		if(isDebug.islocal){
			Configuration.addDefaultResource("src/main/resources/core-site.xml");
			Configuration.addDefaultResource("src/main/resources/hdfs-site.xml");
		}
		conf=new Configuration();		
		try {
			dfs=FileSystem.get(conf);
			System.out.println("=====hdfs连接初始化成功=====");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
