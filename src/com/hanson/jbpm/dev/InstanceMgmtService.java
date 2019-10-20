package com.hanson.jbpm.dev;

import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;

public class InstanceMgmtService {
	
	private String instanceId;
	//private ProcessDefinition processDefinition;
	private String processName;
	
	public InstanceMgmtService(ProcessDefinition processDefinition, String instanceId) {
		this.instanceId = instanceId;
		//this.processDefinition = processDefinition;
		this.processName = processDefinition.getProcessName();
	}
		
	public void setCurrentTaskFormData(String taskId, Map<String, Object> map) throws BpmException {
		map.put("TASK_ID", taskId);
		new TaskFormDataModifier(processName, instanceId, false).modify(map);
	}
	
	public void setHistoryTaskFormData(String taskId, Map<String, Object> map) throws BpmException {
		map.put("TASK_ID", taskId);
		new TaskFormDataModifier(processName, instanceId, true).modify(map);
	}
	
	/**
	 * @deprecated ���� 0.9 �汾��TaskId ������ map ���洫��
	 * @param map
	 * @throws BpmException
	 */
	public void setFormData(Map<String, Object> map) throws BpmException {				
		new TaskFormDataModifier(processName, instanceId, true).modify(map);
	}	
}
