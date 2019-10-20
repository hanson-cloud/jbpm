package com.hanson.jbpm.jpdl.def.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.jpdl.exe.impl.AssignmentHandler;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TriggerHandler;

public class Task {
	private TaskNode taskNode;
	
	private String name;
	
	private double dueDate;
	
	private String form;
	
	private String formName;
	
	private List<AssignmentHandler> assignments = new ArrayList<AssignmentHandler>();
	
	private List<TriggerHandler> triggers = new ArrayList<TriggerHandler>();
	
	private List<String> formVariableNames;
	
	private Map<String,String> formButtons;
	
	protected String condition = null;
	
	private Reminder reminder;
	
	private boolean blocking;
	
	private boolean independent;
	
	private boolean notify;
	
	private String expression;
	
	private String type;
	
	private String actorId;
	
	private String className; //��������
	
	private String method; //�ɷ���ʽ mail, sms
	
	public boolean isBlocking() {
		CommonLogger.logger.info(this.getName() + " isBlocking = " + blocking);
		return blocking;
	}
	
	public void setBlocking(boolean blocking) {		
		this.blocking = blocking;
	}
	
	public boolean isIndependent() {
		CommonLogger.logger.info(this.getName() + " isIndependent = " + independent);
		return this.independent;
	}
	
	public void setIndependent(boolean independent) {
		this.independent = independent;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getDueDate() {
		return dueDate;
	}
	
	public void setDueDate(double dueDate) {
		this.dueDate = dueDate;
	}
	public String getForm() {
		return form;
	}
	
	public void setForm(String form) {
		this.form = form;
	}
	
	public List<AssignmentHandler> getAssignment() {
		return assignments;
	}
	
	public void addAssignment(AssignmentHandler assignment) {
		this.assignments.add( assignment );
	}

	public List<TriggerHandler> getTrigger() {
		return triggers;
	}
	
	public void addTrigger(TriggerHandler trigger ) {
		this.triggers.add( trigger );
	}
	
	public Reminder getReminder() {
		return reminder;
	}
	
	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}
	public String getCondition() {
		return condition;
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public TaskNode getTaskNode() {
		return taskNode;
	}
	
	public void setTaskNode(TaskNode taskNode) {
		this.taskNode = taskNode;
	}
	
	public List<String> getFormVariableNames() {
		return formVariableNames;
	}
	public void addFormVariableName(String name){
		if (formVariableNames == null)
			formVariableNames = new ArrayList<String>();
		formVariableNames.add(name);
	}
	
	public void setFormVariableNames(List<String> formVariableNames) {
		this.formVariableNames = formVariableNames;
	}
	
	public Map<String, String> getFormButtons() {
		return formButtons;
	}
	public void addFormButton(String buttonName, String transition){
		if (formButtons == null)
			formButtons = new HashMap<String,String>();
		formButtons.put(buttonName, transition);
	}
	
	public void setFormButtons(Map<String, String> formButtons) {
		this.formButtons = formButtons;
	}
	
	public String getFormName() {
		return formName;
	}
	
	public void setFormName(String formName) {
		this.formName = formName;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	public String getMethod() {
		return method;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	
}
