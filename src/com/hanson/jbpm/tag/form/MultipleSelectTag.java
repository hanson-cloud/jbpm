package com.hanson.jbpm.tag.form;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.suntek.ccf.dao.DaoFactory;

public class MultipleSelectTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 1L;

	private String id = "";
	private String sql = "";
	private String size = "20";
	private String width = "150";
	private String height = "100";
		
	public int doEndTag() throws JspException {		
		try {
			String html = "<table cellspacing=0 cellpadding=0><tr><td>";
			
			html = html + "<input type='text' clazz='multiple' class='value-check-textbox textbox-border-blur' " +
					"size="+ size +" id='"+ id +"' name='"+ id +"'/>";
			html = html + "<td class=\"ms_imgArea2\">";
			html = html + "<a href=\"javascript:showCheckList('MS_"+id+"')\"></a></td></tr></table>";
			html = html + "<div class='ui-dropdownchecklist-dropcontainer-wrapper' id='MS_"+id+"' style='position:absolute;display:none'>";
			html = html + "<div class='ui-dropdownchecklist-dropcontainer' style='overflow-y:auto; height:"+height+"; width:"+width+"'>";
			html = html + "<span class='ui-dropdownchecklist-wrapper' style='inline-block:default'>";
			
			String moduleName = ((HttpServletRequest)pageContext.getRequest()).getContextPath().substring(1);
			List list = DaoFactory.getJdbc(moduleName).queryForList(sql);
			Map map = null;
			String key, value;
			for (int i=0; i<list.size(); i++) {
				map = (Map)list.get(i);
				key = (String)map.get("id");
				value = (String)map.get("name");
				html = html + "<input type='checkbox' id='CK_"+id+"_"+i+"' value='"+key+"' chname='"+value+"'/>" +
						"<label for='CK_"+id+"_"+i+"'>"+value+"</label><br>";			
			}
			html = html + "</span>";
			html = html + "</div>";
			html = html + "</div>";
			
			pageContext.getOut().print(html);
			
		} catch (Exception e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}
