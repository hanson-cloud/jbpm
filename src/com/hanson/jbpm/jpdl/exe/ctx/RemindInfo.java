package com.hanson.jbpm.jpdl.exe.ctx;

/**
 * Copyright (C)2012 , ������̫�Ƽ��ɷ����޹�˾
 * �������������Ϣ
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�RemindInfo.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�May 16, 2012
 */
public class RemindInfo {
	private String reminded = "0";
	private String remindUser = "";
	private String remindMsg = "";
	
	public RemindInfo() {
		
	}
	
	public RemindInfo(String reminded, String remindUser, String remindMsg) {
		this.reminded = reminded;
		this.remindUser = remindUser;
		this.remindMsg = remindMsg;
	}
	
	
	public void setRemindUser(String remindUser) {
		this.remindUser = remindUser;
	}
	public String getReminded() {
		return reminded;
	}
	public void setReminded(String reminded) {
		this.reminded = reminded;
	}
	public String getRemindMsg() {
		return remindMsg;
	}
	public void setRemindMsg(String remindMsg) {
		this.remindMsg = remindMsg;
	}
	public String getRemindUser() {
		return remindUser;
	}
}
