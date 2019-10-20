package com.hanson.jbpm.dev;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstance;
import com.hanson.jbpm.identity.User;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.ctx.LuceneTableRollback;
import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstanceMgmt;
import com.hanson.jbpm.jpdl.exe.util.RequestTransformer;

/**
 * ��ʹ��ProcessInstanceService.java
 * @author tl
 *
 */
@Deprecated
public class NewInstanceService {
	/**
	 * �½�����ʵ���������������̵ĵ���
	 * ���� Transition ��ת���ƴ������֧�֣�Ĭ�Ͻ��������̵���������֧
	 * 
	 * @param user			������
	 * @param processName	������������
	 * @param context		��ȡ jdbc �������һ���̵�ִ�������ģ����������̵��ô�����һ������
	 * @param formElements	���̱�����
	 * 
	 * @deprecated
	 */
	public void post(User user, String processName,	ExecutionContext context, List<FormElement> formElements) {
		String transition = "";		// ����ת
		String instanceId = null;	// �µ�ʵ��
		String taskId = null;		
				
		ProcessInstance instance = null;
		
		try {
			ProcessInstanceMgmt pim = ProcessEngine.getProcessInstanceMgmt(processName);
						
			instance = pim.findInstance(instanceId, taskId);
			
			RequestTransformer transformer = new RequestTransformer();
			
			instance.setTaskContext(transformer.formDataToXml(formElements));
			
			instance.setEnvContext(transformer.userToXml(user.getLoginName()));
			
			instance.getExecutionContext().setJdbc(context.getJdbc());
			
			instance.getToken().leave(transition);			
			
		} catch (Exception ex) {
			new LuceneTableRollback().handleException(instance, taskId);					
			throw new BpmException("�½������ӿڷ�������쳣: " + ex.getMessage(), ex);
		}
	}
	
	/**
	 * �½�����ʵ���������������̵ĵ���
	 * ���� Transition ��ת���ƴ������֧�֣�Ĭ�Ͻ��������̵���������֧
	 * 
	 * @param request		servlet ����
	 * @param processName	������������
	 * @param context		��ȡ jdbc �������һ���̵�ִ�������ģ����������̵��ô�����һ������
	 * @param formElements	���̱�����
	 * 
	 */
	public void post(HttpServletRequest request, String processName,
			ExecutionContext context, List<FormElement> formElements) {
		String transition = "";		// ����ת
		String instanceId = null;	// �µ�ʵ��
		String taskId = null;		
				
		ProcessInstance instance = null;
		
		try {
			ProcessInstanceMgmt pim = ProcessEngine.getProcessInstanceMgmt(processName);
						
			instance = pim.findInstance(instanceId, taskId);
			
			instance.setRequest(request);
			
			RequestTransformer transformer = new RequestTransformer();
			
			instance.setTaskContext(transformer.formDataToXml(formElements));
			
			instance.setEnvContext(transformer.envDataToXml(request));
			
			instance.getExecutionContext().setJdbc(context.getJdbc());
			
			instance.getToken().leave(transition);			
			
		} catch (Exception ex) {
			new LuceneTableRollback().handleException(instance, taskId);					
			throw new BpmException("�½������ӿڷ�������쳣: " + ex.getMessage(), ex);
		}
	}
}
