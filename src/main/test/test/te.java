/**
 * 
 */
package test;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.proweb.common.file.MyLog;
import com.proweb.job.libObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import Common.Directory_File;
import Common.Print;
import Common.REDIS;
import Common.TASK_DEFINITION;
import Model.TaskName;
import debug.isDebug;
import net.sf.json.*;
import redis.clients.jedis.Jedis;

public class te {
	 
	public static void main(String[] args){
		Jedis jedis=REDIS.jedis;
		Iterator keys=REDIS.keys_iterator;
	   
	    while(keys.hasNext()){
	      Object key = keys.next();
	      if(key!=null){
	    	  System.out.println(key+" "+jedis.get(key.toString()));
	      }		      
	    }
	}
  
}
