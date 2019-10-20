package com.hanson.jbpm.mgmt.ctx;

import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.ccf.dao.DaoFactory;

public class LastSameTaskOrSavingCtxQuery extends ContextQuery {
	private String instId;
	private String taskId;
	private String taskName;
	private String userId;
	
	public LastSameTaskOrSavingCtxQuery(String instId, String taskId, String taskName, String userId, 
			String queryByTaskId) {
		this.instId = instId;
		this.taskId = taskId;
		this.taskName = taskName;
		this.userId = userId;
		this.queryByTaskId = queryByTaskId;
	}
	
	public String getContext() throws BpmException {
		if (queryByTaskId != null)
			return getContextByTaskId();
		else
			return getContextByTaskName();
	}
	
	private String getContextByTaskName() throws BpmException {		
		String sql = "select CONTEXT from BPM_TASK_MEMO where TASK_ID='" + taskId + "'" +
					" and DEAL_UID='" + userId + "' and MEMO_TYPE='save'";
		CommonLogger.logger.debug(sql);
		List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
		if (list.size() == 0) {
			sql = "select CONTEXT from BPM_TASK_EXT a, BPM_TASK b" +
				  " where a.INST_ID='" + instId + "' and a.TASK_ID=b.TASK_ID" +
			 	  " and TASK_NAME='" + taskName + "' and OVERTIME is not null" + //"' and DEAL_TIME is not null" + �����л��˵Ĺ���
			 	  " order by a.TASK_ID desc";
			CommonLogger.logger.debug(sql);
			list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
		}						
		String text = "<a></a>";
		Map row;
		for (int i=0; i<list.size(); i++) {
			row = (Map)list.get(0);
			if (row.get("CONTEXT") == null)
				continue;
			return (String)row.get("CONTEXT");
		}
		return text;
	}
	
	private String getContextByTaskId() throws BpmException {	
		List list;
		String sql;
		if (this.taskId == null) {
			sql = "select CONTEXT from BPM_TASK_EXT where INST_ID=? order by TASK_ID";
			list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql, new String[]{instId});
		} else {
			sql = "select CONTEXT from BPM_TASK_EXT where INST_ID=? and TASK_ID=?";
			list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql, new String[]{instId, taskId});
		}
		CommonLogger.logger.debug(sql);				
		String text = "<a></a>";
		Map row;
		for (int i=0; i<list.size(); i++) {
			row = (Map)list.get(i);
			text = (String)row.get("CONTEXT");
			if (text != null)
				return text;
		}
		return text;
	}
}
