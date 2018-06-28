package datamanage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import com.proweb.common.file.MyLog;
import com.proweb.job.libObject;

import Common.Constant;
import Model.task_spacetime_lib;
import Model.task_tracepegging_lib;

public class AnalysizeDataDiskManager {
	
	/**磁盘文件的文件夹路径*/
	private static final String DATA_DIR_PATH = Constant.ANALYSIZE_DATA_CACHE_PATH;
	/**清除文件的超时时间(10分钟)*/
	private static final int TIME_FILE_TIMEOUT = 10*60*1000;
	
	
	/**
	 * 清除文件夹里过时的文件
	 */
	public static void clearAnalysizeDataFile(){
		long currentTime = System.currentTimeMillis();
		File file = new File(DATA_DIR_PATH);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length != 0) {
	            	for (File file2 : files) {
                    if (file2.isFile()) {
                    		long lastModifyTime = file2.lastModified();
                    		if(currentTime - lastModifyTime >= TIME_FILE_TIMEOUT){
                    			file2.delete();
                    		}
                    }
	             }
	         }
        }
	}
	
	/**
	 * 保存任务结果到磁盘文件
	 * @param taskid
	 */
	public static void saveDataToDisk(String taskid){
		MyLog.AddLog("actual_data_analyzer.log","step===saveDataToDisk");
		HashMap<String, ArrayList<libObject>> job = AnalysizeDataCacheManager.getJobByTaskid(taskid);
		Set<Entry<String, ArrayList<libObject>>> entrySet = job.entrySet();
		for (Entry<String, ArrayList<libObject>> entry : entrySet) {
			String tableName = entry.getKey();
			ArrayList<libObject> value = entry.getValue();
			switch (tableName) {
			case "trace_pegging":
				saveTracePeggingToFile(taskid,value);
				break;
			case "spacetime":
				saveSpaceTimeToFile(taskid,value);
				break;
			}
			return;//只取第一个
		}
	}
	
	private static void saveSpaceTimeToFile(String taskid,ArrayList<libObject> value) {
		MyLog.AddLog("actual_data_analyzer.log","step===saveSpaceTimeToFile");
		StringBuffer sb = new StringBuffer();
		for (libObject obj : value) {
			task_spacetime_lib bean = (task_spacetime_lib)obj;
			sb.append(bean.getTaskid()+","+bean.getMac()+","+bean.getSvc()+","+bean.getStime()+","+bean.getEtime() +"\r\n");
		}
		writeToFile(taskid,sb.toString());
	}

	private static void saveTracePeggingToFile(String taskid,ArrayList<libObject> value) {
		MyLog.AddLog("actual_data_analyzer.log","step===saveTracePeggingToFile");
		StringBuffer sb = new StringBuffer();
		for (libObject obj : value) {
			task_tracepegging_lib bean = (task_tracepegging_lib)obj;
			sb.append(bean.getTaskid()+","+bean.getMac()+","+bean.getSvc()+","+bean.getStime()+","+bean.getEtime() +"\r\n");
		}
		writeToFile(taskid,sb.toString());
	}
	
	/**
	 * 写入数据到临时文件
	 * @param content
	 */
	private static void writeToFile(String taskid,String content){
		MyLog.AddLog("actual_data_analyzer.log","step===writeToFile");
		String path = DATA_DIR_PATH + File.separator + System.currentTimeMillis() + ".sld";
		File file = new File(path);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(content.getBytes("utf-8"),0,content.getBytes().length);
            bos.flush();
            bos.close();
            renameFile(taskid,file);
		} catch (Exception e) {
			MyLog.AddLog("actual_data_analyzer.log","step===writeToFile Exception");
		}finally {
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 重命名，拷贝新文件
	 * @param file
	 */
	private static void renameFile(String taskid,File file) {
		MyLog.AddLog("actual_data_analyzer.log","step===renameFile");
		String path = DATA_DIR_PATH + File.separator + taskid + "_result.sld";
		File newFile = new File(path);
		boolean  b = file.renameTo(newFile);
		if(b){
			file.delete();
		}else{
			renameFile(taskid,file);
		}
	}

	

}
