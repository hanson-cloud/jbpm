package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.DocumentException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.identity.OrgCache;
import com.hanson.jbpm.identity.User;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.exe.util.DataTransformer;
import com.hanson.jbpm.jpdl.exe.util.DateFormat;
import com.hanson.jbpm.jpdl.exe.util.RequestTransformer;
import com.hanson.jbpm.mgmt.ProcessClient;

public class ProcessInstance {
	private ProcessDefinition processDefinition;
	private String instanceId;
	private String processContext = "";
	private String taskContext = "";
	private String envContext = "";
	private ExecutionContext executionContext = null;
	private InstanceContext instanceContext = null;
	private Token token;
	private String currentNodeName;
	private String prevTaskName;
	private String taskId;
	private String bizId;
	private String currentTaskForkId = null;
	private String currentTaskDueTime;
	private HttpServletRequest request;
	private Map parameterMap;
	
	public boolean hasBeenClosed = false;
	public String taskIsLocked = "";
	public String taskLockUserId = "";
	public Timestamp taskDealTime;
	public String taskDealer = "";
		
	public ProcessInstance() {}
	
	public ProcessInstance(ProcessDefinition processDefinition, String instanceId, String taskId) {
		if (processDefinition == null)
			throw new BpmException("���̶���Ϊ�գ��޷���������ʵ����");
		
		this.processDefinition = processDefinition;
		this.instanceId = instanceId;
		this.taskId = taskId;
		
		queryProcessContext();
	}
	
	/**
	 * ��ѯ��ǰ����ʵ�������� XML ����
	 * @return
	 */
	private void queryProcessContext() 
	{
		JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
		
		if (instanceId == null) {
			currentNodeName = processDefinition.getStart().getName();
			CommonLogger.logger.debug("instanceId Ϊ�գ��Ӳ�ѯʵ����������ֱ�ӷ���!");
			return;
		}
		/*String sql = "select b.CONTEXT, a.ASSIGN_UID, a.ASSIGN_DEPTID, a.DUE_TIME" +					 
					 " from BPM_TASK a, BPM_TASK_EXT b" +
					 " where a.INST_ID='" + instanceId + "' and PRE_TASKID=' ' and a.TASK_ID=b.TASK_ID";*/
		String sql = "select a.INST_ID, a.TASK_ID, a.TASK_NAME, c.CONTEXT," +
					 " b.INIT_UID, b.INIT_DEPTID, a.ASSIGN_UID, a.ASSIGN_DEPTID," +
					 " a.DUE_TIME DUE_TIME2, c.FORK_ID, b.DUE_TIME, a.PRE_TASKNAME, " +
					 " a.LOCKED, a.LOCK_USERID, a.DEAL_TIME, a.DEAL_UID " + // ���ύ��֤��
					 " from BPM_TASK a, BPM_INST b, BPM_TASK_EXT c" +
					 " where a.INST_ID='" + instanceId + "' and (PRE_TASKID=' ' or a.TASK_ID='" + taskId + "')" +
					 " and b.INST_ID=a.INST_ID and a.TASK_ID=c.TASK_ID order by TASK_ID";
		CommonLogger.logger.debug("��ǰ�����������ݲ�ѯ:" + sql);
		
		List taskList = jdbc.queryForList(sql);
		
		if (taskList == null) {
			this.hasBeenClosed = true;
			return;
		}
				
		Map firstTask = (Map)taskList.get(0);
		Map currentTask = firstTask;
		for (int i=0; i<taskList.size(); i++) {
			CommonLogger.logger.debug("["+currentTask.get("TASKID")+"]");
			currentTask = (Map)taskList.get(i);
			if (currentTask.get("DEAL_TIME") == null)
				break; 
		}
		
		processContext = (String)firstTask.get("CONTEXT");
		// ���� ����ʵ������ʱ��, �������̽���ʱ�����Ƿ���ʱ�޳�ʱ
		processContext = processContext + "<DUE_TIME>" + (Timestamp)firstTask.get("DUE_TIME") + "</DUE_TIME>";
		processContext = processContext + "<INIT_UID>" + (String)firstTask.get("ASSIGN_UID") + "</INIT_UID>";
		processContext = processContext + "<INIT_DEPTID>" + (String)firstTask.get("ASSIGN_DEPTID") + "</INIT_DEPTID>";
		
		/*sql = "select a.INST_ID, a.TASK_ID, a.TASK_NAME, a.ASSIGN_UID, a.ASSIGN_DEPTID," +
					" a.LOCKED, a.LOCK_USERID, a.DEAL_TIME, a.DEAL_UID, " + // ���ύ��֤��
					" a.DUE_TIME DUE_TIME2, a.PRE_TASKNAME" +
					" from BPM_TASK a, BPM_INST b" +
					" where a.TASK_ID='" + taskId + "' and a.INST_ID=b.INST_ID";*/
		//Map currentTask = jdbc.queryForMap(sql);
		
		currentTaskForkId = (String)currentTask.get("FORK_ID");

		this.taskIsLocked = (String)currentTask.get("LOCKED");		
		this.taskLockUserId = (String)currentTask.get("LOCK_USERID");
		this.taskDealer = (String)currentTask.get("DEAL_UID");
		this.taskDealTime = (Timestamp)currentTask.get("DEAL_TIME");
		
		currentNodeName = (String)currentTask.get("TASK_NAME");		
		bizId = (String)currentTask.get("BIZ_ID");
		prevTaskName = (String)currentTask.get("PRE_TASKNAME");
				
		currentTaskDueTime = new DateFormat().format((Timestamp)currentTask.get("DUE_TIME2"));
		
		processContext = processContext + "<INST_ID>" + (String)currentTask.get("INST_ID") + "</INST_ID>";
		processContext = processContext + "<TASK_ID>" + (String)currentTask.get("TASK_ID") + "</TASK_ID>";
		processContext = processContext + "<PRE_TASKNAME>" + prevTaskName + "</PRE_TASKNAME>";
		
		processContext = processContext + "<ASSIGN_UID>" + (String)currentTask.get("ASSIGN_UID") + "</ASSIGN_UID>";
		processContext = processContext + "<ASSIGN_DEPTID>" + (String)currentTask.get("ASSIGN_DEPTID") + "</ASSIGN_DEPTID>";
	}
	
