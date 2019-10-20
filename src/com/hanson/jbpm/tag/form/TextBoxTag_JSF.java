package com.hanson.jbpm.tag.form;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class TextBoxTag_JSF extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	
	private String id = "";
	private String check = "";
	private String readonly = "";
	
	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	private String required = "";
	private String pattern = "";
	private String warning = "";
	private String value = "";
	private String size = "30";	
	private String style;
	private String type = "text"; // ��Ҫ��password����֧��
	
	public int doEndTag() throws JspException {
		
		try {
			if (readonly == null) readonly = "";
			else { 
				if (readonly.equals("true"))
					readonly = "readonly"; 
			}
			
			String html = "<input type='"+type+"' class='value-check-textbox textbox-border-blur' " +
					"size="+ size + " "+ readonly + " id='"+ id +"' name='"+ id +"'";
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

	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
