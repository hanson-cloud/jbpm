package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.JoinNode;
import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.identity.OrgCache;
import com.hanson.jbpm.identity.OrgCacheLoader;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.um.IPortalMessageGateway;

public class AssignmentPersistence {
	private TaskPersistence taskPersist;
	private Timestamp dealTime;
	/* �����������Ϣ���͸����������� */
	private String message;
	//private String procName;
	
	public AssignmentPersistence(TaskPersistence taskPersist) {
		this.taskPersist = taskPersist;
		InstanceContext ctx = taskPersist.getExecutionContext().getInstanceContext();
		//this.procName = taskPersist.getExecutionContext().getProcessDefinition().getProcessName();
		this.message = ctx.getParameter(Context.values.TASKTITLE, Context.types.RUNTIME);
		String from = ctx.getParameter(Context.values.ULOGNAME, Context.types.USER);
		this.message = from + ": ����" + taskPersist.getBizId() + taskPersist.getLeavedNode().getName() + ": " + this.message;
		dealTime = new Timestamp(taskPersist.currentTimeOfDate.getTime());
	}
	
	public void save() throws Exception {
		ArrayList<String> sql = new ArrayList<String>();
		ArrayList<Object[]> params = new ArrayList<Object[]>(); //SQL�󶨱���
		
		IPortalMessageGateway mg = null; 
		try {
			mg = (IPortalMessageGateway) OpenCC.getComponentContainer().getComponent(IPortalMessageGateway.class);
			//�����ṩ��������
		} catch (Exception ex) {}
		
		ProcessDefinition pdf = taskPersist.getExecutionContext().getProcessDefinition();
		String processName = pdf.getProcessName();
		Timestamp assignTimeSql = new Timestamp(taskPersist.currentTimeOfDate.getTime());
		Timestamp dueTimeSql = new Timestamp(taskPersist.dueTimeOfDate.getTime());
		
		String taskName = taskPersist.getEnterNode().getName();
		
		/* ����������ķ����¼ */		
		if (taskPersist.getAssignment() != null) {			
			CommonLogger.logger.debug("Assign to " + taskPersist.getAssignment().size() + " users.");
			if (taskPersist.getAssignment().size() == 0 && !(taskPersist.getEnterNode() instanceof JoinNode))
				throw new BpmException("����Ӧ�÷��䵽�ˣ���ָ�������˻��������н�������.");
			
			String userCode;
			String dealFlag = State.FALSE;
			
			TaskNode enteringTaskNode = (TaskNode)taskPersist.getEnterNode();
			
			int index = 0;
			int lastIndex = taskPersist.getAssignment().size();
			
			if (pdf.getAssignStrategy().equals("avg")) {
				index = enteringTaskNode.getAssignmentIndex();
				if (index == taskPersist.getAssignment().size())
					index = 0;
				enteringTaskNode.setAssignmentIndex(index + 1);
				lastIndex = index + 1;
			}
			
			CommonLogger.logger.debug("Strategy: " + pdf.getAssignStrategy() + ", " +
									  "AssignmentIndex: " + enteringTaskNode.getAssignmentIndex() + ", lastIndex: " + lastIndex + "; " +
									  "Run: onechoice-" + enteringTaskNode.isOnechoice() + 
									  		", sequence-" + enteringTaskNode.isSequence() + 
									  		", parallel-" + enteringTaskNode.isParallel() + 
									  		", async-" + enteringTaskNode.isAsync());
			
			
			for (int i=index; i<lastIndex; i++) 
			{
				userCode = taskPersist.getAssignment().get(i);
				String dealDeptId = "";
				if(userCode.indexOf("D") == 0) {
					dealDeptId = userCode.substring(1, userCode.length());
				}else {
					dealDeptId = OrgCacheLoader.getInstance().queryUserDept(userCode);
				}
				String taskId = taskPersist.getTaskId();
				if (taskPersist.newTaskIdList.size()>1) { // ��˳�����
					taskId = taskPersist.newTaskIdList.get(i);
				}
				
				dealFlag = State.FALSE;
				
				if (!(enteringTaskNode.isOnechoice())) {
					if (i>0 && (enteringTaskNode.isSequence())) {						
						dealFlag = State.WAIT;
					}
				}
				sql.add("insert into BPM_ASSIGN(INST_ID, TASK_ID, DEALER, CC_FLAG, READ_FLAG, DEAL_FLAG, RUN_IDX, " +
						"ASSIGN_TIME, DUE_TIME, PROC_NAME, TASK_NAME, DEAL_DEPTID)" +
						" values (?,?,?,?, ?,?,?,?,?,?,?,?)");
				params.add(new Object[]{taskPersist.getProcessId(), taskId, userCode, State.FALSE, 
						State.FALSE, dealFlag, i, assignTimeSql, dueTimeSql, processName, taskName,
						dealDeptId});
				//��־���ύʱͳһ��ӡ
				//CommonLogger.logger.debug("assign:" + sql.get(sql.size()-1));
			};		
		}
		
		/* �����˷����¼  */
		if (taskPersist.getCcUsers() != null) {
			CommonLogger.logger.debug("save CC to " + taskPersist.getCcUsers().size() + " users.");
			
			for (int i=0; i<taskPersist.getCcUsers().size(); i++) {
				String userCode = taskPersist.getCcUsers().get(i);
				String dealDeptId = "";
				if(userCode.indexOf("D") == 0) {
					dealDeptId = userCode.substring(1, userCode.length());
				}else {
					dealDeptId = OrgCacheLoader.getInstance().queryUserDept(userCode);
				}
				sql.add("insert into BPM_ASSIGN(INST_ID, TASK_ID, DEALER, CC_FLAG, READ_FLAG, DEAL_FLAG, " +
						"ASSIGN_TIME, DUE_TIME, PROC_NAME, TASK_NAME, DEAL_DEPTID)" +
						" values (?,?,?,?,?,?,?,?,?,?,?)");
				params.add(new Object[]{taskPersist.getProcessId(), taskPersist.getTaskId(), 
						userCode, State.TRUE, State.FALSE, State.FALSE, 
						assignTimeSql, dueTimeSql, processName, taskName,
						dealDeptId});
				
				//CommonLogger.logger.debug(sql.get(i));
			}
		}	
		
		//Э���˼�¼
		if(taskPersist.getCoUsers() != null) {
			CommonLogger.logger.debug("save CO to " + taskPersist.getCoUsers().size() + " users.");
			
			for (int i=0; i<taskPersist.getCoUsers().size(); i++) {
				String userCode = taskPersist.getCoUsers().get(i);
				String dealDeptId = "";
				if(userCode.indexOf("D") == 0) {
					dealDeptId = userCode.substring(1, userCode.length());
				}else {
					dealDeptId = OrgCacheLoader.getInstance().queryUserDept(userCode);
				}
				sql.add("insert into BPM_ASSIGN(INST_ID, TASK_ID, DEALER, CO_FLAG, READ_FLAG, DEAL_FLAG, " +
						"ASSIGN_TIME, DUE_TIME, PROC_NAME, TASK_NAME, DEAL_DEPTID)" +
						" values (?,?,?,?,?,?,?,?,?,?,?)");
				params.add(new Object[]{
						taskPersist.getProcessId(), taskPersist.getTaskId(), userCode,
						State.TRUE, State.FALSE, State.FALSE, assignTimeSql, dueTimeSql,
						processName, taskName, dealDeptId
				});
				
			}
		}
		
		BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql, params);
		/*if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc"))
			taskPersist.getExecutionContext().getJdbc().executeBatch(sql);
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate")) {
			for (int i=0; i<sql.size(); i++)
			taskPersist.getExecutionContext().getSession().createSQLQuery(sql.get(i)).executeUpdate();
		}*/
	}
	
	public void updatePrevTaskAssignmentDealFlag() throws SQLException {	
		String operator = taskPersist.currentUser.getUserId();
		/*if(BpmConstants.RoleDeptDispatchMode == true) {
			operator = BpmConstants.PREFIX_DEPT + OrgCacheLoader.getInstance().queryUserDept(operator);
		}*/
		
		//�������ɵ�ģʽʱ��updateDEALERδ�Լ�
		String updateDeptToMe = "update BPM_ASSIGN set DEALER=? where TASK_ID=? AND DEALER=?";
		BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), updateDeptToMe, 
				operator, taskPersist.getPreTaskId(), BpmConstants.PREFIX_DEPT+OrgCache.getCache().getUserDept(operator));
		
		/* �Ƚ�BPM_ASSIGN���еļ�¼�е�DEAL_FLAG ����Ϊ2,��ʾ�����˴��� */
		String otherFlagSql = "update BPM_ASSIGN set DEAL_FLAG=?" +
			 ", DEAL_TIME=? where TASK_ID=?" ;
		
		BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), otherFlagSql, 
				State.OTHER_TRUE, dealTime, taskPersist.getPreTaskId());
		
		/* �ٽ��Լ������DEAL_FLAG ����Ϊ1*/
		String sql = "update BPM_ASSIGN set DEAL_FLAG=?" +
			 ", DEAL_TIME=? where TASK_ID=?  and DEALER = ? ";	
		
		BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql, 
				State.TRUE, dealTime, taskPersist.getPreTaskId(), operator );
	}
	
	/*public void updatePrevTaskAssignmentDealFlagOnlyMyself(String operator) throws SQLException {
		if(BpmConstants.RoleDeptDispatchMode == true) {
			operator = BpmConstants.PREFIX_DEPT + OrgCacheLoader.getInstance().queryUserDept(operator);
		}
		String sql = "update BPM_ASSIGN set DEAL_FLAG=? , DEAL_TIME=? " +
				" where TASK_ID=? and DEALER=?  and DEAL_FLAG=?";
		
		 ��BPM_ASSIGN���д�����Ϊ�����˵ļ�¼�е�DEAL_FLAG ����Ϊ2,��ʾ�����˴��� 
		String otherFlagSql = "update BPM_ASSIGN set DEAL_FLAG=? , DEAL_TIME=?" +
				" where TASK_ID=? and DEALER<>? and DEAL_FLAG=?";
		
		
		BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql, 
				State.TRUE, dealTime, taskPersist.getPreTaskId(), operator, State.FALSE );
		BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), otherFlagSql, 
				State.OTHER_TRUE, dealTime, taskPersist.getPreTaskId(), operator, State.FALSE);
	}*/
}
