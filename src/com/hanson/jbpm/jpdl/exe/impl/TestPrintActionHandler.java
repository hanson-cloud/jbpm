package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.log.CommonLogger;

public class TestPrintActionHandler implements ActionHandler {	
	public String execute(ExecutionContext ctx) throws Exception {
		CommonLogger.logger.info("TestPrint: leave Node " + ctx.getLeavedNode() + " to " + ctx.getNextNode());
		return "true";
	}

}
