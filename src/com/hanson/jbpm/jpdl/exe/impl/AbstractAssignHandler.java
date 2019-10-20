package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;

public abstract class AbstractAssignHandler implements AssignmentHandler
{
	protected String ccflag = "";
	
	protected String condition = "";
	
	public AbstractAssignHandler(String condition, String ccflag)
	{
		this.condition = condition;
		this.ccflag = ccflag;
	}
	
	protected boolean isCC()
	{
		return "1".equals(ccflag);
	}
	
	protected void setCCFlag(String ccflag)
	{
		this.ccflag = ccflag;
	}
	
	protected void setCondition(String condition)
	{
		this.condition = condition;
	}
	
	protected boolean expressionIsTrue(ExecutionContext ctx)
	{
		if (condition == null || condition.equals(""))
			return true;
		String result = ExpressionEvaluator.evaluate(this.condition, ctx);		
		return ("true".equals(result));
	}
}
