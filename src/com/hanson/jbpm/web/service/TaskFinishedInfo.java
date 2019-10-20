package com.hanson.jbpm.web.service;

public class TaskFinishedInfo {
	private boolean finished;
	private boolean archived;	
	private String dealer = "";
	private String dealTime;
	
	private boolean locked;
	private String lockedUser = "";
	
	public boolean isArchived() {
		return archived;
	}
	public void setArchived(boolean archived) {
		this.finished = archived;
		this.archived = archived;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	public String getDealer() {
		return dealer;
	}
	public void setDealer(String dealer) {
		this.dealer = dealer;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLockedUser(String lockedUser) {
		this.lockedUser = lockedUser;
	}
	public String getLockedUser() {
		if (lockedUser == null) 
			lockedUser = "";
		return lockedUser;
	}
}
