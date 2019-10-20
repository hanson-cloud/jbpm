package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class DefaultActionHandler implements ActionHandler {
	
	public static final String DEFAULT_BIZTITLE_NAME = "BIZ_TITLE";
	
	public static final String DEFAULT_TASKTITLE_NAME = "TASK_TITLE";
	
	public static final String DEFAULT_DUETIME_NAME = "DUE_TIME";
	
	public String execute(ExecutionContext ctx) throws Exception {
		
		CommonLogger.logger.debug("DefaultActionHandler.execute()");
		String title = ctx.getInstanceContext().getParameter(DEFAULT_BIZTITLE_NAME, Context.types.FORM);
		if (title.equals(""))
			title = ctx.getLeavedNode().getName() + " OK";
		if(title.length() > 1000) {title = title.substring(0, 1000);}
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.TASKTITLE, title);
		
		/*�趨ʱ��*/
		String dueTime = ctx.getInstanceContext().getParameter(DefaultActionHandler.DEFAULT_DUETIME_NAME, 
				Context.types.FORM);
		if(!dueTime.equals("")) {
			ctx.getInstanceContext().setTaskDueTime(dueTime);
		}
		String bizId = ctx.getInstanceContext().getParameter(Context.values.BIZID, Context.types.FORM);		
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.BIZID, bizId);	
		ctx.getInstanceContext().setFormElement(Context.values.BIZID, bizId);
		
		

		return "true";
	}

	
}
