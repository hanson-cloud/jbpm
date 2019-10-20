/**
 * 
 */
package com.hanson.jbpm.dev;

import java.util.Map;

import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.log.CommonLogger;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstance;
import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstanceMgmt;


/**
 * @author tl
 * ʵ�������̵��ýӿ�
 */
public class ProcessInstanceService {
	
	
	/**
	 * �½�����
	 * @param instId      ����ʵ��ID
	 * @param processName ������
	 * @param user        ִ�й�����openeap�û�
	 * @param title       �˴ι�����ת�ı���
	 * @param transition  ִ��transition�ߵ�����
	 * @param formParams  ������
	 * @throws Exception 
	 */
	public static void newInstance(String instId, String processName, SysUser user,
			String title, String transition, Map formParams) throws Exception {
		
		CommonLogger.logger.debug("[�ӿڵ���]�½�������" + instId);
		
		ProcessInstanceMgmt pim = ProcessEngine.getProcessInstanceMgmt(processName);
		ProcessInstance instance = pim.newInstance();
		instance.setEnvContext(user);	
		instance.setTaskContext(formParams);
		instance.getInstanceContext().setFormElement(
				Context.values.INSTID, instId);
		if(title != null) {
			instance.getInstanceContext().setFormElement(
					Context.values.TASKTITLE, title);	//�������
			instance.getInstanceContext().appendRuntimeVariable(Context.values.TASKTITLE, title);
		}
		
		
		
		instance.getInstanceContext().setFormElement(Context.values.UID, user.getLoginName());
		instance.getToken().leave(transition);
		
		
	}
	
	
	/**
	 * ������������
	 * @param instId       ����ʵ��ID
	 * @param taskId       ��һ�������TASK_ID
	 * @param processName  ��������
	 * @param user         ִ�й�����openeap�û�
	 * @param title        �˴ι�����ת�ı���
	 * @param transition  ִ��transition�ߵ�����
	 * @param formParams  ������
	 * @throws Exception 
	 */
	public static void runInstance(String instId, String taskId, String processName, SysUser user, 
			String title, String transition, Map formParams) throws Exception {
		
		CommonLogger.logger.debug("[�ӿڵ���]ִ�й�����" + instId);
		
		ProcessInstanceMgmt pim = ProcessEngine.getProcessInstanceMgmt(processName);
		ProcessInstance instance = pim.findInstance(instId, taskId);
		instance.setEnvContext(user);	
		instance.setTaskContext(formParams);
		instance.getInstanceContext().setFormElement(
				Context.values.INSTID, instId);
		instance.getInstanceContext().setFormElement(Context.values.TASKID, taskId);
		if(title != null) {
			instance.getInstanceContext().setFormElement(
					Context.values.TASKTITLE, title);	//�������
			instance.getInstanceContext().appendRuntimeVariable(Context.values.TASKTITLE, title);
		}
		
		instance.getInstanceContext().setFormElement(Context.values.UID, user.getLoginName());
		instance.getToken().leave(transition);
	}
	

}
