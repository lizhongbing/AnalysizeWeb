package Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class test {
	public static void main(String[] args) {
		
		Map<String,String> hashMap = new HashMap<String,String>();
		hashMap.put("2", "asdfa");
		hashMap.put("1", "asdfa");
		hashMap.put("4", "asdfa");
		hashMap.put("7", "asdfa");
		hashMap.put("3", "asdfa");
		
	    Set<Entry<String, String>> entrySet = hashMap.entrySet();
		
		List<Map.Entry<String,String>> orderdList=new ArrayList<Map.Entry<String, String>>(entrySet);
		
		Collections.sort(orderdList, new Comparator<Map.Entry<String,String>>(){  
	          @Override  
	          public int compare(Entry<String,String> o1,Entry<String, String> o2) {    
	              return o2.getKey().compareTo(o1.getKey());  
	          }}); 
	          
		for (Entry<String, String> entry : entrySet) {
			System.out.println("entrySet==="+entry.getKey());
		}
		

		for (Entry<String, String> entry : orderdList) {
			System.out.println("orderdList==="+entry.getKey());
		}
		
	}

}
