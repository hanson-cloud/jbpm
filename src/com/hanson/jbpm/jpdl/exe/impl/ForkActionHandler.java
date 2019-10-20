package com.hanson.jbpm.jpdl.exe.impl;

import java.util.Calendar;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class ForkActionHandler implements ActionHandler {
	private String forkId;
	
	public ForkActionHandler(String forkId) {
		this.forkId = forkId;
	}
	
	public String execute(ExecutionContext ctx) throws Exception {		
		String newForkId = forkId + "_" + Calendar.getInstance().getTimeInMillis();
		CommonLogger.logger.debug(" ForkActionHandler create ForkId = " + newForkId);
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.FORKID, newForkId);
		return "true";
	}
}
