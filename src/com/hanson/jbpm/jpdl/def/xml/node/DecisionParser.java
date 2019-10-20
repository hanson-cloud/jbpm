package com.hanson.jbpm.jpdl.def.xml.node;

import com.hanson.jbpm.jpdl.exe.impl.DecisionHandler;
import com.hanson.jbpm.jpdl.exe.impl.ExpressionDecisionHandler;
import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.Decision;
import com.hanson.jbpm.jpdl.def.xml.DefinitionParserException;

public class DecisionParser extends CommonParser {	
	public Decision parser(Element el, ProcessDefinition pdf,ProcessDefinitionParser pdp) {
		
		parseNameAndException(el, pdf);
		
		DecisionHandler handler = null;
		String expression = el.attributeValue("expression");
		CommonLogger.logger.debug("decision parsered expression: " + expression);
		if (expression != null && !"".equals(expression))
			handler = new ExpressionDecisionHandler(expression);
		Element eh = (Element)el.element("handler");
		try {
			if (eh != null)
				handler = (DecisionHandler)Class.forName(eh.attributeValue("class")).newInstance();
		} catch (Exception ex) {
			throw new DefinitionParserException(el, ex);
		}
		
		Decision node;
		if (handler != null)
			node = new Decision(name, new DecisionHandler[]{handler}, exception);
		else
			node = new Decision(name, new DecisionHandler[]{}, exception);
		
		parseTransitions(el, node, pdf);
		
		return node;
	}
}
