package com.hanson.jbpm.mgmt;

import java.util.HashMap;
import java.util.Map;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.def.BpmConstants;

public class TaskCurrentVisitorCache {
	
	private final static Map<String, String> visitorList = new HashMap<String, String>();
	
	/**
	 * �ӹ����б�˫���򿪱�ʱ����¼�����
	 * @param loginName
	 * @param instId
	 */
	public static void attend(String module, String loginName, String instanceId, String taskId) {
		String sql = "update BPM_TASK set LOCKED='" + BpmConstants.LOCKED + "', LOCK_USERID='" + loginName + "'" +
					 " where TASK_ID='" + taskId + "' and LOCKED is null";
		CommonLogger.logger.debug(sql);
		DaoFactory.getJdbc(module).execute(sql);

		if (!visitorList.containsKey(instanceId)) {
			CommonLogger.logger.debug(loginName + " ���빤��[" + instanceId + "] �������, �ܼ�¼�� " + visitorList.size());
			visitorList.put(instanceId, loginName+",");
		}
		else {
			String currentList = visitorList.get(instanceId);
			currentList = currentList.replace(loginName + ",", "") + loginName + ",";
			CommonLogger.logger.debug(loginName + " ���빤�� [" + instanceId + "] �������, ��¼����" + visitorList.size());
			visitorList.put(instanceId, currentList);
		}
	}
	
	/**
	 * �뿪ʱ�������Լ�֮�⣬û�б����ˣ���ɾ��
	 * @param loginName
	 * @param instId
	 */
	public static void leave(String module, String loginName, String taskId) {
		String sql = "update BPM_TASK set LOCKED=" + BpmConstants.UNLOCKED + ", LOCK_USERID=''" +
					 " where TASK_ID='" + taskId + "' and LOCKED='" + BpmConstants.LOCKED + "' and LOCK_USERID='" + loginName + "'";
		CommonLogger.logger.debug(sql);
		DaoFactory.getJdbc(module).execute(sql);

		CommonLogger.logger.debug(loginName + " �뿪���� [" + taskId + "] �������, ��¼���� " + visitorList.size());
		String currentList = visitorList.get(taskId);		
		currentList = currentList.replace(loginName + ",", "");		
		if (currentList.equals(""))
			visitorList.remove(taskId);
		else
			visitorList.put(taskId, currentList);
	}
	
	/**
	 * �����깤ʱ��ɾ��
	 * @param instId
	 */
	public static void finish(String instanceId) {
		CommonLogger.logger.debug("������� [" + instanceId + "] �鿴��Ա�б�, ��¼���� " + visitorList.size());
		visitorList.remove(instanceId);
	}
	
	/**
	 * ��ѯ��ǰ������б������Լ�֮���������
	 * @param loginName
	 * @param instId
	 * @return
	 */
	public static String get(String loginName, String instanceId) {
		String visitors = visitorList.get(instanceId);
		if (visitors == null)
			return "��";
		else {
			visitors = visitors.replace(loginName + ",", "");
			if (visitors.equals(""))
				return "��";
			else
				return visitors.substring(0, visitors.length()-1);
		}
	}
}
