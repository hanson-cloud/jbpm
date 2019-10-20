package com.hanson.jbpm.jpdl.def.node;

import java.util.ArrayList;
import java.util.List;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.impl.ExceptionHandler;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.log.LoggerUtil;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.impl.ActionHandler;

public class NodeBase {
	protected String name;
	protected StateException exception;
	protected List<ExceptionHandler> exceptionHandlers = null;
	protected ActionHandler[] handlers = null;
	protected List<Transition> leavingTransitions = new ArrayList<Transition>();
	//protected List<Transition> arrivingTransitions = new ArrayList<Transition>();
	protected ProcessDefinition processDefinition;
	protected String actionHandlerExecuteResult;	
		
	public NodeBase(String name, StateException exception) {
		if (name == null) name = "";
		setName(name);
		setException(exception);
	}

	/**
	 * ������нڵ��Ǩ, ȷ����һ���ڵ������
	 * �����ж��Ƿ����������ı�Ǩ�ڵ㣬��������ѡ��ı�Ǩ������ת
	 * @param triggerTransition TransitionButton ������ Transition
	 * @param ctx
	 * @return
	 */
	protected String getNext(String triggerTransitionName, ExecutionContext ctx) {
		Transition transition = null;
		
		CommonLogger.logger.debug("triggerTransitionName = " + triggerTransitionName);
		
		/* �ж� transition �Ƿ�Ϊϵͳ�Ļ��˹��� */
		if (triggerTransitionName != null && triggerTransitionName.startsWith(BpmConstants.RET_TRANS_PREFIX)) {
			if (triggerTransitionName.equals("JBPM_RET_ONESTEP")) {
				ctx.setEnterNode(processDefinition.getNode(ctx.getPrevTaskName()));
				triggerTransitionName = ctx.getPrevTaskName();
			} else {
				ctx.setEnterNode(processDefinition.getNode(triggerTransitionName.substring(9)));
			}
			String user = ctx.getInstanceContext().getParameter(Context.values.UNAME, Context.types.USER);
			String taskTitle = "";
			if (ctx.getRequest()!=null && ctx.getRequest().getParameter(Context.values.TASKTITLE) != null) {
				taskTitle = ctx.getRequest().getParameter(Context.values.TASKTITLE);
			} else {
				taskTitle = ctx.getInstanceContext().getParameter(Context.values.TASKTITLE, Context.types.FORM);
			}
			ctx.getInstanceContext().appendRuntimeVariable(Context.values.TASKTITLE, user + " ִ���˻��˴���: " + taskTitle);			
			if (triggerTransitionName.startsWith(BpmConstants.RET_TRANS_PREFIX))
				triggerTransitionName =  triggerTransitionName.substring(9);
			ctx.getInstanceContext().appendRuntimeVariable("transition", "����");
			return triggerTransitionName;
		}
		
		if (triggerTransitionName != null && triggerTransitionName.equals("JBPM_CLOSE_DIRECTLY")) {
			triggerTransitionName = "����";
			ctx.setEnterNode(processDefinition.getNode("����"));
			String user = ctx.getInstanceContext().getParameter(Context.values.UNAME, Context.types.USER);
			String taskTitle = "";
			if (ctx.getRequest()!=null && ctx.getRequest().getParameter(Context.values.TASKTITLE) != null) {
				taskTitle = ctx.getRequest().getParameter(Context.values.TASKTITLE);
			} else {
				taskTitle = ctx.getInstanceContext().getParameter(Context.values.TASKTITLE, Context.types.FORM);
			}
			ctx.getInstanceContext().appendRuntimeVariable(Context.values.TASKTITLE, user + " ִ����ֱ�ӹرմ���: " + taskTitle);			
			ctx.getInstanceContext().appendRuntimeVariable("transition", "�ر�");
			return triggerTransitionName;
		}
		
		/* �����ж��Ƿ����������ı�Ǩ�ڵ㣬��������ѡ��ı�Ǩ������ת */
		if ("".equals(triggerTransitionName)) 
			transition = getUnConditionBranch(ctx);
		
		if (transition == null)	{			
			transition = getBranchByTriggerName(ctx, triggerTransitionName);
		}
		
		if (transition == null) {
			CommonLogger.logger.debug("triggerTransitionName δƥ��, ��ʼ���ҵ�һ��������ķ�֧");
			transition = getTrueBranch(ctx);		
		}
		
		if (transition == null) {
			CommonLogger.logger.debug("��һ��������ķ�֧Ϊ�治ƥ��, ��ʼȡ���һ��transaction");
			if(leavingTransitions.size() > 0) {
				transition = leavingTransitions.get(0);
			}
		}
		
		if (transition == null) {
			String message = "���̶�����������쳣����ת���� " + triggerTransitionName + 
							 " �ڽڵ� " + this.getName() + " �Ҳ�����Ӧ��·��!";
			CommonLogger.logger.error(message);
			throw new BpmException(message);
		}
		
		ctx.setEnterNode(processDefinition.getNode(transition.getTo()));
		
		transition.executeAction(ctx);
		
		ctx.getInstanceContext().appendRuntimeVariable("transition", transition.getName());
		
		return transition.getTo();
	}

