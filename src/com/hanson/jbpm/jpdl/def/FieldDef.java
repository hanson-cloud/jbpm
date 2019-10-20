package com.hanson.jbpm.jpdl.def;

import com.suntek.eap.util.jdbc.Dialect;
import com.hanson.jbpm.jpdl.BpmException;

public class FieldDef {
	public final static String VARCHAR = "VARCHAR";
	public final static String CHAR = "CHAR";
	public final static String NUMBER = "NUMBER";
	public final static String DATETIME = "DATE";
	
	public String name;
	public String type;
	
	public FieldDef(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getValue(Dialect dialect, String value) {
		if (type.equals(VARCHAR) || type.equals(CHAR))
			return "'" + value + "'";
		if (type.equals(NUMBER))
			return  value;
		if (type.equals(DATETIME))
			return dialect.todate("'" + value+ "'");
		else
			throw new BpmException("�ֶ� " + name + " ���� " + type + ", �Ǳ�׼��������");
	}
}
