package com.hanson.jbpm.jpdl.exe.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.node.NodeBase;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class ExceptionHandler {
	private static final long serialVersionUID = 1L;

	//long id = 0;
	protected String exceptionClassName = null;
	protected NodeBase nodeBase = null;
	protected List<ActionHandler> handlers = new ArrayList<ActionHandler>();

	public ExceptionHandler() {
	}

	public boolean matches( Throwable exception ) {
		boolean matches = true;
		if (exceptionClassName!=null) {
			Class clazz;
			try {
				clazz = this.getClass().getClassLoader().loadClass(exceptionClassName);
			} catch (ClassNotFoundException e) {
				throw new BpmException("�����ڸ��ࣺ '"+exceptionClassName+"'", e);
			}
			if (! clazz.isAssignableFrom(exception.getClass())) {
				matches = false;
			}
		}
		return matches;
	}

	public void handleException(NodeBase nodeBase, ExecutionContext executionContext) throws Exception {
		Iterator<ActionHandler> iter = handlers.iterator();
		ActionHandler handler;
		while (iter.hasNext()) {
			handler = iter.next();
			handler.execute(executionContext);
		}
	}
	
	public void addAction(ActionHandler handler) {
		handlers.add(handler);
	}	
	
	public String getExceptionClassName() {
		return exceptionClassName;
	}
	
	public void setExceptionClassName(String exceptionClassName) {
		this.exceptionClassName = exceptionClassName;
	}
	
	public NodeBase getNodeBase() {
		return nodeBase;
	}
}
