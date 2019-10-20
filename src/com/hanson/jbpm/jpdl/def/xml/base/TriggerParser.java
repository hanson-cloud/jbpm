package com.hanson.jbpm.jpdl.def.xml.base;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.base.Task;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.exe.impl.trigger.DefaultTriggerHandler;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TriggerHandler.EventType;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TriggerHandler.TriggerAction;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TriggerHandler.TriggerTarget;

public class TriggerParser
{
	private Task task;
	
	private Map<String, EventType> eventTypes = new HashMap<String, EventType>(); 
	private Map<String, TriggerAction> actionTypes = new HashMap<String, TriggerAction>();
	private Map<String, TriggerTarget> targetTypes = new HashMap<String, TriggerTarget>();
	
	public TriggerParser(Task task)
	{
		this.task = task;
		
		eventTypes.put("leftNode", EventType.leftNode);
		eventTypes.put("enterNode", EventType.enterNode);
		
		actionTypes.put("sms", TriggerAction.sms);
		actionTypes.put("email", TriggerAction.email);
		
		targetTypes.put("cust", TriggerTarget.cust);
		targetTypes.put("custMgr", TriggerTarget.custMgr);
		targetTypes.put("dealer", TriggerTarget.dealer);
		targetTypes.put("deptMgr", TriggerTarget.deptMgr);
	}
	
	public void parser(Element el, ProcessDefinition pdf) throws Exception 
	{
		List<Element> assignments = el.elements("trigger");
		for (Element e: assignments) {
			if (e != null) {
				String expression = e.attributeValue("expression");
				String condition = e.attributeValue("condition");
				String _action = e.attributeValue("action");
				String _target = e.attributeValue("target");
				String _type = e.attributeValue("type");
				
				EventType type = eventTypes.get(_type);
				TriggerAction action = actionTypes.get(_action);
				TriggerTarget target = targetTypes.get(_target);
				
				task.addTrigger(new DefaultTriggerHandler(type, condition, expression, action, target));
			} 
		}	
	}
}
