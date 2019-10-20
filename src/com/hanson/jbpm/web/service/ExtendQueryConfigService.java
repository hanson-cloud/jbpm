package com.hanson.jbpm.web.service;

import java.util.HashMap;
import java.util.Map;

import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinitionCache;
import com.hanson.jbpm.jpdl.def.xml.node.ProcessDefinitionParser;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * Copyright (C)2012 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�ExtendQueryConfigService.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Jun 22, 2012
 */
public class ExtendQueryConfigService {
	
	/**
	 * ����Map�з���ExtendQuery�����List<FormField>����
	 * @param procName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getExtendQuery(String procName) {
		Map map = new HashMap();
		map.put("extendQuery", ProcessEngine.getExtendQuery(procName));
		map.put("formFields", ProcessEngine.getFields(procName));
		return map;
	}
	
	
	/**
	 * ����ѯ���ñ��浽�������̶�����
	 * @param xml
	 * @return
	 */
	public String saveExtendQuery(String procName, String xml) {
		try {
			CommonLogger.logger.info("[�����ѯ����]" + xml);
			Document doc  = DocumentHelper.parseText(xml);
			Element el = doc.getRootElement();
    		ProcessDefinition pdf = ProcessEngine.getProcessDefinition(procName);
    		pdf.setExtendQuery(new ProcessDefinitionParser().parseExtendQuery(ProcessClient.MODULE, el));
    		ProcessDefinitionCache.setProcessDefinition(pdf);
    		return "����ɹ�";
		}catch(Exception ex) {
			CommonLogger.logger.error(ex, ex);
			return "����ʧ��!" + ex.getMessage();
		}
	}
}
