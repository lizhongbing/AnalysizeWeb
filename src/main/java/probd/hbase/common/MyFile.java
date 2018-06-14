package probd.hbase.common;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class MyFile {

	public static long GetFileLen(String pathname){
		//long filelen=0;
		File realFile = new File(pathname);
		
		return realFile.length();
	}
	public static String GetOnlyDfsFilename(FileSystem dfs,String parentDir,String filename,long timeval){
		String resultName=filename;
		String rootdir=parentDir;
		if((rootdir.charAt(rootdir.length()-1)=='/')||
				(rootdir.charAt(rootdir.length()-1)=='\\')){
		}else{
			rootdir+="/";
		}
		for(long i=0;i<10;i++){
			if(timeval>0){
				resultName=rootdir+filename+"_"+Long.toString(timeval)+"_"+Long.toString(i);
			}else{
				resultName=rootdir+filename+"_"+Long.toString(i);
			}
			try {
				if(!dfs.exists(new Path(resultName)))return resultName;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resultName;
	}
}
