package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.SQLException;

import com.hanson.jbpm.jpdl.def.base.Reminder;
import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;

public class ReminderDAO {
	/**
	 * ���Ӷ�ʱ��������
	 * @param taskId
	 * @param assignUId
	 * @param assignUName
	 * @param dueTime
	 * @throws SQLException 
	 */
	protected void addReminderData(TaskPersistence taskPersist, String taskId, String assignUId, 
			String assignUName, String dueTime)	
	throws SQLException {				
		Reminder reminder = ((TaskNode)taskPersist.getEnterNode()).getTask().getReminder();
		if (reminder != null) {
			ExecutionContext ctx = taskPersist.getExecutionContext();
			String msg = reminder.getMsg();
			msg = ExpressionEvaluator.evaluate(msg, ctx);
			String instId = ctx.getInstanceId();
			if (msg != null && !msg.equals("")) {
				String sql = "insert into BPM_TASK_REMINDER (" +
						"INST_ID, TASK_ID, PROC_NAME, ASSIGN_UID, ASSIGN_UNAME, DEALER, MSG, DUETIME, FLAG ) " +
						" values ('" + instId + "', '" + taskId + "', " +
						" '" + ctx.getProcessDefinition().getProcessName() + "'," +
						" '" + assignUId + "','" + assignUName + "'," +
						" '" + ctx.getAssignable().getTaskDealerIds() + "'," +
						" '" + msg + "', " + ctx.getDialect().todate("'"+dueTime+"'") + ", '0' ) ";
				
				BpmDaoSQLExecutor.executeBatch(ctx, sql);
			}
		}
	}
}
