package com.hanson.jbpm.dev.action;

import java.util.ArrayList;
import java.util.List;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.util.jdbc.Dialect;
import com.suntek.eap.util.jdbc.DialectFactory;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.mgmt.ProcessClient;

public class BatchArchiveAction {
	private Dialect dialect = DialectFactory.getDialect(AppHandle.getHandle("jbpm").getDatasourceName());
	public String execute(String procName, String beginDate, String endDate) {
		try {			
			List<String> sqls = buildArchivedSQL(procName, beginDate, endDate);		
			String[] sqlArray = new String[sqls.size()];
			sqls.toArray(sqlArray);
			for (int i=0; i<sqlArray.length; i++) {
				CommonLogger.logger.debug(sqlArray[i]);
			}
			DaoFactory.getTransactionTemplate(ProcessClient.MODULE).batchUpdate(sqlArray);
			return "�����ѹ鵵����ע���鵵��Ĺ������ڹ鵵������ѯ��";
		} catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			return "�����鵵����ʧ��: " + ex.getMessage();
		}		
	}
	
	private List<String> buildArchivedSQL(String procName, String beginDate, String endDate) {
		String condition = "";
		if (!"-1".equals(procName)) {
			condition = condition + " and a.PROC_NAME='" + procName + "'";
		}
		if (beginDate != null) {
			condition = condition + " and a.INIT_TIME>" + dialect.todate("'" + beginDate + " 00:00:00'");
		}
		if (endDate != null) {
			condition = condition + " and a.INIT_TIME<" + dialect.todate("'" + endDate + " 23:59:59'");
		}
		
		List<String> sqls = new ArrayList<String>();
		
		sqls.add("insert into BPM_INST_HIS(" + BpmConstants.INST_FIELDS + ")" +
				" select " + BpmConstants.INST_FIELDS + 
				" from BPM_INST a" +
				" where 1=1 " + condition);
		
		sqls.add(" insert into BPM_TASK_HIS(" + BpmConstants.TASK_FIELDS + ")" +
				" select b.* " +
				" from BPM_TASK b, BPM_INST a where a.INST_ID=b.INST_ID " + condition);
		
		/* ɾ���ѱ��ݼ�¼ */
		sqls.add("delete from BPM_TASK b where b.INST_ID=(select INST_ID from BPM_INST a where a.INST_ID=b.INST_ID " + condition + ")");
		sqls.add("delete from BPM_INST a where 1=1 " + condition);
		return sqls;
	}
}
