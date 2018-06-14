package Common;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import probd.hbase.common.MyLog;

public class Directory_File {
	/*
	 * FileSystem dfs,
		String soupath,
		String destpath,
		boolean bappend,    true 尾部增加 false 新建
		String process_suffix  null  复制文件过程标记 默认为 .process
	 */
	public static boolean copyfile_dfs_dfs(FileSystem dfs,
									String soupath,
									String destpath,
									boolean bappend,
									String process_suffix){
		try {
			System.out.println("==>copyfile_dfs_dfs: souptah:"+soupath+" destpath:"+destpath+" bappend:"+bappend);
			
			String suffix=process_suffix;
			if(suffix==null)suffix=".process";
			String destpath_process=destpath+suffix;
			
			if(bappend){
				destpath_process=destpath;
				suffix=null;
			}
		
			StringBuilder  msg=new StringBuilder(); 
			FSDataInputStream in = dfs.open(new Path(soupath));
			FSDataOutputStream out;
			msg.append("copyfile :");
			if(bappend){
				Path path=new Path(destpath_process);
				if(dfs.exists(path)){
					msg.append(" dfs.append");
					out = dfs.append(path);
				}else{
					msg.append(" dfs.create");
					out = dfs.create(path);
				}
			}else{
				msg.append(" dfs.create2");
				out = dfs.create(new Path(destpath_process));
			}
			IOUtils.copyBytes(in, out, 4096, true);
			
			if(!destpath_process.equals(destpath)){
				Path destpath_process_path=new Path(destpath_process);
				Path destpath_path=new Path(destpath);
				dfs.rename(destpath_process_path, destpath_path);
			}
			
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}  
		return true;
	}
	
	/*
	 * FileSystem dfs,
		String soupath,
		String destpath,
		String process_suffix  null  复制文件过程标记 默认为 .process
	 */
	public static boolean copyfile_local_dfs(FileSystem dfs,
									String soupath,
									String destpath,
									String process_suffix){
		try {
			String suffix=process_suffix;
			if(suffix==null)suffix=".process";			
			String destpath_process=destpath+suffix;
			Path soupath_path=new Path(soupath);
			Path destpath_process_path=new Path(destpath_process);
			dfs.copyFromLocalFile(soupath_path, destpath_process_path);		
			Path destpath_path=new Path(destpath);
			dfs.rename(destpath_process_path, destpath_path);
			System.out.println("copyfile_local_dfs: souptah:"+soupath+" destpath:"+destpath);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}  
		return true;
	}
	
