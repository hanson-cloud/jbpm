package com.hanson.jbpm.jpdl.def.xml.node;

import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.Start;

public class StartStateParser extends CommonParser {
	public Start parser(Element el, ProcessDefinition pdf, ProcessDefinitionParser pdp) {				
		parseNameAndException(el, pdf);				
		
		Start node = new Start(name, exception);
		
		parseTransitions(el, node, pdf);
		
		pdf.setStart(node);
		
		return node;
	}
}
