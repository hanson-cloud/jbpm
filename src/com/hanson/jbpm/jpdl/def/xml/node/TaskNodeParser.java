package com.hanson.jbpm.jpdl.def.xml.node;

import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.base.Task;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.jpdl.def.xml.base.TaskParser;

public class TaskNodeParser extends CommonParser {
	public TaskNode parser(Element el, ProcessDefinition pdf,ProcessDefinitionParser pdp) 
	throws Exception {
		parseNameAndException(el, pdf);
		
		/* Ŀǰ��֧��һ��TaskNode����һ��Task */
		Task task = new TaskParser().parser((Element)el.element("task"), pdf);	
		
		TaskNode node = new TaskNode(name, task, exception);	
		
		String async = el.attributeValue("async");		
		String seperated = el.attributeValue("seperated");
		String block = el.element("task").attributeValue("block");
		
		
		
		if (async == null && block == null) {
			node.setOnechoice(true);
		} else {
			if ("true".equals(async))
				node.setAsync(true);
			if ("true".equals(seperated))
				node.setSeperated(true);
			if ("false".equals(block))
				node.setSequence(false);
			if ("true".equals(block))
				node.setParallel(true);			
		}
		
		parseTransitions(el, node, pdf);	
		
		parseAction(el, node, pdf);
		
		return node;
	}	
}
