package com.hanson.jbpm.jpdl.exe.ctx;

import java.util.Date;

import com.hanson.jbpm.jpdl.exe.util.DateUtils;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.app.session.SessionRegister;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.base.OracleBigString;
import com.hanson.jbpm.jpdl.def.base.OracleClob;
import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;
import com.hanson.jbpm.jpdl.exe.util.DialectUtil;

public class TaskDAO {
	private TaskPersistence taskPersist;
	private ExecutionContext executionContext;
	
	public TaskDAO(TaskPersistence taskPersist) {
		this.taskPersist = taskPersist;
		executionContext = taskPersist.getExecutionContext();
		
	}
		
	/**
	 * ���´������������ͳ������
	 * @param taskName
	 * @param taskId
	 * @throws SQLException
	 */
	/*protected void updateCommentsCount(String taskId, String taskName) throws Exception {
		String sql = "update BPM_TASK set COMMENT_CNT=(select max(COMMENT_CNT) from BPM_TASK" +
			  " where INST_ID='" + taskPersist.processId + "' and TASK_NAME='" + taskName + "')" +
			  " where TASK_ID='" + taskId + "'";
		CommonLogger.logger.debug(sql);
		executionContext.getJdbc().execute(sql);
	}*/
	
	/**
	 * ��¼�������Ϣ
	 * @param prevTaskName	�뿪����һ����ڵ����Ϣ
	 * @param taskName		����ĵ�ǰ�������Ϣ
	 * @throws Exception
	 */
	protected void insert(String prevTaskId, String prevTaskName, 
			String taskId, String taskName, String dealUId, String dealDept, 
			Date dealTime, String overTime, String dealFlag, String context) throws Exception {
		
		
		/* �ӵ�ǰ�ڵ��ȡ��ǰ�����ʱ�� */
		
		String dueTime = DateUtils.DEFAULT_DATE;
		
		String due2Str = taskPersist.dueTime;
		
		if (taskPersist.getEnterNode() instanceof TaskNode) { // �п����� End �ڵ�, ��ʱ����Ҫ��¼ dueTime2
			//due2 = new Double(((TaskNode)taskPersist.getEnterNode()).getTask().getDueDate()).intValue();
			if(!"".equals(due2Str)) {
				dueTime = due2Str;
			}
		}
		
		/* �Զ�������ʱ��, ���費�ñ�����ʱ�ޡ���ֻ����ʱ�� */
		// InstanceContext ctx = taskPersist.getExecutionContext().getInstanceContext();		
		// String formSetDueTime = new DateFormat().getFormSetDueTime(ctx);
		// if (!formSetDueTime.equals("")) dueTime = formSetDueTime;
		 						
		if (taskPersist.dealMemo.equals("")) 
			taskPersist.dealMemo = "�ޱ���";
		else 
			if (taskPersist.dealMemo.length() > 1330) taskPersist.dealMemo = taskPersist.dealMemo.substring(0, 1300) + "...";
				
		String transitionName = executionContext.getInstanceContext().getParameter("transition", Context.types.FORM);
		String callId = getCurrentCallId();
		
		CommonLogger.logger.info("insert into BPM_TASK,due_time="+taskPersist.dueTimeOfDate.getTime());
		
		StringBuffer sql = new StringBuffer("");
		sql.append("insert into BPM_TASK(TASK_ID, INST_ID, PROC_NAME, PRE_TASKID, PRE_TASKNAME, ")
			.append(" TASK_NAME, ASSIGN_UID, ASSIGN_DEPTID, ASSIGN_TIME, DEALER,")
			.append(" DUE_TIME, TRANS_NAME ,REMINDED, REMIND_USERID, REMIND_MSG,")
			.append(" BO_YEAR, BO_MONTH, BO_DAY, CALL_ID, CC_DEALER, CO_DEALER, ")
			.append(" DEAL_UID, DEAL_DEPTID, DEAL_TIME, OVERTIME, DEAL_FLAG)")
			.append(" VALUES (?,?,?,?,?,?,?,?," + ProcessEngine.getDialect().sysdate() + 
					",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		Object[] args = new Object[25];
		args[0] = taskId;
		args[1] = taskPersist.processId;
		args[2] = executionContext.getProcessDefinition().getProcessName();
		args[3] = prevTaskId;
		args[4] = prevTaskName;
		args[5] = taskName;
		args[6] = taskPersist.currentUser.getUserId();
		args[7] = taskPersist.currentUser.getDeptId();
		//args[8] = new java.sql.Timestamp(taskPersist.currentTimeOfDate.getTime());
		args[8] = executionContext.getAssignable().getDealsOmit(executionContext.getAssignable().getTaskDealers());
		
		args[9] = new java.sql.Timestamp(taskPersist.dueTimeOfDate.getTime());
		args[10] = transitionName;
		args[11] = taskPersist.remindInfo.getReminded();
		args[12] = taskPersist.remindInfo.getRemindUser();
		args[13] = taskPersist.remindInfo.getRemindMsg();
		args[14] = DateUtils.getYear();
		args[15] = DateUtils.getMonth();
		args[16] = DateUtils.getDate();
		args[17] = callId;
		args[18] = executionContext.getAssignable().getDealsOmit(executionContext.getAssignable().getTaskCcDealers());
		args[19] = executionContext.getAssignable().getDealsOmit( executionContext.getAssignable().getTaskCoDealers());
		args[20] = dealUId;
		args[21] = dealDept;
		if(dealTime != null) {
			args[22] = new java.sql.Timestamp(dealTime.getTime());
		}else {
			args[22] = dealTime;
		}
		args[23] = overTime;
		args[24] = dealFlag;	
		
		BpmDaoSQLExecutor.executeBatch(executionContext, sql.toString(), args);
		
		sql = new StringBuffer("");
		sql.append("insert into BPM_TASK_EXT(TASK_ID, INST_ID, ASSIGN_MEMO, PASS_NODE, FORK_ID, CONTEXT)")
			.append(" VALUES (?,?,?,?,?,?)");
		Object[] args2 = new Object[6];
		args2[0] = taskId;
		args2[1] = taskPersist.processId;
		args2[2] = OracleBigString.build(taskPersist.dealMemo);
		args2[3] = taskPersist.getExecutionContext().passedTaskThisTime;
		args2[4] = taskPersist.forkId;
		if(DialectUtil.isOracle(ProcessEngine.getDialect())) {
			args2[5] = OracleClob.build(context);
		}else {
			args2[5] = context;
		}
					
		BpmDaoSQLExecutor.executeBatch(executionContext, sql.toString(), args2);
	}
	
	
	/**
	 * �жϵ�ǰ�Ƿ���ͨ����, ���ͨ��,��ȡ��ǰcallId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getCurrentCallId() {
		String callId = executionContext.getInstanceContext()
			.getParameter(Context.values.CALLID, Context.types.FORM);
		
		if(callId.equals("")) {
			//ȡСд
			callId = executionContext.getInstanceContext()
				.getParameter("callId", Context.types.FORM);
		}
		CommonLogger.logger.debug("[TaskDAO]�ӵ�ǰ��ȡ��ǰͨ��CALL_ID:" + callId);
		if(callId.equals("")) {
			callId = executionContext.getInstanceContext()
				.getParameter(Context.values.CALLID, Context.types.RUNTIME);
		}
		if(callId.equals("")) {//�ӱ���ȡ,���Ϊ�������ϯ��ܽӿ���ȡ
    		String inCalling = (String)SessionRegister
    				.getCallProperty(taskPersist.currentUser.getUserId(), "InCalling");
    		if("true".equals(inCalling)) {
    			callId = (String)SessionRegister.getCallProperty(taskPersist.currentUser
    								.getUserId(), "CallID");
    			CommonLogger.logger.debug("[TaskDAO]ȡ��ǰͨ��CALL_ID:" + callId);
    			
    		}else {//����ͨ����
    			return "";
    		}
		}
		return callId;
	}
}