	public String getPrevTaskName() {
		return prevTaskName;
	}

	public String getCurrentTaskForkId() {
		return currentTaskForkId;
	}

	protected void setCurrentTaskForkId(String currentTaskForkId) {
		this.currentTaskForkId = currentTaskForkId;
	}

	public void setTaskContext(String taskContext) {
		if (taskContext.indexOf(Context.values.BIZID)>0) {
			this.taskContext = taskContext;
		} else {
			if (this.instanceId != null) {
				this.taskContext = taskContext + "<" + Context.values.INSTID + ">" + this.instanceId + "</" + Context.values.INSTID + ">";
				this.taskContext = this.taskContext + "<" + Context.values.BIZID + ">" + this.bizId + "</" + Context.values.BIZID + ">";			
				this.taskContext = this.taskContext + "<" + Context.values.TASKID + ">" + this.taskId + "</" + Context.values.TASKID + ">";
			} else {
				this.taskContext = taskContext;
			}
		}
	}
	
	public void setTaskContext(Map map) {
		try {
			String taskContext = DataTransformer.map2Xml(map);
			this.parameterMap = map;
			
			if (taskContext.indexOf(Context.values.BIZID)>0) {
				this.taskContext = taskContext;
			} else {
				if (this.instanceId != null) {
					this.taskContext = taskContext + "<" + Context.values.INSTID + ">" + this.instanceId + "</" + Context.values.INSTID + ">";
					this.taskContext = this.taskContext + "<" + Context.values.BIZID + ">" + this.bizId + "</" + Context.values.BIZID + ">";			
					this.taskContext = this.taskContext + "<" + Context.values.TASKID + ">" + this.taskId + "</" + Context.values.TASKID + ">";
				} else {
					this.taskContext = taskContext;
				}
			}
		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
			throw new RuntimeException(e);
		}
	}
	
	public void setTaskContext2(String taskContext) {
		this.taskContext = taskContext;
	}
	
	public void setEnvContext(String envContext) {
		this.envContext = envContext;
	}
	
	public void setEnvContext(User user) {
		String envContext = "<user><logname>" + user.getId() + "</logname>" +
							"<uname>" + user.getName() + "</uname>";
		String deptId = OrgCache.getCache().getUserDept(user.getId());
		envContext += "<deptId>" + deptId + "</deptId>" + 
							"<deptName>" + OrgCache.getCache().getDept(deptId) + "</deptName>" + 
							"</user>";
		this.envContext = envContext;
	}
	
	public InstanceContext getInstanceContext() throws DocumentException {
		if (instanceContext == null) {
			instanceContext = new InstanceContext(envContext, taskContext, processContext);
			if (currentTaskForkId != null)
				instanceContext.appendRuntimeVariable(Context.values.FORKID, currentTaskForkId);
			instanceContext.setParameterMap(this.parameterMap);
		}
		return instanceContext;
	}
	
	public ExecutionContext getExecutionContext() throws DocumentException {
		if (executionContext == null) {
			executionContext = new ExecutionContext(processDefinition).setInstanceContext(getInstanceContext());
			executionContext.setPrevTaskName(prevTaskName);
		}
		return executionContext;
	}
	
	public Token getToken() {		
		if (token == null) { 
			token = new Token(processDefinition.getNode(currentNodeName));
			token.setProcessInstance(this);
		}
		return token;
	}
	
	/**
	 * ���ҵ�ǰ����ʵ������������
	 * @return
	 */
	protected String getProcessContext() {
		return processContext;
	}
	
	protected void end() {
	}
		
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	protected void setProcessDefinition(ProcessDefinition pdf) {
		this.processDefinition = pdf;
	}

	protected String getInstanceId() {
		return instanceId;
	}

	protected void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	protected String getTaskId() {
		return taskId;
	}

	protected void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	protected String getCurrentTaskDueTime() {
		return currentTaskDueTime;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) throws DocumentException {		
		if (this.envContext.equals("")) {
			RequestTransformer transformer = new RequestTransformer();
			this.setEnvContext(transformer.envDataToXml(request));
			
			if (instanceId != null) {
				this.getExecutionContext().getInstanceContext().setFormElement(Context.values.INSTID, instanceId);
				this.getExecutionContext().getInstanceContext().setFormElement(Context.values.TASKID, taskId);
				this.getExecutionContext().getInstanceContext().setFormElement(Context.values.BIZID, bizId);
			}
		}
		
		this.request = request;
		this.getExecutionContext().setRequest(request);
	}	
}
