package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.base.OracleClob;
import com.hanson.jbpm.jpdl.def.node.NodeBase;
import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.jpdl.exe.calendar.BpmCalendar;
import com.hanson.jbpm.jpdl.exe.util.DateUtils;
import com.hanson.jbpm.jpdl.exe.util.IdentityCreator;
import com.hanson.jbpm.log.CommonLogger;

import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.util.calendar.DateUtil;
import com.hanson.jbpm.jpdl.def.node.ForkNode;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;
import com.hanson.jbpm.jpdl.exe.util.DialectUtil;
import com.suntek.util.time.CurrentDateTime;

public class TaskPersistence {
	private List<String> assignment;
	private List<String> ccUsers;
	private List<String> coUsers;
	private ExecutionContext executionContext;
	private InstanceContext instanceContxt;
	private NodeBase leavedNode;
	private NodeBase firstLeaveNode;
	private NodeBase enterNode;	
	protected String processId;
	protected String taskId;		
	protected List<String> newTaskIdList = new ArrayList<String>();
	protected String bizId;
	protected String prevTaskId;	
	protected String transition;	
	protected String dueTime;
	protected Date dueTimeOfDate;
	protected String forkId;
	protected String dealMemo;	
	protected String currentTime;
	protected Date currentTimeOfDate;
	protected TaskUser currentUser;
	protected RemindInfo remindInfo;
	protected int dueDays = BpmConstants.DEFAULT_DUEDATE / 24;
	protected boolean isEnd = false;
	protected String isDraft = "false";
	
	
	/**
	 * 
	 * @param assignment
	 * @param ccUsers
	 * @param coUsers
	 * @param executionContext
	 * @param transition
	 * @param isEnd �Ƿ����̽���
	 */
	public TaskPersistence(List<String> assignment, List<String> ccUsers, List<String> coUsers, 
			ExecutionContext executionContext, String transition, boolean isEnd) {
		this.isEnd = isEnd;
		init(assignment, ccUsers, coUsers, executionContext, transition);
	}
	
	
	/**
	 * Constructor
	 * @param leavedNode 	�뿪������ڵ�
	 * @param enterNode		���������ڵ�
	 * @param assignment	ָ�����б�
	 * @param ccUsers		�������б�
	 * @param executionContext	������
	 * @param transition		��ǰ�ύ�������ת����
	 */
	public TaskPersistence(List<String> assignment, List<String> ccUsers, List<String> coUsers, 
			ExecutionContext executionContext, String transition) {
		
		init(assignment, ccUsers, coUsers, executionContext, transition);
	}
	
	
	/**
	 * 
	 * @param assignment
	 * @param ccUsers
	 * @param coUsers
	 * @param executionContext
	 * @param transition
	 */
	private void init(List<String> assignment, List<String> ccUsers, List<String> coUsers, 
			ExecutionContext executionContext, String transition) {
		instanceContxt = executionContext.getInstanceContext();
		this.executionContext = executionContext;
		
		this.transition = transition;
		this.assignment = assignment;
		this.ccUsers = ccUsers;
		this.coUsers = coUsers;
		this.enterNode = executionContext.getEnterNode();
		this.leavedNode = executionContext.getLeavedNode();
		this.firstLeaveNode = executionContext.getFirstLeavedNode();
		bizId 		= instanceContxt.getParameter(Context.values.BIZID, 	Context.types.FORM);
		this.processId = getProcessIdFromContext(instanceContxt);
		
		currentTime = CurrentDateTime.getCurrentDateTime();
		currentTimeOfDate = Calendar.getInstance().getTime();
		prevTaskId 	= instanceContxt.getParameter(Context.values.TASKID, 	Context.types.FORM);
		if (prevTaskId.equals("")) prevTaskId = " ";
		dueTime = instanceContxt.getParameter(Context.values.DUETIME, 	Context.types.FORM);	
		if(!"".equals(dueTime)) { //����ʵ�ʵ�ʱ��
			if(dueTime.length()<10) { //�����ڸ�ʽ
				dueDays = Integer.parseInt(dueTime);
			    dueTime = BpmCalendar.getInstance().computeDueTime(dueDays);
			    dueDays = dueDays / 24; //ת��Ϊ�쵥λ
			} else { //���ڸ�ʽ, ���������
				dueDays = BpmCalendar.getInstance().computeDueDays(currentTime, dueTime);
			}
		}else {
			//dueTime = BpmCalendar.getInstance().computeDueTime(BpmConstants.DEFAULT_DUEDATE);
			dueTime="2100-01-01 00:00:00";  //û���������ޣ�Ĭ��2100��
		}
		try {
			dueTimeOfDate = DateUtils.getDateTimeFormat().parse(dueTime);
		} catch (ParseException e) {
			CommonLogger.logger.error(e, e);
		}
		
		forkId 		= instanceContxt.getParameter(Context.values.FORKID, 	Context.types.RUNTIME);		
		dealMemo 	= instanceContxt.getParameter(Context.values.TASKTITLE, Context.types.FORM);
		if (dealMemo.equals(""))
			dealMemo 	= instanceContxt.getParameter(Context.values.TASKTITLE, Context.types.RUNTIME);
		
		if (bizId.equals(""))
			instanceContxt.getParameter(Context.values.BIZID, Context.types.RUNTIME);
		
		currentUser = new TaskUser(instanceContxt);
		
		/* ��runtime�л�ȡ����, ����ߵ��ֶ���, ���ôߵ���ʶ,�Ӷ�����ʹ�����ö�����̾�ű�ʶ  */
		String remindMsg = instanceContxt.getParameter(Context.values.REMIND_MSG, Context.types.RUNTIME);
		if(!"".equals(remindMsg)) {
			remindInfo = new RemindInfo("1", currentUser.getUserId(), remindMsg);
		}else {
			remindInfo = new RemindInfo();
		}
		
		isDraft = executionContext.getInstanceContext().getParameter("IS_DRAFT", 
				Context.types.FORM);
	}
	
	
	/**
	 * ִ�й���ʵ�����ݵĳ־û�
	 * @param forkSuspended		�Ƿ������ڵ������
	 * @return
	 * @throws Exception
	 */
	public String execute(boolean forkSuspended) throws Exception {
		CommonLogger.logger.debug("��ʼִ�й���ʵ�����ݵĳ־û� ");
		String prevTaskName = " ";
		
		if (executionContext.isNewInstance()) { // ��¼��ʼ����������ڵ���Ϣ
			if (!executionContext.isForkLoopPassSecond()) {				
				taskId = IdentityCreator.getTaskId(5);
				executionContext.setLastTaskId(taskId);
				new TaskDAO(this).insert(prevTaskId, " ", taskId, 
						executionContext.getProcessDefinition().getStart().getName(),
						currentUser.getUserId(), currentUser.getDeptId(), currentTimeOfDate, 
						"0", "1", instanceContxt.getFormDataInXml());
				
				// ����һ��ָ�ɼ�¼�����ұ�Ǹü�¼Ϊ�����Ѿ�����
				new AssignmentDAO(processId, taskId).send(executionContext, currentUser.getUserId(), State.FALSE, State.TRUE, State.TRUE);			
				prevTaskName = executionContext.getProcessDefinition().getStart().getName();
				
				//updatePrevTaskDealInfo(taskId);
				
				if (!executionContext.isForked())
					prevTaskId = taskId;
			}
			// zhout
			//new TaskMemoDAO().deleteFinishedTaskMemo(executionContext, "", currentUser.getUserId());

		} else {
			prevTaskName = executionContext.getFirstLeavedNode().getName();
			// zhout
			//new TaskMemoDAO().deleteFinishedTaskMemo(executionContext, prevTaskId, currentUser.getUserId());
		}
		
		if (executionContext.isNewInstance() || (executionContext.isForked() && executionContext.isForkLoopPassSecond())) {
			// Do nothing.
		} else {
			updatePrevTaskDealInfo(prevTaskId);
			new AssignmentPersistence(this).updatePrevTaskAssignmentDealFlag();
		} 

		if (!forkSuspended) {
			int loop = 1;
			if (this.getEnterNode() instanceof TaskNode &&
					((TaskNode)this.getEnterNode()).getTask().isIndependent()) // ������������һ������
				loop = this.getAssignment().size();
			for (int i=0; i<loop; i++) {
				taskId = IdentityCreator.getTaskId(5);
				this.newTaskIdList.add(taskId); 
				insertNewTask(prevTaskId, prevTaskName, taskId, executionContext.getEnterNode().getName());
			}
		}
		
		//if (this.getEnterNode() instanceof TaskNode && !executionContext.isNewInstance())
		//	new ReminderDAO().addReminderData(this, taskId, currentUser.getUserId(), currentUser.getUserName(), dueTime);
				
		// ������������¼
		new AssignmentPersistence(this).save();	
				
		//�����µĻ���¹�������ʵ����ҵ���
		if (executionContext.isNewInstance()) {
			if (!(executionContext.isNewInstance() && executionContext.isForkLoopPassSecond())) {
				new ProcessPersistence(this).newInstance();
				
				if("true".equals(isDraft)) { //�ݸ幤������insert����update
					new BizPersistence(this).save();
				}else {
					new BizPersistence(this).insert();
				}
			}
		} else {
			new ProcessPersistence(this).synchronizeTaskDataInInstance();
			new BizPersistence(this).save();
		}
		String taskDealers = executionContext.getAssignable().getTaskDealers();
		executionContext.getInstanceContext().appendRuntimeVariable(Context.values.NEXTSTEP_DEALER, taskDealers);
		
		CommonLogger.logger.debug("ִ�й���ʵ�����ݵĳ־û� ���!");
		
		if(AppHandle.getHandle("jbpm").getProperty("WriteContextFile").equals("true")) { 
			//дcontext���ݵ��ļ���
			ContextFilePersistance.build().save(processId, prevTaskId, instanceContxt.getFormDataInXml());
		}
		return taskId;
	}
	
