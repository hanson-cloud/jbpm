package com.hanson.jbpm.tag.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.suntek.ccf.log.CCFLogger;


public class FormIncludeTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
		
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			String html = "";
			
			String moduleName = ((HttpServletRequest)pageContext.getRequest()).getContextPath().substring(1);
			
			String mainFormData = (String)request.getSession().getAttribute("mainFormData");
			String readonly = (String)request.getSession().getAttribute("formReadOnly");
			
			if (mainFormData != null) {
				html = "<script>\r\n";
				/* ģ������ */
				html = html + "MODULE_NAME = '" + moduleName + "'; \r\n";
				/* ��������  */
				html = html + "PROCESS_NAME = '" + (String)request.getSession().getAttribute("processName")+ "'; \r\n";
				/* ʵ��id */
				html = html + "INSTANCE_ID = '" + (String)request.getSession().getAttribute("instanceId")+ "'; \r\n";
				
				html = html + "TASK_ID = '" + (String)request.getSession().getAttribute("taskId")+ "'; \r\n";		
				/* ��ֻ������ */
				
				html = html + "FORM_READONLY = '" + readonly + "'; \r\n";
				/* �ɵ�ʱ��, ���ڼ��㳬ʱ */
				String dueTime = (String)request.getSession().getAttribute("dueTime");
				html = html + "DUE_TIME = '" + dueTime + "'; \r\n";
				/* ��������, ���������� */			
				html = html + "MAINFORM_DATA = " + mainFormData +"; \r\n";
				
				html = html + "</script>\r\n";
				
				request.getSession().removeAttribute("dueTime");
				request.getSession().removeAttribute("mainFormData");
			}
			pageContext.getOut().print(html);
		} catch (Exception e) {
			CCFLogger.logger.error(e,e);
		}
		return SKIP_BODY;
	}
}
