package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class InitUserGetterAssignHandler extends AbstractAssignHandler implements AssignmentHandler {

	public InitUserGetterAssignHandler(String condition, String ccflag)
	{
		super(condition, ccflag);
	}

	public void assign(ExecutionContext ctx) throws Exception {
		if (!this.expressionIsTrue(ctx))
			return;
		
		String initUid	= ctx.getInstanceContext().getParameter("INIT_UID", Context.types.MAINFORM);
		if (initUid.equals(""))
			throw new RuntimeException("��ȡ������������Ϣ�쳣");
		
		if (this.isCC())
			ctx.getAssignable().setCCUserId(initUid);
		else
			ctx.getAssignable().setUserId(initUid);
	}

}
