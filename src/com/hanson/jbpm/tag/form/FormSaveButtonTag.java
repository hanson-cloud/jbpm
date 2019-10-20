package com.hanson.jbpm.tag.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import com.suntek.ccf.log.CCFLogger;

public class FormSaveButtonTag extends TransitionButtonTag_JSF {
	private static final long serialVersionUID = 1L;
		
	public int doEndTag() throws JspException {
		try {
			String caption = getBodyContent().getString();
			
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			
			String formReadonly = request.getParameter("readonly");
			
			String html = "";
			if (formReadonly != null && formReadonly.equals("true")) {
				html = "<button class=\"submitbtn\" onclick=\"saveForm('')\" disabled>" + caption + "</button>";
			} else {
				html = "<button class=\"submitbtn\" onclick=\"saveForm('')\">" + caption + "</button>";
			}			
			pageContext.getOut().print(html);
			
		} catch (Exception e) {
			CCFLogger.logger.error(e,e);
		}
		return SKIP_BODY;
	}
}
