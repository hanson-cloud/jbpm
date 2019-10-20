package com.hanson.jbpm.dev.action;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.exe.calendar.BpmCalendar;
import com.hanson.jbpm.jpdl.exe.util.DateUtils;
import com.hanson.jbpm.log.CommonLogger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.util.jdbc.Dialect;
import com.hanson.jbpm.jpdl.exe.util.DialectUtil;
import com.hanson.jbpm.mgmt.ProcessClient;

public class SetTimeLimitAction {
	
	private JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
	private Dialect dialect = ProcessEngine.getDialect();
	private SimpleDateFormat format = DateUtils.getDateTimeFormat();
	private String startTime = null;
	
	public static final String TASK_NAME = "���ڴ���";
	
	
	public SetTimeLimitAction() {
		
	}
	
	
	/**
	 * ָ����ʼ���ڹ���
	 * @param startTime
	 */
	public SetTimeLimitAction(String startTime) {
		this.startTime = startTime;
	}
	
	
	/**
	 * ͨ��ָ����������
	 * @param instId
	 * @param taskId
	 * @param dueDays
	 * @param userId
	 * @param reason
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String execute(String instId, String taskId, int dueHour, String userId, String reason) {
		int dueDays = dueHour / 24;
		dueDays = dueDays==0 ? 1 : dueDays;
		try {
			String startTime = getDueTime(taskId);
			String dueTime = BpmCalendar.getInstance().computeDueTime(startTime, dueHour);
			update(instId, taskId, dialect.todate("'" + dueTime + "'"), dueDays, userId, reason);
			return "���ڴ���ɹ�!";
		}catch (Exception e) {
			CommonLogger.logger.error(e, e);
			return "���ڴ���ʧ��!" + e.getMessage();
		}
	}
	
	/**
	 * ͨ��ָ����������
	 * @param instId
	 * @param taskId
	 * @param dueTime �������� yyyy-MM-dd HH:mm:ss��ʽ
	 * @param userId
	 * @param reason
	 * @return
	 */
	public String execute(String instId, String taskId, String dueTime, String userId, String reason) {
		try {
			String startTime = getDueTime(taskId);
			int dueDays = BpmCalendar.getInstance().computeDueDays(startTime, dueTime);
			if(dueDays > 0) {
				update(instId, taskId, dialect.todate("'" + dueTime + "'"), dueDays, userId, reason);
			}
			return "���ڴ���ɹ�!";
		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
			return "���ڴ���ʧ��!" + e.getMessage();
		}
		
	}
	
	
	private void update(String instId, String taskId, String dueTime, int dueDays, String userId, String reason) {
		String[] sqls = new String[3];
		sqls[0] = "update BPM_TASK set DUE_TIME=" + dueTime + 
			" where TASK_ID='" + taskId + "' and INST_ID='" + instId + "'";
		sqls[1] = "update BPM_BIZ set DEAL_DEADLINE = " + dueTime + 
			", DEAL_MONITOR='����', IS_DELAYED = '1', DEADLINE_DAYS=DEADLINE_DAYS+" + 
			dueDays + " where INST_ID='" + instId + "'";
		sqls[2] = new RecordTaskTrackAction().getExecuteSql(userId, taskId, TASK_NAME, reason, null);
		for(String sql : sqls) {
			CommonLogger.logger.debug("[����ʱ��]" + sql);
		}
		
		jdbc.batchUpdate(sqls);
		
	}
	
	
	/**
	 * ��ȡ��ǰ��������
	 * @param taskId
	 * @return
	 */
	private String getDueTime(String taskId) {
		if(this.startTime != null) {
			return this.startTime;
		}
		Map map = jdbc.queryForMap("select DUE_TIME from BPM_TASK where TASK_ID=?", 
				new String[]{taskId});
		String startTime = format.format(map.get("DUE_TIME"));
		return startTime;
	}
	
	
	/**
	 * ���Сʱ��
	 * @param field
	 * @param hour
	 * @return
	 */
	private String addDate(String field, int hour) {
		if(DialectUtil.isOracle(dialect)) {
			return field + hour + "/24";
		}else if(DialectUtil.isMySQL(dialect)) {
			return "DATE_ADD(" + field + ", INTERVAL " + hour + " HOUR)";
		}else {
			return "dateadd(hour, " + hour + ", " + field + ")";
		}
	}

}
