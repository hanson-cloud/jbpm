package com.hanson.jbpm.jpdl.exe.impl.trigger;


public abstract class TriggerHandler implements ITriggerHandler
{
	public enum TriggerTarget {cust, custMgr, dealer, deptMgr};
	public enum TriggerAction {sms, email};
	public enum EventType {leftNode, enterNode, timeout, escalate, emergent};
	
	protected EventType event;
	protected String condition;
	protected String expression;
	protected TriggerAction action;
	protected TriggerTarget target;
	
	public TriggerHandler(EventType event, String condition, String expression, 
			TriggerAction action, TriggerTarget target) 
	{
		this.event = event;
		this.condition = condition;
		this.expression = expression;
		this.action = action;
		this.target = target;
	}
	
	public boolean isLeftNodeTrigger() {
		return (this.event == EventType.leftNode);
	}

	
	public boolean isEnterNodeTrigger() {
		return (this.event == EventType.enterNode);
	}
	
	public String getCondition() 
	{
		return this.condition;
	}
	
	public String getExpression()
	{
		return this.expression;
	}
	
	public TriggerTarget getTarget()
	{
		return this.target;
	}
	
	public TriggerAction getAction()
	{
		return this.action;
	}
}
