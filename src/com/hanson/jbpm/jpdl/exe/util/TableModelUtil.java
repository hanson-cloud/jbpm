package com.hanson.jbpm.jpdl.exe.util;

/**
 * Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�qlzqcustinfo
 * <p>�ļ����ƣ�TableModelUtil.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Jun 30, 2011
 */
public class TableModelUtil {
	
	
	/**
	 * ��ȡ��ǰ���Ե�GETTER����
	 * @param fieldName
	 * @return
	 */
	public static String getGetterMethod(String fieldName) {
		String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		return methodName;
	}
	
	
	/**
	 * ��ȡ��ǰ���Ե�SETTER����
	 * @param fieldName
	 * @return
	 */
	public static String getSetterMethod(String fieldName) {
		String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		return methodName;
	}
	
	
	/**
	 * ����������ת���ɴ�д�ı��ֶ��� ����(custName��תΪCUST_NAME)
	 * @param classFieldName
	 * @return
	 */
	public static String toTableField(String classFieldName) {
		if(classFieldName != null) {
			StringBuilder colName = new StringBuilder(classFieldName);
			for(int i=0;i<colName.length();i++) {
				char c = colName.charAt(i);
				if (Character.isUpperCase(c) && i != 0) {
					colName.insert(i, '_');
					i++;
				}
			}
			return colName.toString().toUpperCase();
		}else {
			return null;
		}
	}
	
	
	/**
	 * �����ֶ�ת��Ϊ���ֶ����ĸ�ʽ(���� CUST_TYPEתΪ custType)
	 * @param tableColName
	 * @return
	 */
	public static String toClassField(String tableColName) {
		if(tableColName != null) {
			StringBuilder classField = new StringBuilder(tableColName.toLowerCase());
			while(classField.indexOf("_") >= 0) {
				int index = classField.indexOf("_");
				classField.setCharAt(index + 1, 
						Character.toUpperCase(classField.charAt(index + 1)));
				classField.deleteCharAt(index);
			}
			return classField.toString();
		}else {
			return null;
		}
	}
	
	
	public static void main(String args[]) {
		String a = "returnOtDays";
		System.out.println(toTableField(a));
	}
	
	
}
