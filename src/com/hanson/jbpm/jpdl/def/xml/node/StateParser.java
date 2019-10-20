package com.hanson.jbpm.jpdl.def.xml.node;

import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.State;

public class StateParser extends CommonParser {
	public State parser(Element el, ProcessDefinition pdf,ProcessDefinitionParser pdp) {		
		parseNameAndException(el, pdf);		
		
		State node = new State(name, exception);	
		
		parseTransitions(el, node, pdf);		
		
		return node;
	}
}
