package Common;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class Base64Encode {
	
	public final static String ENCODING="UTF-8";    
	
	/**
	 * 加密 
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 */
    public static String encoded(String data) throws UnsupportedEncodingException {  
    	if(data==null||(data.isEmpty())) return null;
        byte[] b=Base64.encodeBase64(data.getBytes(ENCODING));            
        return new String(b,ENCODING);    
    }    
    
    /**
     * 解密
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String data) throws UnsupportedEncodingException{  
    	if(data==null||(data.isEmpty())) return null;
        byte[] b=Base64.decodeBase64(data.getBytes(ENCODING));    
        return new String(b,ENCODING);    
    }  
	
  }
