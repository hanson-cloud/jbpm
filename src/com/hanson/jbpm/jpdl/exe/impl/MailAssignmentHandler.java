package com.hanson.jbpm.jpdl.exe.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.hanson.jbpm.identity.User;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.cipher.JbpmCipher;
import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.MailSender;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.eap.core.app.AppHandle;
import com.hanson.jbpm.identity.SysUser;


/**
 * Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�SendMailActionHandler.java
 * <p>ժ����Ҫ���ʼ��ɵ�������
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Oct 20, 2011
 */
public class MailAssignmentHandler implements AssignmentHandler {

	public static String DEFUALT_MAIL_TITLE = "";
	public static String DEFAULT_MAIL_CONTENT = "�����������ӷ��ʴ˹���������(������ܵ��,�븴�Ƶ�������ٴ�):\n";
	private String openeapInternetIp = AppHandle.getHandle(ProcessEngine.getClientModuleName())
											.getProperty("OPENEAP_INTERNET_IP");
	private MailSender sender;
	
	
	public MailAssignmentHandler() {
		if("".equals(DEFUALT_MAIL_TITLE)) {
			DEFUALT_MAIL_TITLE = AppHandle.getHandle(ProcessEngine.getClientModuleName())
					.getProperty("EMAIL_TITLE");
		}
	}
	
	
	public void assign(ExecutionContext ctx) throws Exception {
		
		try {
			List<String> users = ctx.getAssignable().getUsers();
			List<String> ccUsers = ctx.getAssignable().getCCUsers();
			
			String bizId = ctx.getInstanceContext().getParameter(Context.values.BIZID, Context.types.FORM);
			String taskId = ctx.getInstanceContext().getParameter(Context.values.TASKID, Context.types.FORM);
			String instId = ctx.getInstanceContext().getParameter(Context.values.INSTID, Context.types.FORM);
			String processName = ctx.getProcessDefinition().getProcessName();
			
			sendToUser(bizId, instId, taskId, processName, users, buildTitle(ctx, false), false);
			sendToUser(bizId, instId, taskId, processName, ccUsers, buildTitle(ctx, true), true);
		}catch(Exception e) {
			CommonLogger.logger.error(e, e);
			throw new Exception("�����ʼ��ɵ�ʧ��, ���鷢���˵������ַ�Ƿ���ȷ����, �Լ�JBPM�ʼ��������Ƿ���ȷ����");
		}
		
	}
	
	
	public void sendToUser(String bizId, String instId, String taskId, String processName,
			List<String> userIds, String title, boolean isReadonly) throws Exception {
		User[] users = new SysUser().getMultiInstance(userIds);
		for(User user : users) {
			String email = user.getEmail();
			if(email == null || "".equals(email)) {
				continue;
			}
			if(sender == null) {
				sender = getJbpmMailSender();
			}
			
			String content = getMailInfo(bizId, instId, taskId, processName, user, isReadonly);
			
			sender.send(email, title, content, null);
			CommonLogger.logger.debug("[MailActionHandler]�����ʼ��ɵ�to:" + user.getName()
					+ "(" + user.getId() + ")" + content);
		}
	}
	
	
	/**
	 * �����ʼ���������
	 * @param ctx
	 * @param userInfo
	 * @param isReadonly
	 * @throws Exception 
	 */
	protected String getMailInfo(String bizId, String instId, String taskId, String processName,
			User user, boolean isReadonly) throws Exception {
		StringBuffer content = new StringBuffer(DEFAULT_MAIL_CONTENT);
		String userId = user.getLoginName();
		String password = user.getPassword();
		password = new JbpmCipher().enCrypt(password);
		password = password.replace("=", "%3D").replace("+", "%2B"); //��URL�еĵȺ�,�Ӻ���ת��ΪURL����
		String moduleName = ProcessClient.MODULE;
		
		processName = URLEncoder.encode(processName, "UTF-8");
		
		content.append(openeapInternetIp)
			.append("/jbpm/jsp/AccessFromExternal.jsp?loginUser=").append(userId)
			.append("&password=").append(password).append("&moduleName=")
			.append(moduleName).append("&taskId=").append(taskId)
			.append("&instId=").append(instId)
			.append("&bizId=").append(bizId).append("&processName=")
			.append(processName).append("&readonly=");
		if(isReadonly) {
			content.append("true");
		}else {
			content.append("false");
		}
		
		return content.toString();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public MailSender getJbpmMailSender() {
		String emailServer = AppHandle.getHandle(ProcessEngine.getClientModuleName())
			.getProperty("EMAIL_SERVER");
		String emailUser = AppHandle.getHandle(ProcessEngine.getClientModuleName())
			.getProperty("EMAIL_USER");
		String emailPassword = AppHandle.getHandle(ProcessEngine.getClientModuleName())
			.getProperty("EMAIL_PASSWORD");
		CommonLogger.logger.debug("[MailActionHandler]��ȡ�ʼ���������Ϣ server:"
				+ emailServer + " user:" + emailUser + " password:" + emailPassword);
		
		MailSender mailSender = new MailSender(emailServer);
		mailSender.setFrom(emailUser, emailPassword);
		return mailSender;
	}
	
	
	protected String buildTitle(ExecutionContext ctx, boolean isReadonly) {
		String title = DEFUALT_MAIL_TITLE ;
		if(isReadonly) {
			title += "(����)";
		}
		title += "��" + ctx.getProcessDefinition().getProcessName() + "��:" + ctx.getInstanceContext()
			.getParameter(Context.values.TASKTITLE, Context.types.RUNTIME);
		return title;
	}
	
	
	public static void main(String[] args) {
		try {
			System.out.println(URLEncoder.encode("+", "UTF-8"));
		}catch(UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
