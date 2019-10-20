package com.hanson.jbpm.identity;

import java.util.List;

public abstract class User { 	
	/* �û����� */
	private String userId;
	/* �û���¼���� */
	private String loginName;
	/* �û���������*/
	private String userName;
	
	private String password;
	
	private String email;
	
	private String mobile;
	
	private String fax;
	
	protected boolean deptManager;
	
	public User() {}
	
	/**
	 * �����û���¼����(request.getRemoteUser())�����û�����
	 * @param loginName
	 */
	public User(String loginName) {
		this.loginName = loginName;
	}
		
	/**
	 * ��ѯ�û����ڵĲ����б�
	 * @return
	 */
	public abstract Department[] getDepts();
	
	public abstract User getDeptManager();
	
	public abstract Role[] getRoles();
	
	/**
	 * �Ƿ�ϵͳ����Ա
	 * @return
	 */
	public abstract boolean isAdministrator();
	public abstract boolean isDeptManager();
	public abstract void setDeptManager(boolean isDeptManager);
	
	/**
	 * �Ƿ����쵼
	 * @param deptId
	 * @return
	 */
	public abstract boolean isLeader(String deptId);
	
	public abstract String getUserNamesByLoginNames(List<String> userList);
		
	public String getId() {
		return this.userId;
	}
	
	public String getName() {		
		return this.userName;
	}
	
	public String getLoginName() {
		return this.loginName;
	}
	
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFax() {
		return fax;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
}
