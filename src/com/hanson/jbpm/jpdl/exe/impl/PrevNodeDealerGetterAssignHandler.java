package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class PrevNodeDealerGetterAssignHandler extends AbstractAssignHandler implements AssignmentHandler {

	public PrevNodeDealerGetterAssignHandler(String condition, String ccflag)
	{
		super(condition, ccflag);
	}

	public void assign(ExecutionContext ctx) throws Exception { 
		if (!this.expressionIsTrue(ctx))
			return;
		
		String taskId = ctx.getInstanceContext().getParameter(Context.values.TASKID, Context.types.FORM);
		String sql = "select ASSIGN_UID from BPM_TASK where TASK_ID='" + taskId + "'";
		CommonLogger.logger.debug(sql);
		String prevNodeDealer = ctx.getJdbc().getSingleValue(sql);
		if (this.isCC())
			ctx.getAssignable().setCCUserId(prevNodeDealer);
		else
			ctx.getAssignable().setUserId(prevNodeDealer);
	} 
	
}
