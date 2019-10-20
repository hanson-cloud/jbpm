package com.hanson.jbpm.dev.action;

import com.hanson.jbpm.identity.OrgCache;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.exe.ctx.State;
import com.hanson.jbpm.jpdl.exe.util.DateUtils;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.ccf.dao.DaoFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Э�촦��
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�DealCooperateAction.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Nov 5, 2013
 */
public class DealCooperateAction {
	
	private JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
	
	public static final String CO_TASK_NAME = "Э�촦��";
	public static final String CO_TASK_RETURN_NAME = "�������Э��";

	
	/**
	 * Э�촦��
	 * @param instId
	 * @param taskId
	 * @param dealMsg Э������
	 * @param dealId  ������
	 */
	public String execute(String instId, String taskId, String dealMsg, String dealId) {
		String departDealer = BpmConstants.PREFIX_DEPT + OrgCache.getCache().getUserDept(dealId);
		String sql = "update BPM_ASSIGN set CO_FLAG = '2', DEAL_FLAG = '1' where TASK_ID='" + 
			taskId + "' and INST_ID='" + instId + "'  and CO_FLAG = '1' and DEAL_DEPTID='" + 
			OrgCache.getCache().getUserDept(dealId) + "'";
		CommonLogger.logger.debug("[Э�촦��]" + sql);
		try {
			String[] sqls = new String[]{sql, new RecordTaskTrackAction()
				.getExecuteSql(dealId, taskId, CO_TASK_NAME, dealMsg, "")};
			jdbc.batchUpdate(sqls);
			return "�ύЭ�촦��ɹ�";
		}catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			return "����Э�촦��ʧ��: " + ex.getMessage();
		}
	}
	
	
	/**
	 * Э���˻�
	 * @param instId
	 * @param currentTaskId ��ǰ������̱��
	 * @param pastTaskId �ϴδ���Э��Ĺ��̱��
	 * @param coDealer ������Э�촦����
	 * @param returnMsg
	 * @param dealId
	 * @return
	 */
	public String returnExecute(String instId, String currentTaskId, String pastTaskId,
			String coDealer, String returnMsg,  String dealId, String processName) {
		List<String> sqls = new ArrayList<String>();
		String coDealerSql = "'" + coDealer + "','" + 
			BpmConstants.PREFIX_DEPT + OrgCache.getCache().getUserDept(dealId)
			+ "'";
		sqls.add("delete from BPM_ASSIGN where TASK_ID='" + currentTaskId + 
				"' and CO_FLAG='1' and DEAL_DEPTID='" + 
			OrgCache.getCache().getUserDept(dealId) + "'");  //ɾ����ǰ�������Э���¼
		String assignTimeSql = ProcessEngine.getDialect().todate("'" +
				DateUtils.getDayOffset(0, "yyyy-MM-dd HH:mm:ss") + "'");
		String dueTimeSql = ProcessEngine.getDialect().todate("'" + 
				DateUtils.DEFAULT_DATE + "'"); 
		sqls.add("update BPM_ASSIGN set CO_FLAG='3' where INST_ID='" + instId + 
				"' and DEAL_DEPTID='" + OrgCache.getCache().getUserDept(dealId) + 
				"' and CO_FLAG='2'"); //�Ѹ�Э�����Ѵ����Э���¼ȫ������Ϊ3,��ʾ���˻�
		sqls.add("insert into BPM_ASSIGN(INST_ID, TASK_ID, DEALER, CO_FLAG, READ_FLAG, DEAL_FLAG, " +
				"ASSIGN_TIME, DUE_TIME, PROC_NAME, TASK_NAME, DEAL_DEPTID)" +
				" values ('" + instId + "','" + currentTaskId + "'," +
				"'" + coDealer + "','" + State.TRUE + "','" + State.FALSE + "'," +
				"'" + State.FALSE + "', " + assignTimeSql + "," + dueTimeSql + ", '" + 
				processName + "', (select TASK_NAME from BPM_TASK where TASK_ID='" + 
				currentTaskId + "'), '" + OrgCache.getCache().getUserDept(coDealer) + "')");
		sqls.add("update BPM_TASK_COMMENTS set TASK_NAME = '" + CO_TASK_NAME + 
				" (�ذ�)' where TASK_ID='" + pastTaskId + "' and INST_ID='" + instId + 
				"' and DEAL_UID='" + coDealer + "' and TASK_NAME='" + CO_TASK_NAME + "'");
		sqls.add(new RecordTaskTrackAction().getExecuteSql(dealId, currentTaskId, CO_TASK_RETURN_NAME, 
				returnMsg, ""));
		
		try {
			for(int i=0;i<sqls.size()-1;i++) {
				CommonLogger.logger.debug("[�˻�Э�촦��]" + sqls.get(i));
			}
			jdbc.batchUpdate(sqls.toArray(new String[sqls.size()]));
			return "���Э�����´���ɹ�";
		}catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			return "���Э�����´���ʧ��: " + ex.getMessage();
		}
		
	}
	
	
	/**
	 * ���·���Э����
	 * @param instId
	 * @param taskId
	 * @param processName ��������
	 * @param users  ��ת����
	 * @param name   ��ת��������(��ʾ��)
	 * @param dealId ������
	 * @param msg    �������
	 * @return
	 */
	public String reAssign(String instId, String taskId, String processName, List<String> users, String name, 
			String dealId, String msg) {
		String coDealerSql = "'" + dealId + "','" + 
			BpmConstants.PREFIX_DEPT + OrgCache.getCache().getUserDept(dealId) + "'";
		List<String> sqls = new ArrayList<String>();
		String assignTimeSql = ProcessEngine.getDialect().todate("'" + 
				DateUtils.getDayOffset(0, "yyyy-MM-dd HH:mm:ss") + "'"); 
		String dueTimeSql = ProcessEngine.getDialect().todate("'" + 
				DateUtils.DEFAULT_DATE + "'"); 
		for(int i=0;i<users.size();i++) {
			String userId = users.get(i);
			sqls.add("delete from BPM_ASSIGN where TASK_ID='" + taskId + 
					"' and CO_FLAG in('1','2') and DEAL_DEPTID='" + OrgCache.getCache().getUserDept(dealId) + 
				"'");  //ɾ����ǰ�������Э���¼
			sqls.add("insert into BPM_ASSIGN(INST_ID, TASK_ID, DEALER, CO_FLAG, READ_FLAG, DEAL_FLAG, " +
				" ASSIGN_TIME, DUE_TIME, PROC_NAME, TASK_NAME, DEAL_DEPTID)" + 
				" values('" + instId + "', '" + taskId + "', '" + userId + "', '1','0','0' " + 
				", " + assignTimeSql + ", " + dueTimeSql + ", '" + processName + 
				"', (select TASK_NAME from BPM_TASK where TASK_ID='" + taskId + "'), '" + 
				OrgCache.getCache().getUserDept(userId) + "')");
		}
		msg = "ת�� " + name + " Э�촦��\n" + msg;
		sqls.add( "update BPM_ASSIGN set CO_FLAG = '2', DEAL_FLAG = '1' where TASK_ID='" + 
				taskId + "' and INST_ID='" + instId + "' and DEAL_DEPTID='" + OrgCache.getCache().getUserDept(dealId) + 
				"' and CO_FLAG = '1'");
		sqls.add(new RecordTaskTrackAction().getExecuteSql(dealId, taskId, CO_TASK_NAME, msg, ""));
		try {
			for(int i=0;i<sqls.size()-1;i++) {
				CommonLogger.logger.debug("[ת��Э�촦��]" + sqls.get(i));
			}
			jdbc.batchUpdate(sqls.toArray(new String[sqls.size()]));
			return "ת��Э�촦��ɹ�";
		}catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			return "ת��Э�촦��ʧ��: " + ex.getMessage();
		}
	}
}
