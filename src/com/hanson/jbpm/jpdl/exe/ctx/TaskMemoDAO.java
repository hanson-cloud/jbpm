package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.SQLException;

import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;

public class TaskMemoDAO {
	/**
	 * ɾ����ǰ�����˵���һ��������޸ı�ע��������Ϣ
	 * @param assignUId
	 * @throws SQLException 
	 */
	public void deleteFinishedTaskMemo(ExecutionContext context, String taskId, String assignUId) 
	throws SQLException {
		
		String flowId = context.getProcessDefinition().getProcessName();
		
		String sql = "delete from BPM_TASK_MEMO where TASK_ID='" + taskId + "' and DEAL_UID='" + assignUId + "'";
		
		if (taskId.equals("")) {
			String draftId = context.getInstanceContext().getParameter("DRAFT_ID", Context.types.FORM);
			if (draftId.equals("")) {
				sql = "delete from BPM_TASK_MEMO where DEAL_UID='" + assignUId + "'" +
				  		" and PROC_NAME='" + flowId + "' and MEMO_TYPE='save'";
			} else {
				sql = "update BPM_TASK_DRAFT set DEALED='2' where DRAFT_ID='" + draftId + "'";
			}
		}
		
		BpmDaoSQLExecutor.executeBatch(context, sql);		
	}
}
