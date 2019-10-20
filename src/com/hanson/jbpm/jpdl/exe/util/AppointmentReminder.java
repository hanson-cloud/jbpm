package com.hanson.jbpm.jpdl.exe.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.um.IPortalMessageGateway;

/**
 * ���÷��Ͷ�ʱԤԼ������Ϣ
 * �����趨һ������ʱ��, jbpm���������ʱ���ָ���˷�����Ϣ, (ð������, �ʼ�����, ��������)
 * TODO ��taskPool���е�Task��̫��ʱ, �����Ƽ��������
 * Copyright (C)2012 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�MessageReminder.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Jun 12, 2012
 */
public class AppointmentReminder {
	
	public enum Type {portal, sms, mail};
	
	private AppointmentReminder() {
		
	}
	
	private static AppointmentReminder reminder = new AppointmentReminder();
	
	/* TimerTask�����, ÿ�β����µ�TimerTask���Ա����ڸó���, ���Ը��� */
	private List<RemindTask> taskPool = new ArrayList<RemindTask>();
	
	private Timer timer = new Timer();
	
	
	/**
	 * �Ӷ�����л�ȡһ��ִ����Ķ���
	 * @return
	 */
	private synchronized RemindTask getTask() {
		for(RemindTask task : taskPool) {
			if(task.getState() == 0) {
				return task;
			}
		}
		RemindTask task = new RemindTask();
		taskPool.add(task);
		return task;
	}
	

	
	/**
	 * ������ϯ��Ϣ
	 * @param agentIds
	 * @param msg
	 * @param sendDate
	 */
	@SuppressWarnings("deprecation")
	public static void sendFromPortal(String[] agentIds, String msg, Date sendDate) {
		CommonLogger.logger.debug("[MessageReminder]ԤԼ��" + sendDate.toLocaleString() + " ����ϯ������Ϣ");
		RemindTask task = reminder.getTask();
		task.setPortalMsg(agentIds, msg, sendDate);
		reminder.timer.schedule(task, sendDate);
	}
	
	/**
	 * ���Ͷ�����Ϣ
	 * @param tels
	 * @param msg
	 * @param sendDate
	 */
	public static void sendFromSms(String[] tels, String msg, Date sendDate) {
		//TODO ��ʵ��
	}
	
	/**
	 * �����ʼ�
	 * @param addresses
	 * @param title
	 * @param msg
	 * @param sendDate
	 */
	public static void sendFromMail(String[] addresses, String title, String msg, Date sendDate) {
		//TODO ��ʵ��
	}
	
	
	class RemindTask extends TimerTask {
		
		
		private String[] agentIds;
		private String[] tels;
		private String[] addresses;
		private String title;
		private String msg;
		private Type type;
		private Date sendDate;
		
		public void setPortalMsg(String[] agentIds, String msg, Date sendDate) {
			this.agentIds = agentIds;
			this.msg = msg;
			this.sendDate = sendDate;
			type = Type.portal;
			
		}
		
		public void setSmsMsg(String[] tels, String msg, Date sendDate) {
			this.tels = tels;
			this.msg = msg;
			this.sendDate = sendDate;
			type = Type.sms;
		}
		
		public void setMailMsg(String[] addresses, String title, String msg, Date sendDate) {
			this.addresses = addresses;
			this.title = title;
			this.msg = msg;
			this.sendDate = sendDate;
			type = Type.mail;
		}
		
		
		/**
		 * ִ�з�����Ϣ����, ��task������ָ��ԤԼʱ��ִ��
		 */
		@Override
		public void run() {
			if(type == null) {
				return ;
			}
			switch(type) {
				case portal :
					sendPortalMsg();
					break;
				case sms : break;
				case mail : break;
			}
			setStateVirgin();
		}
		
		@SuppressWarnings("deprecation")
		private void sendPortalMsg() {
			if(agentIds == null) {
				return;
			}
			CommonLogger.logger.debug("[MessageReminder.RemindTask]��ԤԼʱ��" + sendDate.toLocaleString()
					+ "����ϯ��������");
			IPortalMessageGateway mg = null; 
			try {
				mg = (IPortalMessageGateway) OpenCC.getComponentContainer()
					.getComponent(IPortalMessageGateway.class);
			} catch (Exception ex) {}
			mg.sendToAgent(agentIds, IPortalMessageGateway.LEVEL_MIDDLE, msg, false);
			
		}
		
		
		/**
		 * ��ȡTimerTask�е�state����
		 * ����state�ǰ��ڷ���, ������ֻ���÷����ȡ��ֵ
		 * @return
		 */
		public synchronized int getState() {
			try {
    			Field field = TimerTask.class.getDeclaredField("state");
    			field.setAccessible(true);
				return field.getInt(this);
			}catch(Exception e) {
				return 1;
			}
		}
		
		
		/**
		 * ��TimerTaskִ����Ϻ����½���state�������ó�VIRGIN״̬, �����ظ�����
		 * һ��TimerTask�ٱ�Timerִ����Ϻ�,state����ΪEXECUTED����SCHEDULED,�ٴ�ʹ�ý��׳��쳣
		 * ����state�����ǰ��ڿɷ���, ����Ҳֻ���÷���ȥ�ı���ֵ
		 */
		private synchronized void setStateVirgin() {
			try {
    			Field field = TimerTask.class.getDeclaredField("state");
    			field.setAccessible(true);
				field.set(this, 0);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
