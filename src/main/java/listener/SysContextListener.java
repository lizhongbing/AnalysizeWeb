/**
 * 
 */
package listener;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import Common.DatabaseParas;
import Common.REDIS;
import datamanage.DataThreadManager;
public class SysContextListener implements ServletContextListener{

	private Timer timer = null;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		//在这里初始化监听器，在tomcat启动的时候监听器启动，可以在这里实现定时器功能
		timer = new Timer(true);
		System.out.println("启动成功");
		event.getServletContext().log("监听器已启动--------------------------------------------------");//添加日志，可在tomcat日志中查看到
		REDIS.redis_init();
		DatabaseParas.init();
		DataThreadManager.start();
	}

	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		//在这里关闭监听器，所以在这里销毁定时器。
		timer.cancel();
		event.getServletContext().log("定时器销毁--------------------------------------------------");
		
	}
   
	
}