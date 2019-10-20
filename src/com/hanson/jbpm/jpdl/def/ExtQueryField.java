package com.hanson.jbpm.jpdl.def;

import java.util.List;
import java.util.Map;

public class ExtQueryField {
	private String id;
	private String name;
	private String align;
	private String width;
	private String asParam;
	private String operator;
	private String fieldExpr;
	private List<Map> option;
	private String subElement;
	private String optionSQL;
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getAsParam() {
		return asParam;
	}
	public void setAsParam(String asParam) {
		this.asParam = asParam;
	}
	public void setOption(List option) {
		this.option = option;
	}
	public List<Map> getOption() {
		return option;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOperator() {
		return operator;
	}
	public void setFieldExpr(String fieldExpr) {
		this.fieldExpr = fieldExpr;
	}
	public String getFieldExpr() {
		return fieldExpr;
	}
	public void setSubElement(String subElement) {
		this.subElement = subElement;
	}
	public String getSubElement() {
		return subElement;
	}
	public void setOptionSQL(String optionSQL) {
		this.optionSQL = optionSQL;
	}
	public String getOptionSQL() {
		return optionSQL;
	}	
}
