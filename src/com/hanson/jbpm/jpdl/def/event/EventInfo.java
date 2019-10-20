package com.hanson.jbpm.jpdl.def.event;

import java.util.Date;

/**
 * �ص��첽�¼�ʱ��Ϊ����ʹ��
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�EventInfo.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Nov 19, 2013
 */
public class EventInfo {
	
	public static final String TYPE_OVERTIME = "overtime"; //��ʱ�����¼�
	public static final String TYPE_ARCHIVE = "archive";   //�鵵�¼�
	public static final String TYPE_TASK_SUBMIT = "taskSubmit"; //������ת����¼���ָ��һ�ڵ���ת����һ�ڵ����
	
	private String eventType;
	
	private EventTargetUser[] targetUsers;
	
	private String processName;
	
	private Date archiveLastDate;    //�鵵���ر�ʱ��(��ʾ���鵵�ر�����<=��ʱ��Ĺ���)
	
	private Exception exception = null;
	
	private String instId = null;
	
	private String lastTaskId = null;

	public String getEventType() {
		return eventType;
	}
	

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public EventTargetUser[] getTargetUsers() {
		return targetUsers;
	}

	public void setTargetUsers(EventTargetUser[] targetUsers) {
		this.targetUsers = targetUsers;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}


	public void setArchiveLastDate(Date archiveLastDate) {
		this.archiveLastDate = archiveLastDate;
	}


	public Date getArchiveLastDate() {
		return archiveLastDate;
	}
	
	public Exception getException() {
		return exception;
	}


	public void setException(Exception exception) {
		this.exception = exception;
	}


	public String getInstId() {
		return instId;
	}


	public void setInstId(String instId) {
		this.instId = instId;
	}


	public String getLastTaskId() {
		return lastTaskId;
	}


	public void setLastTaskId(String lastTaskId) {
		this.lastTaskId = lastTaskId;
	}
	

}
