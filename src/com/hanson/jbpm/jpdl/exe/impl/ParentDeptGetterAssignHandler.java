package com.hanson.jbpm.jpdl.exe.impl;


import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.suntek.eap.structure.DepartmentModel;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class ParentDeptGetterAssignHandler extends AbstractAssignHandler implements AssignmentHandler {

	public ParentDeptGetterAssignHandler(String condition, String ccflag)
	{
		super(condition, ccflag);
	}

	public void assign(ExecutionContext ctx) throws Exception {
		String deptId = ctx.getInstanceContext().getParameter(Context.values.DEPTID, Context.types.USER);
		DepartmentModel dept = new DepartmentModel(deptId).getParentDepartment();
		if (dept == null)
			throw new RuntimeException("�ò���[" + deptId + "]û���ϼ�����");
		ctx.getAssignable().setDeptId(dept.getDepartmentCode());
	}

}
