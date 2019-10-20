package com.hanson.jbpm.jpdl.def.xml.base;

import java.lang.reflect.Constructor;
import java.util.List;

import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.flow.ProcessNodesOrder;
import com.hanson.jbpm.jpdl.def.node.NodeBase;
import com.hanson.jbpm.jpdl.def.node.Transition;
import com.hanson.jbpm.jpdl.def.xml.DefinitionParserException;
import com.hanson.jbpm.jpdl.exe.impl.ActionHandler;
import com.hanson.jbpm.jpdl.exe.impl.ExpressionActionHandler;

public class TransitionParser {
	private NodeBase node;
	public TransitionParser(NodeBase node) {
		this.node = node;
	}
	public Transition parser(Element el, ProcessDefinition pdf) {
		Transition transition = null;
		try {
			String to = el.attributeValue("to");
			
			to = ProcessNodesOrder.trimIndex(pdf.getProcessName(), to); 
			
			String name = el.attributeValue("name");
			
			Element e = (Element)el.element("condition");
			String condition = null;
			if (e != null)
				condition = e.attributeValue("expression");
			StateException exception = new StateExceptionParser().parser((Element)el.element("exception-handler"));
			
			ActionHandler[] handlers = null;
						
			List<Element> list = el.elements("action");
			if (list != null && list.size() > 0) {
				handlers = new ActionHandler[list.size()];				
				for (int i=0; i<list.size(); i++) {
					String eh = (list.get(i)).attributeValue("class");
					if (eh != null && !eh.equals("")) {
						//handler = (ActionHandler)Class.forName(eh).newInstance();
						Class clazz = Class.forName(eh);
						List params = (list.get(i)).elements();
						Class[] parameterTypes = new Class[params.size()];
						for (int j=0; j<params.size(); j++) { parameterTypes[j] = String.class; }
						Object[] initargs = new Object[params.size()];
						for (int j=0; j<params.size(); j++) { initargs[j] = ((Element)params.get(j)).getText().trim(); }						
						Constructor constructor = clazz.getConstructor(parameterTypes);
						handlers[i] = (ActionHandler)constructor.newInstance(initargs);
					} else {
						eh = el.attributeValue("expression");
						if (eh != null) {
							String exprname = el.attributeValue("name");
							handlers[i] = new ExpressionActionHandler(exprname, eh);
						} 
					}
				}				
			}			
			
			transition = new Transition(name, to, condition, handlers, exception);
			return transition;
		} catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			throw new DefinitionParserException(el, ex);
		}		
	}	
}
