package com.hanson.jbpm.dev.action;

import java.util.ArrayList;
import java.util.List;

import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * ������������� Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>
 * All rights reserved.
 * <p>
 * ��Ŀ���ƣ�com.suntek.jbpm
 * <p>
 * �ļ����ƣ�DelegateSetAction.java
 * <p>
 * ժ����Ҫ��
 * <p>
 * ��ǰ�汾��1.0
 * <p>
 * �������ߣ�zhout
 * <p>
 * ������ڣ�2011-1-28
 */
public class DelegateSetAction
{
	/**
	 * ���� taskids �������ִ�д�����Ϊ userid
	 * 
	 * @param taskId ����id
	 * @param userId ������
	 * @return
	 */
	public String execute(String taskIds, String oldUserId, String userId)
	{
		try
		{
			taskIds = taskIds.replace(",", "','");
			String userName = new SysUser(userId).getName();
			String[] sql = new String[2];
			sql[1] = "update BPM_TASK set DEALER='" + userName + "' where TASK_ID in ('" + taskIds + "')";

			if(oldUserId == null)
			{
				sql[0] = "update BPM_ASSIGN set DEALER='" + userId + "',DEAL_FLAG='0'" + 
						 " where TASK_ID in ('" + taskIds + "')";
			}
			else
			{
				sql[0] = "update BPM_ASSIGN set DEALER='" + userId + "',DEAL_FLAG='0'" + 
						 " where TASK_ID in ('" + taskIds + "') and DEALER='" + oldUserId + "'"; 
			}

			DaoFactory.getTransactionTemplate(ProcessClient.MODULE)
			.batchUpdate(sql);
			return "��ǰ������ָ�ɸ� " + userName;
		}
		catch(Exception ex)
		{
			CommonLogger.logger.error("�������������ʧ��: " + ex);
			return ex.getMessage();
		}
	}

	public String batchExecute(String taskIds, String userIds)
	{
		try
		{
			String updateBpmTaskSql;
			String updateBpmAssignSql;
			List<String> sqlList = new ArrayList<String>();
			String taskIdArray[] = taskIds.split(",");
			String userIdArray[] = userIds.split(",");
			int taskIdAmount = taskIdArray.length;//��Ҫָ������������
			int userIdAmount = userIdArray.length;//������ϯ������

			if(taskIdAmount < 1 || userIdAmount<1){
				return "��Ҫָ�����������߷�����ϯ������Ϊ��!";
			}

			int singleUserTaskAmout =   (taskIdAmount / userIdAmount);
			if(taskIdAmount > userIdAmount ){//��Ҫָ������������    ����   ������ϯ������
				if(taskIdAmount != singleUserTaskAmout * userIdAmount ){//���"��Ҫָ������������"����"������ϯ������"��������
					singleUserTaskAmout += 1;//��100��taskIds��3��userIdsΪ����ǰ����userId���ֵ�34��.
				}

				for(int i = 0; i < userIdArray.length; i++){
					int taskIndex = singleUserTaskAmout * i;//��100��taskIdsΪ������һ��userId�ֵ�0-33,�ڶ���userId�ֵ�34-77
					StringBuilder taskIdBuilder = new StringBuilder("");


					for(int j = taskIndex; j < singleUserTaskAmout * (i + 1)
					&& j < taskIdArray.length; j++){
						taskIdBuilder.append("'"+taskIdArray[j]+"',");
					}

					String taskIdStr = taskIdBuilder.substring(0,taskIdBuilder.length()-1);
					updateBpmTaskSql = "update BPM_TASK set DEALER='"
						+ userIdArray[i] + "' where TASK_ID in ("
						+ taskIdStr + ")";

					sqlList.add(updateBpmTaskSql);
					updateBpmAssignSql = "update BPM_ASSIGN set DEALER='"
						+ userIdArray[i] + "',DEAL_FLAG='0'"
						+ " where TASK_ID in ("
						+ taskIdStr + ")";
					sqlList.add(updateBpmAssignSql);
				}
			}else{//��Ҫָ������������    С�� ���ߵ���   ������ϯ������
				for(int i = 0; i < taskIdArray.length; i++){
					updateBpmTaskSql = "update BPM_TASK set DEALER='"
						+ userIdArray[i] + "' where TASK_ID in ('"
						+ taskIdArray[i] + "')";

					sqlList.add(updateBpmTaskSql);
					updateBpmAssignSql = "update BPM_ASSIGN set DEALER='"
						+ userIdArray[i] + "',DEAL_FLAG='0'"
						+ " where TASK_ID in ('"
						+ taskIdArray[i]+ "')";
					sqlList.add(updateBpmAssignSql);
				}
			}

			CommonLogger.logger.debug("������������˵�SQL�������Ϊ:"+sqlList.size());
			String sqlArray[] = new String[sqlList.size()] ;
			sqlArray = sqlList.toArray(sqlArray);
			DaoFactory.getTransactionTemplate(ProcessClient.MODULE)
			.batchUpdate(sqlArray);
			return "������������˳ɹ�";
		}
		catch(Exception ex)
		{
			CommonLogger.logger.error("�������������ʧ��: " + ex);
			return ex.getMessage();
		}
	}

}
