package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;


public interface ActionHandler {
	public String execute(ExecutionContext ctx) throws Exception;
}
