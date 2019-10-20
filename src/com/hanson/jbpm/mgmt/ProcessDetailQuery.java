package com.hanson.jbpm.mgmt;

import java.util.ArrayList;
import java.util.List;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;

public class ProcessDetailQuery {
	public List getDetail(String instId) {
		return getDetail(instId, "");
	}
	
	public List getDetail(String instId, String orderType) {		
		String sql = "select TASK_ID, INST_ID, TASK_NAME, PROC_NAME, DEAL_TIME" +
					 " from BPM_TASK where INST_ID='" + instId + "' order by TASK_ID " + orderType;
		CommonLogger.logger.debug(sql);
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
		String sql = "select * from BPM_TASK_HIS where INST_ID='" + instId + "'" +
					 " and DEAL_TIME is not null order by TASK_ID " + orderType;
		CommonLogger.logger.debug(sql);
		List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
		if (list.size()>0)
			return list;
		else
			return new ArrayList();
	}
	
	/*public Map getTaskInfo(String taskId) {
		String sql = "select * from BPM_TASK where TASK_ID='" + taskId + "'";
		return DaoFactory.getJdbc(ProcessClient.MODULE).queryForMap(sql);
	}*/
}
