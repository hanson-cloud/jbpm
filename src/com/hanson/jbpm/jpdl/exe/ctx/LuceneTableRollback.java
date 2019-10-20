package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.SQLException;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;

public class LuceneTableRollback {
	public void handleException(ProcessInstance instance, String taskId) {
		try { 	
			if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc"))
				instance.getExecutionContext().getJdbc().roolback();
			if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate"))
				instance.getExecutionContext().getSession().getTransaction().rollback();
			
			String instanceId = instance.getExecutionContext().getInstanceId();
			/* ������ʱ���쳣��ɾ�����������ݡ��޸ĵ�ʱ���쳣�����¶�������������¼  */
			if (ProcessEngine.luceneSupportInvalid() && ProcessEngine.luceneRealtimeIndex()) {
				if (instance.getExecutionContext().isNewInstance())
					new InstanceLuceneTable(InstanceLuceneTable.instanceTableName).findByPrimaryKey(instanceId).delete();
				else
					new InstanceLuceneTable(InstanceLuceneTable.instanceTableName).resumeIndex(instanceId);
			}
		} catch(SQLException se) {
			CommonLogger.logger.error(se, se);
		} catch(Exception se) {
			CommonLogger.logger.error(se, se);
		} 
	}
}
