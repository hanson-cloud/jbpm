package com.hanson.jbpm.jpdl.def;

import java.util.ArrayList;
import java.util.List;

public class ExtendQuery {
	public static String BPM_FIELD_LEFT = "INST_TITLE,INIT_TIME,ASSIGN_UNAME,ASSIGN_TIME," +
										  "DEALER,TASK_NAME,ASSIGN_UNAME,ASSIGN_TIME,TASK_NAME";
	
	private String tableName = "";
	private String bpmFieldLeft = BPM_FIELD_LEFT;
	private String bpmFieldRight = "";
		
	private List<ExtQueryField> fields = new ArrayList<ExtQueryField>();
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setFields(List<ExtQueryField> fields) {
		this.fields = fields;
	}
	public List<ExtQueryField> getFields() {
		return fields;
	}
	public void setBpmFieldLeft(String bpmFieldLeft) {
		if (bpmFieldLeft == null) 
			bpmFieldLeft = BPM_FIELD_LEFT;
		this.bpmFieldLeft = bpmFieldLeft;
	}
	public String getBpmFieldLeft() {
		return bpmFieldLeft;
	}
	public void setBpmFieldRight(String bpmFieldRight) {
		this.bpmFieldRight = bpmFieldRight;
	}
	public String getBpmFieldRight() {
		return bpmFieldRight;
	}
}