	/**
	 * ���ɵ�ǰ���������Ϣ
	 * @param prevTaskName	��һ��������
	 * @param taskName		��ǰ��������
	 * @throws Exception 
	 */
	private void insertNewTask(String prevTaskId, String prevTaskName, String taskId, String taskName)
	throws Exception {
			
		instanceContxt.setFormElement(Context.values.BIZID, bizId);
		instanceContxt.setFormElement(Context.values.INSTID, processId);		
		instanceContxt.setFormElement(Context.values.TASKID, taskId);
		
		int due = 0;
		if (getEnterNode() instanceof TaskNode)
			due = new Double(((TaskNode)getEnterNode()).getTask().getDueDate()).intValue();				
		String dueTime = DateUtil.getPreDateTime(currentTime, DateUtil.HOUR, due);
		instanceContxt.setFormElement(Context.values.DUETIME, dueTime);	
		
		new TaskDAO(this).insert(prevTaskId, prevTaskName, taskId, taskName, 
				"", "", null, "0", "0", " ");		
	}
	
	private void updatePrevTaskDealInfo(String prevTaskId) throws Exception {		
		if (dueTime.equals("null")) dueTime = currentTime;	// �½�����ʱ��û�� dueTime ��������
		if (dueTime.length()>19)	dueTime = dueTime.substring(0,19);
		
		String overTime = Calendar.getInstance().after(dueTime)?"1":"0";
		String formDataXml = instanceContxt.getFormDataInXml();
			
		String sql = "update BPM_TASK set DEAL_UID=?, DEAL_DEPTID=?, DEAL_TIME=?,  OVERTIME=?, " +
				" DEAL_FLAG='1'" +
		 //" CONTEXT='" + formDataXml + "'," +
		 //" PASS_NODE='" + executionContext.passedTaskThisTime + "'" +
		 " where TASK_ID = ?";	
				
		BpmDaoSQLExecutor.executeBatch(executionContext, sql, currentUser.getUserId(), currentUser.getDeptId(), 
				new Timestamp(currentTimeOfDate.getTime()), overTime, prevTaskId);
		sql = "update BPM_TASK_EXT set CONTEXT=?, PASS_NODE=?  where TASK_ID = ?";	
		if(DialectUtil.isOracle(ProcessEngine.getDialect())) {
			//oracle���⴦��clob�ֶ�, ��������long����ת������
			BpmDaoSQLExecutor.executeBatch(executionContext, sql, OracleClob.build(formDataXml),
					executionContext.passedTaskThisTime, prevTaskId);
//			BpmDaoSQLExecutor.executeBatch(executionContext, sql, formDataXml, 
//					executionContext.passedTaskThisTime, prevTaskId);
		}else {
			BpmDaoSQLExecutor.executeBatch(executionContext, sql, formDataXml, 
				executionContext.passedTaskThisTime, prevTaskId);
		}
		
	}
	
