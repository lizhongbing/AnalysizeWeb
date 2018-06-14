package probd.hbase.common;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
 
public class Stream extends Thread {  
 
   InputStream is;  
   String type;  
 
   Stream(InputStream  is, String type){  
       this.is = is;  
       this.type = type;  
     }  
 
   public void run(){
       InputStreamReader isr=null;
       BufferedReader br=null;
       try {
	       isr=new InputStreamReader(is);
	       br=new BufferedReader(isr);
	       String line=null;
	       
			while((line=br.readLine())!=null){
				 System.out.println(line);
			}
	   } catch (IOException e) {		
		e.printStackTrace();
	   }finally{
		   close(isr,br);
	   }
    		   
   }  
 
   public void close(InputStreamReader isr, BufferedReader br){  
	   if (null != br)  
       {  
           try  
           {  
               br.close();  
           }  
           catch (IOException e)  
           {  
               br = null;  
           }  
       }  
 
       if (null != isr)  
       {  
           try  
           {  
               isr.close();  
           }  
           catch (IOException e)  
           {  
               isr = null;  
           }  
       }  
   }  
 
}  



