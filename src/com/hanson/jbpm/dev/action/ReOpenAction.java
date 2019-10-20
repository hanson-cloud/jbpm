package com.hanson.jbpm.dev.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.exe.ctx.State;
import com.hanson.jbpm.log.CommonLogger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.identity.Department;
import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.identity.User;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.exe.util.IdentityCreator;
import com.hanson.jbpm.mgmt.ProcessClient;

public class ReOpenAction
{
	private JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
	
	private enum CloseType { CLOSED, NORMAL_ARCHIVED, EXCEPT_ARCHIVE};
	
	private String lastTaskId, lastTaskName;
	private String deptId = "";
	
	private CloseType closedType = CloseType.CLOSED;
	
	
	public String resume(String instanceId) {
		String sql = "update BPM_INST set DELETED='0' where INST_ID='" + instanceId + "'";
		DaoFactory.getJdbc(ProcessClient.MODULE).update(sql);
		return "ɾ�������ѻָ���ԭ�����˴���";
	}
	
	
	
	public String execute(String instId, String oriTaskId, String processName, String userCode, String msg)  {
		if (msg == null) {
			msg = "δ��д���´�ԭ��.";
		}
		try {
			closedType = checkNormalArchived(instId);
			
			findLastTaskIdAndName(instId);
			
			//String toTaskStat = (this.lastTaskName == null)?"":" and TASK_NAME = '" + this.lastTaskName + "' ";
			
			findUserDepartment(userCode);
				
			List<String> sqlList = new ArrayList<String>();
			List<Object[]> sqlPara = new ArrayList<Object[]>();
			
			String newTaskId = IdentityCreator.getTaskId(5);
			
			if (closedType != CloseType.CLOSED) {
				sqlList.add("update BPM_INST_HIS set CLOSE_TIME = null, CLOSE_UID = null," +
							" CLOSE_TYPE = '" + State.CLOSED + "', LAST_TASKID='" + this.lastTaskId + "'" +
							" where INST_ID = ?");
				sqlPara.add(new String[]{ instId });
				
				//sqlList.add("update BPM_TASK_HIS set DEAL_TIME = NULL, DEAL_UID=?, DEAL_DEPTID = ?" +
				//			" where TASK_ID = ?");
				//sqlPara.add(new String[]{ this.getUserCode(), deptId, this.lastTaskId });
				
				sqlList.add("insert into BPM_INST(" + BpmConstants.INST_FIELDS + ")" +
							" select " + BpmConstants.INST_FIELDS +
							" from BPM_INST_HIS where INST_ID = ?");
				sqlPara.add(new String[]{ instId });
				
				//sqlList.add("insert into BPM_TASK(" + Constants.TASK_FIELDS + ")" +
				//			" select " + Constants.TASK_FIELDS + " from BPM_TASK_HIS" +
				//			" where INST_ID=? and TASK_ID <> ?");	
				//sqlPara.add(new String[]{ instId, this.lastTaskId });
				
				sqlList.add("insert into BPM_TASK(" + BpmConstants.TASK_FIELDS + ")" +
							" select " + BpmConstants.TASK_FIELDS + 
							" from BPM_TASK_HIS where INST_ID=? and TASK_NAME<>'����'");
				sqlPara.add(new String[]{ instId });
				
				sqlList.add("insert into BPM_TASK_EXT(" + BpmConstants.TASK_EXT_FIELDS + ")" +
						" select " + BpmConstants.TASK_EXT_FIELDS + 
						" from BPM_TASK_EXT_HIS where INST_ID=? ");
				sqlPara.add(new String[]{ instId });
				
				sqlList.add("insert into BPM_TASK_COMMENTS(" + BpmConstants.TASK_COMMENTS_FIELDS + ")" +
						" select " + BpmConstants.TASK_COMMENTS_FIELDS + 
						" from BPM_TASK_COMMENTS_HIS where INST_ID=? ");
				sqlPara.add(new String[]{ instId });
				
				sqlList.add("insert into BPM_ASSIGN(" + BpmConstants.ASSIGN_FIELDS + ")" +
						" select " + BpmConstants.ASSIGN_FIELDS + 
						" from BPM_ASSIGN_HIS where INST_ID=? ");
				sqlPara.add(new String[]{ instId });
				
				
				sqlList.add("delete from BPM_INST_HIS where INST_ID = ?");
				sqlPara.add(new String[]{ instId });
				
				sqlList.add("delete from BPM_TASK_HIS where INST_ID = ?");
				sqlPara.add(new String[]{ instId });
				
				sqlList.add("delete from BPM_TASK_EXT_HIS where INST_ID = ?");
				sqlPara.add(new String[]{ instId });
				sqlList.add("delete from BPM_TASK_COMMENTS_HIS where INST_ID = ?");
				sqlPara.add(new String[]{ instId });
				sqlList.add("delete from BPM_ASSIGN_HIS where INST_ID = ?");
				sqlPara.add(new String[]{ instId });
			} else {				
				sqlList.add("update BPM_INST set CLOSE_TIME = null, CLOSE_UID = null," +
							" CLOSE_TYPE = '" + State.CLOSED + "', LAST_TASKID ='" + newTaskId + 
							"', LAST_TASK_NAME='" + lastTaskName + "'" +
							" where INST_ID = ?");
				sqlPara.add(new String[]{ instId });
				//sqlList.add("update BPM_TASK set DEAL_TIME = NULL, DEAL_UID = ?, DEAL_DEPTID = ?" +
				//			" where TASK_ID = ?");
				//sqlPara.add(new String[]{ this.getUserCode(), deptId, lastTaskId });
			}
			
			if (this.closedType == CloseType.CLOSED) {				
				//String current = ProcessEngine.getDialect().sysdate();
				//String dueTime = ProcessEngine.getDialect().dateadd(current, 1, Dialect.TYPE_HOUR);
				
				Calendar cal = Calendar.getInstance(); 
				cal.add(Calendar.DATE, 1);
				Date dueTime = cal.getTime();
				
				sqlList.add("insert into BPM_TASK(TASK_ID, INST_ID, PROC_NAME, PRE_TASKID, PRE_TASKNAME, TASK_NAME," +
							" ASSIGN_UID, ASSIGN_DEPTID, ASSIGN_TIME, DUE_TIME, DEAL_UID, DEAL_DEPTID, DEAL_TIME," +
							" TRANS_NAME, OVERTIME, LOCKED, LOCK_USERID, REMINDED, REMIND_USERID, REMIND_MSG)" +
							" select ?, INST_ID, PROC_NAME, TASK_ID, TASK_NAME, ?, ?, ?, ?, ?, NULL, NULL, NULL, " +
							" TRANS_NAME, '0', NULL, NULL, '0', NULL, ''" +
							" from BPM_TASK" +
							" where TASK_ID = ?");
				sqlPara.add(new Object[]{ newTaskId, lastTaskName, userCode, deptId, new Date(), dueTime, lastTaskId });
				
				sqlList.add("insert into BPM_TASK_EXT ( TASK_ID, INST_ID, ASSIGN_MEMO, PASS_NODE, FORK_ID, CONTEXT )" +
					  		" values(?, ?, ?, ?, ?, ?)");
				sqlPara.add(new String[]{ newTaskId, instId, "���´��˹���,ԭ��:" + msg, "���´򿪹���", "", " " });
				
				//sqlList.add("update BPM_TASK set TASK_NAME='" + this.lastTaskName + "' where INST_ID=? and TASK_NAME='����'");
				//sqlPara.add(new String[]{ instId});
				
				sqlList.add("insert into BPM_ASSIGN(INST_ID, TASK_ID, DEALER, CC_FLAG, READ_FLAG, DEAL_FLAG, PROC_NAME, TASK_NAME)" +
						  	"SELECT ?, ?, ?, '0', '0', '0', PROC_NAME, '" + lastTaskName + "' from BPM_INST where INST_ID=?");
				sqlPara.add(new String[]{ instId, newTaskId, userCode, instId });
			} else {
				sqlList.add("insert into BPM_ASSIGN(INST_ID, TASK_ID, DEALER, CC_FLAG, READ_FLAG, DEAL_FLAG, PROC_NAME, TASK_NAME)" +
			  				"SELECT ?, ?, ?, '0', '0', '0', PROC_NAME, '" + lastTaskName + "' from BPM_INST where INST_ID=?");
				sqlPara.add(new String[]{ instId, lastTaskId, userCode, oriTaskId });
			}
						
//			sqlList.add("insert into BPM_TASK_COMMENTS(TASK_ID, INST_ID, CALL_ID, TASK_NAME," +
//						" DEAL_UID, DEAL_UNAME, DEAL_DEPTID, DEAL_DEPT, DEAL_TIME, CONTEXT)" +
//						" values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
//			sqlPara.add(new Object[]{ lastTaskId, instId, "", "���´�", 
//						this.getUserCode(),  OrgCache.getCache().getUser(this.getUserCode()), 
//						deptId, OrgCache.getCache().getDept(deptId), new Date(), 
//						this.getUserCode() + "���´��˹���,ԭ��:" + msg });
			
			//if (PicoContainer.getInstance().containsComponent(ProcessClient.REOPEN_ACTION))
			//	PicoContainer.getInstance().getComponent(ProcessClient.REOPEN_ACTION).invoke(new String[]{bizId, processName});
			
			String[] sqls = new String[sqlList.size()];
			sqlList.toArray(sqls);
			Object[][] params = new Object[sqlPara.size()][];
			sqlPara.toArray(params);
			
			for (int i=0; i<sqls.length; i++)
				CommonLogger.logger.debug(sqls[i] + "; " + StringUtils.arrayToDelimitedString(params[i], ","));
			
			DaoFactory.getTransactionTemplate(ProcessClient.MODULE).batchUpdate(sqls, params);
						
			return "�����Ѵ򿪣����½��봦������";
		} catch (Exception ex) {
			CommonLogger.logger.error(ex,ex);
			return ("���´򿪹���ִ���쳣: " + ex.getMessage());
		}		
	}
	
