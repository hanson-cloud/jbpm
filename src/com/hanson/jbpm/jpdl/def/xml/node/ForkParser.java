package com.hanson.jbpm.jpdl.def.xml.node;

import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.ForkNode;

public class ForkParser extends CommonParser {
	public ForkNode parser(Element el, ProcessDefinition pdf, ProcessDefinitionParser pdp) 
	throws Exception {		
		parseNameAndException(el, pdf);		
		ForkNode node = new ForkNode(name, exception);
		parseTransitions(el, node, pdf);
		return node;
	}
}
