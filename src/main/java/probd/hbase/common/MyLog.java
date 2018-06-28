package probd.hbase.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class MyLog {
	
	//read conf xml  $HADOOP_HOME/etc/hadoop/pro_loginfo.xml
	
	public static String logbasedir;
	public static String Conf_Set_logpath="probd.spark.log.path";
	
	public static String GetCurrTime(){
		String timestr="";
		Date date= new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timestr=sdf.format(date);
		return timestr;
	}
	public static String GetPathFromEnv(String key){
		String path="";
		Map<String, String> map =new HashMap<String, String>();
		map = System.getenv();
		
		Iterator<Entry<String, String>> it = map.entrySet().iterator();  
		while(it.hasNext()){
		    Entry<String, String> entry = (Entry<String, String>)it.next();
		    if(key.equals(entry.getKey())){
		    	path=(String) entry.getValue();
		    	break;
		    }  
		}  
		return path;
	}
	public static boolean GetLogBaseDir(){
		if(logbasedir==null){
			String hadoopconf=GetPathFromEnv("HADOOP_HOME");
			
			if((hadoopconf==null)||(hadoopconf.isEmpty())){
				logbasedir="";
				return false;
			}
			hadoopconf+="/etc/hadoop/pro_loginfo.xml";
			
			logbasedir=GetConfMark(hadoopconf, Conf_Set_logpath);		
		}
		if(logbasedir.isEmpty())return false;
		return true;
	}
	public static void AddLog(String filename,String buffer){
		//if(!isDebug.islocal) return;//正式环境去掉日志
		String pathname=filename;
		if(!GetLogBaseDir())return;
		if(!filename.startsWith(logbasedir)){
			pathname=logbasedir+"/"+filename;
		}
		FileWriter fw = null;
		try {
			//����ļ����ڣ���׷�����ݣ�����ļ������ڣ��򴴽��ļ�
			File f=new File(pathname);
			fw = new FileWriter(f, true);	
			PrintWriter pw = new PrintWriter(fw);
			
			pw.println(buffer);
		
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException e) {
		//	e.printStackTrace();
		}
	}
	
	public static void AddImportLog(String path,String type,long filelen,long timelen){
		String pathname;
		if(!GetLogBaseDir()){
			logbasedir="/data/";
		}
		pathname=logbasedir+"import.log";
		
		
		String timestr=MyLog.GetCurrTime();
		timestr+="\t";
		timestr+=type;
		timestr+="\t";
		timestr+=path;
		timestr+="\t";
		timestr+=Long.toString(filelen);
		timestr+="\t";
		timestr+=Long.toString(timelen);
		
		System.out.println("AddImportLog:"+timestr);
		AddLog(pathname,timestr);
	}
	public static void AddNormalLog(String path,String title,String content){
		String pathname=path;
		if(!GetLogBaseDir())return;
		if(!path.startsWith(logbasedir)){
			pathname=logbasedir+"/"+path;
		}
		
		String timestr=MyLog.GetCurrTime();
		timestr+="\t";
		timestr+=title;
		timestr+="\t";
		timestr+=content;
				
		System.out.println("AddNormalLog:"+timestr);
		AddLog(pathname,timestr);
	}
	
	public static void setConfMark(String confFile, String name, String value) {
		
		System.out.println("setConfMark  confFile "+confFile+ " name:"+name+"  value "+value);
		
		if (confFile == null) return;
	  
		try {
			SAXReader saxReader = new SAXReader();
			File fileConf = new File(confFile);
			Document docConf = saxReader.read(fileConf);
			Element xmlRoot = docConf.getRootElement();
			
	        Element xmlName = null, xmlVal = null;
	        List<Element> xmlPropertyList = xmlRoot.elements("property");
	        for (Element xmlProperty : xmlPropertyList) {
	        
	        	xmlName = xmlProperty.element("name");
	        	if (xmlName == null) continue;
	        	
	        	if (xmlName.getText().equalsIgnoreCase(name) == true) {
	        		
	        		xmlVal = xmlProperty.element("value");
	        		if (xmlVal == null) {
	        			xmlVal = xmlProperty.addElement("value");
	        		}
	        		xmlVal.setText(value);
	        		break;
	        	}
	        	xmlName = null;
	        }
	        
	        if (xmlName == null) {
	        	Element xmlProperty = xmlRoot.addElement("property");
	        	xmlName = xmlProperty.addElement("name");
	        	xmlName.setText(name);
	        	
	        	xmlVal = xmlProperty.addElement("value");
	        	xmlVal.setText(value);
	        	System.out.println("run.xmlProperty=" + xmlProperty);
	        }
	        
	        System.out.println("run.docConf=" + docConf);
	        
	        XMLWriter xmlWriter=new XMLWriter(new java.io.FileWriter(confFile)); 
	        xmlWriter.write(docConf); 
	        xmlWriter.close();
		}
		catch (Exception e) {
			// e.printStackTrace();
	
		}
	  }
	 public static String GetConfMark(String confFile, String name) {
		String result="";
		
		
		if (confFile == null) return result;
	  
		try {
			SAXReader saxReader = new SAXReader();
			File fileConf = new File(confFile);
			Document docConf = saxReader.read(fileConf);
			Element xmlRoot = docConf.getRootElement();
			
	        Element xmlName = null, xmlVal = null;
	        List<Element> xmlPropertyList = xmlRoot.elements("property");
	        for (Element xmlProperty : xmlPropertyList) {
	        
	        	xmlName = xmlProperty.element("name");
	        	if (xmlName == null) continue;
	        	
	        	if (xmlName.getText().equalsIgnoreCase(name) == true) {
	        		xmlVal=xmlProperty.element("value");
	        		result=xmlVal.getText();
	        		break;
	        	}
	        	xmlName = null;
	        }
		}
		catch (Exception e) {
			 e.printStackTrace();
	
		}
		
		return result;
	}
}