	/**
	 * ����������һ�ڵ�
	 * @return
	 */
	private Transition getUnConditionBranch(ExecutionContext ctx) {
		Transition transition = null;
		for (int i = 0; i < leavingTransitions.size(); i++) {
			if (leavingTransitions.get(i).isUnCondition()) {
				transition = leavingTransitions.get(i);
				CommonLogger.logger.debug("������������ת��֧: " + transition.getTo());
				break;
			}
		}
		return transition;
	}
	
	/**
	 * ��һ��ƥ��Ϊ��Ľڵ�
	 * @param ctx
	 * @return
	 */
	private Transition getTrueBranch(ExecutionContext ctx) {
		Transition transition = null;
		for (int i = 0; i < leavingTransitions.size(); i++) {
			if (leavingTransitions.get(i).conditionIsMeeted(ctx)) {
				transition = leavingTransitions.get(i);					
				CommonLogger.logger.debug("ƥ��������֧Ϊ�����һ�ڵ�: " + transition.getTo());
				break;
			}
		}
		return transition;
	}
	
	/**
	 * ������ƥ��ķ�֧
	 * @param ctx
	 * @param triggerName
	 * @return
	 */
	private Transition getBranchByTriggerName(ExecutionContext ctx, String triggerName) {
		Transition transition = null;
		for (int i = 0; i < leavingTransitions.size(); i++) {
			if (leavingTransitions.get(i).getName().equals(triggerName)) {
				transition = leavingTransitions.get(i);					
				CommonLogger.logger.debug("ƥ��triggerName����һ�ڵ�: " + transition.getTo());
				break;
			}
		}
		
		return transition;
	}
	
	/**
	 * �򱾽�㶯̬���һ���뿪��Transition
	 * @throws IllegalArgumentException
	 *             ����Ҫ��ӵ�TransitionΪ��
	 */
	public Transition addLeavingTransition(Transition leavingTransition) {
		if (leavingTransition == null)
			throw new IllegalArgumentException("���̶����쳣���������һ���յ���ת����ǰ�ڵ�!");
		leavingTransition.setFrom(this.getName());
		leavingTransitions.add(leavingTransition);
		return leavingTransition;
	}

	public List<Transition> getLeavingTransitions() {
		return this.leavingTransitions;
	}

	/**
	 * ִ�н�㶯��
	 * @param action    		��Ӧ�Ķ�����
	 * @param executionContext 	�����Ļ���
	 */
	public void executeAction(ExecutionContext executionContext) {
		try {
			if (handlers != null && handlers.length > 0) {
				for (int i=0; i<handlers.length; i++) {
					actionHandlerExecuteResult = handlers[i].execute(executionContext);
					LoggerUtil.getLogger().debug("Actionhandler ִ�н����" + actionHandlerExecuteResult);
				}
			}			
		} catch (Exception exception) {
			// ����ͳһ���쳣�������ж��Ƿ��Ѷ����쳣�����࣬��û�����׳��쳣
			// �ݲ�ʵ��			
			throw new BpmException(exception.getMessage(), exception);
		} 
	}

	/**
	 * �뿪��ǰ���, ������һ�ڵ�
	 * @param executionContext
	 */
	public void leave(String triggerTransitionName, ExecutionContext ctx) throws Exception {		
		CommonLogger.logger.debug("�뿪�ڵ�: " + this.getName() + ", triggerTransitionName:" + triggerTransitionName);
		processDefinition.getNode(getNext(triggerTransitionName, ctx)).enter(triggerTransitionName, ctx);
	} 
	
	/**
	 * ���뵱ǰ�ڵ�
	 */
	public void enter(String triggerTransitionName, ExecutionContext ctx) throws Exception {
		ctx.passedTaskThisTime = ctx.passedTaskThisTime + this.getName() + ",";
		long start = System.currentTimeMillis();
		CommonLogger.logger.debug("����ڵ�: " + this.getName());	
		executeAction(ctx);	
		CommonLogger.logger.debug("ִ��ActionHandler��ʱ��" + (System.currentTimeMillis() - start));
		triggerTransitionName = "";
		leave(triggerTransitionName, ctx);		
	}
		
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StateException getException() {
		return exception;
	}

	public void setException(StateException exception) {
		this.exception = exception;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public ActionHandler[] getHandlers() {
		return handlers;
	}

	public void setHandlers(ActionHandler[] handlers) {
		this.handlers = handlers;
	}

	public String getActionHandlerExecuteResult() {
		return actionHandlerExecuteResult;
	}

	public void setActionHandlerExecuteResult(String actionHandlerExecuteResult) {
		this.actionHandlerExecuteResult = actionHandlerExecuteResult;
	}
}
