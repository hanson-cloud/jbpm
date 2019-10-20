package com.hanson.jbpm.jpdl.exe.impl.trigger;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public interface ITriggerHandler
{
	public void exec(ExecutionContext ctx) throws Exception;
}
