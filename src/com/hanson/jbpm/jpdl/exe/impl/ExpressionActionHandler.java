package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;

public class ExpressionActionHandler implements ActionHandler {
	private String name;
	private String expression;
	
	public ExpressionActionHandler(String name, String expression) {
		this.name = name;
		this.expression = expression;
	}
	
	public String execute(ExecutionContext ctx) throws Exception {
		String ret = ExpressionEvaluator.evaluate(expression, ctx);
		ctx.getInstanceContext().appendRuntimeVariable(name, ret);
		return ret;
	}
}
