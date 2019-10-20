package com.hanson.jbpm.jpdl.exe.impl;

import java.util.List;
import java.util.Map;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;

import com.hanson.jbpm.identity.SysWorkGroup;
import com.hanson.jbpm.identity.WorkGroup;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.suntek.util.string.StringHelper;

public class WorkGroupAssignHandler extends AbstractAssignHandler implements AssignmentHandler {

	private String groupId;
	
	public WorkGroupAssignHandler(String condition, String ccflag, String groupId)
	{
		super(condition, ccflag);
		this.groupId = groupId;
	}
	
	public void assign(ExecutionContext ctx) throws Exception {
		if (!this.expressionIsTrue(ctx))
			return;
		
		if (BpmConstants.RoleDeptDispatchMode)
			assignToWorkGroup(ctx);
		else
			assignToEveryOne(ctx);
	}
	
	private void assignToWorkGroup(ExecutionContext ctx) throws Exception {
		try {
			String[] groups = StringHelper.split(",", groupId);
			WorkGroup group;
			for (int i=0; i<groups.length; i++) {
				group = new SysWorkGroup(groups[i]);
				ctx.getAssignable().setUserIdInGroup(group.getId(), group.getName());
			}
		} catch(Exception ex){			
			CommonLogger.logger.error(ex,ex);
			throw new BpmException("����ɫ���䴦�����쳣: " + ex.getMessage());
		}
		
	}
	
	private void assignToEveryOne(ExecutionContext ctx) throws Exception {
		try {
			groupId = StringHelper.replace(groupId, ",", "','");
			
			String sql = "select distinct a.user_code from user_group a, workgroup_view b" +
			 			 " where a.grop_code=b.grop_code and b.grop_code in ('" + groupId + "')";

			CommonLogger.logger.debug(sql);

			List list = DaoFactory.getJdbc("jbpm", "openEAP").queryForList(sql);
			
			for (int i=0; i<list.size(); i++) {
				if (this.isCC())
					ctx.getAssignable().setCCUserId((String)((Map)list.get(i)).get("USER_CODE"));
				else
					ctx.getAssignable().setUserId((String)((Map)list.get(i)).get("USER_CODE"));
			}
		} catch(Exception ex) {
			CommonLogger.logger.error(ex,ex);
			throw new BpmException("����ɫ���䴦�����쳣: " + ex.getMessage());
		}
	}
}
