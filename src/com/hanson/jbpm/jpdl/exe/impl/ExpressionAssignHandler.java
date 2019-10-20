package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;

/**
 * ���ʽAssignHandlerĬ��Ϊ�û�����ģʽ
 * @author zhout
 *
 */
public class ExpressionAssignHandler implements AssignmentHandler 
{	
	private String expression;
	private String type;
	
	public ExpressionAssignHandler(String expression, String type) 
	{
		this.expression = expression;
		this.type = type;
	}
	
	public void assign(ExecutionContext ctx) throws Exception 
	{		
		String ret = ExpressionEvaluator.evaluate(expression, ctx);
		CommonLogger.logger.debug("ExpressionAssign[" + type + "]: " + ret);
		if (type == null || type.equals("user")) {
			if (ret.indexOf(",")>0) {
				String[] users = ret.split(",");
				for (int i=0; i<users.length; i++) {
					ctx.getAssignable().setUserId(users[i]);
				}
			} else {
				ctx.getAssignable().setUserId(ret);
			}
		}
		else {
			ctx.getAssignable().setDeptId(ret);
		}
	}
}
