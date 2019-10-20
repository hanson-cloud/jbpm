package com.hanson.jbpm.mgmt.ctx;

import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.mgmt.ProcessClient;

public class CurrentTaskContextQuery extends ContextQuery {
	
	private String taskId;
	
	public CurrentTaskContextQuery(String taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public String getContext() throws BpmException {
		String sql = "select CONTEXT from BPM_TASK where TASK_ID='" + taskId + "'";
		CommonLogger.logger.info(sql);
		Map map = DaoFactory.getJdbc(ProcessClient.MODULE).queryForMap(sql);
		return (String)map.get("CONTEXT");
	}

}
