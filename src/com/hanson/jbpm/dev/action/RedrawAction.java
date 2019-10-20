package com.hanson.jbpm.dev.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.identity.User;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.util.jdbc.Dialect;
import com.hanson.jbpm.identity.Department;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.util.time.CurrentDateTime;

public class RedrawAction
{
	
	/**
	 * 
	 * @param instId
	 * @param taskId
	 * @param prevTaskId ��һ����ڵ�ID
	 * @param taskName  '�����س�'
	 * @param userId
	 * @param msg  �س�ԭ��
	 * @return
	 */
	public String execute(String instId, String taskId, String prevTaskId, String taskName, 
			String userId, String msg) {
		try {
			String cntSQL = "select count(1) cnt from BPM_TASK where PRE_TASKID='" + taskId + "' and DEAL_TIME is not null";
			cntSQL += " union select count(1) cnt from BPM_TASK where TASK_ID='" + taskId + "' and DEAL_TIME is not null";
			int cnt = 0;
			List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(cntSQL);
			for (int i=0; i<list.size(); i++) 
				cnt = cnt + Integer.parseInt(((Map)list.get(i)).get("cnt").toString());
			if (cnt >= 1)
				return "��ǰ�����ѱ�������֧�ֻس�����"; 
			
			cntSQL = "select count(1) from BPM_ASSIGN where TASK_ID='" + prevTaskId + "'";		
			cnt = DaoFactory.getJdbc(ProcessClient.MODULE).queryForInt(cntSQL);
			if (cnt == 0)
				return "��ǰ�����費֧�ֻس�����"; 
			
			User user = new SysUser(userId);
			String userName = user.getName();
			String deptId = "", deptName = "";
			Department[] dept = user.getDepts();
			if (dept.length > 0) {
				deptId = dept[0].getId();
				deptName = dept[0].getName();
			}
			
			Dialect dialect = ProcessEngine.getDialect();
				
			String returnMsg = "���Ϊ  " + instId + " �Ĺ����ѻس���";
			
			List<String> sqls = new ArrayList<String>();
			
			if (isFirstStep(instId)) {
				sqls.add("update BPM_TASK set TASK_NAME=PRE_TASKNAME where TASK_ID='" + taskId + "'");
				sqls.add("update BPM_ASSIGN set DEALER='" + userId + "' where TASK_ID='" + taskId + "'");
				sqls.add("update BPM_ASSIGN set DEAL_FLAG='0' where TASK_ID='" + taskId + "'");
				sqls.add("update BPM_TASK set TRANS_NAME='����' where TASK_ID='" + prevTaskId + "'");
				sqls.add("update BPM_INST set LAST_TASK_NAME=(select TASK_NAME from BPM_TASK where TASK_ID='" + 
						prevTaskId + "') where INST_ID='" + instId + "'");
				sqls.add("update BPM_BIZ set LAST_TASK_NAME=(select TASK_NAME from BPM_TASK where TASK_ID='" + 
						prevTaskId + "') where INST_ID='" + instId + "'");
			} else {
				sqls.add("delete from BPM_TASK where TASK_ID='" + taskId + "'");
				sqls.add("delete from BPM_ASSIGN where TASK_ID='" + taskId + "'");
				sqls.add("update BPM_ASSIGN set DEAL_FLAG='0' where TASK_ID='" + prevTaskId + "'");
				sqls.add("update BPM_TASK set DEAL_UID = null, DEAL_DEPTID = null, DEAL_FLAG='0', " +
						 	" DEAL_TIME = null" + ", TRANS_NAME='����' where TASK_ID='" + prevTaskId + "'");
				sqls.add("update BPM_INST set LAST_TASKID = '" + prevTaskId + 
						"', LAST_TASK_NAME=(select TASK_NAME from BPM_TASK where TASK_ID='" + 
						prevTaskId + "') where INST_ID='" + instId + "'");
				sqls.add("update BPM_BIZ set LAST_TASKID = '" + prevTaskId + 
						"', LAST_TASK_NAME=(select TASK_NAME from BPM_TASK where TASK_ID='" + 
						prevTaskId + "') where INST_ID='" + instId + "'");
			}
			
			String current = CurrentDateTime.getCurrentDateTime();
			
			sqls.add("insert into BPM_TASK_COMMENTS(TASK_ID, INST_ID, TASK_NAME, DEAL_UID, DEAL_UNAME," +
					 " DEAL_DEPTID, DEAL_DEPT, DEAL_TIME, CONTEXT) values ("
								  + "'" + prevTaskId + "', "
								  + "'" + instId + "', "
								  + "'" + taskName + "', "
								  + "'" + userId + "' ,"
								  + "'" + userName + "' ,"
								  + "'" + deptId + "' ,"
								  + "'" + deptName + "' ,"							  
								  + dialect.todate("'" + current + "'") +" ," 							  
								  + "'��������ԭ��:" + msg + "')");
						
			for (int i=0; i<sqls.size(); i++) CommonLogger.logger.debug(sqls.get(i));
			
			DaoFactory.getTransactionTemplate(ProcessClient.MODULE).batchUpdate(sqls.toArray(new String[sqls.size()]));
					
			return returnMsg;
		} catch (Exception ex) {
			CommonLogger.logger.error(ex,ex);
			return "���ع���ִ��ʧ��: " + ex.getMessage();
		}		
	}
	
	private boolean isFirstStep(String instId) {
		String cntSQL = "select count(1) from BPM_TASK where INST_ID='" + instId + "'";		
		int cnt = DaoFactory.getJdbc(ProcessClient.MODULE).queryForInt(cntSQL);
		return (cnt < 3) ;
	}
}
