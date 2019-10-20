package com.hanson.jbpm.mgmt;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.suntek.eap.core.app.AppHandle;

public class ProcessClient {	
	public static String MODULE = "jbpm";
	public static String TEMPLATE = "notes";	
	public static String HOST = AppHandle.getHandle("security")
			.getProperty("OPENEAP_NODE_NAME", "1").trim();
	
	public final static String REMIND_ACTION = "WORKITEM_REMIND";	
	public final static String UNLOCK_ACTION = "WORKITEM_UNLOCKER";
	public final static String DELETE_ACTION = "WORKITEM_DELETE";
	public final static String REOPEN_ACTION = "WORKITEM_REOPEN";
	
	public static String getIp() throws UnknownHostException
	{
		return InetAddress.getLocalHost().getHostAddress();
	}
}
