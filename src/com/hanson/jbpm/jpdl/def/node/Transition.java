package com.hanson.jbpm.jpdl.def.node;


import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.exe.impl.DefaultStartActionHandler;
import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.impl.ActionHandler;

public class Transition {
	private String name = "";
	private StateException exception;
	private String from;
	private String to;
	private ProcessDefinition processDefinition;

	/* �������ʽ */
	private String condition;

	/* Transition �ڵ��ִ�� */
	private ActionHandler[] handlers;
	
	public Transition(){
	}
	
	public Transition(String name, String to, String condition, ActionHandler[] handlers, StateException exception) {
		if (name == null) name = "";
		this.name = name;
		this.to = to;
		this.condition = condition;		
		this.setHandler(handlers);
		this.setException(exception);
		//CommonLogger.logger.debug("Transition: " + name + ", handler = " + handler);
	}

	public ActionHandler[] getHandler() {
		return handlers;
	}

	public void setHandler(ActionHandler[] handlers) {
		this.handlers = handlers;
	}
	
	/**
	 * �ж�Transition���������ʽ�Ƿ���������
	 * @param ctx
	 * @return
	 */
	public boolean conditionIsMeeted(ExecutionContext ctx) {
		String eval = ExpressionEvaluator.evaluate(condition, ctx);
		if (eval == null) {
			CommonLogger.logger.debug(condition + " is null, evaluation passed");
			return true;
		}
		boolean ret = Boolean.valueOf(eval);
		if (ret)
			CommonLogger.logger.debug(condition + " evaluation passed");
		else
			CommonLogger.logger.debug(condition + " evaluation didn't pass");
		return ret; 
	}
	
	/**
	 * ִ�н�㶯��
	 * @param action    		��Ӧ�Ķ�����
	 * @param executionContext 	�����Ļ���
	 */
	public void executeAction(ExecutionContext ctx) {
		try {
			if (handlers == null || handlers.length == 0 || handlers[0] == null) {
				CommonLogger.logger.debug("Transition " + this.getName() + " �� Action ���� ");
				if ((this.from.equals(ctx.getProcessDefinition().getStart().getName()))) {					
					handlers = new ActionHandler[]{new DefaultStartActionHandler()};
				}
			}
			if (handlers != null) {				
				for (int i=0; i<handlers.length; i++) {
					CommonLogger.logger.debug("Transition " + this.getName() + " ִ��: " + handlers[i].getClass().getName());
					String result = handlers[i].execute(ctx);
					ctx.getLeavedNode().setActionHandlerExecuteResult(result);
				}
			}
		} catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			// ����ͳһ���쳣�������ж��Ƿ��Ѷ����쳣�����࣬��û�����׳��쳣
			throw new BpmException(ex.getMessage(), ex);
		} 
	}
	
	public boolean isUnCondition() {
		return (this.condition == null);
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public StateException getException() {
		return exception;
	}

	public void setException(StateException exception) {
		this.exception = exception;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTo() {
		return to;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
