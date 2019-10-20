package com.hanson.jbpm.jpdl.exe.impl;

import java.sql.SQLException;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class EndActionHandler implements ActionHandler {	
	public String execute(ExecutionContext ctx) throws SQLException  {
		String title = ctx.getInstanceContext().getParameter(DefaultActionHandler.DEFAULT_BIZTITLE_NAME, Context.types.FORM);
		if (title.equals(""))
			title =  ctx.getLeavedNode().getName() + " OK";
		
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.TASKTITLE, title);
		
		String bizId = ctx.getInstanceContext().getParameter(Context.values.BIZID, Context.types.FORM);
				
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.BIZID, bizId);	
		ctx.getInstanceContext().setFormElement(Context.values.BIZID, bizId);
		
		return "true";
	}

}
