package com.hanson.jbpm.dev.action;

import java.util.List;

import com.hanson.jbpm.log.CommonLogger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.util.calendar.DateUtil;
import com.suntek.eap.util.jdbc.Dialect;
import com.suntek.eap.util.jdbc.DialectFactory;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * Copyright (C)2012 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm.client
 * <p>�ļ����ƣ�RedistributeTaskDAO.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Feb 15, 2012
 */
public class RedistributeTaskAction {
	
	private JdbcTemplate jdbc = DaoFactory.getJdbc("jbpm");
	private JdbcTemplate jdbcEap = DaoFactory.getJdbc("jbpm", "openEAP");
	
	
	/**
	 * ��ȡ���м�����
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List getGroups() {
		String sql = "select grop_code, grop_name from workgroup_view";
		return jdbcEap.queryForList(sql);
	}
	
	
	/**
	 * ����ǰ��ѯ�������й������ɵ���
	 * @param groupCode
	 * @param whereSql
	 * @return
	 */
	public String changeGroup(String groupCode, String sql) {
		
		try {
			int fromIndex = sql.indexOf("FROM");
			if(fromIndex >=0 ) {
				sql = "select c.TASK_ID " + sql.substring(fromIndex, sql.indexOf("order"));
			}
			String updateSql = "update BPM_ASSIGN SET DEALER = '" + groupCode + "' "
				+ " where TASK_ID in (" + sql + ")";
			Dialect dialect = DialectFactory.getDialect(AppHandle.getHandle(ProcessClient.MODULE).getDatasourceName());
			String today = dialect.todate("'"+DateUtil.getDateTime()+"'");
			String updateAssignSql = "update BPM_TASK SET ASSIGN_TIME = " + today + " where TASK_ID in ("
				+ sql + ")";
			CommonLogger.logger.info("[RedistributeTaskAction]���·��ɹ�������:" + updateSql + "\n" + updateAssignSql);
			jdbc.setQueryTimeout(60);
			jdbc.batchUpdate(new String[]{updateSql, updateAssignSql});
			return "���ɳɹ�!";
		}catch(Exception ex) {
			CommonLogger.logger.error(ex, ex);
			return "����ʧ��, ���ݿ�����쳣!";
		}
	}
	
	
	public String changeGroup(String groupCode, String[] taskIdArr) {
		
		StringBuffer taskIds = new StringBuffer("");
		for(String taskId : taskIdArr) {
			taskIds.append(",'").append(taskId).append("'");
		}
		if(taskIds.length() > 0) {
			return changeGroup(groupCode, taskIds.substring(1));
		}else {
			return "'0'";
		}
		
	}
}
