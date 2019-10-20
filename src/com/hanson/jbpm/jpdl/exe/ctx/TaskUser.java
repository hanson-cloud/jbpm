package com.hanson.jbpm.jpdl.exe.ctx;

public class TaskUser {
	private String userId;
	private String userName;
	private String deptId;
	private String deptName;
	
	public TaskUser(InstanceContext ctx) {
		userId = ctx.getParameter(Context.values.ULOGNAME, Context.types.USER);
		userName = ctx.getParameter(Context.values.UNAME, Context.types.USER);
		deptId = ctx.getParameter(Context.values.DEPTID, Context.types.USER);
		deptName = ctx.getParameter(Context.values.DEPTNAME, Context.types.USER);
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
}
