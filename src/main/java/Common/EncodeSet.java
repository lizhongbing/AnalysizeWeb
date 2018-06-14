package Common;

import java.io.UnsupportedEncodingException;

public class EncodeSet {

   /**
    * utf-8转成iso885915
    * @param str
    * @return
    */
   public static String utf8_to_iso885915(String str){
	   String convertstr = null;
	   try {
			convertstr =new String(str.getBytes("utf-8"),"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	   return convertstr;
   }
   
   /**
    * utf-8转GBK
    * @param str
    * @return
    */
   public static String utf8_toGBK(String str){
	   try { 
		   new String(str.getBytes("UTF-8"),"GBK")  ;	
		   } catch (UnsupportedEncodingException e) { 
	   }
	   return str;
   }
   
   /**
    * GBK转udf-8,这个可以将汉字正常显示
    * @param str
    * @return
    */
   public static String GBK_to_utf8(String str){
		try {
			str =new String(str.getBytes("GBK"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str; 
   }
   
   /**
    * GB2312转udf-8,这个可以将汉字正常显示
    * @param str
    * @return
    */
   public static String GB2312to_utf8(String str){
		try {
			str =new String(str.getBytes("GB2312"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str; 
   }
   
   
   /**
    * GBK转ISO8859_1(这个是最原始，最简单的编码，但表示范围窄),这个可以将汉字正常显示
    * @param str
    * @return
    */
   public static String GBK_to_ISO8859_1(String str) {
		String name = null;
		try {
			name = new String(str.getBytes("GBK"), "ISO8859_1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		return name;
	}
   
	/**
	 * 将汉字编码在浏览器进行正确显示
	 * @param str
	 * @return
	 */
	public static String get_EncodeConvert(String str){
		if(str.length()>0){
			try {
			str=new String(str.getBytes("ISO-8859-1"),"GBK"); 
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		 }  
		}
		return str;
	}
	
	/**
	 * 将单个字符串转成utf-8编码
	 * @param str
	 * @return
	 */
	public static String str_toUTF8(String str){
		if(str==null||str.isEmpty())return null;
	
    		try{
			str = new String(str.getBytes("ISO-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	   	return str;
	   }	
	   
	/**
	 * 将数组里面的字符串转成utf-8
	 * @param strs
	 * @return
	 */
	public static String[] arrayStr_toUTF8(String[] strs){
	   	if(strs.length>0){
	   		for(int i=0;i<strs.length;i++){
	   			try {
	   				strs[i] = new String(strs[i].getBytes("ISO-8859-1"),"utf-8");
				} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
				}
	   		}	    		
	   	}    	
	   	return strs;
	   }
	  
	/**
	 * 查询当前字符串属于哪种编码类型
	 */
    public static String getEncoding(String str) {  
        String encode = "GB2312";  
        try {  
            if (str.equals(new String(str.getBytes(encode), encode))) {  
                String s = encode;  
                return s;  
            }  
        } catch (Exception exception) {  
        }  
        encode = "ISO-8859-1";  
        try {  
            if (str.equals(new String(str.getBytes(encode), encode))) {  
                String s1 = encode;  
                return s1;  
            }  
        } catch (Exception exception1) {  
        }  
        encode = "UTF-8";  
        try {  
            if (str.equals(new String(str.getBytes(encode), encode))) {  
                String s2 = encode;  
                return s2;  
            }  
        } catch (Exception exception2) {  
        }  
        encode = "GBK";  
        try {  
            if (str.equals(new String(str.getBytes(encode), encode))) {  
                String s3 = encode;  
                return s3;  
            }  
        } catch (Exception exception3) {  
        }  
        return "";  
    } 
    
    
  
}
