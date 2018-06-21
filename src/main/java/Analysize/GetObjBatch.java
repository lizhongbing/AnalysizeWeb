package Analysize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysqlObject.mysqlRow;

import Common.ConnectToDatabase;
import Common.Task_result_Judge_new;
import Common.TimeDate;
import servlet.baseServlet;

public class GetObjBatch extends baseServlet {
	private String mac;
	private String ENDRESULT;
	private int pageno=0;
	private int pagesize=10;
	private String limit;
	private String page="0";
	private int totals=0;
	private List<String> maclist=new ArrayList<String>();
	private Map<String ,String> mac_taskid_map=new HashMap<String ,String>();
	private Map<String, String> tablemap=new HashMap<String,String>();
	
	@Override
	public String handle() {
		mac=getObject("mac");
		limit=getObject("limit");
		page=getObject("page");
		ENDRESULT=Manage();
		return ENDRESULT;
	}
	
	/*
	 * 处理程序主入口
	 */
	public String Manage(){
		ENDRESULT="";
		maclist.clear();
		mac_taskid_map.clear();
		String result="{\"status\":\"1\",\"data\":[]}";
		if(init_paras()){
			init_limitstr();
			ConnectToDatabase.connect();	
			mysqlObject sqlobj= new mysqlObject();		
		    String res=Operatemac_out_taskid(sqlobj);
			if(res.endsWith(",")) res=res.substring(0,res.length()-1);
			result="{\"status\":\"0\",\"totalnums\":\""+totals+"\","+res+"}";
		}			
		return result;
	}
	
	/**
	 * mac不为空，输出对应taskid
	 */
	public String Operatemac_out_taskid(mysqlObject obj){
		String combinestr="";
		for(int i=0;i<maclist.size();i++){
			String inmac=(String) maclist.get(i);
			Set<String> tableset=tablemap.keySet();
			Iterator<String> itor=tableset.iterator();
			while(itor.hasNext()){
				String tablename=(String) itor.next();
				String mac_columnname=(String) tablemap.get(tablename);
				//obj.clearObject();
				String selec_taskid_sql="select  distinct(taskid) from "+tablename+" where "+mac_columnname+"=\'"+inmac+"\'";
				List<mysqlRow> selec_taskid_sql_rows=obj.ExeSqlQuery(selec_taskid_sql);
				if(selec_taskid_sql_rows.size()>0){
					String result=obj.toJosn(0, totals, 0);
					result=result.split(",\"rows\"")[1];
					result=result.substring(2,result.length()-2);
					String taskidstr=result.replace("\"taskid\":","").replace("\"","").replace("{","").replace("}","");
					if(!mac_taskid_map.containsKey(inmac)) mac_taskid_map.put(inmac,taskidstr); 
					else mac_taskid_map.put(inmac,mac_taskid_map.get(inmac)+","+taskidstr); 
				}
			}			
			
		}		  
		for (Map.Entry<String ,String> entry : mac_taskid_map.entrySet()) { 
		    combinestr+="{\"mac\":\""+entry.getKey()+"\",\"taskid\":\""+entry.getValue()+"\"},";
		}
		if(combinestr.endsWith(",")) combinestr=combinestr.substring(0,combinestr.length()-1);
		totals=combinestr.split("taskid").length-1;
		String res=combinestr.replace("},{","}={");
		res=Task_result_Judge_new.getdata_fromMtoN_likepage_instr(res,pageno, pagesize);		
		res="\"data\":["+res+"]";
        return res;
	}	
	
	//参数处理
	public Boolean init_paras(){
		boolean flag=true;
		if(mac==null||(mac.equals("{}"))||mac.equals("\"\"")||mac.isEmpty()){		
			flag=false;	
		}else{
			mac=mac.replace("{","").replace("}","").replace("，",",");
			if(mac.endsWith(",")) mac=mac.substring(0,mac.length()-1);
			if(mac.contains(",")){
				String[] mac_arr=mac.split(",");
				for(int i=0;i<mac_arr.length;i++){
					maclist.add(mac_arr[i]);
				}
			}
			else maclist.add(mac);
		}		
		return flag;
	}
		
	/**
	 * 生成limitstr
	 */
    public void init_limitstr(){
		//limitstr="";
		//把任务表的名字添加到任务表数组中
		tablemap.put("danger_result","mac");
		tablemap.put("fellow","name");
		tablemap.put("permanent_ad","mac");
		tablemap.put("work_person_ad","mac");
		tablemap.put("vip_svc","mac");
		//long begno=0;
		//long endno=0;
		if(TimeDate.isnum(page)) pageno=Integer.parseInt(page);
		if(TimeDate.isnum(limit)) pagesize=Integer.parseInt(limit);
		//begno=pageno*pagesize;
		//limitstr ="  limit "+begno+","+pagesize;			
	}
    
    
}
