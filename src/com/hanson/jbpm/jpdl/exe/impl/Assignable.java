package com.hanson.jbpm.jpdl.exe.impl;

import java.util.ArrayList;
import java.util.List;

import com.hanson.jbpm.identity.OrgCache;
import com.hanson.jbpm.identity.SysDepartment;
import com.hanson.jbpm.log.CommonLogger;

import com.hanson.jbpm.identity.Department;
import com.hanson.jbpm.jpdl.def.BpmConstants;

public class Assignable {
	
	private List<String> users = new ArrayList<String>(); //������
	private List<String> ccUsers = new ArrayList<String>(); //������
	private List<String> coUsers = new ArrayList<String>(); //Э����
	
	private StringBuffer taskDealers = new StringBuffer(); //����������
	private StringBuffer taskCcDealers = new StringBuffer(); 
	private StringBuffer taskCoDealers = new StringBuffer();
	
	
	/**
	 * 
	 * @return
	 */
	public String getTaskDealers() {
		String dealers = taskDealers.toString();
		if (dealers.length()>1)
			dealers = dealers.substring(0, dealers.length() -1);
		else {
			if (users.size() > 0)
				dealers = users.get(0);
		}
		return dealers;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getTaskCcDealers() {
		String dealers = taskCcDealers.toString();
		if (dealers.length()>1)
			dealers = dealers.substring(0, dealers.length() -1);
		else {
			if (ccUsers.size() > 0)
				dealers = ccUsers.get(0);
		}
		return dealers;
	}
	
	
	/**
	 * Э��������
	 * @return
	 */
	public String getTaskCoDealers() {
		String dealers = taskCoDealers.toString();
		if (dealers.length()>1)
			dealers = dealers.substring(0, dealers.length() -1);
		else {
			if (coUsers.size() > 0)
				dealers = coUsers.get(0);
		}
		return dealers;
	}

	/**
	 * ����������, ���������...ʡ��
	 * @param dealers
	 * @return
	 */
	public String getDealsOmit(String dealers) {
		if (dealers.length() > 256)
			dealers = dealers.substring(0, 256) + "...";
		return dealers;
	}

	public String getTaskDealerIds() {
		StringBuffer taskDealerIds = new StringBuffer();
		for (int i=0; i<users.size(); i++) {
			taskDealerIds.append(users.get(i)).append(",");
		}
		return taskDealerIds.toString();
	}
	
	public void setUserId(String userId) {
		
		if (OrgCache.getCache().containsDelegatee(userId)) {
			userId = OrgCache.getCache().getDelegator(userId);
		}
		if(!users.contains(userId)) {
    		users.add(userId);
    		//taskDealers.append(new SysUser(userId).getName()).append(",");
    		taskDealers.append(OrgCache.getCache().getUser(userId)).append(",");
		}
	}
	
	public void setUserId(String userId, String userName) {
		
		if (OrgCache.getCache().containsDelegatee(userId)) {
			userId = OrgCache.getCache().getDelegator(userId);
			userName = OrgCache.getCache().getUser(userId);
		}
		
		if(!users.contains(userId)) {
    		users.add(userId);
    		taskDealers.append(userName).append(",");
		}
	}
	
	/**
	 * �ַ�����ɫ����
	 * @param roleId
	 * @param roleName
	 */
	public void setUserIdInRole(String roleId, String roleName) {
		String id = BpmConstants.PREFIX_ROLE + roleId;
		if(!users.contains(id)) {
    		users.add(id);
    		taskDealers.append(roleName).append(",");
		}
	}

	/**
	 * �ַ����������
	 * @param groupId
	 * @param groupName
	 */
	public void setUserIdInGroup(String groupId, String groupName) {
		String id = BpmConstants.PREFIX_GROUP + groupId;
		if(!users.contains(id)) {
    		users.add(id);
    		taskDealers.append(groupName).append(", ");
		}
	}	

	
	/**
	 * �ַ������ų���,��ָ��������
	 * @param deptId
	 * @param deptName
	 */
	public void setUserIdInDept(String deptId, String deptName) {
		String id = BpmConstants.PREFIX_DEPT + deptId;
		if(!users.contains(id)) {
    		users.add(id);
    		taskDealers.append(deptName).append(",");
		}
	}
	
	
	/**
	 * ָ�����������ַ���
	 * @param userList
	 */
	public void setUsers(List<String> userList) {
		users.addAll(userList);
		String userNames = getUserNamesByLoginNames(userList);
		taskDealers.append(userNames).append(",");
	}
	
	public void setUsers(List<String> userList, String userNames) {
		users.addAll(userList);
		taskDealers.append(userNames).append(",");
	}
	
	public List<String> getUsers() {
		return users;
	}
	
	public void setCCUserId(String userId) {
		if(!ccUsers.contains(userId)) {
			ccUsers.add(userId);
			taskCcDealers.append(OrgCache.getCache().getUser(userId)).append(",");
		}
	}

	
	public void setCCUsers(List<String> userList) {
		ccUsers.addAll(userList);
		String userNames = getUserNamesByLoginNames(userList);
		taskCcDealers.append(userNames).append(",");
	}
	
	
	/**
	 * ָ�����������Ʒ���
	 * @param userId
	 * @param userName
	 */
	public void setCCusers(List<String> userList, String userNames) {
		ccUsers.addAll(userList);
		taskCcDealers.append(userNames).append(",");
	}
	
	
	/**
	 * ���ų���ģʽ��ֻ���͵����ų���
	 * @param deptId
	 * @param deptName
	 */
	public void setCCUserIdInDept(String deptId, String deptName) {
		String id = BpmConstants.PREFIX_DEPT + deptId;
		if(!ccUsers.contains(id)) {
			ccUsers.add(id);
    		taskCcDealers.append(deptName).append(",");
		}
	}
	
	public List<String> getCCUsers() {
		return ccUsers;
	}
	
	
	public void setCoUserId(String coUserId) {
		coUsers.add(coUserId);
		taskCoDealers.append(OrgCache.getCache().getUser(coUserId)).append(",");
	}
	
	public void setCoUsers(List<String> users) {
		coUsers.addAll(users);
		taskCoDealers.append(getUserNamesByLoginNames(users)).append(",");
	}
	
	
	/**
	 * ָ��Э�������Ʒ���
	 * @param users
	 * @param userNames
	 */
	public void setCoUsers(List<String> users, String userNames) {
		coUsers.addAll(users);
		taskCoDealers.append(userNames).append(",");
	}
	
	
	/**
	 * ���ų���ģʽ��ֻ���͵����ų���
	 * @param deptId
	 * @param deptName
	 */
	public void setCoUserIdInDept(String deptId, String deptName) {
		String id = BpmConstants.PREFIX_DEPT + deptId;
		if(!coUsers.contains(id)) {
			coUsers.add(id);
    		taskCoDealers.append(deptName).append(",");
		}
	}
	
	public List<String> getCoUsers() {
		return coUsers;
	}
	
	
	/**
	 * �ַ��������е�ÿһ����Ա
	 * @param deptIds
	 */
	public void setDeptId(String deptIds) {
		String[] deptId = deptIds.split(",");
		Department dept = null;
		for (int i=0; i<deptId.length; i++) {
			dept = new SysDepartment(deptId[i]);
			users.addAll(dept.getUserIds());
			taskDealers.append(dept.getName()).append(",");
		}
	}	
	
	public void setDeptName(String deptNames) {
		String[] depts = deptNames.split(",");
		Department dept = null;
		for (int i=0; i<depts.length; i++) {
			dept = new SysDepartment(depts[i]);
			users.addAll(dept.getUserIds());
			taskDealers.append(dept.getName()).append(",");
		}
	}
	
	public void clear() { 
		CommonLogger.logger.info("��������������ڵ�ָ�ɼ�¼");
		users.clear();
		ccUsers.clear();
		coUsers.clear();
		if (taskDealers.length() > 0) {
			taskDealers.delete(0, taskDealers.length());
		}
		taskCcDealers.delete(0, taskCcDealers.length());
		taskCoDealers.delete(0, taskCoDealers.length());
		
	}
	
	
	private String getUserNamesByLoginNames(List<String> userList) {
		String loginNames = "";
		for (int i=0; i<userList.size(); i++) 
			loginNames = loginNames + userList.get(i) + ",";
		return loginNames.substring(0, loginNames.length() - 1);
	}
}
