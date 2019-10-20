package com.hanson.jbpm.jpdl.def.node;

import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.exe.impl.ActionHandler;


public class Node extends NodeBase {	
	
	private ActionHandler[] handlers = null;

	public Node(String name, ActionHandler action, StateException exception) {
		super(name, exception);
		this.setHandlers(handlers);
	}
}
