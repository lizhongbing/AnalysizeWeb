/**
 * 
 */
package AnalysizeOntime;

import com.proweb.job.libObject;
import com.proweb.job.objManager;

import Common.TASK_DEFINITION;
import Model.task_vip_svc_lib;
import Model.task_vip_trace_lib;
import Model.task_work_person_ad_lib;
import Model.task_work_person_lib;
import Model.task_actual_taskname_lib;
import Model.task_danger_result_lib;
import Model.task_fellow_ad_lib;
import Model.task_fellow_lib;
import Model.task_permanent_ad_lib;
import Model.task_permanent_lib;
import Model.task_tasklog_lib;
import Model.task_taskresult_lib;
import Model.task_temp_mac_lib;
import Model.task_tracepegging_lib;
 
public class taskObjManager extends objManager{

	//根据jobname获取lib类
	@Override
	public libObject getObject(String objname) {
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_viptrace))       return new task_vip_trace_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_vipsvc))   	   return new task_vip_svc_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.task_danger_jobname))      return new task_danger_result_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_permanent))      return new task_permanent_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_permanent_ad))   return new task_permanent_ad_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_work_person))    return new task_work_person_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_work_person_ad)) return new task_work_person_ad_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_fellow))         return new task_fellow_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_fellow_ad))      return new task_fellow_ad_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_tasklog))        return new task_tasklog_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_actual_taskname))return new task_actual_taskname_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_temp_mac))       return new task_temp_mac_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_taskresult))     return new task_taskresult_lib();
		if(objname.equalsIgnoreCase(TASK_DEFINITION.tablename_trace_pegging))  return new task_tracepegging_lib();
		return null;
	}
	

}
