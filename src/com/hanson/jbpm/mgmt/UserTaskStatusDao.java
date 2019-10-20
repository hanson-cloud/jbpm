package com.hanson.jbpm.mgmt;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.suntek.ccf.dao.DaoFactory;
import com.suntek.util.time.CurrentDateTime;

public class UserTaskStatusDao {
	public void setReadFlag(String taskId, String userId) {
		String sql = "update BPM_ASSIGN set READ_FLAG='" + BpmConstants.READFLAG+ "', " +
					 "READ_TIME = " + ProcessEngine.getDialect().todate("'" +
							 CurrentDateTime.getCurrentDateTime() + "'") + 
				 	 " where TASK_ID='" + taskId + "' and READ_FLAG='" + BpmConstants.INITFLAG + "'" +
				 	 " and DEALER='" + userId + "'";
		DaoFactory.getJdbc(ProcessClient.MODULE).execute(sql);
	}	
	
	public void setReadFlagByInstId(String instId, String userId) {
		String sql = "update BPM_ASSIGN set READ_FLAG='" + BpmConstants.READFLAG+ "', " +
					 "READ_TIME = " + ProcessEngine.getDialect().todate("'" + 
							 CurrentDateTime.getCurrentDateTime() + "'") + 
				 	 " where INST_ID='" + instId + "' and READ_FLAG='" + BpmConstants.INITFLAG + "'" +
				 	 " and DEALER='" + userId + "'";
		DaoFactory.getJdbc(ProcessClient.MODULE).execute(sql);
	}
}
