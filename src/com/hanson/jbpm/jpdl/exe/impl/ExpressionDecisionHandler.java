package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;

public class ExpressionDecisionHandler implements DecisionHandler {
	private String expression;
	
	public ExpressionDecisionHandler(String expression) {
		this.expression = expression;
	}
	
	public String execute(ExecutionContext executionContext) throws Exception {		
		Object ret = ExpressionEvaluator.evaluate(expression, executionContext);
		CommonLogger.logger.debug("Decision expression eval: " + expression + " : " + ret);
		return (String)ret;
	}
}
