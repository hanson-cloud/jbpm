package com.hanson.jbpm.jpdl.exe.impl.trigger;

import com.hanson.jbpm.identity.Customer;
import com.hanson.jbpm.identity.User;
import com.suntek.eap.structure.exception.EntityNotFoundException;
import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.jpdl.exe.impl.trigger.TriggerHandler.TriggerTarget;

public class TargetFKUtil
{
	public static String getMobile(TriggerTarget target, String targetFK)
	{
		if (target == TriggerTarget.dealer)
			return getUserMobile(targetFK);
		if (target == TriggerTarget.deptMgr)
			return getDeptMgrMobile(targetFK);
		if (target == TriggerTarget.cust)
			return getCustMobile(targetFK);
		if (target == TriggerTarget.custMgr)
			return getCustMgrMobile(targetFK);
		return "";
	}

	public static String getEmail(TriggerTarget target, String targetFK) throws EntityNotFoundException
	{
		if (target == TriggerTarget.dealer)
			return getUserEmail(targetFK);
		if (target == TriggerTarget.deptMgr)
			return getDeptMgrEmail(targetFK);
		if (target == TriggerTarget.cust)
			return getCustEmail(targetFK);
		if (target == TriggerTarget.custMgr)
			return getCustMgrEmail(targetFK);
		throw new EntityNotFoundException();
	}
	
	private static String getUserEmail(String userCode)
	{
		return new SysUser(userCode).getEmail();
	}
	
	private static String getUserMobile(String userCode)
	{
		return new SysUser(userCode).getMobile();
	}
	
	private static String getDeptMgrMobile(String userCode)
	{
		return new SysUser(userCode).getDeptManager().getMobile();
	}

	private static String getDeptMgrEmail(String userCode)
	{
		User user = new SysUser(userCode).getDeptManager();
		if (user != null)
			return user.getEmail();
		else
			return null;
	}
	
	private static String getCustEmail(String custId)
	{
		return new Customer(custId).getEmail();
	}

	private static String getCustMobile(String custId)
	{
		return new Customer(custId).getMobile();
	}

	private static String getCustMgrEmail(String custId)
	{
		return new Customer(custId).getEmail();
	}

	private static String getCustMgrMobile(String custId)
	{
		return new Customer(custId).getMobile();
	}	
}
