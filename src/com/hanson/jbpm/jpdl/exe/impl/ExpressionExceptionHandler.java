package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class ExpressionExceptionHandler extends ExceptionHandler {
	private String expression;
	public ExpressionExceptionHandler(String expression) {
		this.expression = expression;
	}
	public void execute(Exception exception, ExecutionContext ctx) {
		//ctx.getInstanceContext().appendRuntimeVariable(name, expression);
	}
}
