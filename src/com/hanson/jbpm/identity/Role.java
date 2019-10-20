package com.hanson.jbpm.identity;

import java.util.List;

public abstract class Role {
	private String id;
	private String name;
	
	public Role() {}
	
	public Role(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public abstract List<User> getUsers();
	
	public abstract List<User> getUsersByRoleNames();
}
