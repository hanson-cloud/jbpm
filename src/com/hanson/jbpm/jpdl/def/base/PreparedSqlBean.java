package com.hanson.jbpm.jpdl.def.base;

/**
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�PreparedSqlBean.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Dec 9, 2013
 */
public class PreparedSqlBean {
	
	private String sql;
	
	private Object[] args;
	
	
	public static PreparedSqlBean build(String sql, Object... args) {
		PreparedSqlBean bean = new PreparedSqlBean();
		bean.setArgs(args);
		bean.setSql(sql);
		return bean;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

}
