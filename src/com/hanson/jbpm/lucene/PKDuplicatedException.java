package com.hanson.jbpm.lucene;

public class PKDuplicatedException extends Exception {
	private  static final long serialVersionUID = 1l;
	
	public String getMessage() {
		return "Lucene���ݱ��¼�������������ظ�!";
	}
}
