package com.hanson.jbpm.jpdl.def.node;

import java.util.List;

import com.hanson.jbpm.jpdl.exe.impl.CurrentNodeLastDealerAssignHandler;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.def.base.Task;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.ctx.TaskPersistence;
import com.hanson.jbpm.jpdl.exe.impl.AssignmentHandler;
import com.hanson.jbpm.jpdl.exe.impl.MailAssignmentHandler;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TriggerHandler;

/**
 * Ĭ�� һ�� TaskNode �ڵ� ֻ��һ�� Task
 * @author zhout
 *
 */
public class TaskNode extends NodeBase {
	
	private Task task;
	//private boolean block = false;
	private boolean onechoice = true;
	private boolean async = false;	
	private boolean sequence = false;
	private boolean parallel = false;
	private boolean seperated = false;
	
	private int assignmentIndex = 0;

	public TaskNode(String name, Task task, StateException exception) {
		super(name, exception);
		addTask(task);
	}

	public void addTask(Task task) {
		this.task = task;
		task.setTaskNode(this);
	}

	public Task getTask() {
		return task;
	}
	
	/**
	 * ���뵱ǰ�ڵ�
	 */
	public void enter(String transition, ExecutionContext ctx) throws Exception {		
		CommonLogger.logger.debug("Leave tasknode: " + ctx.getLeavedNode().getName());
		CommonLogger.logger.debug("transition: " + transition);
		CommonLogger.logger.debug("Enter tasknode: " + this.getName());	
			
		ctx.setEnterNode(this);
				
		CommonLogger.logger.debug("Context=" + ctx.getInstanceContext().toXML());
		
		/* ִ�е�ǰ�ڵ��Actionhandler */
		executeAction(ctx);
		/* ���㵱ǰ�ڵ��ָ�������� */
		if (ctx.getTriggerTransitionName().startsWith(BpmConstants.RET_TRANS_PREFIX)) {
			new CurrentNodeLastDealerAssignHandler("", "0").assign(ctx);
		} else {
			if (!ctx.mustLeavingABlockingNode()) {
				List<AssignmentHandler> assignments = getTask().getAssignment();
				for (AssignmentHandler assignment: assignments) {
					assignment.assign(ctx);
				}
			}
		}
		
		List<TriggerHandler> triggers = this.getTask().getTrigger();
		TriggerHandler trigger;
		for (int i=0; i<triggers.size(); i++) {
			trigger = triggers.get(i);
			if (trigger.isEnterNodeTrigger()) {
				trigger.exec(ctx);
			}
		}
		
		List<String> users = ctx.getAssignable().getUsers();
		List<String> ccUsers = ctx.getAssignable().getCCUsers();
		
		/* ���г־û����ݴ��� */		
		if (!ctx.mustLeavingABlockingNode()) {
			if (((TaskNode)ctx.getLeavedNode()).getTask().isIndependent())
				new TaskPersistence(users, ccUsers, ctx.getAssignable().getCoUsers(), ctx, transition).updatePrevTaskAssignmentDealFlagOnlyMyself();
			new TaskPersistence(users, ccUsers, ctx.getAssignable().getCoUsers(), ctx, transition).execute(false);
		} else {
			new TaskPersistence(users, ccUsers, ctx.getAssignable().getCoUsers(), ctx, transition).updatePrevTaskAssignmentDealFlagOnlyMyself();
		}
		assginByOtherMethod(ctx); //ִ��������ʽ�ɵ�
		ctx.getAssignable().clear();
	}
	
	
	/**
	 * ִ��������ʽ���ɵ�(�ʼ�,����)
	 * @param ctx
	 * @throws Exception 
	 */
	protected void assginByOtherMethod(ExecutionContext ctx) throws Exception {
		String method = getTask().getMethod();
		if(method != null) {
			if("email".equals(method)) {
				new MailAssignmentHandler().assign(ctx);
			}
		}
	}

	public void leave(String transition, ExecutionContext executionContext) throws Exception {
		super.leave(transition, executionContext);
		
		List<TriggerHandler> triggers = this.getTask().getTrigger();
		for (TriggerHandler trigger: triggers) {
			if (trigger.isLeftNodeTrigger())
				trigger.exec(executionContext);
		}
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public boolean isAsync() {
		CommonLogger.logger.debug(this.getName() + " isAsync = " + async);
		return async;
	}

	public boolean isOnechoice()
	{
		return onechoice;
	}

	public void setOnechoice(boolean onechoice)
	{
		this.onechoice = onechoice;
	}

	public boolean isSequence()
	{
		return sequence;
	}

	public void setSequence(boolean sequence)
	{
		this.sequence = sequence;
	}

	public boolean isParallel()
	{
		return parallel;
	}

	public void setParallel(boolean parallel)
	{
		this.parallel = parallel;
	}

	public boolean isSeperated()
	{
		return seperated;
	}

	public void setSeperated(boolean seperated)
	{
		this.seperated = seperated;
	}

	public int getAssignmentIndex()
	{
		return assignmentIndex;
	}

	public void setAssignmentIndex(int assignmentIndex)
	{
		this.assignmentIndex = assignmentIndex;
	}

}
