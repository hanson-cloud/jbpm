package com.hanson.jbpm.dev.action;

import java.lang.reflect.InvocationTargetException;

import com.hanson.jbpm.log.CommonLogger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.util.jdbc.Dialect;
import com.hanson.jbpm.dev.pico.PicoContainer;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.util.time.CurrentDateTime;


public class LockAction
{
	
	public static final int LOCK_OT = 30;   //������ʱʱ��, 30����
	
	/**
	 * ������������
	 * @param userId
	 * @return
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 */
	public String batchLock(String taskIds, String userId) {
		try {
			Dialect dialect = ProcessEngine.getDialect();
			//User user = new SysUser(userId);
			taskIds = taskIds.replace(",", "','");
			String[] sqls = new String[2];
			sqls[0] = "update BPM_TASK set LOCKED='" + BpmConstants.LOCKED + "', LOCK_USERID='" + userId + "'," +
						 " RECV_TIME=" + dialect.todate("'" + CurrentDateTime.getCurrentDateTime() + "'") +
						 " where TASK_ID in ('" + taskIds + "') ";
			sqls[1] = "update BPM_BIZ set LOCKED='" + BpmConstants.LOCKED + "', LOCK_USERID='" + userId + "'," +
						 " LOCK_TIME=" + dialect.todate("'" + CurrentDateTime.getCurrentDateTime() + "'") +
						 " where INST_ID in (select INST_ID from BPM_TASK WHERE TASK_ID in ('" + taskIds + "'))";
			for(String sql : sqls) {
				CommonLogger.logger.debug("[��������]" + sql);
			}
			DaoFactory.getJdbc(ProcessClient.MODULE).batchUpdate(sqls);
			
			if (PicoContainer.getInstance().containsComponent("WORKITEM_LOCKER"))
				PicoContainer.getInstance().getComponent("WORKITEM_LOCKER").invoke(new String[]{taskIds, userId});
			
			return "�����Ѿ�����";
		} catch (Exception ex) {
			CommonLogger.logger.error(ex,ex);
			return "������������ִ��ʧ��: " + ex.getMessage();
		}
	}
	
	/**
	 * ���������������
	 * @param taskId
	 * @param userId
	 * 
	 * �ӿڷ�����
	 * 		PicoContainer.getInstance().registerImplement(ProcessClient.UNLOCK_ACTION, clazz, methodName);
	 * 		clazz.method = function(bizId, userId)
	 * 
	 * @return
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 */
	@Deprecated
	public String batchUnlock(String taskIds, String userId) {
		try {
			//User user = new SysUser(userId);
			taskIds = taskIds.replace(",", "','");
			String sql = "select count(1) from BPM_TASK where TASK_ID IN ('" + taskIds + "') and LOCK_USERID='" + userId + "'";
			
			JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
			
			int cnt = jdbc.queryForInt(sql);
			
			if (cnt >0) {
				sql = "update BPM_TASK set LOCKED=" + BpmConstants.UNLOCKED + ", LOCK_USERID=''" +
					  " where TASK_ID in ('" + taskIds + "') and LOCK_USERID='" + userId + "'";
				CommonLogger.logger.debug(sql);
				jdbc.execute(sql);
				
				if (PicoContainer.getInstance().containsComponent(ProcessClient.UNLOCK_ACTION))
					PicoContainer.getInstance().getComponent(ProcessClient.UNLOCK_ACTION).invoke(new String[]{taskIds, userId});
				
				return "���������Ѿ����";
			} else 
				return "��ǰ���������������������޷����������ϵ��ǰ�����˽��";
		} catch (Exception ex) {
			CommonLogger.logger.error(ex,ex);
			return "������������ִ��ʧ��: " + ex.getMessage();
		}
		
	}
	
	
	/**
	 * 
	 * @param taskIds
	 * @return
	 */
	public String batchUnlock(String taskIds) {
		try {
			taskIds = taskIds.replace(",", "','");
			String[] sqls = new String[2];
			sqls[0] = "update BPM_TASK set LOCKED='0', LOCK_USERID=''," +
						 " RECV_TIME=null" +
						 " where TASK_ID in ('" + taskIds + "') ";
			sqls[1] = "update BPM_BIZ set LOCKED='0', LOCK_USERID=''," +
						 " LOCK_TIME=null" +
						 " where INST_ID in (select INST_ID from BPM_TASK WHERE TASK_ID in ('" + taskIds + "'))";
			for(String sql : sqls) {
				CommonLogger.logger.debug("[��������]" + sql);
			}
			DaoFactory.getJdbc(ProcessClient.MODULE).batchUpdate(sqls);
			
			if (PicoContainer.getInstance().containsComponent("WORKITEM_LOCKER"))
				PicoContainer.getInstance().getComponent("WORKITEM_LOCKER").invoke(new String[]{taskIds, ""});
			
			return "���������ɹ�";
		} catch (Exception ex) {
			CommonLogger.logger.error(ex,ex);
			return "������������ִ��ʧ��: " + ex.getMessage();
		}
	}
}