	private String getProcessIdFromContext(InstanceContext ctx) {
		if (processId == null) {
			processId = ctx.getParameter(Context.values.INSTID, Context.types.MAINFORM);
			CommonLogger.logger.debug("��������ȡ����ʵ��ID: " + processId);
		}
		if (executionContext.getInstanceId() != null && !executionContext.getInstanceId().equals("")) {
			processId = executionContext.getInstanceId();
			CommonLogger.logger.debug("������ʱ���������Ļ�ȡ����ʵ��ID: " + processId);
		}
		
		/* ���� processId */
		if (processId.equals("")) {
			String isntIdFromRuntime = instanceContxt.getParameter(Context.values.INSTID, Context.types.RUNTIME);
			
			/* ���ǳ�ʼ���ڵ���� ������ڵ㣨������ �жϸ� fork �ڵ��Ǵӳ�ʼ�ڵ� enter �ģ�*/
			if (Context.checkNodeIsFirstTask(leavedNode) || (leavedNode instanceof ForkNode)) {
				if(!isntIdFromRuntime.equals("")) {
					processId = isntIdFromRuntime; //���ҵ��ģ����ֵ��INST_ID, ��ôȡ��ֵ
					CommonLogger.logger.debug("��ҵ��ģ���ȡruntime��INST_ID: " + processId);
				}else {
					processId = IdentityCreator.getInstId(5);
				}
				executionContext.setInstanceId(processId);
				executionContext.setNewInstance(true);
			}
			else {
				throw new RuntimeException("��������ȡ��ʵ��id����Ϊ��");
			}
		}
		return processId;
	}
	
	
	public void updatePrevTaskAssignmentDealFlagOnlyMyself() throws SQLException {
		String operator = this.currentUser.getUserId();
		new AssignmentPersistence(this).updatePrevTaskAssignmentDealFlag();
	}
	
	protected String getProcessId() {
		return this.processId;
	}

	public ExecutionContext getExecutionContext() {
		return executionContext; 
	}

	public NodeBase getEnterNode() {
		return enterNode;
	}

	public NodeBase getLeavedNode() {
		return leavedNode;
	}

	public List<String> getAssignment() {
		return assignment;
	}

	public List<String> getCcUsers() {
		return ccUsers;
	}
	
	public void setCoUsers(List<String> coUsers) {
		this.coUsers = coUsers;
	}

	public List<String> getCoUsers() {
		return coUsers;
	}

	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getPreTaskId() {
		return prevTaskId;
	}
	public void setPreTaskId(String preTaskId) {
		this.prevTaskId = preTaskId;
	}
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public NodeBase getFirstLeaveNode() {
		return firstLeaveNode;
	}
	public void setFirstLeaveNode(NodeBase firstLeaveNode) {
		this.firstLeaveNode = firstLeaveNode;
	}
}
