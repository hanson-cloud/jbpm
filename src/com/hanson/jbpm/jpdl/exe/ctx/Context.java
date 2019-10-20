package com.hanson.jbpm.jpdl.exe.ctx;

import com.hanson.jbpm.jpdl.def.node.NodeBase;

public class Context {
	public final static ContextType types = new ContextType();
	
	public final static ContextConstant values = new ContextConstant();
	
	public static boolean checkNodeIsFirstTask(NodeBase node) {
		return node.getName().equals(node.getProcessDefinition().getStart().getName());
	}	
}
