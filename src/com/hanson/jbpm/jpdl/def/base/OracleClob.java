package com.hanson.jbpm.jpdl.def.base;

/**
 * Copyright (C)2014 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�OracleClob.java
 * <p>ժ����Ҫ��αװoracle CLOB����, �����ύʱ���⴦��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Feb 11, 2014
 */
public class OracleClob {
	
	private String text;
	
	/**
	 * ����ʵ��
	 * @param text
	 * @return
	 */
	public static OracleClob build(String text) {
		OracleClob inst = new OracleClob();
		inst.setText(text);
		return inst;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	
	public String toString() {
		return text;
	}

}
