package com.hanson.jbpm.dev.action;

import com.hanson.jbpm.identity.*;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.util.jdbc.Dialect;
import com.suntek.util.time.CurrentDateTime;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

public class RecordTaskTrackAction {
	private String userName="", deptId="", deptName="";
	private JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
	
	public RecordTaskTrackAction() {
		
	}
	
	public RecordTaskTrackAction(String userName, String deptId, String deptName) {
		this.userName = userName;
		this.deptId = deptId;
		this.deptName = deptName;
	}
	
	public String execute(String userId, String taskId, String msg) {
		return execute(userId, taskId, null, msg);
	}
	
	public String execute(String userId, String taskId, String taskName, String msg) {
		return execute(userId, taskId, taskName, msg, null);
	}
	
	
	/**
	 * 
	 * @param userId ������
	 * @param taskId 
	 * @param taskName ����ڵ���
	 * @param msg  ��������
	 * @param callId  
	 * @param time ����ʱ�� yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public String execute(String userId, String taskId, String taskName, String msg, String callId, String time) {
		
		try {
			
			String sql = getExecuteSql(userId, taskId, taskName, msg, callId, time);
			CommonLogger.logger.debug("[��ӹ�������ۼ�]" + sql);
			jdbc.execute(sql);
			return "��¼��������ۼ����";
		} catch (Exception ex) {
			CommonLogger.logger.error(ex,ex);
			return "��¼��������ۼ�ִ��ʧ��: " + ex.getMessage();
		}		
	}
	
	
	public String execute(String userId, String taskId, String taskName, String msg, String callId) {
		String current = CurrentDateTime.getCurrentDateTime();
		return execute(userId, taskId, taskName, msg, callId, current);
	}
	
	
	public String getExecuteSql(String userId, String taskId, String taskName, String msg, String callId) {
		return getExecuteSql(userId, taskId, taskName, msg, callId, CurrentDateTime.getCurrentDateTime());
	}
	
	
	public String getExecuteSql(String userId, String taskId, String taskName, String msg, String callId, String time) {
		String sql = "select INST_ID, TASK_NAME from BPM_TASK where TASK_ID = ?";
		
		Map row = null;
		String isHis = "";
		try {
			row = jdbc.queryForMap(sql, new String[]{taskId});
		}catch(Exception ex) {
			sql = "select INST_ID, TASK_NAME from BPM_TASK_HIS where TASK_ID = ?";
			row = jdbc.queryForMap(sql, new String[]{taskId});
			isHis = "_HIS";
		}
		String instId = (String) row.get("INST_ID");
		
		if (taskName == null) {
			taskName = (String) row.get("TASK_NAME");
		}
		if (callId == null) {
			callId = "";
		}
		return getExecuteSql(userId, instId, taskId, taskName, msg, callId, time, isHis);
	}
	
	public String getExecuteSql(String userId, String instId, String taskId, String taskName, String msg, String callId, String time, String isHis) {
		
		Dialect dialect = ProcessEngine.getDialect();
		
		getUserInfo(userId);
		
		
		String sql = "insert into BPM_TASK_COMMENTS" + isHis + " (TASK_ID, INST_ID, CALL_ID, " +
			  " TASK_NAME, DEAL_UID, DEAL_UNAME, DEAL_DEPTID, DEAL_DEPT," +
			  " DEAL_TIME, CONTEXT) values ("
					  + "'" + taskId + "', "
					  + "'" + instId + "', "
					  + "'" + callId + "', "
					  + "'" + taskName + "', "
					  + "'" + userId + "' ,"
					  + "'" + userName + "' ,"
					  + "'" + deptId + "' ,"
					  + "'" + deptName + "' ,"							  
					  + dialect.todate("'" + time + "'") +" ,"							  
					  + "'" + msg + "')";
		
		return sql;
	}
	
	
	private void getUserInfo(String userId) {
		if("".equals(userName) && "".equals(deptName)) {
			if (userId.startsWith("D")) {
				Department dept = new SysDepartment(userId);
				userName = dept.getName();
				deptId = userId.substring(1);
				deptName = userName;			
				return;
			}
			
			if (userId.startsWith("R")) {
				Role role = new SysRole(userId);
				userName = role.getName();
				return;
			}
			
			User user = new SysUser(userId);
			userName = user.getName();		
			Department[] dept = user.getDepts();
			if (dept.length > 0) {
				deptId = dept[0].getId();
				deptName = dept[0].getName();
			}
		}
	}
}
