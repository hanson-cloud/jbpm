package com.hanson.jbpm.jpdl.def.form;

public class TaskFormDef {
	private String url;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getWritableElement() {
		if (writableElement == null)
			writableElement = "";
		return writableElement;
	}
	public void setWritableElement(String writableElement) {
		this.writableElement = writableElement;
	}
	private String writableElement;
}
