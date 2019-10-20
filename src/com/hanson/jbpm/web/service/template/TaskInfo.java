package com.hanson.jbpm.web.service.template;

public class TaskInfo {
	public final static String SUBMIT = "1";
	public final static String COMMENT = "2";
	
	public String TASK_ID;
	public String INST_ID;
	public String BIZ_ID;
	public String CALL_ID = "";
	
	public String PROC_NAME;
	
	public String TASK_NAME;
	
	public String ASSIGN_UNAME;
	public String ASSIGN_DEPT;
	public String ASSIGN_TIME;
	public String ASSIGN_MEMO;
	
	public String DEAL_UNAME;
	public String DEAL_DEPT;	
	public String DEAL_TIME;
	
	public String FLAG;
	
	public String getFLAG() {
		return FLAG;
	}
	public void setFLAG(String flag) {
		FLAG = flag;
	}
	public String getTASK_ID() {
		return TASK_ID;
	}
	public void setTASK_ID(String task_id) {
		TASK_ID = task_id;
	}
	public String getINST_ID() {
		return INST_ID;
	}
	public void setINST_ID(String inst_id) {
		INST_ID = inst_id;
	}
	public String getBIZ_ID() {
		return BIZ_ID;
	}
	public void setBIZ_ID(String biz_id) {
		BIZ_ID = biz_id;
	}
	public String getPROC_NAME() {
		return PROC_NAME;
	}
	public void setPROC_NAME(String proc_name) {
		PROC_NAME = proc_name;
	}
	public String getTASK_NAME() {
		return TASK_NAME;
	}
	public void setTASK_NAME(String task_name) {
		TASK_NAME = task_name;
	}
	public String getASSIGN_UNAME() {
		return ASSIGN_UNAME;
	}
	public void setASSIGN_UNAME(String assign_uname) {
		ASSIGN_UNAME = assign_uname;
	}
	public String getASSIGN_DEPT() {
		return ASSIGN_DEPT;
	}
	public void setASSIGN_DEPT(String assign_dept) {
		ASSIGN_DEPT = assign_dept;
	}
	public String getASSIGN_TIME() {
		return ASSIGN_TIME;
	}
	public void setASSIGN_TIME(String assign_time) {
		ASSIGN_TIME = assign_time;
	}
	public String getASSIGN_MEMO() {
		return ASSIGN_MEMO;
	}
	public void setASSIGN_MEMO(String assign_memo) {
		ASSIGN_MEMO = assign_memo;
	}
	public String getDEAL_UNAME() {
		return DEAL_UNAME;
	}
	public void setDEAL_UNAME(String deal_uname) {
		DEAL_UNAME = deal_uname;
	}
	public String getDEAL_DEPT() {
		return DEAL_DEPT;
	}
	public void setDEAL_DEPT(String deal_dept) {
		DEAL_DEPT = deal_dept;
	}
	public String getDEAL_TIME() {
		return DEAL_TIME;
	}
	public void setDEAL_TIME(String deal_time) {
		DEAL_TIME = deal_time;
	}
	

	public String getCALL_ID() {		
		return CALL_ID;
	}
	public void setCALL_ID(String call_id) {
		if (call_id == null)
			call_id = "";
		CALL_ID = call_id;
	}
	
	
}
