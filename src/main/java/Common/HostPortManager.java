package Common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

import debug.isDebug;
import probd.hbase.common.MyLog;

public class HostPortManager {
	
	public static int bindport=9000;
	
	/**
	 * 获取主机IP
	 * @return
	 */
	public static String getLinuxOrWindowIP(){
		String ip=null;
		if(isDebug.islocal){
			try {
				ip=getWindowIp();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			ip=getLinuxIp();
		}
		return ip;
	}
	
	/**
	 * 获取可使用的端口
	 * @return
	 */
	public static int getListernPort(){
		bindport++;
		boolean portUsing = isPortUsing(getLinuxOrWindowIP(), bindport);
		if(portUsing){
			getListernPort();
		}
		if(bindport>10000)bindport=9000;
		return bindport;
	}
	
	/**
	 * 获取linux中eth1的ip地址
	 * @return
	 */
	public static String getLinuxIp(){
		String ipstr=null;
		InetAddress host = null;
		try {
			host = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		ipstr=host.getHostAddress();
		return ipstr;
	}
	
	/**
	 * 获取本机ip
	 * @return
	 * @throws Exception
	 */
	public static String getWindowIp() throws Exception {
	    try {
	        InetAddress candidateAddress = null;
	        // 遍历所有的网络接口
	        for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // 在所有的接口下再遍历IP
	            for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
	                    if (inetAddr.isSiteLocalAddress()) {
	                        // 如果是site-local地址，就是它了
	                        return inetAddr.toString().replace("/","");
	                    } else if (candidateAddress == null) {
	                        // site-local类型的地址未被发现，先记录候选地址
	                        candidateAddress = inetAddr;
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            return candidateAddress.toString().replace("/","");
	        }
	        // 如果没有发现 non-loopback地址.只能用最次选的方案
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        return jdkSuppliedAddress.toString().replace("/","");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	 /*** 
     * 判断主机Host的port端口是否被使用
     * @param host 
     * @param port 
     * @throws UnknownHostException  
     */  
	public static boolean isPortUsing(String host,int port){  
        boolean flag = false;  
        try {  
        		InetAddress Address = InetAddress.getByName(host);  
			@SuppressWarnings({ "unused", "resource" })
			Socket socket = new Socket(Address,port);  //建立一个Socket连接
            flag = true;  
        } catch (IOException e) {  
        }  
        return flag;  
    }

}