	/*
	 * FileSystem dfs					文件系统
		String soupath					源文件			
		String destpath					目的文件
		String process_suffix  null  复制文件过程标记 默认为 .process
	 */
	public static boolean copyfile_dfs_local(FileSystem dfs,
									String soupath,
									String destpath,
									String process_suffix){
		try {			
			String suffix=process_suffix;
			if(suffix==null||suffix.length()==0)suffix=".process";			
			String destpath_process=destpath+suffix;
			Path soupath_path=new Path(soupath);
			Path destpath_process_path=new Path(destpath_process);
			MyLog.AddLog("copyfile_dfs_local_log",soupath_path+" "+destpath_process_path);
			dfs.copyToLocalFile(soupath_path, destpath_process_path);
		
			File  file=new File(destpath_process);
			File dest=new File(destpath);
			file.renameTo(dest);
			System.out.println("copyfile_dfs_local: souptah:"+soupath+" destpath:"+destpath);
			MyLog.AddLog("copyfile_dfs_local_log",soupath+" "+destpath);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}  
		return true;
	}
	
	
	/*
		String srcFileName,文件名,不是文件夹
		String destFileName,
		String process_suffix  null  复制文件过程标记 默认为 .process
	 */
	public static boolean copyfile_local_local(String srcFileName, String destFileName, String process_suffix) {  
        File srcFile = new File(srcFileName);  
  
        String suffix=process_suffix;
		if(suffix==null)suffix=".process";
		String destpath_process=destFileName+suffix;
		
        if (!srcFile.exists()) {  
            return false;  
        } else if (!srcFile.isFile()) {  
              
            return false;  
        }   
        File destFile_process = new File(destpath_process);  
        if (destFile_process.exists()) {        
             new File(destpath_process).delete();  
        } else {   
            if (!destFile_process.getParentFile().exists()) {    
                if (!destFile_process.getParentFile().mkdirs()) {    
                    return false;  
                }  
            }  
        }  
        int byteread = 0; // 锟斤拷取锟斤拷锟街斤拷锟斤拷  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(srcFile);  
            out = new FileOutputStream(destFile_process);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            File destFile=new File(destFileName);
            destFile_process.renameTo(destFile);
            return true;  
        } catch (FileNotFoundException e) {  
            return false;  
        } catch (IOException e) {  
            return false;  
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }
        }  
        
    }  
	/**
     * 判断多级路径是否存在，不存在就创建 ,兼容Windows，Linux,只针对目录，不针对文件
     *  
     * @param filePath 支持带文件名的Path：如：D:\news\2014\12\abc.text，和不带文件名的Path：如：D:\news\2014\12
     */
    public static void isExistDir(String filePath) {
        String paths[] = {""};
        //切割路径
        try {
            String tempPath = new File(filePath).getCanonicalPath();//File对象转换为标准路径并进行切割，有两种windows和linux
            paths = tempPath.split("\\\\");//windows            
            if(paths.length==1){paths = tempPath.split("/");}//linux
        } catch (IOException e) {
            System.out.println("切割路径错误");
            e.printStackTrace();
        }
        //判断是否有后缀
        boolean hasType = false;
        if(paths.length>0){
            String tempPath = paths[paths.length-1];
            if(tempPath.length()>0){
                if(tempPath.indexOf(".")>0){
                    hasType=true;
                }
            }
        }        
        //创建文件夹
        String dir = paths[0];
        for (int i = 0; i < paths.length - (hasType?2:1); i++) {// 注意此处循环的长度，有后缀的就是文件路径，没有则文件夹路径
            try {
                dir = dir + "/" + paths[i + 1];//采用linux下的标准写法进行拼接，由于windows可以识别这样的路径，所以这里采用警容的写法
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdir();
                    System.out.println("成功创建目录：" + dirFile.getCanonicalFile());
                }
            } catch (Exception e) {
                System.err.println("文件夹创建发生异常");
                e.printStackTrace();
            }
        }
    }
	/**
     * 删除空目录
     * @param dir 将要删除的目录路径
     */
    public static void doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("Successfully deleted empty directory: " + dir);
        } else {
            System.out.println("Failed to delete empty directory: " + dir);
        }
    }
    
    /**
     * 递归删除目录,及该目录下的所有文件(包括子目录下所有文件)
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
    	Print.println(dir.toString());
    	boolean flag=true;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        } 
        dir.delete();
        return flag ;
    }
    /**获取某个目录下的所有目录名,以list数组返回，不包括子目录下的文件名*/
	public static List<String>  getDirectoryName(String path) {
		List<String> directoryName_list=new ArrayList<String>();
	    File f = new File(path);
	    if (f.exists()) {
		    File fa[] = f.listFiles();
		    for (int i = 0; i < fa.length; i++) {
		        File fs = fa[i];
		        if (fs.isDirectory()) {
		            System.out.println(fs.getName());
		            directoryName_list.add(fs.getName());
		        } 
		    }
	    }
	    return directoryName_list;
   }
	/**获取某个目录下的所有文件名，不包括子目录下的文件,返回List*/
	public static List<String> getFileName(String path) {
		List<String> fileName_list=new ArrayList<String>();
	    File f = new File(path);
	    File fa[] = f.listFiles();
	    for (int i = 0; i < fa.length; i++) {
	        File fs = fa[i];
	        if (!fs.isDirectory()) {
	            System.out.println(fs.getName());
	            fileName_list.add(fs.getName());
	        }
	    }
	    return fileName_list;
	}
	
	/**将某个目录下的文件复制到另一个目录下，但是两个目录必须对应，不能缺失*/
	public static boolean IOCopy(String path,String path1){
		boolean flag=false;
	    File file = new File(path);
	    File file1 = new File(path1);
	    byte[] b = new byte[(int) file.length()];
	    FileInputStream is = null;
	    FileOutputStream ps = null;
	    if(file.isFile()){
	         try {
				  is= new FileInputStream(file);
				  ps= new FileOutputStream(file1);
				  is.read(b);
				  ps.write(b);
				  flag=true;
			 } catch (Exception e) {
			     e.printStackTrace();
			 }finally {
				 try {
					 if(is != null)is.close();
					 if(ps != null)is.close();
				} catch (Exception e2) {
					 e2.printStackTrace();
				}
			}
	   }else if(file.isDirectory()){	
		    if(!file.exists())	 file.mkdir();
		    String[] list = file.list();
		    for(int i=0;i<list.length;i++){
		      Directory_File.IOCopy(path+File.separator+list[i], path1+File.separator+list[i]);
		    }
		    flag=true;
	   }
	  return flag;
	}
	 /**
     * 删除hdfs文件或者目录
     * @param hdfs
     * @param fullNasme
	 * @throws IOException 
	 * @throws IllegalArgumentException 
     * @throws Exception
     */
	public static void rmdir_hdfs(FileSystem dfs, String path) throws IllegalArgumentException, IOException {
		@SuppressWarnings("deprecation")
		boolean res = dfs.delete(new Path(path));
        if (res) {
            System.out.println("------rmdir Success------" );
        } else {
            System.out.println("------rmdir Fail------" );
        }
        dfs.close();		
	}
	
}
