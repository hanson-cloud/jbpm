package com.hanson.jbpm.mgmt.ctx;

import java.util.List;
import java.util.Map;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.mgmt.ProcessClient;

public class HistoryTaskContextQuery extends ContextQuery {

	private String taskId;
	private String instanceId;
	private String taskName;

	public HistoryTaskContextQuery(String taskId) {
		this.taskId = taskId;
	}
	
	public HistoryTaskContextQuery(String instanceId, String taskId, String taskName, String queryByTaskId) {
		this.instanceId = instanceId;
		this.taskId = taskId;
		this.taskName = taskName;		
		this.queryByTaskId = queryByTaskId;
	}
	
	public String getContextByTaskId() {
		String sql = "select CONTEXT from BPM_TASK_HIS where TASK_ID='" + taskId + "'";
		List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
		String text = "<a></a>";
		if (list.size()>0)
			text = (String)((Map)list.get(0)).get("CONTEXT");
		return text;
	}

	@Override
	public String getContext() throws BpmException {	
		if (queryByTaskId != null)
			return getContextByTaskId();
		else {			
    		String sql = "select CONTEXT from BPM_TASK_HIS where INST_ID='" + instanceId + "'" +
    			" and TASK_NAME='" + taskName + "' and DEAL_TIME is not null" +
    			" order by TASK_ID desc";
    		CommonLogger.logger.info(sql);
    		List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
    								
    		String text = "<a></a>";
    		if (list.size()>0)
    			text = (String)((Map)list.get(0)).get("CONTEXT");
    		return text;
		}
	}

}
