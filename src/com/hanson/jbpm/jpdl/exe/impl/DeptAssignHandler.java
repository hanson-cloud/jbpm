package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.identity.SysDepartment;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class DeptAssignHandler extends AbstractAssignHandler implements AssignmentHandler 
{
	/* �����б�, ���ŷָ� */
	private String deptIds;
	
	public DeptAssignHandler(String condition, String ccflag, String deptIds) { 
		super(condition, ccflag);
		this.deptIds = deptIds;
	}
	
	public void assign(ExecutionContext ctx) throws Exception 
	{
		if (!this.expressionIsTrue(ctx))
			return;
		
		if (BpmConstants.RoleDeptDispatchMode)
			assignToDept(ctx);
		else
			assignToEveryOne(ctx);
	}
	
	private void assignToDept(ExecutionContext ctx) {
		String[] deptId = deptIds.split(",");
		SysDepartment dept;
		for (int i=0; i<deptId.length; i++) {
			dept = new SysDepartment(deptId[i]);
			ctx.getAssignable().setUserIdInDept(dept.getId(), dept.getName());
		}
	}
	
	private void assignToEveryOne(ExecutionContext ctx)
	throws Exception {
		String[] deptId = deptIds.split(",");
		for (int i=0; i<deptId.length; i++) {
			if (this.isCC())
				ctx.getAssignable().setCCUsers(new SysDepartment(deptId[i]).getUserIds());
			else
				ctx.getAssignable().setUsers(new SysDepartment(deptId[i]).getUserIds());
		}
	}

}
