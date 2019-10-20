package com.hanson.jbpm.jpdl.def.flow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hanson.jbpm.jpdl.util.ProcessDefinitionReader;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.log.LoggerUtil;
import com.hanson.jbpm.jpdl.def.xml.node.ProcessDefinitionParser;

/**
 * ģ�����������ļ��Ļ���
 * Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�ProcessDefinitionCache.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�zhout
 * <p>������ڣ�2011-1-25
 */
public class ProcessDefinitionCache
{
	private final static Map<String,ProcessDefinition> definitions = new ConcurrentHashMap<String,ProcessDefinition>();
	
	public static ProcessDefinition getProcessDefinition(String processName) {		
		processName = processName.trim();
		
		if (definitions.containsKey(processName))
			return definitions.get(processName);
		
		CommonLogger.logger.info("��ȡ���� <" + processName + "> �����ļ�.");
		
		try {
			String processFileName = processName + "/processdefinition.xml";
			String content = new ProcessDefinitionReader().getPdfContent(processFileName);
			ProcessDefinition pdf = new ProcessDefinitionParser().parse(content);
			/*try {
				pdf.setBizTable(new ProcessDeployer().findDefinedBizTable(processName));
			} catch (DocumentException e) {
				LoggerUtil.getLogger().error("��ȡ�����Զ���ҵ���ʧ��: " + e.getMessage());
			}*/
			definitions.put(processName, pdf);
				
			return pdf;
		}catch(Exception e) {
			LoggerUtil.getLogger().error(e, e);
			LoggerUtil.getLogger().error("��ȡ���������ļ�" + processName + "ʧ��");			
			return null;
		}
	}
	
	/**
	 * ���������ü��뵽�����в�����
	 * @param pdf
	 */
	public static void setProcessDefinition(ProcessDefinition pdf) {
		definitions.put(pdf.getProcessName(), pdf);
		//TODO:���������ó־û�
	}
	
	public static void clear() {
		System.out.println("��������Ѽ��ص����̻���");
		definitions.clear();
	}
}
