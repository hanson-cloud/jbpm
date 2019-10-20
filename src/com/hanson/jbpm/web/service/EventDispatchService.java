package com.hanson.jbpm.web.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.jpdl.def.event.EventInfo;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.event.Event;
import com.hanson.jbpm.jpdl.def.event.EventTargetUser;
import com.hanson.jbpm.jpdl.exe.impl.EventHandler;
import com.hanson.jbpm.log.LoggerUtil;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * �첽�¼��ַ�, �糬ʱ�¼��ȵ�, �¼���ѵ������jbpm.war
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�DispatchEventService.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Nov 18, 2013
 */
@SuppressWarnings("unchecked")
public class EventDispatchService extends HttpRequestService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4080816187880170568L;
	private JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
			String processName = request.getParameter("processName");
			String className = getEventClass(processName);
			String eventType = request.getParameter("type");
			if(className != null) {
				if(EventInfo.TYPE_OVERTIME.equals(eventType)) {
					fireOrverTimeEvent(processName, className);
				}
				if(EventInfo.TYPE_ARCHIVE.equals(eventType)) {
					fireArchiveEvent(processName, className, request);
				}
			
			}else {
				LoggerUtil.getLogger().debug("[û������ע���¼�]");
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().error(e, e);
		}
		
	}
	
	
	/**
	 * ������ʱ�¼�
	 * @param processName
	 * @param eventInfo
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private void fireOrverTimeEvent(String processName, String className) {
		try {
			EventInfo eventInfo = buildEventInfo(processName, EventInfo.TYPE_OVERTIME);
			String sql = "select DEALER, sum(EARLYWARN_COUNT) SUM_EARLYWARN, " +
					"sum(WARN_COUNT)  SUM_WARN, sum(SERIOUSWARN_COUNT) SUM_SERIOUSWARN " +
					"from  (select a.DEALER,  " +
					" decode(b.DEAL_MONITOR,'Ԥ��',1,0) as EARLYWARN_COUNT, " +
					" decode(b.DEAL_MONITOR,'�澯',1,0) as WARN_COUNT, " +
					" decode(b.DEAL_MONITOR,'���ظ澯',1,0) as SERIOUSWARN_COUNT " +
					" from BPM_ASSIGN a,BPM_BIZ b  where a.DEAL_FLAG = '0' and a.CC_FLAG = '0' " +
					" and a.TASK_ID = b.LAST_TASKID "+
					" and exists(select 1 from BPM_BIZ c where c.INST_ID=a.INST_ID and c.PROCESS_NAME=?))  " +
					" where (EARLYWARN_COUNT+WARN_COUNT+SERIOUSWARN_COUNT)>0 group by DEALER";
			
			LoggerUtil.getLogger().debug("[��ѯ��ʱͳ��]" + sql.replace("?", "'" + processName + "'"));
			List<Map> list = jdbc.queryForList(sql, new String[]{processName});
			LoggerUtil.getLogger().debug("[��ѯ��ʱͳ��]���:" + list);
			
			String[] userIds = new String[list.size()];
			int[] ew = new int[list.size()];
			int[] w = new int[list.size()];
			int[] sw = new int[list.size()];
			for(int i=0;i<list.size();i++) {
				Map map = list.get(i);
				userIds[i] = (String) map.get("DEALER");
				ew[i] = Integer.parseInt(String.valueOf(map.get("SUM_EARLYWARN")));
				w[i] = Integer.parseInt(String.valueOf(map.get("SUM_WARN")));
				sw[i] = Integer.parseInt(String.valueOf(map.get("SUM_SERIOUSWARN")));
			}
			EventTargetUser[] tUsers = EventTargetUser.newMutilInstance(userIds, ew, w, sw);
			eventInfo.setTargetUsers(tUsers);
			EventHandler h = (EventHandler) Class.forName(className).newInstance();
			h.handle(eventInfo);
		
		} catch (Exception e) {
			LoggerUtil.getLogger().error(e, e);
		}
	}
	
	
	private void fireArchiveEvent(String processName, String className, HttpServletRequest request) {
		try {
			String endDate = request.getParameter("endDate");
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
			EventInfo eventInfo = buildEventInfo(processName, EventInfo.TYPE_ARCHIVE);
			eventInfo.setArchiveLastDate(date);
			EventHandler h = (EventHandler) Class.forName(className).newInstance();
			h.handle(eventInfo);
		} catch (Exception e) {
			LoggerUtil.getLogger().error(e, e);
		}
	}
	
	
	
	private EventInfo buildEventInfo(String processName, String type) {
		EventInfo eventInfo = new EventInfo();
		eventInfo.setEventType(type);
		eventInfo.setProcessName(processName);
		return eventInfo;
	}
	
	
	/**
	 * 
	 * @param processName
	 * @return
	 */
	private String getEventClass(String processName) {
		Event event = ProcessEngine.getProcessDefinition(processName).getEvent();
		if(event != null) {
			return event.getClassName();
		}else {
			return null;
		}
		
	}

}
