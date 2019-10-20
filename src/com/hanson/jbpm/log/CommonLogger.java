package com.hanson.jbpm.log;
 
import org.apache.log4j.Logger;

import com.suntek.eap.core.log.ILogger;
import com.suntek.eap.core.log.IVersion;

public class CommonLogger extends ILogger {
	public CommonLogger() {}

	// ������ʱ��̬ѡ����־���
	public static Logger logger = new CommonLogger().getLogger();

	public String getLoggerName() {
		return "jbpm";
	}

	public IVersion getModuleVersion() {
		return new CommonVersion();
	}
}
