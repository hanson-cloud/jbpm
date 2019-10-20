package com.hanson.jbpm.identity;

public interface IFunctionPermission {
	/**
	 * ��� loginName �Ƿ�߱� funcName Ȩ��
	 * @param loginName
	 * @param funcName
	 * @return
	 */
	public FunctionList getFunctionList(String loginName);
}
