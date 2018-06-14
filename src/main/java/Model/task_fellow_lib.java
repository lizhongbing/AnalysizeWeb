/**
 * 
 */
package Model;

import com.proweb.job.libObject;

import Common.TASK_DEFINITION;

//fellow  lib实现类
public class task_fellow_lib   extends libObject{

    public String day;
    public String taskid;
	public String name;
    public String fellowid;
	
	@Override
	public boolean addRec(String recs) {
		System.out.println(recs);
		String[] strlist=recs.split(TASK_DEFINITION.task_jobname_cutmark);
		if(strlist.length!=4)return false;
		day=strlist[0];
		taskid=strlist[1];
		name=strlist[2];
		fellowid=strlist[3];
		return true;
	}
}
