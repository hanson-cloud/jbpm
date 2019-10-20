package com.hanson.jbpm.dev;

import java.util.HashMap;
import java.util.Map;

import com.hanson.jbpm.mgmt.WorkItemListLuceneQuery;

/**
 * Ϊ�ͻ����ṩ�������ݵ�ȫ�ļ�������
 * Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�LuceneService.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�zhout
 * <p>������ڣ�2011-1-25
 */
public class LuceneService
{
	// ������
	private String processName;
	private WorkItemListLuceneQuery luceneQuery = new WorkItemListLuceneQuery();
	
	// ��ѯ����
	private Map<String, String> parameters;
	
	public LuceneService(String processName) {
		this.processName = processName;
	}
	
	/**
	 * ����,����ƥ���ҵ�����
	 * @param keywords
	 * @param isHistory
	 * @return
	 */
	public String[] find(String keywords, boolean isHistory) {
		parameters = processParameter(keywords, isHistory);			
		return luceneQuery.fulltextFindInstanceIds(parameters).split(",");
	}
	
	/**
	 * ����������ƥ��ҵ������ Grid Provider �� SQL
	 * @param keywords
	 * @param isHistory
	 * @return
	 */
	public String buildQuerySql(String keywords, boolean isHistory) {				
		return luceneQuery.buildQuerySql(parameters);
	}

	/**
	 * ����������ƥ��ҵ������ Grid Provider �� SQL
	 * @param keywords
	 * @param isHistory
	 * @return
	 */
	public String buildCountSql(String keywords, boolean isHistory) {
		parameters = processParameter(keywords, isHistory);
		return luceneQuery.buildCountSql(parameters);
	}
	
	
	private Map<String, String> processParameter(String keywords, boolean isHistory) {
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("processName", processName);
		map.put("findText", keywords);
		
		if (isHistory)
			map.put("view", "history");
		
		return map;
	}
}
