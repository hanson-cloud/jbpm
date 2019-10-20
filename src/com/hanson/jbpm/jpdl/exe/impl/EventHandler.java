package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.def.event.EventInfo;

/**
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�EventHandler.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Nov 19, 2013
 */
public interface EventHandler {
	
	/**
	 * 
	 * @param event
	 * @param target
	 */
	public void handle(EventInfo event);

}
