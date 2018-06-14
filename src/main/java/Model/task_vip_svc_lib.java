/**
 * 
 */
package Model;

import com.proweb.job.libObject;

import Common.TASK_DEFINITION;
//vip_svc  lib实现类
public class task_vip_svc_lib  extends libObject{
    public String mac;
    public String svc;
    public String timelen;
    public String counts;
    public String taskid;
    public String day;
    @Override
	public boolean addRec(String recs) {
		// TODO Auto-generated method stub
		System.out.println(recs);
		String[] strlist=recs.split(TASK_DEFINITION.task_jobname_cutmark);
		if(strlist.length!=6)return false;
		mac=strlist[0];
		svc=strlist[1];
		timelen=strlist[2];
		counts=strlist[3];
		taskid=strlist[4];
		day=strlist[5];
		return true;
	}
}
