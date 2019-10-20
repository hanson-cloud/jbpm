package com.hanson.jbpm.jpdl.exe.ctx;


import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import com.hanson.jbpm.jpdl.def.node.NodeBase;
import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.jpdl.exe.impl.Assignable;
import com.hanson.jbpm.log.CommonLogger;
import org.hibernate.Session;

import com.suntek.eap.jdbc.QueryHelper;
import com.suntek.eap.util.jdbc.Dialect;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;

public class ExecutionContext {
	private ProcessDefinition processDefinition;
	private InstanceContext context;
	private HttpServletRequest request;
	
	private IExecutionContextSQLHelper sqlHelper;
		
	private Assignable assignable;
	private NodeBase leavedNode;
	private NodeBase firstLeavedNode;
	private NodeBase enterNode;
	private String instanceId;
	private boolean isNewInstance;
	public String passedTaskThisTime = "";
	private String prevTaskName = "";
	private String triggerTransitionName = "";
	
	private boolean forkLoopPassSecond = false;
	private boolean isForked = false;
	private String shouldLeavingABlockingNode = null;
	
	private String lastTaskId;
	
	public ExecutionContext(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
		this.assignable = new Assignable();
	}
	
	public ExecutionContext setInstanceContext(InstanceContext context) {
		this.context = context;
		return this;
	}	

	public IExecutionContextSQLHelper getSQLHelper() {
		if (sqlHelper == null)
			sqlHelper = new ExecutionContextSQLHelper();
		return this.sqlHelper;
	}
	
	public QueryHelper getJdbc() throws SQLException {
		return this.getSQLHelper().getJdbc();
	}	
	
	public void setJdbc(QueryHelper jdbc) {
		this.getSQLHelper().setJdbc(jdbc);
	}
	
	public Dialect getDialect() throws SQLException {
		return this.getSQLHelper().getDialect();
	}
	
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}
	
	public InstanceContext getInstanceContext() {
		return context;
	}

	public Assignable getAssignable() {
		return assignable;
	}

	public void setLeavedNode(NodeBase leavedNode) {
		this.leavedNode = leavedNode;
	}

	public NodeBase getLeavedNode() {
		return leavedNode;
	}

	public NodeBase getEnterNode() {
		return enterNode;
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	public NodeBase getNextNode() {
		return enterNode;
	}

	public void setEnterNode(NodeBase _enterNode) { 
		enterNode = _enterNode;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setNewInstance(boolean isNewInstance) {
		this.isNewInstance = isNewInstance;
	}

	public boolean isNewInstance() {
		return isNewInstance;
	}
	
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setPrevTaskName(String prevTaskName) {
		this.prevTaskName = prevTaskName;
	}

	public String getPrevTaskName() {
		return prevTaskName;
	}

	public void setForkLoopPassSecond(boolean forkLoopPassSecond) {
		this.forkLoopPassSecond = forkLoopPassSecond;
	}

	public boolean isForkLoopPassSecond() {
		return forkLoopPassSecond;
	}

	public void setForked(boolean isForked) {
		this.isForked = isForked;
	}

	public boolean isForked() {
		return isForked;
	}
	
	public boolean mustLeavingABlockingNode() throws SQLException {
		if (shouldLeavingABlockingNode == null) {
			if (((TaskNode)this.firstLeavedNode).getTask().isBlocking()) {
				String taskId = this.context.getParameter(Context.values.TASKID, 	Context.types.FORM);
				String sql = "select DEALER from BPM_ASSIGN where TASK_ID='" + taskId + "'" +
							 " and (DEAL_FLAG='" + BpmConstants.INITFLAG + "')";				
				CommonLogger.logger.debug(sql);
				shouldLeavingABlockingNode = String.valueOf(this.getJdbc().executeQuery(sql).size()>1);
				return shouldLeavingABlockingNode.equalsIgnoreCase("true");
			} else {
				shouldLeavingABlockingNode = "false";
				return false;
			}
		} else {
			return shouldLeavingABlockingNode.equalsIgnoreCase("true");
		}
	}

	public void setFirstLeavedNode(NodeBase firstLeavedNode) {
		this.firstLeavedNode = firstLeavedNode;
	}

	public NodeBase getFirstLeavedNode() {
		return firstLeavedNode;
	}

	public Session getSession() {
		return this.getSQLHelper().getSession();
	}
	
	public void addSQL(String sql) {
		this.getSQLHelper().addSQL(sql);
	}
	
	public void addSQL(String sql, Object... params) {
		this.getSQLHelper().addSQL(sql, params);
	}
	
	public void executeBatchSQL() throws Exception {
		this.getSQLHelper().executeBatchSQL();
	}

	public void setTriggerTransitionName(String triggerTransitionName) {
		this.triggerTransitionName = triggerTransitionName;
	}

	public String getTriggerTransitionName() {
		return triggerTransitionName;
	}

	public void setLastTaskId(String lastTaskId) {
		this.lastTaskId = lastTaskId;
	}

	public String getLastTaskId() {
		return lastTaskId;
	}
}
