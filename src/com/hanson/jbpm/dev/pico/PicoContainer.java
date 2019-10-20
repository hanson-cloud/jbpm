package com.hanson.jbpm.dev.pico;

import java.util.Hashtable;


/**
 * �����������
 * @author zhout
 */
public class PicoContainer {
	/* ���������������� */
	private Hashtable<String, ComponentDefinition> componentDefList = new Hashtable<String, ComponentDefinition>();
	
	/* �������ж��� */
	public static PicoContainer container = null;
	
	public synchronized static PicoContainer getInstance() {
		if (container == null) {
			container = new PicoContainer();
		}
		return container;
	}
	
	/**
	 * ע�������
	 * @param serviceId
	 * @param clz
	 * @param methodName
	 */
	public void registerImplement(String componentId, Class clz, String methodName) {
		System.out.println("��ǰע�������[" + componentDefList.size() + "]: " + clz.getName() + ":" + methodName);
		componentDefList.put(componentId, new ComponentDefinition(clz, methodName, ComponentDefinition.IMPLEMENT_COMPONENT));
	}
	
	/**
	 * ע���������
	 * @param serviceId
	 * @param obj
	 * @param methodName
	 */
	public void registerInstance(String componentId, IComponent obj, String methodName) {
		System.out.println("��ǰע���������[" + componentDefList.size() + "]: " + obj.getClass().getName() + ":" + methodName);
		componentDefList.put(componentId, new ComponentDefinition(obj, methodName, ComponentDefinition.INSTANCE_COMPONENT));
	}
	
	/**
	 * ע���������
	 * @param componentId
	 */
	public void unregister(String componentId) {
		System.out.println("ע���������: " + componentId);
		componentDefList.remove(componentId);
	}
	
	/**
	 * contains
	 * @param componentId
	 * @return
	 */
	public boolean containsComponent(String componentId) { 
		return componentDefList.containsKey(componentId);
	}
	
	/**
	 * ��ȡ������ô���
	 * @param serviceId
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Component getComponent(String componentId) 
	throws InstantiationException, IllegalAccessException {
		ComponentDefinition def = componentDefList.get(componentId);
		IComponent component = null;

		if (def == null) {
			for (int i=0; i<componentDefList.size(); i++)
				System.out.println("��ע�����[" + String.valueOf(i) + "]: " + componentDefList.get(i));
			
			throw new RuntimeException("��ǰ��� " + componentId + " δע��");
		}
		if (def.getComponentType() == ComponentDefinition.INSTANCE_COMPONENT) 
			component = (IComponent)def.getRegisterDesc();
		else		
			component = (IComponent)((Class)def.getRegisterDesc()).newInstance();
		
		Component proxy = new Component();
		proxy.setComponent(component);
		proxy.setMethodName(def.getMethodName());
				
		return proxy;
	}
}
