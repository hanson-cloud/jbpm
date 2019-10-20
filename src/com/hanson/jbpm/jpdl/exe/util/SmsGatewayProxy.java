package com.hanson.jbpm.jpdl.exe.util;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.um.ISmsGateway;

public class SmsGatewayProxy
{
	private final static String from = AppHandle.getHandle(ProcessClient.MODULE).getProperty("SMS_FROM", "123456");
	
	public static void send(String mobile, String message)
	{
		try { 
			ISmsGateway proxy = (ISmsGateway) OpenCC.getComponentContainer().getComponent(ISmsGateway.class);
			proxy.send(from, mobile, message);
		} catch (Exception e) {
			CommonLogger.logger.error(e,e);
		}
	}
}
