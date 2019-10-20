package com.hanson.jbpm.jpdl.def.node;

import java.util.ArrayList;
import java.util.List;

import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.impl.DecisionHandler;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;

public class Decision extends NodeBase {
	
	private List<Transition> transitions = new ArrayList<Transition>();

	public Decision(String name, DecisionHandler[] handler, StateException exception) {
		super(name, exception);
		setHandlers(handler);
	}
	
	/**
	 * ������нڵ��Ǩ, ȷ����һ���ڵ������
	 * �����ж��Ƿ����������ı�Ǩ�ڵ㣬��������ѡ��ı�Ǩ������ת
	 * @param ctx
	 * @return
	 */
	public String getNext(String transition, ExecutionContext ctx) {
		String nextNodeName = "";
		boolean ret;
		String tempCondition;
		if(this.handlers.length > 0) {
			try {
				nextNodeName = getNextNodeNameFromHandler(ctx);
			} catch (Exception e) {
				throw new BpmException("��ת["+transition+ "] ����DecisionHandlerʧ�� ");
			}
		}
		for (int i = 0; i < leavingTransitions.size(); i++) {
			Transition currentTran = leavingTransitions.get(i);
			if(!nextNodeName.equals("")) {
				if(nextNodeName.equals(currentTran.getTo())) {
					ctx.setEnterNode(processDefinition.getNode(nextNodeName));
					currentTran.executeAction(ctx);
				}
			}else {
				tempCondition = leavingTransitions.get(i).getCondition();
				CommonLogger.logger.debug("Decision �ж�����[" + i + "]: " + tempCondition);
				ret = Boolean.valueOf(ExpressionEvaluator.evaluate(tempCondition, ctx));
				if (ret) {
					nextNodeName = currentTran.getTo();
					if("".equals(ctx.getInstanceContext().getParameter("transition", Context.types.FORM))) {
						ctx.getInstanceContext().setFormElement("transition", currentTran.getName());
					}
					
					ctx.setEnterNode(processDefinition.getNode(nextNodeName));
					currentTran.executeAction(ctx);
					break;
				}
			}
			
				
		}
		if (nextNodeName.equals(""))
			throw new BpmException("��ת["+transition+
					"] �Ҳ���ƥ��ķ�֧ [" + this.getName() + "]");
		return nextNodeName;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}	
	
	
	/**
	 * �Ӷ����handler�ϻ�ȡ��һ���ڵ�����
	 * @return
	 * @throws Exception 
	 */
	private String getNextNodeNameFromHandler(ExecutionContext ctx) throws Exception {
		DecisionHandler handler = (DecisionHandler) handlers[0];
		return handler.execute(ctx);
	}
}
