package com.hanson.jbpm.mgmt;

import java.util.ArrayList;
import java.util.List;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.log.CommonLogger;

public class ProcessDetailLinkedQuery {
	public List getDetail(String instId) {
		return getDetail(instId, "");
	}
	
	public List getDetail(String instId, String orderType) {		
		/*String sql = "select a.BIZ_ID, a.TASK_ID, a.INST_ID, a.TASK_NAME, a.ASSIGN_DEPT, a.ASSIGN_UID," +
					 " a.ASSIGN_UNAME, a.DEAL_DEPT, a.DEAL_UNAME, a.DEAL_UID, a.DEAL_TIME, b.ASSIGN_TIME, b.ASSIGN_MEMO" +
					 " from BPM_TASK a, BPM_TASK b" +
					 " where a.TASK_ID=b.PRE_TASKID and a.INST_ID='" + instId + "'" +
					 " order by b.TASK_ID " + orderType;*/
		String sql = "select a.TASK_ID, a.INST_ID, a.TASK_NAME, a.ASSIGN_DEPT, a.ASSIGN_UID," +
					 " a.ASSIGN_UNAME, a.DEAL_DEPT, a.DEAL_UNAME, a.DEAL_UID, a.DEAL_TIME," +
					 " a.ASSIGN_TIME, a.ASSIGN_MEMO" +
					 " from BPM_TASK a" +
					 " where a.INST_ID='" + instId + "'" +
					 " order by a.TASK_ID " + orderType;
		CommonLogger.logger.info(sql);
		List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
		
		if (list.size()>0)
			return list;
		else
			return new ArrayList();
	}
	
	public List getDetailHis(String instId) {
		return getDetailHis(instId, "");
	}
	
	public List getDetailHis(String instId, String orderType) {
		/*String sql = "select a.BIZ_ID, a.TASK_ID, a.INST_ID, a.TASK_NAME, a.ASSIGN_DEPT, a.ASSIGN_TIME," +
					 " b.ASSIGN_MEMO, a.DEAL_UID," +
					 " a.ASSIGN_UNAME, a.ASSIGN_UID, a.DEAL_DEPT, a.DEAL_UNAME, a.DEAL_TIME " +
					 " from BPM_TASK_HIS a, BPM_TASK_HIS b" +
					 " where a.TASK_ID=b.PRE_TASKID and a.INST_ID='" + instId + "'" +
					 " order by a.TASK_ID " + orderType;*/
		String sql = "select a.TASK_ID, a.INST_ID, a.TASK_NAME, a.ASSIGN_DEPT, a.ASSIGN_TIME," +
					 " a.ASSIGN_MEMO, a.DEAL_UID," +
					 " a.ASSIGN_UNAME, a.ASSIGN_UID, a.DEAL_DEPT, a.DEAL_UNAME, a.DEAL_TIME " +
					 " from BPM_TASK_HIS a" +
					 " where a.INST_ID='" + instId + "'" +
					 " order by a.TASK_ID " + orderType;
		CommonLogger.logger.info(sql);
		List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
		if (list.size()>0)
			return list;
		else
			return new ArrayList();
	}	
}
