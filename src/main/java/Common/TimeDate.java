package Common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.proweb.common.timeop;

public class TimeDate {	
	
	static Calendar calendar = Calendar.getInstance();
	//获取今天的字符串日期，20170815格式
	static String nowday=timeop.getDayFromTime(timeop.GetCurrentTime()).replace("-","");
	//获取今天的数字日期，20170815格式
	static int date=Integer.parseInt(nowday);
	 
	//获取年月日,yyyyMMddHHmmss
	public  static String getDate(){
		 Date date = new Date();  
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
	     String dateNowStr = sdf.format(date);
		 return dateNowStr;
	}
	//获取当前时间戳
	public static long timeStamp(){
		return  System.currentTimeMillis();
	}
	
	/**获取当前月*/
	public static int getMonth(){
		int month=calendar.get(Calendar.MONTH)+1;//月
		return month;
	}
	/**获取今天星期几*/
	public static int getWeekNum_today(){
		int weeknum=calendar.get(Calendar.DAY_OF_WEEK)-1;//星期几
		return weeknum;
	}
	/**获取当前月的第几周数字*/
	public static int getWeekNum_inmonth(){
		int weeknum=calendar.get(Calendar.WEEK_OF_MONTH);//当前月的第几周
		return weeknum;
	}
	/**获取当前年的第几周数字*/
	public static int getWeekNum_inyear(){
		int weeknum=calendar.get(Calendar.WEEK_OF_YEAR);//当前年份的第几周
		return weeknum;
	}
	/**获取当前日*/
	public static int getDay(){
		int day=calendar.get(Calendar.DAY_OF_MONTH);//日
		return day;
	}
	/**获取当前时，24小时制*/
	public static int getHour(){
		int hour=calendar.get(Calendar.HOUR_OF_DAY);//时
		return hour;
	}
	/**获取当前分*/
	public static int getMinutes(){
		int minutes=calendar.get(Calendar.MINUTE);//分
		return minutes;
	}
	/**获取当前秒*/
	public static int getSecond(){
		int second=calendar.get(Calendar.SECOND);//秒
		return second;
	}
	/**获取当前毫秒*/
	public  static int getMiliSeconds(){
		int millionseconds=calendar.get(Calendar.MILLISECOND);//毫秒
		return millionseconds;
	}
	/**检查时间戳范围输入是否合法*/
	public static boolean check_isNormalTimeStamp_timerang(String timestamp_timerang) throws ParseException {
		boolean flag=false;
		if(timestamp_timerang.contains("~")){
			String[] str=timestamp_timerang.split("~");
			if(str.length==2){
				String len1=getDate_byTimeStamp(str[0]);
				String len2=getDate_byTimeStamp(str[1]);
				if (isfixed_length_num(8, len1)&&isfixed_length_num(8,len2)){
					if(Integer.parseInt(str[1])>Integer.parseInt(str[0])) flag=true;
				}
			}
		}
		return flag;
	}

	/**检查时间戳输入是否合法*/
	public static boolean check_isNormalTimeStamp(String timestamp) throws ParseException {
		boolean flag=false;
		String len=getDate_byTimeStamp(timestamp);
		if(isfixed_length_num(8,len)) flag=true;
		return flag;
	}
	
