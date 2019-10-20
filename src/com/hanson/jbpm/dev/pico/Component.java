package com.hanson.jbpm.dev.pico;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ������õĴ���
 * @author zhout
 */
public class Component {
	/* ������ */
	private String methodName;
	/* ������� */
	private IComponent component;
	
	/**
	 * ����������󷽷�
	 * 
	 * @param params	��������
	 * @return
	 * 
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Object invoke(Object[] params) 
		throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		Method[] methods = component.getClass().getMethods();	
		Method method = null;
		for (int i = 0; i < methods.length; i++) {				
			if (methods[i].getName().equals(methodName)) {
				method = methods[i];		
			}
		}		
		
		Object ret = method.invoke(component, params);
		
		return ret;
	}
	
	public IComponent getComponent() {
		return component;
	}

	public void setComponent(IComponent component) {
		this.component = component;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}
