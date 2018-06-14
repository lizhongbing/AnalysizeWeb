package probd.hbase.common;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;



public class confManager {

	public confManager(){}
	public static void setConfMark(String confFile, String name, String value) {
			
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
	        
	        XMLWriter xmlWriter=new XMLWriter(new java.io.FileWriter(confFile)); 
	        xmlWriter.write(docConf); 
	        xmlWriter.close();
		}
		catch (Exception e) {
		}
	  }

	public static String GetConfMark(String confFile, String name){
		String result="";
		if (confFile == null) return result;
		try {
			SAXReader saxReader = new SAXReader();
			File fileConf = new File(confFile);
			Document docConf = saxReader.read(fileConf);
			Element xmlRoot = docConf.getRootElement();
			Element xmlName = null, xmlVal = null;
		    List<Element> xmlPropertyList = xmlRoot.elements("property");
			for(Element xmlProperty:  xmlPropertyList){
				xmlName = xmlProperty.element("name");
	        	if (xmlName == null) continue;
	        	if (xmlName.getText().equalsIgnoreCase(name) == true) {
	        		xmlVal = xmlProperty.element("value");
	        		result=xmlVal.getText();
	        		break;
	        	}
	        	xmlName=null;
			}	
		}
		catch (Exception e) {
			 e.printStackTrace();	
		}
		return result;
	}

    
	public static String GetKeyValue(String filename,String key){
		
		String str=GetConfMark(filename,key);
		System.out.println("GetKeyValue: "+str+"\n");
		return str;
	}

	public static void SetKeyValue(String filename,String key,String value){
		String path=Class.class.getClass().getResource("/").getPath()+"/"+filename;
		setConfMark(path, key, value);
	}
	
	
	
}
