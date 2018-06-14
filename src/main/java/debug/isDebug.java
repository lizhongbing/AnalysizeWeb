package debug;

public class isDebug {
	
   //是否为本地调试	
   public static boolean islocal=false;
   
   static{
	   String os = System.getProperty("os.name");  
	   if(os.toLowerCase().startsWith("win")) islocal=true;	  
	   if(os.toLowerCase().startsWith("mac")) islocal=true;	  
   }
  
}
