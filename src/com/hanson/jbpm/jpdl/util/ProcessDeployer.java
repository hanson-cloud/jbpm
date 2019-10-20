package com.hanson.jbpm.jpdl.util;

import java.io.IOException;
import java.net.URL;

import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.log.LoggerUtil;

/**
 * ģ�����̲��𹤾�
 * Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�ProcessDeployer.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�zhout
 * <p>������ڣ�2011-1-25
 */
public class ProcessDeployer
{
	/**
	 * ����ģ��ָ������
	 * @param moduleName	ģ����
	 * @param processName	������
	 */
	public static void deploy(String module, String process) {
		JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessEngine.getClientModuleName());		
		String maxId = (String) jdbc.queryForObject("select max(FLOW_ID) from BPM_FLOW", String.class);
		
		maxId = formatMaxId(maxId);
		
		String sql = "INSERT INTO BPM_FLOW ( FLOW_ID, FLOW_NAME, MAX_ID, MODULE_NAME, FLOW_DESC )" + 
				 	 " VALUES ( '" + maxId + "', '" + process + "', 1, '" + module + "', '" + process + "') ";
		LoggerUtil.getLogger().info(sql);
		jdbc.execute(sql);
	}
	
	public static void copyDeployFile(String module, String process ) throws IOException {
		//String _process = module + "/" + process; //ChineseToShortSpell.getShortSpell(process);
		FileUtil util = new FileUtil();
		util.copy(module, process, "/processdefinition.xml", false);
		util.copy(module, process, "/forms.xml", false);
		util.copy(module, process, "/gpd.xml", false);
		util.copy(module, process, "/processimage.jpg", false);
		//util.copy(module, process, "/fields.xml", false);
		
		util.copy(BpmConstants.CONFIG_PATH + "query-schema-standard.xml", 
					BpmConstants.CONFIG_PATH + module + "/query-schema.xml", true);
		
		util.copy(BpmConstants.CONFIG_PATH + "fields-standard.xml", 
				BpmConstants.CONFIG_PATH + module + "/" + process + "/fields.xml", true);
		
		//�ж������Ŀ��û��query-schema.xml,���jbpm.war�и���query-schema-standard.xml
		URL schemaPath = ProcessDeployer.class.getClassLoader().getResource("../../META-INF/flow/" + 
				process + "/query-schema.xml");
		if(schemaPath != null) {
			util.copy(module, process, "/query-schema.xml", false);
		}else {
			util.copy(BpmConstants.CONFIG_PATH + "query-schema-standard.xml",  
					BpmConstants.CONFIG_PATH + module + "/" + process + "/query-schema.xml", true);
		}
	}
	
	/**
	 * ��ʽ���� 3 λ�������
	 * @param maxId
	 * @return
	 */
	private static String formatMaxId(String maxId) {
		if (maxId == null) 
			maxId = "0";		
		
		return String.format("%0" + 3 + "d", Integer.parseInt(maxId) + 1);
	}
}
