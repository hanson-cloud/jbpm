package com.hanson.jbpm.jpdl.exe.ctx;


import java.lang.reflect.InvocationTargetException;

import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.dev.action.ArchiveAction;
import com.hanson.jbpm.dev.action.DelegateSetAction;
import com.hanson.jbpm.dev.action.DeleteAction;
import com.hanson.jbpm.dev.action.LockAction;
import com.hanson.jbpm.dev.action.ReOpenAction;
import com.hanson.jbpm.dev.action.RecordTaskTrackAction;
import com.hanson.jbpm.dev.action.RedrawAction;
import com.hanson.jbpm.dev.action.RemindAction;

public class ProcessInstanceMgmt {
	private ProcessDefinition processDefinition;
	
	public ProcessInstanceMgmt() {}
	
	public ProcessInstanceMgmt(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}  
	
	/**
	 * ��ȡ����ʵ����ִ�б�����ά�� 
	 * @param instanceId
	 * @return
	 */
	/*public InstanceMgmtService findInstance(String instanceId) {
		return new InstanceMgmtService(processDefinition, instanceId);
	}*/
	
	/**
	 * ��ȡ����ʵ������ѯ����״̬����
	 * @param instanceId
	 * @param taskId
	 * @return
	 */
	public ProcessInstance findInstance(String instanceId, String taskId) {
		ProcessInstance pi = new ProcessInstance(processDefinition, instanceId, taskId);
		return pi;
	}
	
	public ProcessInstance findInstance(String instanceId) {
		String sql = "select max(TASK_ID) TASKID from BPM_TASK where INST_ID='" + instanceId + "'";
		String taskId = (String)DaoFactory.getJdbc(ProcessClient.MODULE).queryForMap(sql).get("TASKID");
		ProcessInstance pi = new ProcessInstance(processDefinition, instanceId, taskId);
		return pi;
	}
	
	public ProcessInstance newInstance() {
		return new ProcessInstance(processDefinition, null, null);
	}
	
	/**
	 * ɾ������ʵ��
	 * @param instanceId
	 * @return
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException
	 */
	public String deleteIntance(String instanceId, String bizId, String processName) 
	throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return new DeleteAction().execute(instanceId, bizId, processName);
	}
	
	/**
	 * ָ�����������
	 * @param instId
	 * @param taskId
	 * @param userId
	 * @return
	 */
	public String changeAssignmentRole(String taskId, String oldUserId, String userId) {
		return new DelegateSetAction().execute(taskId, oldUserId, userId);
	}
	
	public String recordTaskTrack(String userId, String taskId, String taskName, String msg) {
		return new RecordTaskTrackAction().execute(userId, taskId, taskName, msg);
	}
	
	/**
	 * ������������
	 * @param taskId
	 * @param userId
	 * @return
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 */
	public String batchLock(String taskIds, String userId) 
	throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return new LockAction().batchLock(taskIds, userId);
	}
	
	/**
	 * ���������������
	 * @param taskId
	 * @param userId
	 * 
	 * �ӿڷ�����
	 * 		PicoContainer.getInstance().registerImplement(ProcessClient.UNLOCK_ACTION, clazz, methodName);
	 * 		clazz.method = function(bizId, userId)
	 * 
	 * @return
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 */
	public String batchUnlock(String taskIds, String userId) 
	throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return new LockAction().batchUnlock(taskIds, userId);
	}
	
	/**
	 * �ߵ�����
	 * 
	 * �ӿڷ�����
	 * 		PicoContainer.getInstance().registerImplement("WORKITEM_REMIND", clazz, methodName);
	 * 		clazz.method = function(bizId, userId)
	 * 
	 * @param taskId
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public String remindInstance(String instId, String taskId, String bizId, String userId, String msg) throws Exception 
	{
		return new RemindAction().execute(instId, taskId, bizId, userId, msg);
	}
	
	/**
	 * �س�����
	 *  
	 * @param taskId
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public String redrawTask(String instId, String taskId, String prevTaskId, String taskName, 
			String bizId, String userId, String msg) throws Exception 
	{
		return new RedrawAction().execute(instId, taskId, prevTaskId, taskName, userId, msg);
	}
	
	/**
	 * ���´򿪹���
	 * @param processName 	�򿪹������ڵ�����
	 * @param instId		�򿪹�����ʵ������
	 * @param bizId			�򿪹�����ҵ����룬���ͻ��˵Ļص�����ʹ��
	 * @param userCode		�򿪹����ͽ��ܹ����������
	 * @return			ִ�н��
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public String reopenInstance(String instId, String bizId, String userCode, String reason) 
	throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return new ReOpenAction().execute(instId, bizId, this.processDefinition.getProcessName(), userCode, reason);
	}
	
	public String archiveInstance(String instId) {
		return new ArchiveAction().execute(instId);
	}
}
