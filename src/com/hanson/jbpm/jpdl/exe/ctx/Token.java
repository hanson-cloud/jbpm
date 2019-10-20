package com.hanson.jbpm.jpdl.exe.ctx;


import com.hanson.jbpm.jpdl.def.event.EventInfo;
import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.DocumentException;
import org.hibernate.HibernateException;

import com.hanson.jbpm.jpdl.def.event.Event;
import com.hanson.jbpm.jpdl.def.node.NodeBase;
import com.hanson.jbpm.jpdl.exe.impl.EventHandler;


public class Token {
	private NodeBase node;
	private ProcessInstance instance;
	
	public Token(NodeBase node) {
		this.node = node;
	}
	
	public void setProcessInstance(ProcessInstance instance) {
		this.instance = instance;
	}
	
	public void leave(String triggerTransitionName) throws Exception {
		String eventClass = getEventClass();
		try {						
			instance.getExecutionContext().getSQLHelper().beginTransaction();
			instance.getExecutionContext().getSQLHelper().setTimeout(5);
						
			leaveInTransaction(triggerTransitionName);
			
			instance.getExecutionContext().executeBatchSQL();			
			
			instance.getExecutionContext().getSQLHelper().commitTransaction();
			
			if(eventClass != null) {
				//���̽��������ûص�
				EventInfo eventInfo = buildEventInfo();
				EventHandler h = (EventHandler) Class.forName(eventClass).newInstance();
				h.handle(eventInfo);
			}
			
		} catch (Exception ex) {
			if(eventClass != null) {
				//���̽��������ûص�
				EventInfo eventInfo = buildEventInfo();
				eventInfo.setException(ex);
				EventHandler h = (EventHandler) Class.forName(eventClass).newInstance();
				h.handle(eventInfo);
				
			}
			new LuceneTableRollback().handleException(instance, instance.getTaskId());
			throw ex;
		} finally {						
			try {
				instance.getExecutionContext().getSQLHelper().closeHibernateSession();
			} catch (HibernateException e) {
				CommonLogger.logger.error(e);
			} catch (DocumentException e) {
				CommonLogger.logger.error(e);
			} catch (Exception e) {
				CommonLogger.logger.error(e);
			}
		}
		
	}
	
	public void leaveInTransaction(String triggerTransitionName) throws Exception {
		instance.getExecutionContext().setTriggerTransitionName(triggerTransitionName);
		/* ���õ�ǰ�������leaving�ڵ�, �����־û���ʱ����нڵ����� */
		CommonLogger.logger.debug("Token leaving node: " + node.getName());
		instance.getExecutionContext().setFirstLeavedNode(node);
		
		String instanceId = instance.getInstanceId();
		if (instanceId == null || "".equals(instanceId) || "null".equals(instanceId))
			instance.getExecutionContext().setNewInstance(true);
		
		instance.getExecutionContext().setLeavedNode(node);		
		node.leave(triggerTransitionName, instance.getExecutionContext());
	}

	public ProcessInstance getInstance() {
		return instance;
	}
	
	private String getEventClass() {
		Event event = instance.getProcessDefinition().getEvent();
		if(event != null && EventInfo.TYPE_TASK_SUBMIT.equals(event.getType())) {
			return event.getClassName();
		}else {
			return null;
		}
	}
	
	private EventInfo buildEventInfo() throws DocumentException {
		EventInfo info = new EventInfo();
		info.setInstId(instance.getInstanceId());
		info.setLastTaskId(instance.getExecutionContext().getLastTaskId());
		return info;
	}
}
