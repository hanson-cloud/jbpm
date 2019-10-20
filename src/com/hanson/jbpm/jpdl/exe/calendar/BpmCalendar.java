package com.hanson.jbpm.jpdl.exe.calendar;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.log.CommonLogger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.util.calendar.DateUtil;
import com.suntek.eap.util.jdbc.Dialect;
import com.suntek.util.time.CurrentDateTime;

/**
 * ������������������Ĺ�����
 * Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�BpmCalendar.java
 * <p>ժ����Ҫ�����ڼ�������ڼ��յ�ʱ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�zhanghe
 * <p>������ڣ�2011-1-12
 */
@SuppressWarnings("unchecked")
public class BpmCalendar 
{
	/* ��¼�ڼ������ڼ���������Ҫ�Ӻ��Сʱ�� */
	private final static Map<String, Integer> holidays = new HashMap<String, Integer>();

	private final static BpmCalendar bpmCalendar = new BpmCalendar();
	
	private JdbcTemplate jdbc = DaoFactory.getJdbc("jbpm");
	private boolean holidayConfig = false;  //�Ƿ��нڼ������ñ�SCA_WORK_CALENDAR
	SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private BpmCalendar() {
		try {
			jdbc.queryForList("select count(1) from SCA_WORK_CALENDAR");
			holidayConfig = true;
		}catch (Exception e) {
			holidayConfig = false;
		}
	}
	
	
	public static BpmCalendar getInstance() {
		return bpmCalendar;
	}
	
	public Map<String, Integer> getHolidays() {
		return holidays;
	}
	
	
	public boolean hasHolidayConfig() {
		return holidayConfig;
	}

	/**
	 * �����������ݣ����������ڽ����ۼ�
	 */
	
