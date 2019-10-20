package com.hanson.jbpm.log;

import org.apache.log4j.Logger;

import com.suntek.eap.core.log.ILogger;
import com.suntek.eap.core.log.IVersion;

public class TimerLogger extends ILogger {
	public TimerLogger() {}

	// ������ʱ��̬ѡ����־���
	public static Logger logger = new TimerLogger().getLogger();

	public String getLoggerName() {
		return "jbpm-timer";
	}

	public IVersion getModuleVersion() {
		return new CommonVersion();
	}
}
