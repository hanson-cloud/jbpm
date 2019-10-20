package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.Timestamp;
import java.util.Calendar;

import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.jpdl.exe.util.DateUtils;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.eap.util.calendar.DateUtil;
import com.hanson.jbpm.identity.OrgCache;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.pico.custmgr.Customer;
import com.suntek.util.time.CurrentDateTime;

public class ProcessPersistence {
	private TaskPersistence taskPersist;
	
	public ProcessPersistence(TaskPersistence taskPersist) {
		this.taskPersist = taskPersist;
	}
	
	
	public void newInstance() throws Exception {
		newInstance(taskPersist.isEnd);
	}
	
	
	/**
	 * 
	 * @param isDirectEnd �Ƿ�ֱ�ӹر�
	 * @throws Exception
	 */
	public void newInstance(boolean isDirectEnd) throws Exception {
		InstanceContext instanceContext = taskPersist.getExecutionContext().getInstanceContext();		
		String title = instanceContext.getParameter(Context.values.BIZTITLE, Context.types.FORM);
		if (title.equals(""))
			title = instanceContext.getParameter(Context.values.TASKTITLE, Context.types.RUNTIME);
		else
			instanceContext.appendRuntimeVariable(Context.values.TASKTITLE, title);
		if (title.equals(""))	title = "�ޱ���";
		if (title.length()>50) title = title.substring(0, 40);
		
		String attach = instanceContext.getParameter(Context.values.ATTACHFILE, Context.types.FORM);
		
		String bizId = instanceContext.getParameter(Context.values.BIZID, Context.types.RUNTIME);		
		//String transition = taskT.getTransition();			
		String userId = instanceContext.getParameter(Context.values.ULOGNAME, Context.types.USER);
		//String userName = instanceContext.getParameter(Context.values.UNAME, Context.types.USER);
		
		/*User user = new SysUser(userId);
		Department[] dept = user.getDepts();
		String deptId = "";
		String deptName = "";
		if (dept.length > 0) {
			deptId = dept[0].getId();
			deptName = dept[0].getName();
		}*/
		
		String deptId = OrgCache.getCache().getUserDept(userId);
		//String deptName = OrgCache.getCache().getDept(deptId);
		
		String processId = taskPersist.getProcessId();
		String processName = taskPersist.getEnterNode().getProcessDefinition().getProcessName();
		//String taskId = taskT.getTaskId();
		/* ������Ľڵ����� */
		//String taskName = taskT.getEnterNode().getName();		
		
		String current = CurrentDateTime.getCurrentDateTime();
		/* �ӿ�ʼ�ڵ��ȡ�ܵ�ʱ�� */
		int due1 = new Double(((TaskNode)taskPersist.getLeavedNode()).getTask().getDueDate()).intValue();
		String dueTime1 = DateUtil.getPreDateTime(current, DateUtil.HOUR, due1);
		
		/* �Զ�������ʱ�� */
		String formSetDueTime = taskPersist.dueTime;
		if (!formSetDueTime.equals("")) dueTime1 = formSetDueTime;
		
		String callId = instanceContext.getParameter(Context.values.CALLID, Context.types.FORM);
		String custId = instanceContext.getParameter(Context.values.CUSTID, Context.types.FORM);
		if (custId.equals("")) custId = "0";
		Customer cust = OpenCC.getCallContext(userId).getCallinCust(userId);
		String custName = "";
		if (cust != null) 
			custName = cust.getCustName();
		String custTel = instanceContext.getParameter(Context.values.CUSTTEL, Context.types.FORM);
		
		String linkedSeq = instanceContext.getParameter(Context.values.LINKEDRID, Context.types.FORM);
		String[] linkedSeqs = linkedSeq.split(",");
		
		StringBuffer sql = new StringBuffer(2000);
		Object[] args = null;
		sql.append("insert into BPM_INST(INST_ID, INST_TITLE, ATTACHFILE, PROC_NAME, INIT_UID,").append(
			  " INIT_DEPTID, INIT_TIME, DUE_TIME, RELEVANTED, LAST_TASKID, BO_YEAR, BO_MONTH, ").append(
			  "BO_DAY, LAST_TASK_NAME");
		if(isDirectEnd) { //ֱ�ӹ�������ʱ�������CLOSE_TIME������
			sql.append(", CLOSE_TIME, OVERTIME");
			args = new Object[16];
		}else {
			args = new Object[14];
		}
		sql.append(")");	 
		sql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?");
		if(isDirectEnd) {
			sql.append(",?,?");
		}
		sql.append(")");
		
		args[0] = processId;
		args[1] = title;
		args[2] = (attach.equals("")?"1":"0");
		args[3] = processName;
		args[4] = userId;
		args[5] = deptId;
		args[6] = new Timestamp(taskPersist.currentTimeOfDate.getTime());
		args[7] = new Timestamp(DateUtils.getDateTimeFormat().parse(dueTime1).getTime());
		args[8] = ((linkedSeqs.length>0)?"1":"0");
		args[9] = this.taskPersist.taskId;
		args[10] = DateUtils.getYear();
		args[11] = DateUtils.getMonth();
		args[12] = DateUtils.getDate();
		args[13] = taskPersist.getEnterNode().getName();
		
		if(isDirectEnd) {
			args[14] = new Timestamp(taskPersist.currentTimeOfDate.getTime());
			args[15] = getOverTime();
		}
		
		
		BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql.toString(), args);
		sql.delete(0, sql.length());
		
