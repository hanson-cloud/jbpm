package com.hanson.jbpm.jpdl.exe.impl;

import java.util.List;
import java.util.Map;

import com.hanson.jbpm.identity.Role;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.identity.SysRole;
import com.suntek.util.string.StringHelper;

/**
 * ָ����ɫ�ɵ�ʵ���ࡣ<br>
 * ֧���ɵ�����ɫ�����˺ͷַ�����ɫӵ��������ģʽ��
 * <br>
 * Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�RoleAssignHandler.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�zhout
 * <p>������ڣ�2011-2-16
 */
public class RoleAssignHandler extends AbstractAssignHandler implements AssignmentHandler {

	private String roleId;
	
	public RoleAssignHandler(String condition, String ccflag, String roleId)
	{
		super(condition, ccflag);
		this.roleId = roleId;
	}
	
	public void assign(ExecutionContext ctx) throws Exception {
		if (!this.expressionIsTrue(ctx))
			return;
		
		if (BpmConstants.RoleDeptDispatchMode)
			assignToRole(ctx);
		else
			assignToEveryOne(ctx);
	}
	
	private void assignToRole(ExecutionContext ctx) throws Exception { 	
		try {
			String[] roles = StringHelper.split(",", roleId);
			Role role;
			for (int i=0; i<roles.length; i++) {
				role = new SysRole(roles[i]);
				ctx.getAssignable().setUserIdInRole(role.getId(), role.getName());
			}
		} catch(Exception ex){			
			CommonLogger.logger.error(ex,ex);
			throw new BpmException("����ɫ���䴦�����쳣: " + ex.getMessage());
		}
	}
	
	private void assignToEveryOne(ExecutionContext ctx) throws Exception { 		
		try{
			roleId = StringHelper.replace(roleId, ",", "','");
			
			String sql = "select distinct a.user_code, c.user_name" +
						 " from user_role a, SYS_ROLES b, frameuser c" +
						 " where a.roleid=b.ROLEID and a.user_code=c.user_code" +
						 " and b.ROLEID in ('" + roleId + "')";
			
			CommonLogger.logger.debug(sql);
			
			List list = DaoFactory.getJdbc("jbpm", "openEAP").queryForList(sql);
			Map row;
			for (int i=0; i<list.size(); i++) {
				row = (Map)list.get(i);
				if (this.isCC())
					ctx.getAssignable().setCCUserId((String)row.get("user_code"));
				else
					ctx.getAssignable().setUserId((String)row.get("user_code"), (String)row.get("user_name"));
			}
		} catch(Exception ex){			
			CommonLogger.logger.error(ex,ex);
			throw new BpmException("����ɫ���䴦�����쳣: " + ex.getMessage());
		}
	}
}
