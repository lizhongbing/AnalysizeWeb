/**
 * 
 */
package Common;

 
public class TASK_DEFINITION {
	public static   long	task_type_fellow=16;			//伴随
	public static   long	task_type_permanent=12; 		//常驻
	public static   long	task_type_highrisk=13;	 		//高危
	public static   long	task_type_VIP=15;				//重点人群
	public static   long	task_type_workperson=17;		//上班族
	public static   long	task_type_pegg=9;		        //轨迹反查
	public static   long	task_type_spacetime=1;		    //时空碰撞
	
	public static   int 	max_time_rang=30; 	
	public static   long	tasktype_BaseNum=1000; 			//实时任务任务类型基数，url中tasktype=1000+tasktype
	
	
	public static String   task_vip_jobname="vip";
	public static String   task_danger_jobname="danger";
	public static String   task_permanent_jobname="permanent";
	public static String   task_fellow_jobname="fellow";
	public static String   task_work_jobname="work";
	public static String   task_pegg_jobname="pegging";
	public static String   task_spacetime_jobname="spacetime";
	
   //实时分析生成jobname的特殊符号^,&
   ////String jobname="vip^192.168.218.2^8000^/probd/probd-3.0.1/hadoop-2.6.3/etc/hadoop/task-analyzer.xml&1521536909&C8-F2-30-5B-5C-A4,1C-48-CE-CD-9E-C3,8C-18-D9-10-A7-78&20180324~20180327&es";	
	public static String   task_jobname_startmark="^";
	public static String   task_jobname_cutmark="&";
	
	public static String   zookeeper_local_hostname="probd02,probd03,probd04";
	public static int      zookeeper_client_port=2181;
	public static int      zookeeper_message_port=5000;
	
	public static String   hbase_table_name="pro_portrait";
	public static String   hbase_info="info";
	public static String   hbase__danger="danger";
	public static String   hbase_permanent="permanent";
	public static String   hbase_work="work";
	public static String   hbase_value1="1";
	
	public static String   stream_memjobhost="/memjobhost";
	
	public static int      task_jobname_port=8000;
	public static String   task_job_datasource_es="es";
	public static String   task_job_datasource_hive="hive";
	
	public static String   task_job_classname_prefix="task_";   //Model下表对应的类名前缀
	public static String   task_job_classname_suffix="_lib";    //Model下表对应的类名后缀
	
	
	public static String 	analyse_runstatus_running="2";
	public static String 	analyse_runstatus_finish="3";
	public static String 	analyse_runstatus_fail="4";
	
	public static String    task_analyser_xml_localpath="/probd/probd-3.0.1/hadoop-2.6.3"+Constant.HADOOP_TASKANALYZER_PATH;
	
	public static String 	tablename_vipsvc="vip_svc";
	public static String 	tablename_viptrace="vip_trace";
	public static String 	tablename_permanent="permanent";
	public static String 	tablename_permanent_ad="permanent_ad";
	public static String 	tablename_fellow="fellow";
	public static String 	tablename_fellow_ad="fellow_ad";
	public static String 	tablename_danger_result="danger_result";
	public static String 	tablename_work_person="work_person";
	public static String 	tablename_work_person_ad="work_person_ad";
	public static String 	tablename_taskcount="taskcount";
	public static String 	tablename_tasklog="tasklog";
	public static String 	tablename_temp_mac="temp_mac";
	public static String 	tablename_taskname="taskname";
	public static String 	tablename_actual_taskname="actual_taskname";
	public static String 	tablename_taskresult="taskresult";
	public static String 	tablename_spacetime="spacetime";
	public static String 	tablename_trace_pegging="trace_pegging";


	public static String 	tablefield_status="status";
	public static String 	tablefield_taskid="taskid";
	public static String 	tablefield_svc="svc";
	public static String 	tablefield_mac="mac";
	public static String 	tablefield_day="day";
	public static String 	tablefield_nums="nums";
	public static String 	tablefield_counts="counts";
	public static String 	tablefield_integral="integral";
	public static String 	tablefield_trace="trace";
	public static String 	tablefield_workup="workup";
	public static String 	tablefield_workdown="workdown";
	public static String 	tablefield_timeval="timeval";
	public static String 	tablefield_no="no";
	public static String 	tablefield_fellowid="fellowid";
	public static String 	tablefield_name="name";
	public static String 	tablefield_starttime="starttime";
	public static String 	tablefield_endtime="endtime";
	public static String 	tablefield_result_nums="result_nums";
	public static String 	tablefield_add_nums="add_nums";
	public static String 	tablefield_timelen="timelen";
	public static String 	tablefield_totaltimelen="totaltimelen";
	public static String 	tablefield_content="content";
	public static String 	tablefield_lable="lable";
	public static String 	tablefield_taskmd5="taskmd5";
	public static String 	tablefield_taskdesc="taskdesc";
	public static String 	tablefield_userid="userid";
	public static String 	tablefield_result_tasktype="tasktype";
    
	
}
