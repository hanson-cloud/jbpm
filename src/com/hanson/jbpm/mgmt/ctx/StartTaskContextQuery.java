package com.hanson.jbpm.mgmt.ctx;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.ccf.dao.DaoFactory;

public class StartTaskContextQuery extends ContextQuery {
	private String instId;
	
	public StartTaskContextQuery(String instId) {
		this.instId = instId;
	}

	public String getContext() throws BpmException {
		String sql = "select CONTEXT from BPM_TASK where INST_ID='" + instId + "' and PRE_TASKID=' '";
		String context = (String)DaoFactory.getJdbc(ProcessClient.MODULE).queryForMap(sql).get("CONTEXT");
		return context; 
	}
}
