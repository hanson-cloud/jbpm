package com.hanson.jbpm.jpdl.def.xml.base;

import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.def.xml.DefinitionParserException;
import com.hanson.jbpm.jpdl.exe.impl.ExceptionHandler;
import org.dom4j.Element;

public class StateExceptionParser {
	public StateException parser(Element el) {
		StateException exception = null;
		if (el == null) return exception;
		try {
			exception = new StateException();
			exception.setExceptionClass(el.attributeValue("exception-class"));
			String handlerClassName = ((Element)el.element("action")).attributeValue("class");
			ExceptionHandler handler = (ExceptionHandler)Class.forName(handlerClassName).newInstance();
			exception.setHandler(handler);	
			return exception;
		} catch (Exception ex) {
			throw new DefinitionParserException(el, ex);
		}
	}
}
