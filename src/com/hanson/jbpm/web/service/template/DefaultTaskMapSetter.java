package com.hanson.jbpm.web.service.template;

import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;

public class DefaultTaskMapSetter implements TemplateMapSetter {
	private String mainFormData = "";
	
	public DefaultTaskMapSetter(String mainFormData) {
		if (mainFormData == null) mainFormData = "";
		this.mainFormData = mainFormData;
	}
	
	public void doGet(Map<String, Object> map) throws BpmException {
		String readonly = (String)map.get("readonly");
		String history = (String)map.get("history");
		if ((readonly != null && readonly.equals("true")) || (!"true".equals(history))){
			map.put("mainFormData", mainFormData);
		} else {
			map.put("mainFormData", "");
		}
	}
}