	@SuppressWarnings("deprecation")
	public synchronized void load() {
		if (holidays.size() > 0 || !holidayConfig)
			return;

		// �Ӽ�Чģ����ȡ��������
		try {
			
			Calendar calS = Calendar.getInstance();
			Calendar calE = Calendar.getInstance();
			calS.add(Calendar.MONTH, -3);
			calE.add(Calendar.YEAR, 1);
			String sql =  "select DAY from SCA_WORK_CALENDAR " +
				"where DAY>=? and DAY<=? and DAY_TYPE in('0', '3') order by DAY";
			CommonLogger.logger.info("[�� SCA_WORK_CALENDAR�� ��ʼ���ڼ�����Ϣ(��һ������)...]" +
					sql.replace(">=?", "'" + calS.getTime().toLocaleString() + "'")
						.replace("<=?", "'" + calE.getTime().toLocaleString() + "'"));
			List list = jdbc.queryForList(sql, new Object[]{calS, calE});
			Map row;
			Date day;
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < list.size(); i++) {
				row = (Map) list.get(i);
				day = (Date) row.get("DAY");

				holidays.put(f.format(day), i);
			}
			CommonLogger.logger.info("[��ʼ���ڼ�����Ϣ���]" + holidays);
		} catch (Exception e) {
			CommonLogger.logger.info("[��ʼ���ڼ�����Ϣʧ��]" + e.getMessage());
		}
	}

	/**
	 * ά������֮�����¼��ع�������������
	 */
	public void reload() {
		holidays.clear();
		load();
	}
	
	
	public void clear() {
		if(holidays != null) {
			holidays.clear();
		}
	}

	/**
	 * ���ǽڼ��յ�������棬��������Ĵ���ʱ�ޡ��ӵ�ǰʱ�俪ʼ����
	 * 
	 * @param duetime	ʱ��
	 * @return 			����ʱ���ʽ��: yyyy-mm-dd hh:mi:ss
	 */
	public String computeDueTime(int duetime) {
		return computeDueTime(CurrentDateTime.getCurrentDateTime(), duetime);
	}
	
	/**
	 * ���ǽڼ��յ�������棬��������Ĵ���ʱ�ޡ��ӵ�ǰʱ�俪ʼ����
	 * @param starttime	��ʼʱ��
	 * @param duetime	ʱ��
	 * @return 			����ʱ���ʽ��: yyyy-mm-dd hh:mi:ss
	 */
	public String computeDueTime(String startTime, int duetime) {
		
		CommonLogger.logger.debug("[BpmCalendar]���㴦��ʱ��startTime:" + startTime + ", dueTime:" + duetime);
		
		String currentTime = startTime;
		String currentHour = currentTime.substring(11, 13); // ��ǰ��Сʱ
		int remainHour = 24 - Integer.parseInt(currentHour);// ����ʣ�����Сʱ
		String finishTime = currentTime; // �����ɵ�����
		boolean flag = true;
		while (duetime >= 0) {
			if (duetime < remainHour && flag) { // ���������ɾͲ��ÿ��Ǽ��յ�����
				//int lastHour = Integer.parseInt(currentHour) + duetime;
				finishTime = DateUtil.getPreDateTime(finishTime, DateUtil.HOUR,
						duetime);
				duetime -= remainHour;
			} else { // ������ɲ��ˣ������������ڼ���
				if (flag) {
					finishTime = DateUtil.getPreDateTime(finishTime,// ���һ��ʱ��ʹ���պõ���ڶ���Ŀ�ʼ
							DateUtil.HOUR, remainHour);
					duetime -= remainHour;
					flag = false;
				}
				if (holidays.containsKey(finishTime.substring(0, 10))) { // �����ڼ��ռ����Ӷ�һ��,duetime���ü�
					finishTime = DateUtil.getPreDateTime(finishTime,
							DateUtil.HOUR, 24);
				} else {
					if (duetime < 24) { // ����һ��ͼ���ʣ���Сʱ
						finishTime = DateUtil.getPreDateTime(finishTime,
								DateUtil.HOUR, duetime);
						duetime -= 24;
					} else { // ��������һ��
						finishTime = DateUtil.getPreDateTime(finishTime,
								DateUtil.HOUR, 24);
						duetime -= 24;
					}
				}
			}
		}
		//��ѿ�ʼʱ��ת��Ϊ23:59:59��ʽ
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(fm.parse(finishTime));
		} catch (ParseException e) {
			CommonLogger.logger.error(e, e);
			return null;
		}
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		finishTime = fm.format(cal.getTime());
		CommonLogger.logger.debug("[BpmCalendar]���㴦��ʱ�޽����" + finishTime);
		return finishTime;
	}
	
	
	/**
	 * ���ǽڼ��յ�������棬��������Ĵ���ʱ�ޡ��ӵ�ǰʱ�俪ʼ����
	 * @param starttime	��ʼʱ��
	 * @param duetime	ʱ��
	 * @return 			����ʱ���ʽ��: yyyy-mm-dd hh:mi:ss
	 */
	public String computeDueTimeWithTime(String startTime, int duetime) {
		CommonLogger.logger.debug("[BpmCalendar]���㴦��ʱ��startTime:" + startTime + ", dueTime:" + duetime);
		String currentTime = startTime;
		String currentHour = currentTime.substring(11, 13); // ��ǰ��Сʱ
		int remainHour = 24 - Integer.parseInt(currentHour);// ����ʣ�����Сʱ
		String finishTime = currentTime; // �����ɵ�����
		boolean flag = true;
		while (duetime >= 0) {
			if (duetime < remainHour && flag) { // ���������ɾͲ��ÿ��Ǽ��յ�����
				//int lastHour = Integer.parseInt(currentHour) + duetime;
				finishTime = DateUtil.getPreDateTime(finishTime, DateUtil.HOUR,
						duetime);
				duetime -= remainHour;
			} else { // ������ɲ��ˣ������������ڼ���
				if (flag) {
					finishTime = DateUtil.getPreDateTime(finishTime,// ���һ��ʱ��ʹ���պõ���ڶ���Ŀ�ʼ
							DateUtil.HOUR, remainHour);
					duetime -= remainHour;
					flag = false;
				}
				if (holidays.containsKey(finishTime.substring(0, 10))) { // �����ڼ��ռ����Ӷ�һ��,duetime���ü�
					finishTime = DateUtil.getPreDateTime(finishTime,
							DateUtil.HOUR, 24);
				} else {
					if (duetime < 24) { // ����һ��ͼ���ʣ���Сʱ
						finishTime = DateUtil.getPreDateTime(finishTime,
								DateUtil.HOUR, duetime);
						duetime -= 24;
					} else { // ��������һ��
						finishTime = DateUtil.getPreDateTime(finishTime,
								DateUtil.HOUR, 24);
						duetime -= 24;
					}
				}
			}
		}
		CommonLogger.logger.debug("[BpmCalendar]���㴦��ʱ�޽����" + finishTime);
		return finishTime;
	}
	
	
	/**
	 * ������������֮�������
	 * @param startTime ����ʱ���ʽ��: yyyy-mm-dd hh:mi:ss
	 * @param endTime ����ʱ���ʽ��: yyyy-mm-dd hh:mi:ss
	 */
	public int computeDueDays(String startTime, String endTime) {
		int days = 0;
		try {
			if(holidayConfig) {
				
					Dialect dl = ProcessEngine.getDialect();
					String sql = "select count(1) from SCA_WORK_CALENDAR where DAY >= " + 
					dl.todate("'" + startTime + "'" ) + " and DAY <= " + dl.todate("'" + endTime + "'") + 
					" and DAY_TYPE in ('1', '2')";
					CommonLogger.logger.debug("[��ȡ������]" + startTime + " ~ " + endTime + ":" + sql);
					days = jdbc.queryForInt(sql);
				
			}else {
				days = (int) ((fm.parse(endTime).getTime()-fm.parse(startTime).getTime())/(3600000 * 24));
			}
		}catch (Exception e) {
			CommonLogger.logger.error(e, e);
		}
		days = days==0 ? 1 : days;
		return days;
	}

	public static void main(String[] args) {
		
		
		System.setProperty("EAP_HOME", "d:/OpenEAP_318");
		System.setProperty("ECLIPSE_HOME", "d:/eclipse");
		System.setProperty("EAP_DEBUG", "true");
			
		//String a = CallConst.BILLID;
		System.out.println(BpmCalendar.getInstance().computeDueTime(48));
	}
}
