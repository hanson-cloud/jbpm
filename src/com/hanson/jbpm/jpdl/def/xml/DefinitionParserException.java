package com.hanson.jbpm.jpdl.def.xml;

import org.dom4j.Element;


public class DefinitionParserException extends RuntimeException {
	private final static long serialVersionUID = 1l;
	private Exception ex;
	private Element el;
	
	public DefinitionParserException(Element el, Exception ex) {
		this.ex = ex;
		this.el = el;
	}
	
	public String getMessage() {
		//CommonLogger.logger.debug(ex, ex);
		String msg = "";
		if (el != null) 
			msg = el.asXML();
		return "�����ļ������쳣: " + ex.getClass().getName() + ": " + ex.getMessage()+ "\r\n" + msg;
	}
}
