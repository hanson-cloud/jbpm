package com.hanson.jbpm.tag.form;

import javax.servlet.jsp.tagext.BodyTagSupport;

public class TransitionButtonTag_JSF extends BodyTagSupport {
	private static final long serialVersionUID = 2827897487827770868L;
	
	protected String name;
	
	protected String plugin;
	
	protected String style;
	
	public String getPlugin() {
		return plugin;
	}
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getString() {
		return this.style;
	}
	public void setStyle(String style) {
		this.style = style;
	}	
}
