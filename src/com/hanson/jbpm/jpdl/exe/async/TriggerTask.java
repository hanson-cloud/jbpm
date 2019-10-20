package com.hanson.jbpm.jpdl.exe.async;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.structure.exception.EntityNotFoundException;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TargetFKUtil;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TriggerHandler;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TriggerHandler.TriggerAction;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TriggerHandler.TriggerTarget;
import com.hanson.jbpm.jpdl.exe.util.MailSender;
import com.hanson.jbpm.jpdl.exe.util.SmsGatewayProxy;


public class TriggerTask
{
	private String expression;
	private TriggerAction action;
	private TriggerTarget target;
	private String targetFK;
	
	public TriggerTask(String expression, TriggerAction action, TriggerTarget target, String targetFK) 
	{
		this.expression = expression;
		this.action = action;
		this.target = target;
		this.targetFK = targetFK;
	}	
	
	public void exec() throws EntityNotFoundException {
		if (action == TriggerHandler.TriggerAction.email) {
			MailSender cn = new MailSender(AppHandle.getHandle("jbpm").getProperty("EMAIL_SERVER", "mail.suntektech.com"));
			cn.setFrom(AppHandle.getHandle("jbpm").getProperty("EMAIL_USER", "zt@suntektech.com"), 
					   AppHandle.getHandle("jbpm").getProperty("EMAIL_PASSWORD", "eyx6nv")); 		
			
			String targetEmail = TargetFKUtil.getEmail(target, targetFK);		
			CommonLogger.logger.debug(targetEmail + " => " + expression);
			cn.send(targetEmail, expression, "������.", null);
		}
		
		if (action == TriggerHandler.TriggerAction.sms) {
			String targetMobile = TargetFKUtil.getMobile(target, targetFK);			
			SmsGatewayProxy.send(targetMobile, expression);
		}
	}
	
	public String toString()
	{
		return expression + "," + action + "," + target + "," + targetFK;
	}
}
