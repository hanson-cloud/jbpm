package com.hanson.jbpm.jpdl.def;

/**
 * ִ��
 * Copyright (C)2012 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�FormFields.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Jun 22, 2012
 */
public class FormField {
	
	private String name;
	
	private String title;
	
	private String dsName;
	
	private String provider;
	
	private String formName;  //������ҳ��
	
	public FormField() {
		
	}
	
	public FormField(String name, String title, String dsName, String provider, String formName) {
		this.name = name;
		this.title = name;
		this.dsName = dsName;
		this.provider = provider;
		this.setFormName(formName);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getFormName() {
		return formName;
	}
	
	
}
