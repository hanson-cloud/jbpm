package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.log.CommonLogger;

public class NullActionHandler implements ActionHandler {
	
	public String execute(ExecutionContext ctx) throws Exception {
		CommonLogger.logger.debug("NullActionHandler.execute(" + ctx.getLeavedNode().getName() + ")");
		return "true";
	}
}