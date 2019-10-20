package com.hanson.jbpm.jpdl.def.xml.node;

import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.End;


public class EndStateParser extends CommonParser {
	public End parser(Element el, ProcessDefinition pdf,ProcessDefinitionParser pdp) {
		parseNameAndException(el, pdf);
		
		End node = new End(name, exception);
		
		return node;
	}
}