	/**获取指定时间戳的年月日 返回20171002字符串格式*/
	public static String getDate_byTimeStamp(String timestamp) throws ParseException {
		String str="";
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			str = sdf.format(calendar.getTime());
		}
		return str;
	}
	/**获取指定时间戳的年*/
    public static int getYear_byTimeStamp(String timestamp) throws ParseException {
		int year=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			year=calendar.get(Calendar.YEAR);//年
		}
		return year;
	}
	/**获取指定时间戳的月*/
	public static int getMonth_byTimeStamp(String timestamp) throws ParseException {
		int month=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			month=calendar.get(Calendar.MONTH)+1;//月
		}
		return month;
	}
	/**获取指定时间戳星期几*/
	public static int getWeekNum_nowday_byTimeStamp(String timestamp) throws ParseException {
		int weeknum=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			weeknum=calendar.get(Calendar.DAY_OF_WEEK)-1;//获取时间戳星期几
		}
		return weeknum;
	}
	/**获取指定时间戳在月的第几周*/
	public static int getWeekNum_inmonth_byTimeStamp(String timestamp) throws ParseException {
		int weeknum=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			weeknum=calendar.get(Calendar.WEEK_OF_MONTH);//获取时间戳所在月的第几周
		}
		return weeknum;
	}
	/**获取指定时间戳所在年份的第几周*/
	public static int getWeekNum_inyear_byTimeStamp(String timestamp) throws ParseException {
		int weeknum=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			weeknum=calendar.get(Calendar.WEEK_OF_YEAR);//获取时间戳所在年的第几周
		}
		return weeknum;
	}
	
	/**根据时间戳获取yyyy-MM-dd HH:mm:ss*/
	public static  String getDayTimeFromTime(long timeval){
		String day="1970-01-02";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date date=new Date(timeval*1000);

		day=sdf.format(date);
		
		return day;
	}
	
	
	/**获取指定时间戳的日*/
	public static int getDay_byTimeStamp(String timestamp) throws ParseException {
		int day=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			day=calendar.get(Calendar.DAY_OF_MONTH);//日
		}
		return day;
	}
	/**获取指定时间戳的时*/
	public static int getHour_byTimeStamp(String timestamp) throws ParseException {
		int hour=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			hour=calendar.get(Calendar.HOUR_OF_DAY);//时
		}
		return hour;
	}
	/**获取指定时间戳的分*/
	public static int getMinutes_byTimeStamp(String timestamp) throws ParseException {
		int minutes=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			minutes=calendar.get(Calendar.MINUTE);//分
		}
		return minutes;
	}
	/**获取指定时间戳的秒*/
	public static int getSeconds_byTimeStamp(String timestamp) throws ParseException {
		int second=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			second=calendar.get(Calendar.SECOND);//秒
		}

		return second;
	}
	/**获取指定时间戳的毫秒*/
	public static int getMilliSeconds_byTimeStamp(String timestamp) throws ParseException {
		int milliseconds=0;
		if(isfixed_length_num(10,timestamp)||(isfixed_length_num(13,timestamp))){
			long stime=0;
			if(timestamp.length()==10) stime=Long.parseLong(timestamp)*1000;
			else if(timestamp.length()==13) stime=Long.parseLong(timestamp);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
			Date date=sdf.parse(sdf.format(stime));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			milliseconds=calendar.get(Calendar.MILLISECOND);//毫秒
		}
		return milliseconds;
	}
	
	/**
	 * 输入数字格式的日期，和标记符，比如- 
	 * 将20171012这样的日期转成2017-10-12这样的形式
	 * */
	public static String formatNumsDatePattern(String day,String mark){
		String day_str="";
		if(TimeDate.isnum(day)&&day.length()==8){
			day_str=day.substring(0,4)+mark+day.substring(4,6)+mark+day.substring(6,8);
		}
		return day_str;
	}
	
	
	/**
	 * 获取一周前日期,20171012格式
	 * */
	public static String getOneWeekBefore(){
		String oneweek_before="";
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   -7);
		oneweek_before = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		return oneweek_before;		
	}
	
	/**
	 * 获取昨天日期,20171012格式
	 * */
	public static String getYesterday(){
		String yesterday="";
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   -1);
		yesterday = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		return yesterday;
	}
	
	/**
	 * 获取今天日期,20171012格式
	 * */
	public static String getToday(){
		String today="";
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,0);
		today = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		return today;
	}
	
	/**
	 * 获取明天日期,20171012格式
	 * */
	public static String getTomorrow(){
		String tomorrow="";
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   +1);
		tomorrow = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		return tomorrow;
	}
	/** 
	* 获得指定日期的前n天日期 
	* @param specifiedDay 20171012格式
	* @return 
	* @throws Exception 
	*/ 
	public static String getSpecifiedDayNDaysBefore(String specifiedDay,int nums){ 
		Calendar c = Calendar.getInstance(); 
		Date date=null; 
		try { 
			specifiedDay=TimeDate.cutchar(specifiedDay);
		    date = new SimpleDateFormat("yyyyMMdd").parse(specifiedDay); 
		} catch (ParseException e) { 
		    e.printStackTrace(); 
		} 
		c.setTime(date); 
		int day=c.get(Calendar.DATE); 
		c.set(Calendar.DATE,day-nums); 
	
		String dayBefore=new SimpleDateFormat("yyyyMMdd").format(c.getTime()); 
		return dayBefore; 
	} 
	
	
	/** 
	* 获得指定日期的前一天 
	* @param specifiedDay 20171012格式
	* @return 
	* @throws Exception 
	*/ 
	public static String getSpecifiedDayBefore(String specifiedDay){ 
		Calendar c = Calendar.getInstance(); 
		Date date=null; 
		try { 
			specifiedDay=TimeDate.cutchar(specifiedDay);
		    date = new SimpleDateFormat("yyyyMMdd").parse(specifiedDay); 
		} catch (ParseException e) { 
		    e.printStackTrace(); 
		} 
		c.setTime(date); 
		int day=c.get(Calendar.DATE); 
		c.set(Calendar.DATE,day-1); 
	
		String dayBefore=new SimpleDateFormat("yyyyMMdd").format(c.getTime()); 
		return dayBefore; 
	} 
	/**
	 * 获取一月前日期,20171012格式
	 * */
	public static String getOneMonthBefore(){
		String onemonth_before="";
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   -30);
		onemonth_before = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		return onemonth_before;
	}
	
	/** 
	* 获得指定日期的后一天 
	* @param specifiedDay  20171012格式
	* @return 
	*/ 
	public static String getSpecifiedDayAfter(String specifiedDay){ 
		Calendar c = Calendar.getInstance(); 
		Date date=null; 
		try { 
			specifiedDay=TimeDate.cutchar(specifiedDay);
			date = new SimpleDateFormat("yyyyMMdd").parse(specifiedDay); 
		} catch (ParseException e) { 
		    e.printStackTrace(); 
		} 
		c.setTime(date); 
		int day=c.get(Calendar.DATE); 
		c.set(Calendar.DATE,day+1); 
	
		String dayAfter=new SimpleDateFormat("yyyyMMdd").format(c.getTime()); 
		return dayAfter; 
	} 
	
	
	 //输入起始月份yyyyMM格式和终止月份yyyyMM格式，获取这这两个月份之间的所有月份yyyyMM格式，
    // 返回List,包括起始月份，不包括终止月份
    public static List<String> getMonth_by_Startmonth_Endmonth(String starttime,String endtime) throws ParseException{
        List<String> monthlist=new ArrayList<String>();
        starttime=starttime.replace("-","").replace("/","").replace("_","");
        endtime=endtime.replace("-","").replace("/","").replace("_","");
        if(starttime.length()!=6&&!TimeDate.isnum(starttime)) return monthlist;
        if(endtime.length()!=6&&!TimeDate.isnum(endtime)) return monthlist;
        Date d1 = new SimpleDateFormat("yyyyMM").parse(starttime);
        Date d2 = new SimpleDateFormat("yyyyMM").parse(endtime);
        Calendar dd = Calendar.getInstance();
        dd.setTime(d1);
        while (dd.getTime().before(d2)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            String str = sdf.format(dd.getTime());
            monthlist.add(str);
            dd.add(Calendar.MONTH, 1);
        }
        return monthlist;
    }
    //输入时间范围yyyyMMdd~yyyyMMdd格式，获取所有月份yyyyMM格式，
    // 返回List,包括起始日期，不包括截止日期
    public static List<String> getMonth_by_timerang(String timerang) throws ParseException{
        List<String> monthlist=new ArrayList<String>();
        if(TimeDate.isNormaltime_rang(timerang)){
            String starttime=timerang.split("~")[0].substring(0,6).replace("-", "").replace("/","").replace("_", "");
            String endtime=timerang.split("~")[1].substring(0,6).replace("-", "").replace("/","").replace("_", "");
            monthlist=getMonth_by_Startmonth_Endmonth(starttime,endtime);
        }
        return monthlist;
    }
	
	
	
	/**将时间字符串(20171012,2017/10/12,2017-10-12,2017_10_12)转成时间戳
	 * @throws ParseException */
	public static long getTimeval_bydatestr(String timestr) throws ParseException{
		long timeval=0;
		if(TimeDate.isNormaldatenum(timestr)){
			timestr=timestr.replace("/","").replace("-","").replace("_","").replace(":","").trim().replace(" ","");
			if(timestr.length()>8) timestr=timestr.substring(0,7);
			if(timestr.length()==8){
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			    Date date=sdf.parse(timestr);
			    timeval=date.getTime()/1000;
			}
		}
		return timeval;
	}
	
	
    //判断输入字符串是不是数字
	public  static boolean isnum(String str){
		boolean flag=false;
		try {  
		    Integer.parseInt(str);  
		    flag=true;  
		} catch (NumberFormatException e) {  
			flag=false;  
		} 		
		return flag;
	}
   
	//判断输入的字符串是不是n位数字
    public static boolean isfixed_length_num(int length,String str){
    	boolean flag=false;
    	//int len=str.length();
    	if(isnum(str)){
    		if(Pattern.compile("^[0-9]{"+length+"}").matcher(str).matches()){
    			flag=true;
    		}
    	}    	
    	return flag;
    }	
	
	/**判断输入的字符串是不是数字日期格式*/
    public static boolean isNormaldatenum(String dateStr) {
    	boolean flag=false;
    	//Calendar calendar = Calendar.getInstance();
        if (dateStr == null || dateStr.trim().equals(""))   return flag;
        dateStr = dateStr.trim(); 
        //Date date = null;
        int len=dateStr.length();
        String pattern="";
        if(dateStr.contains("-")){
        	switch(len){
            case 4:
            	pattern="yyyy";
            	break;
            case 7:
            	pattern="yyyy-MM";
            	break;
            case 10:
            	pattern="yyyy-MM-dd";
            	break;
            case 13:
            	pattern="yyyy-MM-dd HH";
            	break;
            case 16:
            	pattern="yyyy-MM-dd HH-mm";
            	break;
            case 19:
            	pattern="yyyy-MM-dd HH-mm-ss";
            	break;
            default:
            	pattern="yyyy-MM-dd";
            	break;        	
            }
        }
        if(dateStr.contains("_")){
        	switch(len){
            case 4:
            	pattern="yyyy";
            	break;
            case 7:
            	pattern="yyyy_MM";
            	break;
            case 10:
            	pattern="yyyy_MM_dd";
            	break;
            case 13:
            	pattern="yyyy_MM_dd HH";
            	break;
            case 16:
            	pattern="yyyy_MM_dd HH_mm";
            	break;
            case 19:
            	pattern="yyyy_MM_dd HH_mm_ss";
            	break;
            default:
            	pattern="yyyy_MM_dd";
            	break;        	
            }
        }
        if(dateStr.contains("/")){
        	switch(len){
            case 4:
            	pattern="yyyy";
            	break;
            case 7:
            	pattern="yyyy/MM";
            	break;
            case 10:
            	pattern="yyyy/MM/dd";
            	break;
            case 13:
            	pattern="yyyy/MM/dd HH";
            	break;
            case 16:
            	pattern="yyyy/MM/dd HH/mm";
            	break;
            case 19:
            	pattern="yyyy/MM/dd HH/mm/ss";
            	break;
            default:
            	pattern="yyyy/MM/dd";
            	break;        	
            }
        }
        if(dateStr.contains(":")){
        	switch(len){
            case 4:
            	pattern="yyyy";
            	break;
            case 7:
            	pattern="yyyy:MM";
            	break;
            case 10:
            	pattern="yyyy:MM:dd";
            	break;
            case 13:
            	pattern="yyyy:MM:dd HH";
            	break;
            case 16:
            	pattern="yyyy:MM:dd HH:mm";
            	break;
            case 19:
            	pattern="yyyy:MM:dd HH:mm:ss";
            	break;
            default:
            	pattern="yyyy:MM:dd";
            	break;        	
            }
        }
        else if((!dateStr.contains(":"))&&(!dateStr.contains("/"))&&(!dateStr.contains("-"))&&(!dateStr.contains("_"))){
        	switch(len){
            case 4:
            	pattern="yyyy";
            	break;
            case 6:
            	pattern="yyyyMM";
            	break;
            case 8:
            	pattern="yyyyMMdd";
            	break;
            case 11:
            	pattern="yyyyMMdd HH";
            	break;
            case 14:
            	pattern="yyyyMMdd HH:mm";
            	break;
            case 17:
            	pattern="yyyyMMdd HH:mm:ss";
            	break;
            default:
            	pattern="yyyyMMdd";
            	break;        	
            }
        }
        
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);//这个的功能是不把1996-13-3 转换为1997-1-3
        dateFormat.setLenient(false);
        try {
            //date = dateFormat.parse(dateStr);
            flag=true;
        } catch (Exception e) {
        }
        return flag;
    }
    
    //判断输入的字符串是不是指定数字日期格式
    public static boolean isfixeddatenum(String dateStr,String pattern) {
    	boolean flag=false;
    	//Calendar calendar = Calendar.getInstance();
        if (dateStr == null || dateStr.trim().equals(""))   return flag;
        dateStr = dateStr.trim(); 
        //Date date = null;   
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);//这个的功能是不把1996-13-3 转换为1997-1-3
        dateFormat.setLenient(false);
        try {
            //date = dateFormat.parse(dateStr);
            flag=true;
        } catch (Exception e) {
        }
        return flag;
    }

    //判断输入的数字日期范围是不是日期格式，时间范围用~隔开
    public static boolean isNormaltime_rang(String time_rang){
    	boolean flag=false;
    	time_rang=cutchar(time_rang);
    	if(time_rang.contains("~")){
    		String[] arr=time_rang.split("~");
    		if(isNormaldatenum(arr[0])&&isNormaldatenum(arr[1])){
    			boolean fla=arr[0].length()==arr[1].length();    			
    			long begday=Long.parseLong(arr[0]);
        		long endday=Long.parseLong(arr[1]);
        		if(begday==endday) endday=begday+1;
    			if((fla==true)&&(endday>=begday)){
    				flag=true;
    			}
    		}    		
    	}
    	return flag;
    }
    
    //判断输入的数字日期范围是不是指定日期格式，时间范围用~隔开
    public static boolean isfixedtime_rang(String time_rang,String pattern){
    	boolean flag=false;
    	if(time_rang.contains("~")){
    		String[] arr=time_rang.split("~");
    		if(isfixeddatenum(arr[0],pattern)&&isfixeddatenum(arr[1],pattern)){
    	        arr[0]=cutchar(arr[0]);
    	        arr[1]=cutchar(arr[1]);
    			boolean fla=arr[0].length()==arr[1].length();
        		long begday=Long.parseLong(arr[0]);
        		long endday=Long.parseLong(arr[1]);
    			if((fla==true)&&(endday>=begday)){
    				flag=true;
    			}
    		}    		
    	}else{
    		if(isfixeddatenum(time_rang,pattern)){
    			flag=true;
    		}
    	}
    	return flag;
    }
    
    //根据输入的日期范围生成startday，endday，如果输入为空，则startday为当前日期，endday为明天日期
    public static  int[] getStartday_Endday_by_timerang(String time_rang){
    	//这里定义数组写成int[] startday_endady_arr = new int[2]; 
    	int[] startday_endady_arr = new int[2]; 
    	//获取今天的字符串日期，20170815格式
    	String nowday=timeop.getDayFromTime(timeop.GetCurrentTime()).replace("-","");
    	//获取今天的数字日期，20170815格式
    	int today=Integer.parseInt(nowday);
    	Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   +1);
		String tomorrow = new SimpleDateFormat( "yyyyMMdd").format(cal.getTime());
    	startday_endady_arr[0]=today;
    	startday_endady_arr[1]=Integer.parseInt(tomorrow);
    	int begday = 0;
    	int endday = 0;
    	if(time_rang.length()>0&&(!time_rang.equalsIgnoreCase("null"))&&time_rang.contains("~")&&!time_rang.contains("null")){
			if(isNormaltime_rang(time_rang)){
			    String[] dayarr=time_rang.split("~");
			    begday=Integer.parseInt(dayarr[0]);
			    endday=Integer.parseInt(dayarr[1]);				
		    	startday_endady_arr[0]=begday;
		    	startday_endady_arr[1]=endday;
			}
		}
    	return startday_endady_arr;
    }
    
    //去除时间字符串中的-,_,/字符
    public static String cutchar(String timestr){
    	if(timestr.contains("-")||timestr.contains("_")||timestr.contains("/")||timestr.contains(":")){
    		timestr=timestr.replace("-", "").replace("_", "").replace(":", "").trim().replace("/", "");
		}
    	return timestr;
    }
	
	
	/*获取一个日期范围字符串包含几天，返回的是数字，日期格式是yyyyMMdd
           比如输入20170528~20170602，返回的是6(前包后不包)，List(20170528,20170529,20170530,20170531,20170601,20170602)*/
   public static int getDayNums_bytimerang(String time_rang) throws ParseException {
	   time_rang=cutchar(time_rang);
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
	   int[] time_arr=getStartday_Endday_by_timerang(time_rang);	   
	   Date begin = sdf.parse(String.valueOf((date)));
	   Date end = sdf.parse(String.valueOf((date+1)));
		try {
			begin = sdf.parse(String.valueOf(time_arr[0]));
			end=sdf.parse(String.valueOf(time_arr[1]));
		} catch (ParseException e) {
			e.printStackTrace();
		}
       List<Long> result = new ArrayList<Long>();
       Calendar tempStart = Calendar.getInstance();
       tempStart.setTime(begin);
       while(begin.getTime()<=end.getTime()){
    	 Date dt=tempStart.getTime();
    	 long date_long=Long.parseLong(sdf.format(dt));
         result.add(date_long);
         tempStart.add(Calendar.DAY_OF_YEAR, 1);
         begin = tempStart.getTime();
      }
      if(result.size()>=2) result.remove(result.size()-1); 
      return result.size();
   }
    
   /*获取一个日期范围字符串所包含的所有日期，返回的是list<long>日期数组，日期格式是yyyyMMdd
           比如输入20170528~20170602，返回的是List(20170528,20170529,20170530,20170531,20170601,20170602)*/
   public static List<Long> getBetweenDates(String time_rang) throws ParseException {
	   time_rang=cutchar(time_rang);
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
	   int[] time_arr=getStartday_Endday_by_timerang(time_rang);	   
	   Date begin = sdf.parse(String.valueOf((date)));
	   Date end = sdf.parse(String.valueOf((date+1)));
		try {
			begin = sdf.parse(String.valueOf(time_arr[0]));
			end=sdf.parse(String.valueOf(time_arr[1]));
		} catch (ParseException e) {
			e.printStackTrace();
		}
       List<Long> result = new ArrayList<Long>();
       Calendar tempStart = Calendar.getInstance();
       tempStart.setTime(begin);
       while(begin.getTime()<=end.getTime()){
    	 Date dt=tempStart.getTime();
    	 long date_long=Long.parseLong(sdf.format(dt));
         result.add(date_long);
         tempStart.add(Calendar.DAY_OF_YEAR, 1);
         begin = tempStart.getTime();
      }
      if(result.size()>=2) result.remove(result.size()-1); 
      return result;
   }
   
   
   //输入两个long类型时间戳，返回一个数组，包括两个时间戳相距天数，小时，分钟数，秒数
    public static  long[] differentDaysByMillise(long stamp1,long stamp2)
    {
        long time=0;
        if(stamp1>stamp2){
            time=stamp1-stamp2;
        }else{
            time=stamp2-stamp1;
        }
        long minutes=time /( 60);
        long hours=time /( 60*60);
        long days=time /(60*60*24);
        //获取两个时间戳的间隔秒数
        long seconds=time/100;
        long[] Array={days,hours,minutes,seconds};
        return Array;
    }
    
}
