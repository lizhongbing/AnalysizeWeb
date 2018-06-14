/**
 * 
 */
package Model;

import com.proweb.job.libObject;

import Common.TASK_DEFINITION;

//permanent_ad  lib实现类
public class task_permanent_ad_lib  extends libObject{
	
    public String taskid;
	public String mac;
    public String svc;
    public String day;
	
	@Override
	public boolean addRec(String recs) {
		System.out.println(recs);
		String[] strlist=recs.split(TASK_DEFINITION.task_jobname_cutmark);
		if(strlist.length!=4)return false;
		taskid=strlist[0];
		mac=strlist[1];
		svc=strlist[2];
		day=strlist[3];
		return true;
	}
}
