/**
 * 
 */
package Model;

import com.proweb.job.libObject;
//tasklog  lib实现类
public class task_tasklog_lib  extends libObject{
    public String taskid;
    public String day;
    public String starttime;
    public String endtime;
    public String timelen;
    
    @Override
	public boolean addRec(String recs) {
		// TODO Auto-generated method stub
		System.out.println(recs);
		String[] strlist=recs.split("&");
		if(strlist.length!=5)return false;
		taskid=strlist[0];
		day=strlist[1];
		starttime=strlist[2];
		endtime=strlist[3];
		timelen=strlist[4];
		return true;
	}
}
