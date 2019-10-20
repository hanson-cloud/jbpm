/**
 * 
 */
package com.hanson.jbpm.jpdl.def.base;

/**
 * �ύ��oracle varchar���ȴ���2000���ֶ��ô����װ����
 * @author tl
 *
 */
public class OracleBigString {
	
	private String value;
	
	public String toString() {
		return value;
	}
	
	
	public OracleBigString(String value) {
		this.value = value;
	}
	
	
	public static OracleBigString build(String value) {
		return new OracleBigString(value);
	}

}
