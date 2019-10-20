package com.hanson.jbpm.jpdl.def.xml.node;

import java.util.List;

import com.hanson.jbpm.jpdl.def.xml.base.TransitionParser;
import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.flow.ProcessNodesOrder;
import com.hanson.jbpm.jpdl.def.node.NodeBase;
import com.hanson.jbpm.jpdl.def.xml.base.StateExceptionParser;
import com.hanson.jbpm.jpdl.exe.impl.ActionHandler;
import com.hanson.jbpm.jpdl.exe.impl.ExpressionActionHandler;

public class CommonParser {
	public String name;
	public StateException exception;
	
	public void parseNameAndException (Element el, ProcessDefinition pdf) {
		name = el.attributeValue("name");
		
		name = ProcessNodesOrder.trimIndex(pdf.getProcessName(), name);
		
		exception = new StateExceptionParser().parser((Element)el.element("exception-handler"));
	}
	
	public void parseTransitions(Element el, NodeBase node, ProcessDefinition pdf) {
		Element e;
		List<Element> list = el.elements("transition");
		for (int i=0; i<list.size(); i++) {
			e = list.get(i);
			node.addLeavingTransition(new TransitionParser(node).parser(e, pdf));
		}
	}
	
	public void parseAction(Element el, NodeBase node, ProcessDefinition pdf) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
		Element e = null;
		List<Element> list = el.elements("action");
		if (list.size() > 0) {
			ActionHandler[] handlers = new ActionHandler[list.size()];
			for (int i=0; i<list.size(); i++) {
				e = list.get(i);
				String attr = e.attributeValue("class");
				//String name = e.attributeValue("name");
				if (attr != null)
					handlers[i] = (ActionHandler)Class.forName(attr).newInstance();
				else {
					attr = e.attributeValue("expression");
					String exprname = e.attributeValue("name");
					handlers[i] = new ExpressionActionHandler(exprname, attr);
				}
			}
			
			if (handlers.length > 0)
				node.setHandlers(handlers);
			
		}
	}	
}
