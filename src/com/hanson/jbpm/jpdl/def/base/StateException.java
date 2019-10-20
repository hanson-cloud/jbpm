package com.hanson.jbpm.jpdl.def.base;

import com.hanson.jbpm.jpdl.exe.impl.ExceptionHandler;

public class StateException {
	private String exceptionClass;
	private ExceptionHandler handler;
	
	public String getExceptionClass() {
		return exceptionClass;
	}
	public void setExceptionClass(String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}
	public ExceptionHandler getHandler() {
		return handler;
	}
	public void setHandler(ExceptionHandler handler) {
		this.handler = handler;
	}
}
