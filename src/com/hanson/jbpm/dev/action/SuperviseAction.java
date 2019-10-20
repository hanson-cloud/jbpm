package com.hanson.jbpm.dev.action;

import com.hanson.jbpm.log.CommonLogger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.mgmt.ProcessClient;


/**
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�SuperviseAction.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Dec 2, 2013
 */
public class SuperviseAction {
	
	public static final int TYPE_NORMAL_SUPERVISE = 1;
	public static final int TYPE_IMPORTANT_SUPERVISE = 2;
	public static final int TYPE_SUPERVISE_REPLY = 3;
	
	private JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
	
	/**
	 * 
	 * @param instId
	 * @param taskId
	 * @param dealId   ������
	 * @param msg      �������
	 * @param superviseType 1:��ͨ����, 2:�ص㶽��, 3:�����
	 * @return
	 */
	public String execute(String instId, String taskId, String dealId, String msg, int superviseType) {
		String sql = "update BPM_BIZ set IS_SUPERVISED='" + superviseType + 
			"' where INST_ID='" + instId + "' ";
		
		try {
			String taskName = "";
			switch(superviseType) {
				case TYPE_IMPORTANT_SUPERVISE : taskName = "�ص㶽��"; break;
				case TYPE_NORMAL_SUPERVISE : taskName = "��ͨ����"; break;
				case TYPE_SUPERVISE_REPLY : taskName = "�����"; break;
				default : taskName = "�ص㶽��"; break;
			}
			String[] sqls = new String[]{sql, new RecordTaskTrackAction()
				.getExecuteSql(dealId, taskId, taskName, msg, "")};
			for(String s : sqls) {
				CommonLogger.logger.debug("[��������]" + s);
			}
			jdbc.batchUpdate(sqls);
			return "ִ�ж���ɹ�";
		}catch (Exception e) {
			CommonLogger.logger.error(e, e);
			return "ִ�ж���ʧ��:" + e.getMessage();
		}
	}

}
