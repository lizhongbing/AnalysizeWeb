/**
 * 
 */
package Model;

import com.proweb.job.libObject;

import Common.TASK_DEFINITION;
//fellow_ad  lib实现类
public class task_fellow_ad_lib    extends libObject{

    public String fellowid;
    public String svc;
	public String no;
	
	@Override
	public boolean addRec(String recs) {
		System.out.println(recs);
		String[] strlist=recs.split(TASK_DEFINITION.task_jobname_cutmark);
		if(strlist.length!=3)return false;
		fellowid=strlist[0];
		svc=strlist[1];
		no=strlist[2];
		return true;
	}
}
