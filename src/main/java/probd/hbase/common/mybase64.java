package probd.hbase.common;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;


@SuppressWarnings("restriction")
public class mybase64 {
	public static String base64_encode(String str){
		
		return Base64.encode(str.getBytes());
	}
	public static String base64_decode(String str){
		try {
			byte b[] =Base64.decode(str.getBytes());
			return new String(b);
			
		} catch (Base64DecodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	
}
