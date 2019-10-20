package com.hanson.jbpm.jpdl.exe.ctx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.lucene.LuceneRecord;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.hanson.jbpm.mgmt.TaskCurrentVisitorCache;

public class ProcessBackup {
	private TaskPersistence taskPersistence;
	
	public ProcessBackup(TaskPersistence tp) {
		taskPersistence = tp;
	}
	
	public void save(ExecutionContext ctx) throws Exception {
		//String processId = ctx.getInstanceContext().getParameter("INST_ID", Context.T_MAINFORM);
		String processId = taskPersistence.getProcessId();
		
		List<String> sqls = new ArrayList<String>();
		
		//sqls.add("delete from BPM_ASSIGN where TASK_ID in (" +
		//		" select TASK_ID from BPM_TASK where INST_ID = '" + processId + "')");	
		
		TaskCurrentVisitorCache.finish(processId);
		
		
		/* �Ƿ��Զ��鵵, ���򱸷������ */
		//List<String> backupSqls = new ArrayList<String>();
		if (BpmConstants.AutoArchived) {		
			//backupLuceneIndex(processId);
			sqls.addAll(buildArchivedSQL(processId));
		}
		
		for (int i=0; i<sqls.size(); i++) {
			//LoggerUtil.getLogger().debug(sqls.get(i));									
			ctx.addSQL(sqls.get(i));
		}
		
		
	}
	
	public void save(String processId) throws IOException, ParseException, Exception {
		backupLuceneIndex(processId);		
		List<String> sqls = buildArchivedSQL(processId);		
		String[] sqlArray = new String[sqls.size()];
		sqls.toArray(sqlArray);
		for (int i=0; i<sqlArray.length; i++) {
			CommonLogger.logger.debug(sqlArray[i]);
		}
		DaoFactory.getTransactionTemplate(ProcessClient.MODULE).batchUpdate(sqlArray);
	}
	
	private void backupLuceneIndex(String processId) throws IOException, ParseException, Exception {
		if (ProcessEngine.luceneSupportInvalid() && ProcessEngine.luceneRealtimeIndex()) {
			LuceneRecord rec = new InstanceLuceneTable(InstanceLuceneTable.instanceTableName).findByPrimaryKey(processId);
			if (rec != null) {
				rec.set("table", HistoryLuceneTable.instanceTableName);
				rec.update();
			}
		}
	}
	
	private List<String> buildArchivedSQL(String processId) {
		List<String> sqls = new ArrayList<String>();
		
		sqls.add("insert into BPM_INST_HIS(" + BpmConstants.INST_FIELDS + ")" +
				" select " + BpmConstants.INST_FIELDS +
				" from BPM_INST" +
				" where INST_ID='" + processId + "'");
		
		sqls.add(" insert into BPM_TASK_HIS(" + BpmConstants.TASK_FIELDS + " )" +
				" select " + BpmConstants.TASK_FIELDS + 
				" from BPM_TASK where INST_ID='" + processId + "'");
		sqls.add("insert into BPM_BIZ_HIS  select * from BPM_BIZ where INST_ID='"
				+ processId + "'");
		sqls.add("insert into BPM_ASSIGN_HIS  select * from BPM_ASSIGN where INST_ID='"
				+ processId + "'");
		
		/* ɾ���ѱ��ݼ�¼ */
		sqls.add("delete from BPM_INST where INST_ID='" + processId + "'");
		sqls.add("delete from BPM_TASK where INST_ID='" + processId + "'");
		sqls.add("delete from BPM_BIZ where INST_ID='" + processId + "'");
		sqls.add("delete from BPM_ASSIGN where INST_ID='" + processId + "'");
		return sqls;
	}
}
