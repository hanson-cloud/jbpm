package com.hanson.jbpm.jpdl.def;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.orm.utils.BeanUtil;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.suntek.util.time.DateUtil;

/**
 * Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�EntityDef.java
 * <p>ժ����Ҫ������ҵ���ʵ�����Լ��ṩ��صĳ־û�����
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Aug 18, 2011
 */
public class EntityDef {
	
	private Class<?> entityClass = null;  //ҵ���ʵ����
	
	private String className;
	
	@Deprecated
	private String serializeId = "instId";    //ʵ������(�ݲ�����,��ģʽͳһʹ��INST_ID��Ϊ����)
	
	
	/**
	 * @param entityClassPathҵ���ʵ����·��
	 * @throws ClassNotFoundException 
	 */
	public EntityDef(String entityClassPath) 
				throws ClassNotFoundException {
		className = entityClassPath;
		if(className.indexOf("BpmBiz") < 0) { //ʹ��ͨ��ҵ���ʱ��ʵ����
			entityClass = Class.forName(entityClassPath);
		}
	}
	
	
	public void insert(ExecutionContext ctx) throws Exception {
		String instId = ctx.getInstanceContext().getParameter(Context.values.INSTID, Context.types.RUNTIME);
		Object entityInstance = entityClass.newInstance();
		entityInstance = BeanUtil.requestToBean(ctx.getRequest(), entityClass);
		setInstIdValue(instId, entityInstance);
		
		ctx.getSession().saveOrUpdate(entityInstance);
	}
	

	public void update(ExecutionContext ctx) throws Exception {
		String instId = ctx.getInstanceContext().getParameter(Context.values.INSTID, Context.types.FORM);
		Object entityInstance;
		CommonLogger.logger.debug("�ύ��ҵ���[" + instId + "]>>>" + ctx.getInstanceContext().toXML());
		CommonLogger.logger.debug("����ҵ���ʵ���ࣺ" + entityClass);
		if (ctx.getRequest() == null) { // API ��ʽ���ã�û�� request			
			instId = ctx.getInstanceContext().getParameter(Context.values.INSTID, Context.types.FORM);
			//CommonLogger.logger.info(">>>[" + instId + "]>>>" + ctx.getInstanceContext().toXML());
			entityInstance = ctx.getSession().get(entityClass, instId);
			//CommonLogger.logger.info(">>>" + entityInstance);
			entityInstance = BeanUtil.mapToBean(ctx.getInstanceContext().getFormDataInMap(), entityInstance);
		} else {
			entityInstance = ctx.getSession().get(entityClass, instId);
			entityInstance = BeanUtil.mapToBean(ctx.getInstanceContext().getFormDataInMap(), entityInstance);
		}
		
		setInstIdValue(instId, entityInstance);
		ctx.getSession().merge(entityInstance);
	}
	
	
	public void delete(ExecutionContext ctx) throws Exception {
		Object entityInstance = entityClass.newInstance();
		entityInstance = BeanUtil.requestToBean(ctx.getRequest(), entityClass);
		String instId = ctx.getInstanceContext()
			.getParameter(Context.values.INSTID, Context.types.RUNTIME);
		setInstIdValue(instId, entityInstance);
		
		ctx.getSession().delete(entityInstance);
	}
	
	
	/**
	 * ���ҳ�����û�ж���instId��ֵ,��ͨ��BeanUtil�޷��󶨵�ʵ������
	 * ��ʱֻ�ܻ�ȡinstId����������setinstId������ֵ
	 * @param ctx
	 * @param entityInstance
	 */
	protected void setInstIdValue(String instId, Object entityInstance) {
		if(entityClass != null) {
			try {
				Method setter = entityClass.getDeclaredMethod("setInstId", new Class[]{String.class});
				setter.invoke(entityInstance, new Object[]{instId});
				
			}catch(Exception e) { /* would not do anything */}
		}
	}
	
	
	/**
	 * ��ҳ���л�ȡʵ��������ֵ
	 * @param ctx
	 * @return
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws Exception 
	 */
	public Serializable getSerializeIdValue(ExecutionContext ctx) throws Exception {
		
		String serializeIdValue = ctx.getInstanceContext().getParameter(serializeId, Context.types.FORM);
		Field field = entityClass.getDeclaredField(serializeId);
		return castObject(serializeIdValue, field.getType().getSimpleName());
	}
	
	
	
	
	
	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	/**
	 * @see com.suntek.ccf.orm.utils.BeanUtil
	 * @param value
	 * @param type
	 * @return
	 */
	private static Serializable castObject(String value, String type)
    {
		try{
			if (type.equalsIgnoreCase("Timestamp"))
				return Timestamp.valueOf(value);
			if (type.equalsIgnoreCase("Short"))
				return Short.parseShort(value);
			if (type.equalsIgnoreCase("Integer"))
				return Integer.parseInt(value);
			if (type.equalsIgnoreCase("Double"))
				return Double.parseDouble(value);
			if (type.equalsIgnoreCase("Long"))
				return Long.parseLong(value);
			if (type.equalsIgnoreCase("Float"))
				return Float.parseFloat(value);
			if (type.equalsIgnoreCase("Date")) {
				if (value.length()>10)
					return DateUtil.fromDateString(value, DateUtil.DB_DATETIME_FORMAT);
				else
					return DateUtil.fromDateString(value, DateUtil.DB_DATE_FORMAT);
			}
			if (type.equalsIgnoreCase("String"))
				return value;
		}catch(Exception e){
			throw new IllegalArgumentException("BeanUtil ����ת���Ƿ������쳣: " + type + ", ����ֵ��" + value);
		}
		throw new IllegalArgumentException("BeanUtil ����ת���Ƿ������쳣: " + type);
    }


	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}


	public Class<?> getEntityClass() {
		return entityClass;
	}


	public void setSerializeId(String serializeId) {
		this.serializeId = serializeId;
	}


	public String getSerializeId() {
		return serializeId;
	}
	
}
