package com.hanson.jbpm.tag.grid;

import com.suntek.ccf.log.CCFLogger;
import com.suntek.ccf.ui.tag.lib.CCFTag;

public class GridFieldTag extends CCFTag {
	private final static long serialVersionUID = 1l;
	
	/* �ֶ����� */
	String name;
	/* �ֶα��� */
	String header;
	
	/* ����ʵ���� ��չ�ֶ�����, ���� CALL_ID,CUST_ID,CUST_TEL */
	String extField;	
	/* ��չ�ֶε�����Դ */
	String provider;
	
	/* �ֶ���ʾ��� */
	String width;
	/* ��ʾ���� */
	String align;
	/* render���� */
	String render;
	/*�Ƿ���Ҫ����*/
	String needExport;
		
	public int doEndTag() {
		try {						
			if (width 	== null) 		width 		= "100";
			if (align 	== null) 		align 		= "left";
			if (render 	== null) 		render 		= "";
			if (needExport == null)		needExport 	= "true";
			
			String options = "";
			
			StringBuffer body = new StringBuffer("<field ");
			body.append("name=\""+name+"\" ");
			body.append("header=\""+header+"\" ");			
			body.append("width=\""+width+"\" ");			
			body.append("align=\""+align+"\" ");
			body.append("render=\""+render+"\" ");
			body.append("needExport=\""+needExport+"\" ");
			body.append("options=\""+options+"\" ");
			body.append(" />");
			pageContext.getOut().print(body);			
		} catch (Exception ex) { 
			CCFLogger.logger.error(ex, ex);
		} 		
		return EVAL_PAGE;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getRender() {
		return render;
	}
	public void setRender(String render) {
		this.render = render;
	}
	public String getNeedExport() {
		return needExport;
	}
	public void setNeedExport(String needExport) {
		this.needExport = needExport;
	}	
	public String getExtField() {
		return extField;
	}
	public void setExtField(String extField) {
		this.extField = extField;
	}
}
