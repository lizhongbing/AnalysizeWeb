package Common;

import java.sql.Connection;

import com.proweb.common.conf.proProperties;
import com.proweb.mysqlobject.mysqlObject;
import com.proweb.mysqlobject.mysql_connect_manage;

import debug.isDebug;

public class ConnectToDatabase {
	
	/**表示数据库连接是否已经初始化*/
	private static boolean connected = false;
	
	/**
	 * 获取连接数据库的参数配置信息
	 * @return String[] 配置信息
	 */
    public static String[] getConnectInfo(){  
	    	String path = isDebug.islocal ? Constant.LOCAL_DEBUG_DIR + "db.properties" : Constant.SERVER_DB_PROPERTIES_PATH;
	    	proProperties.InitDbFile(path);	   	
	    String[] paras = new String[4];
	   	paras[0]=proProperties.getValue("hostname");
	   	paras[1]=proProperties.getValue("taskdatabase");
	   	paras[2]=proProperties.getValue("username");
	   	paras[3]=proProperties.getValue("password");
		return paras; 
    }
    
    /**
     * 连接数据库
     */
    public static synchronized void connect() {
    		if(connected)return;
    		String[] paras = getConnectInfo();
		mysqlObject.SetConnectInfo(paras[0],paras[1],paras[2],paras[3]);	
		//初始化连接
		Connection conn = mysql_connect_manage.ConnectSqlDefault(paras[1]);
		//将连接添加到连接池中
		mysql_connect_manage.CloseConnect(conn);
		connected = true;
	}

}
