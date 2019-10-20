package com.hanson.jbpm.jpdl.def.base;

import java.util.ArrayList;
import java.util.List;

import com.hanson.jbpm.jpdl.exe.impl.ActionHandler;

public class Handler {
	private List<Object> fields;
	private ActionHandler handler;
	
	public void addField(Object o) {
		if (fields == null) fields = new ArrayList<Object>();
		fields.add(o);
	}
	public List<Object> getFields() {
		return fields;
	}

	public void setFields(List<Object> attributes) {
		this.fields = attributes;
	}
	public ActionHandler getHandler() {
		return handler;
	}
	public void setHandler(String handler) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.handler = (ActionHandler)Class.forName(handler).newInstance();
	}	
}
