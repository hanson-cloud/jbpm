package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import com.hanson.jbpm.jpdl.exe.util.DateUtils;
import com.hanson.jbpm.identity.OrgCacheLoader;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;

public class AssignmentDAO {
	private String processId;
	private String taskId;
	
	
	public AssignmentDAO(String processId, String taskId) {
		this.processId = processId;
		this.taskId = taskId;
	}
	
	/**
	 * �ɵ���ָ���û�
	 * @param context 	������
	 * @param man		ָ����id
	 * @param readFlag	�Ѷ�/�����־
	 * @throws SQLException
	 */
	protected void send(ExecutionContext context, String man, String ccFlag, String readFlag, String dealFlag) throws Exception {
		ProcessDefinition pdf = context.getProcessDefinition();
		String processName = pdf.getProcessName();
		Timestamp nowTimeSql = new Timestamp(Calendar.getInstance().getTime().getTime());
		Timestamp dealTimeSql = new Timestamp(DateUtils.getDateTimeFormat()
				.parse(DateUtils.DEFAULT_DATE).getTime());
		if(!"0".equals(dealFlag)) {
			dealTimeSql = nowTimeSql;
		}
		String taskName = context.getFirstLeavedNode().getName();
		String dealDeptId = "";
		if(man.indexOf("D") == 0) {
			dealDeptId = man.substring(1, man.length());
		}else {
			dealDeptId = OrgCacheLoader.getInstance().queryUserDept(man);
		}
		String sql = "insert into BPM_ASSIGN(INST_ID, TASK_ID, DEALER, CC_FLAG, READ_FLAG, DEAL_FLAG, " +
			" ASSIGN_TIME, DUE_TIME, PROC_NAME, TASK_NAME, DEAL_TIME, DEAL_DEPTID)" +
			" values (?,?,?,?,?,?,?,?,?,?,?,?)";

		BpmDaoSQLExecutor.executeBatch(context, sql, processId, taskId, man, ccFlag, readFlag, 
				dealFlag, nowTimeSql, nowTimeSql, processName, taskName, dealTimeSql, dealDeptId);			
	}
}
