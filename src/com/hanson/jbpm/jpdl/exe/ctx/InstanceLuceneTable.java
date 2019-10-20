package com.hanson.jbpm.jpdl.exe.ctx;

import java.util.Map;

import com.hanson.jbpm.lucene.LuceneRecord;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.log.LoggerUtil;
import com.hanson.jbpm.mgmt.ProcessClient;

public class InstanceLuceneTable extends HistoryLuceneTable {
	
	public final static String instanceTableName = "BPM_INST";
		
	public InstanceLuceneTable(String tableName) throws Exception {
		super(tableName);
		setPrimaryKey(primaryKey);
	}
	
	public void update(String instanceId, String processName, String title, String selfDefinedVariable) 
	throws BpmException {
		try {
			LuceneRecord record = findByPrimaryKey(instanceId);
			
			record.setEx("INST_ID", instanceId, false);
			record.setEx("PROC_NAME", processName, false);
			record.set("INST_TITLE", title);			
			record.set(Context.values.LUCENETEXT, selfDefinedVariable);
			
			record.update();
			LoggerUtil.getLogger().info("�޸ļ�¼����: " + instanceId);
		}catch (Exception ex) {
			LoggerUtil.getLogger().error(ex, ex);
			throw new BpmException("Lucene ������ͬ��ʧ��: " + ex.getMessage());
		}
	}
	
	/**
	 * �쳣�ع�ʱ�������ݿ���лָ�������¼
	 * @param instanceId
	 */
	public void resumeIndex(String instanceId) {
		JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
		String sql = "select distinct INST_ID, PROC_NAME, INST_TITLE from BPM_INST" +
					 " where INST_ID='" + instanceId + "'";
		LoggerUtil.getLogger().info(sql);
		Map row = jdbc.queryForMap(sql);
		update((String)row.get("INST_ID"),
			   (String)row.get("PROC_NAME"), (String)row.get("INST_TITLE"), "");
		LoggerUtil.getLogger().info("�ָ���¼����: " + instanceId);
	}
}
