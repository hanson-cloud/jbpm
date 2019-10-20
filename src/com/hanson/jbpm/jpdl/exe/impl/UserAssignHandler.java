package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class UserAssignHandler extends AbstractAssignHandler  {
	/* �û��б�, ���ŷָ�*/
	private String userIds;
	
	public UserAssignHandler(String condition, String ccflag, String userIds) {
		super(condition, ccflag);
		this.userIds = userIds;
	}
	
	public void assign(ExecutionContext ctx) throws Exception
	{
		if (!this.expressionIsTrue(ctx))
			return;
		
		String[] userId = userIds.split(",");
		for (int i=0; i<userId.length; i++) {
			if (this.isCC())
				ctx.getAssignable().setCCUserId(userId[i]);
			else
				ctx.getAssignable().setUserId(userId[i]);
		}
	}
}
