package com.hanson.jbpm.identity;

import java.util.ArrayList;

public class FunctionList extends ArrayList<String> {
	private final static long serialVersionUID = 1l;
	
	/** 
	 * ����Ƿ�߱� funcName Ȩ��
	 * @param funcName
	 * @return
	 */
	public boolean containsFunction(String funcName) {
		return this.contains(funcName);
	}
}
