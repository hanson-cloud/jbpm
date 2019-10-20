package com.hanson.jbpm.jpdl.def.xml.node;

import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.JoinNode;

public class JoinParser  extends CommonParser {
	public JoinNode parser(Element el, ProcessDefinition pdf, ProcessDefinitionParser pdp) 
	throws Exception {		
		parseNameAndException(el, pdf);		
		JoinNode node = new JoinNode(name, exception);
		parseTransitions(el, node, pdf);
		return node;
	}
}
