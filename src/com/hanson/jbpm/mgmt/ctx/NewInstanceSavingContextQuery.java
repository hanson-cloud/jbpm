package com.hanson.jbpm.mgmt.ctx;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;

public class NewInstanceSavingContextQuery extends ContextQuery {
	private String userId;
	private String draftId;
	
	public NewInstanceSavingContextQuery(String userId, String draftId) {
		this.userId = userId;
		this.draftId = draftId;
	}
	
	public String getContext() throws BpmException {
		String sql = "select CONTEXT from BPM_TASK_DRAFT where DRAFT_ID='" + draftId + "'";
		
		CommonLogger.logger.debug(userId + "�������ѯ:" + sql);
		
		String text = "<a></a>";
		
		JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
		List list = jdbc.queryForList(sql);
		if (list.size()>0)
			text = (String)((Map)list.get(0)).get("CONTEXT");
		
		sql = "update BPM_TASK_DRAFT set DEALED='1' where DRAFT_ID='" + draftId + "' and DEALED<>'2'";
		CommonLogger.logger.debug(sql);
		jdbc.execute(sql);
		
		return text;
	}

}
