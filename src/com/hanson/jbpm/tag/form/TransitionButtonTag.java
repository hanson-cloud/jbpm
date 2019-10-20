package com.hanson.jbpm.tag.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.hanson.jbpm.log.CommonLogger;

public class TransitionButtonTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String plugin;
	private String style;
	private String prevNode = "";
	
	public int doEndTag() throws JspException {
		try {
			String caption = getBodyContent().getString();
			
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			
			String formReadonly = request.getParameter("readonly");
			
			String html = "";
			if (formReadonly != null && formReadonly.equals("true")) {
				html = "<button class=\"submitbtn\" onclick=\"submitForm('" + name + "'); return false;\" disabled>" + caption + "</button>";
			} else {
				html = "<button class=\"submitbtn\" onclick=\"submitForm('" + name + "'); return false;\">" + caption + "</button>";
			}			
			pageContext.getOut().print(html);			
		} catch (Exception e) {
			CommonLogger.logger.error(e,e);
			try {
				pageContext.getOut().print(e.getMessage());
			} catch (IOException e1) {
				CommonLogger.logger.error(e1);
			}
		}
		return SKIP_BODY;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public void setPlugin(String plugin)
	{
		this.plugin = plugin;
	}
	public String getPlugin()
	{
		return plugin;
	}

	public String getPrevNode() {
		return prevNode;
	}
	public void setPrevNode(String prevNode) {
		this.prevNode = prevNode;
	}
}
