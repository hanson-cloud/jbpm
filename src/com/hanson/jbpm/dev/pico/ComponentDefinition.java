package com.hanson.jbpm.dev.pico;

/**
 * �����������
 * @author zhout
 *
 */
public class ComponentDefinition {
	/* ���ע��Ϊʵ�� */
	public final static int IMPLEMENT_COMPONENT = 1;
	/* ���ע��Ϊ����ʵ�� */
	public final static int INSTANCE_COMPONENT = 2;
	
	/* ���ע����� */
	private Object registerComponentRef;
	/* ���ע�����ķ�����*/
	private String methodName;
	/* ���ע���������� */
	private int componentType;
	
	/**
	 * ���췽��
	 * @param desc			���ע��Ķ���
	 * @param methodName	������
	 * @param componentType ע������
	 */
	public ComponentDefinition(Object desc, String methodName, int componentType) {
		this.registerComponentRef = desc;
		this.methodName = methodName;
		this.componentType = componentType;
	}
	
	public Object getRegisterDesc() {
		return registerComponentRef;
	}
	public void setRegisterDesc(Object registerRef) {
		this.registerComponentRef = registerRef;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getComponentType() {
		return this.componentType;
	}

	public void setComponentType(int componentType) {
		this.componentType = componentType;
	}
}
