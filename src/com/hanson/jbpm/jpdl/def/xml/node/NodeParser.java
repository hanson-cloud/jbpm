package com.hanson.jbpm.jpdl.def.xml.node;

import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.Node;
import org.dom4j.Element;

import com.hanson.jbpm.jpdl.exe.impl.ActionHandler;

public class NodeParser extends CommonParser {
	public Node parser(Element el, ProcessDefinition pdf, ProcessDefinitionParser pdp)
	throws Exception {		
		parseNameAndException(el, pdf);
		
		ActionHandler handler = null;
		Node node = new Node(name, handler, exception);
		
		parseTransitions(el, node, pdf);

		parseAction(el, node, pdf);
		
		return node;
	}
}
