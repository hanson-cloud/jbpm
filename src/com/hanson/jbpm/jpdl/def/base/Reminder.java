package com.hanson.jbpm.jpdl.def.base;

public class Reminder {
	private int repeatTime;
	private double duedate;
	private String msg;

	public double getDuedate() {
		return duedate;
	}

	public void setDuedate(double duedate) {
		this.duedate = duedate;
	}

	public int getRepeatTime() {
		return repeatTime;
	}

	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}	
}
