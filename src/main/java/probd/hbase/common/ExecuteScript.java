package probd.hbase.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;  
  
public class ExecuteScript{	
	
	 private  String cmd;
	 private  boolean isNeedPrint;
	  
    public ExecuteScript() {}

	public ExecuteScript(String cmd, boolean isNeedPrint) {
		this.cmd=cmd;
		this.isNeedPrint=isNeedPrint;
	}
   
	public static int execute(String cmd, boolean isNeedPrint) throws IOException, InterruptedException{
		Process proc = null;
	    int result = -1;
	    try{
		    List<Stream> list=new ArrayList<Stream>();
		    proc=Runtime.getRuntime().exec(cmd);
		    Stream error=new Stream(proc.getErrorStream(),"Error");
		    Stream output=new Stream(proc.getInputStream(),"Out");
		    if(isNeedPrint){
		    	list.add(error);
		    	list.add(output);
		    }
		    error.start();
		    output.start();
		    if (isNeedPrint){  
	            for (Stream sr : list){  
	                sr.join();  
	            }  
	        } 
		    result=proc.waitFor();
	    }
	    catch (Exception e){  
	        e.printStackTrace();  
	      }  
	      finally{  
	        proc.destroy();  
	      }  
	    return result;
  }

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public boolean isNeedPrint() {
		return isNeedPrint;
	}

	public void setNeedPrint(boolean isNeedPrint) {
		this.isNeedPrint = isNeedPrint;
	}  

	
	
}
	
	

