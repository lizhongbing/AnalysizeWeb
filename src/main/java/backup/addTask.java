/**
 * 
 */
package backup;

import servlet.baseServlet;

public class addTask  extends baseServlet{

	@Override
	public String handle() {
        String taskid=getObject("taskid");
        String taskdesc=getObject("taskdesc");
		
		return taskid+" "+taskdesc;
	}

}
