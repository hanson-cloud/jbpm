package com.hanson.jbpm.identity;

import java.util.List;

import com.suntek.eap.structure.exception.EntityNotFoundException;

public abstract class WorkGroup { 
	/* ��������� */
	private String groupId;
	/* ���������� */
	private String groupName;
	
	public WorkGroup(String groupId) {
		this.groupId = groupId;
	}
	
	/**
	 * ��ѯ����������������û�
	 * @return
	 */
	public abstract List<User> getUsers() throws EntityNotFoundException;	
	
	public abstract List<String> getUserIds() throws EntityNotFoundException;
	
	public abstract List<User> getUsersByGroupNames() throws EntityNotFoundException;

	/**
	 * ���ݹ�����ID���ҹ��������� 
	 * @return
	 */
	public abstract String getNameById(String groupId) throws EntityNotFoundException;
	
	public String getId() {
		return groupId;
	}
	
	public void setId(String id) {
		this.groupId = id;
	}
	
	public String getName() throws EntityNotFoundException {
		if (groupName == null) {
			groupName = getNameById(groupId);
		}
		return groupName;
	}
	
	public void setName(String name) {
		this.groupName = name;
	}
}
