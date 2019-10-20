package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class InitDeptGetterAssignHandler extends AbstractAssignHandler implements AssignmentHandler 
{
	public InitDeptGetterAssignHandler(String condition, String ccflag)
	{
		super(condition, ccflag);
	}
	
	public void assign(ExecutionContext ctx) throws Exception {
		String deptId	= ctx.getInstanceContext().getParameter("/ctx/proc/INIT_DEPTID");
		if (deptId.equals(""))
			throw new RuntimeException("��ȡ���������˲�����Ϣ�쳣");
		ctx.getAssignable().setDeptId(deptId);		
	}
}
