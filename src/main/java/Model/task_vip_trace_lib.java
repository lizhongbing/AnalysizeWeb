package Model;

import com.proweb.job.libObject;

import Common.TASK_DEFINITION;
//vip_trace  lib实现类
public class task_vip_trace_lib extends libObject{
	
	public String mac;
	public String trace;
	public String counts;
	public String taskid;
	public String day;
	@Override
	public boolean addRec(String recs) {
		//System.out.println(recs);
		String[] strlist=recs.split(TASK_DEFINITION.task_jobname_cutmark);
		if(strlist.length!=5)return false;
		mac=strlist[0];
		trace=strlist[1];
		counts=strlist[2];
		taskid=strlist[3];
		day=strlist[4];
		return true;
	}
}
