package com.hanson.jbpm.dev.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hanson.jbpm.identity.User;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstance;
import com.suntek.ccf.pico.IComponentAdvice;
import com.suntek.ccf.web.service.ServiceInvocationException;
import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstanceMgmt;
import com.hanson.jbpm.mgmt.ProcessClient;

public class ReturnStepService implements IComponentAdvice
{

	private String userCode = "";
	
	public String exec(String module, String procName, String instId, String transition, String taskTitle)
	{
		try {
			ProcessInstance instance;
			
			ProcessClient.MODULE = module;
						
			ProcessInstanceMgmt pim = ProcessEngine.getProcessInstanceMgmt(procName);
			
			instance = pim.findInstance(instId);
			
			Map<String, String> form = new HashMap<String, String>();
			form.put("taskTitle", taskTitle);		
			instance.setTaskContext(form);
			
			User user = new SysUser(userCode);
			instance.setEnvContext(user);
									
			instance.getToken().leave(transition);
			
			return "OK";
			
		} catch (Exception ex) {
			throw new BpmException(ex);
		}
	}
	
	public void after(HttpServletRequest arg0) throws ServiceInvocationException {}

	public void before(HttpServletRequest req) throws ServiceInvocationException
	{
		userCode = req.getRemoteUser();
	}

}