		if (BpmConstants.isOpenCCEnv) {
			sql.append("insert into BPM_INST_EXT(INST_ID, CUST_ID, CUST_TEL, CUST_NAME, CALL_ID, RELEVANT_ID)")
				.append(" values (?,?,?,?,?,?)");			
			BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql.toString(), 
					processId, custId, custTel, custName, callId, linkedSeqs[0]);
			sql.delete(0, sql.length());
		}
		if (!linkedSeq.equals("")) {			
			for (int i=0; i<linkedSeqs.length; i++) {
				sql.append("update BPM_RELEVANT set INST_ID=? where R_ID=?");
				
				BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql.toString(), 
						processId, linkedSeqs[i]);
				sql.delete(0, sql.length());
			}
		}
		if (!attach.equals("")) {
			sql.append("update BPM_ATTACHFILE set INST_ID=? where BAT_ID=?");
			BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql.toString(), 
					processId, attach);
			sql.delete(0, sql.length());
		}
		sql = null;
	}
	
	
	private String getOverTime() {
		String dueTime = taskPersist.getExecutionContext().getProcessDefinition().getProcessDuetime();
		/* �����������Ƿ�ʱ, Ĭ��Ϊ����ʱ */
		String overTime = "0";
		if (!dueTime.equals("")) 
			overTime = Calendar.getInstance().after(dueTime)?"1":"0";
		return overTime;
	}
	
	
	public void synchronizeTaskDataInInstance() throws Exception {
		synchronizeTaskDataInInstance(taskPersist.isEnd);
	}
	
	
	/**
	 * �Ƿ�ֱ�ӹر�
	 * @param isDirectEnd
	 * @throws Exception
	 */
	public void synchronizeTaskDataInInstance(boolean isDirectEnd) throws Exception {
		StringBuffer sql = new StringBuffer(2000);
		Object[] args = null;
		
		sql.append("update BPM_INST set LAST_TASKID=?, TASK_NUM = TASK_NUM+1, LAST_TASK_NAME=?");
		if(isDirectEnd) {
			sql.append(", CLOSE_TIME=?,OVERTIME=? ");
			args = new Object[5];
			args[0] = taskPersist.taskId;
			args[1] = taskPersist.getEnterNode().getName();
			args[2] = new Timestamp(taskPersist.currentTimeOfDate.getTime());
			args[3] = getOverTime();
			args[4] = taskPersist.processId;
		}else {
			args = new Object[3];
			args[0] = taskPersist.taskId;
			args[1] = taskPersist.getEnterNode().getName();
			args[2] = taskPersist.processId;
		}
		
		/*sql.append("update BPM_INST set LAST_TASKID='").append(t)
			.append("', TASK_NUM = TASK_NUM+1, LAST_TASK_NAME='")
			.append().append("'");
		if(isDirectEnd) {
			String current = CurrentDateTime.getCurrentDateTime();
			sql.append(", CLOSE_TIME=").append(taskPersist.getExecutionContext()
					.getDialect().todate("'"+current+"'"))
					.append(",OVERTIME='").append(getOverTime()).append("' ");
		}*/
		
		sql.append(" where INST_ID=?");
		BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql.toString(), args);
		sql = null;
	}
	
	public static void main(String[] args) {
		CommonLogger.logger.debug(DateUtil.getPreDateTime(CurrentDateTime.getCurrentDateTime(), DateUtil.HOUR, 20));
	}
}
