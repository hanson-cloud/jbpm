package com.hanson.jbpm.web.service.template;

import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;

public interface TemplateMapSetter {
	public void doGet(Map<String, Object> map) throws BpmException;
}
