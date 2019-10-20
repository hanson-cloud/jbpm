package com.hanson.jbpm.jpdl.exe.async;


import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.hanson.jbpm.log.CommonLogger;

/**
 * 
 * @author zt
 * @since 
 * @version 2012-11-28
 * @Copyright (C)2012 , Suntektech
 */
public class TriggerTaskCache
{
	private final static TriggerTaskCache cache = new TriggerTaskCache();
	
	private boolean isRunning = false;
	
	private Queue<TriggerTask> triggers;
	
	private Timer timer = null;	
	
	
	public TriggerTaskCache()
	{
	}
	
	public void start()
	{
		if (!isRunning) {
			triggers = new ConcurrentLinkedQueue<TriggerTask>();
			
			this.timer = new java.util.Timer(false);
			this.timer.schedule(new TriggerTimerTask(), 5 * 1000, 300 * 1000);
			
			CommonLogger.logger.info(">> �����¼���������ִ���߳�");
			
			this.isRunning = true;
		}
	}
	
	public static TriggerTaskCache getInstance()
	{	
		return cache;
	}
	
	public void addTask(TriggerTask task)
	{
		CommonLogger.logger.info("��Ӵ���������[" + this.triggers.size() + "]" + task.toString());
		this.triggers.offer(task);
	}
	
	public TriggerTask getTask()
	{
		return this.triggers.poll();
	}
	
	public void stop()
	{
		CommonLogger.logger.info("ֹͣ��ʱ��ִ��, ��������Ѽ��صĴ���������.");
		this.timer.cancel();		
		this.triggers.clear();		
	}
}
