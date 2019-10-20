package com.hanson.jbpm.jpdl.exe.async;


import java.util.TimerTask;

import com.suntek.eap.structure.exception.EntityNotFoundException;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.log.TimerLogger;

public class TriggerTimerTask extends TimerTask
{
	public void run()
	{
		
		TriggerTask task = TriggerTaskCache.getInstance().getTask();	
		if (task != null) {
			TimerLogger.logger.debug(">> ִ�д�������������: " + task.toString());
			try {
				task.exec();				
			} catch (EntityNotFoundException e) {
				CommonLogger.logger.error(e,e);
			}
		} else {
			TimerLogger.logger.debug(">> ����������������");
		}
	}
}
