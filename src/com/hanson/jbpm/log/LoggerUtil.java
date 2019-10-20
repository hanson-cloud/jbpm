package com.hanson.jbpm.log;

import org.apache.log4j.Logger;

/**
 * ���ڸ���������������̬�������л���Ի����µ�Logger���
 * 
 * @author Zhou Huan
 *
 * 2009-10-14
 *
 */
public class LoggerUtil {

	private static boolean IS_DEBUG = (System.getProperty("EAP_DEBUG") != null);
	
	public static void setDebugMode() {
		IS_DEBUG = true;
	}


	public static Logger getLogger() {
		if(IS_DEBUG) {
			return new DebugLogger();
		} else {
			return CommonLogger.logger;
		}
	}
	
	
	static class DebugLogger extends Logger {
		public DebugLogger() {
			super("eap.debug");
		}
			
		@Override
		public void debug(Object message) {
			System.out.println(message);
		}
		
		@Override
		public void info(Object message) {
			System.out.println(message);
		}

		@Override
		public void error(Object message, Throwable t) {
			t.printStackTrace();
		}
	}
}