	private void findLastTaskIdAndName(String instId) 
	{
		String sql;
		if (this.closedType == CloseType.CLOSED) {
			sql = "select TASK_ID, TASK_NAME from BPM_TASK where INST_ID = ? order by TASK_ID desc";
		} else {
			sql = "select TASK_ID, TASK_NAME from BPM_TASK_HIS where INST_ID = ? order by TASK_ID desc";
		}
	    Map row;
	    int returnStepIndex;
	    List list = jdbc.queryForList(sql, new String[]{ instId });
	    if (list.size() <= 3) returnStepIndex = list.size()-1;
	    else returnStepIndex = list.size()-2;
	    row = (Map) list.get(returnStepIndex);
	    this.lastTaskId = (String) row.get("TASK_ID");
	    this.lastTaskName = (String) row.get("TASK_NAME");
	}
	
	private void findUserDepartment(String userCode)
	{
		User user = new SysUser(userCode);
		Department[] depts = user.getDepts();		
		if (depts.length>0) {
			deptId = depts[0].getId();
			//deptName = depts[0].getName();
		}
	}
	
	private CloseType checkNormalArchived(String instId) {		
		String sql = "select CLOSE_TYPE from BPM_INST_HIS where INST_ID = ?";
		List list = jdbc.queryForList(sql, new String[]{ instId });
		if (list.size() > 0) {
			String closeType = (String) ((Map) list.get(0)).get("CLOSE_TYPE");
			if (closeType.equals(State.CLOSED))
				return CloseType.NORMAL_ARCHIVED;
			else
				return CloseType.EXCEPT_ARCHIVE;
		} else {
			return CloseType.CLOSED;
		}
	}
	
	private String readReopenToTaskConfig(String processName) {
	    try{
	    	String sql = "select REOPEN_TOTASK from BPM_FLOW where FLOW_NAME='" + processName + "' and MODULE_NAME='"+ProcessClient.MODULE+"'";
	    	return (String) DaoFactory.getJdbc(ProcessClient.MODULE).queryForMap(sql).get("REOPEN_TOTASK");	
	    }catch(EmptyResultDataAccessException e){
	    	return null;
	    }
	}
	
	private boolean checkFinishedAndArchived(String instanceId) {		
		String sql = "select count(1) from BPM_INST where INST_ID='" + instanceId + "'";
		return (DaoFactory.getJdbc(ProcessClient.MODULE).queryForInt(sql) == 0);	
	}
}
