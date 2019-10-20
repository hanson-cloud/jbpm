package com.hanson.jbpm.jpdl.exe.impl;


import java.util.List;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.suntek.eap.structure.DepartmentModel;
import com.suntek.eap.structure.FrameUserModel;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class ParentDeptMgrGetterAssignHandler extends AbstractAssignHandler implements AssignmentHandler {

	public ParentDeptMgrGetterAssignHandler(String condition, String ccflag)
	{
		super(condition, ccflag);
	}
	
	public void assign(ExecutionContext ctx) throws Exception {
		String deptId = ctx.getInstanceContext().getParameter(Context.values.DEPTID, Context.types.USER);
		DepartmentModel dept = new DepartmentModel(deptId).getParentDepartment();
		List<FrameUserModel> users = dept.getDepartmentUsers();
		boolean hasOne = false;
		for (int i=0; i<users.size(); i++) {
			if (users.get(i).isDepartmentManager()) {
				ctx.getAssignable().setUserId(users.get(i).getUserCode());
				hasOne = true;
			}
		}
		if (!hasOne)
			throw new RuntimeException("�ò���[" + deptId + "]û���ϼ����Ź���Ա");
	}	

}
