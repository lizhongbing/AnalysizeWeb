/**
 * 
 */
package Model;

import com.proweb.job.libObject;

import Common.TASK_DEFINITION;


//danger_result lib类
public class task_danger_result_lib extends libObject{
	
	public String taskid;
    public String mac;
    public String day;
    public String svc;
    public String nums;
    public String integral;
    public String timeval;
    
    @Override
	public boolean addRec(String recs) {
		System.out.println(recs);
		String[] strlist=recs.split(TASK_DEFINITION.task_jobname_cutmark);
		if(strlist.length!=7)return false;
		taskid=strlist[0];
		mac=strlist[1];
		day=strlist[2];
		svc=strlist[3];
		nums=strlist[4];
		integral=strlist[5];
		timeval=strlist[6];
		return true;
	}

}
