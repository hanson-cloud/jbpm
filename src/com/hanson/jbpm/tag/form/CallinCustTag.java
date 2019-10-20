package com.hanson.jbpm.tag.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.suntek.opencc.CallContext;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.pico.custmgr.Cust;
import com.suntek.opencc.pico.custmgr.Customer;

public class CallinCustTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	
	private String name = Cust.CUST_NAME;
	private String check = "";
	private String readonly = "";
	
	private String required = "";
	private String pattern = "";
	private String warning = "";
	private String value = "";
	private String size = "30";	
	//private String type = "text"; 
		
	public int doEndTag() throws JspException {	
		try {
			if (readonly == null) { 
				readonly = "";
			}
			else { 
				if (readonly.equals("true"))
					readonly = "readonly"; 
			}
			
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			String view = request.getParameter("action");
			
			if (view != null && view.equals("NewInstance")) {
				// ֻ���½�ʱ����Ҫ�� ����ͻ���Ϣ�л�ȡ����		
				CallContext context = OpenCC.getCallContext(request.getRemoteUser());
				if (context != null) {
					Customer cust = context.getCallinCust();
					if (cust != null)
						value = (String) cust.get(name);
				}
			}
			
			String html = "<input type='text' class='value-check-textbox textbox-border-blur' " +
						  "size="+ size + " "+ readonly + " id='"+ name +"' name='"+ name +"'";
			if(value.length() > 0) {
				html += " value='"+ value + "'";
			}
			
			if(check.length() > 0) {
				html += " check='"+ check + "'";
				if(pattern.length() > 0) html += " pattern='"+ pattern +"'";
				if(required.length() > 0) html += " required='" + required + "'";
				if(warning.length() > 0) html += " warning='" + warning + "'";
				html += " onkeyup='vc_tb_on_keyup(this)'";
			}
			
			html += " onfocus='vc_tb_on_focus(this)' onblur='vc_tb_on_blur(this)'>";
			pageContext.getOut().print(html);
			
		} catch (Exception e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}
	
	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
}
