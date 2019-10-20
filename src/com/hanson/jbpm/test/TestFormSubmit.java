package com.hanson.jbpm.test;

import java.util.HashMap;
import java.util.Map;

import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstance;
import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.identity.User;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstanceMgmt;
import com.hanson.jbpm.mgmt.ProcessClient;

public class TestFormSubmit {
	public void testTask() 
	{		
		try {
			ProcessInstance instance;
			
			String processName = "gas";
			ProcessClient.MODULE = "workflow";
			
			String instanceId = "20120813233624001";
			
			ProcessInstanceMgmt pim = ProcessEngine.getProcessInstanceMgmt(processName);
			
			instance = pim.findInstance(instanceId);
			
			Map<String, String> form = new HashMap<String, String>();
			form.put("RECON_RESULT", "01");
			form.put("RECON_REMARK", "���Կ�����");			
			instance.setTaskContext(form);
			
			User user = new SysUser("zhout");
			instance.setEnvContext(user);
									
			instance.getToken().leave("");
						
		} catch (Exception ex) {
			throw new BpmException(ex);
		} 
	}
	
	public void testStart() 
	{
		try {
			ProcessInstance instance;
			
			ProcessClient.MODULE = "workflow";
			String processName = "gas";
			
			ProcessInstanceMgmt pim = ProcessEngine.getProcessInstanceMgmt(processName);
						
			instance = pim.newInstance();
			
			Map<String, String> form = new HashMap<String, String>();
			form.put("BIZ_TITLE", "abc ���Թ���112341����23433333");
			instance.setTaskContext(form);
			
			User user = new SysUser("zhout");
			instance.setEnvContext(user);
						
			instance.getToken().leave("");
						
		} catch (Exception ex) {
			throw new BpmException(ex);
		} 
	}
		
	public static void main(String[] args) {
		System.setProperty("EAP_HOME", "f:/tomcat");
		System.setProperty("EAP_DEBUG", "true");
		System.setProperty("ECLIPSE_HOME", "f:/eclipse");
		
		//new TestFormSubmit().testTask();
		new TestFormSubmit().testStart();
	}
}
