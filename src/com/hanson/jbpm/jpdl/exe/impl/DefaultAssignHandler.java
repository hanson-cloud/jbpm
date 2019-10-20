package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class DefaultAssignHandler implements AssignmentHandler 
{
	/**
	 * ������Assignment��ʱ��, Ĭ����һ���账����Ϊ�Լ�
	 */
	public void assign(ExecutionContext ctx) throws Exception 
	{		
		String currentUser = ctx.getInstanceContext().getParameter(Context.values.ULOGNAME, Context.types.USER);
		ctx.getAssignable().setUserId(currentUser);
	}

}
