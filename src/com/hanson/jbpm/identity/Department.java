package com.hanson.jbpm.identity;

import java.util.List;


public abstract class Department {
	/* ���ű��� */
	private String deptId;
	/* �������� */
	private String deptName;
	
	public Department(String deptId) {
		this.deptId = deptId;
	}
	
	/**
	 * ��ѯ��������������û�
	 * @return
	 */
	public abstract List<User> getUsers();	
	
	public abstract List<String> getUserIds();

	/**
	 * ���ݲ���ID���Ҳ������� 
	 * @return
	 */
	public abstract String getNameById(String deptId);
	
	public String getId() {
		return deptId;
	}
	
	public void setId(String id) {
		this.deptId = id;
	}
	
	public String getName() {
		if (deptName == null) {
			deptName = getNameById(deptId);
		}
		return deptName;
	}
	
	public void setName(String name) {
		this.deptName = name;
	}
}
