package com.hanson.jbpm.dev.action;

import java.util.List;
import java.util.Map;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.dev.pico.PicoContainer;
import com.hanson.jbpm.jpdl.def.base.BpmBiz;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.log.CommonLogger;
import com.suntek.opencc.um.IPortalMessageGateway;


public class RemindAction
{
	
	/**
	 * 
	 * @param instId
	 * @param taskId
	 * @param bizId
	 * @param userId
	 * @param msg
	 * @return
	 */
	@Deprecated
	public String execute(String instId, String taskId, String bizId, String userId, String msg) {
		return execute(instId, taskId, userId, msg);
	}
	
	/**
	 * 
	 * @param instId
	 * @param taskId
	 * @param userId
	 * @param msg
	 * @return
	 */
	public String execute(String instId, String taskId, String userId, String msg) {
		try {
			String sql[] = new String[4];
			sql[0] = "update BPM_TASK set REMINDED='1', REMIND_USERID='" + userId + "'," +
			 			 " REMIND_MSG='" + msg + "'" +
			 			 " where INST_ID='" + instId + "' and DEAL_TIME is null";
			sql[1] = "update BPM_TASK set REMINDED='1', REMIND_USERID='" + userId + "'," + 
						 " REMIND_MSG='" + msg + "'" + 
						 " where TASK_ID='" + taskId + "'";
			
			BpmBiz biz = new BpmBiz();
			biz.setInstId(instId);
			biz.setIsReminded("1");
			biz.setRemindUid(userId);
			biz.setRemindMsg(msg);
			sql[2] = biz.toUpdateSql();
			sql[3] = new RecordTaskTrackAction().getExecuteSql(userId, taskId, "�ߵ�", msg, "");
			for(String s : sql) {
				CommonLogger.logger.debug("[�ߵ�]" + s);
			}
			
			DaoFactory.getTransactionTemplate(ProcessClient.MODULE).batchUpdate(sql);
			
			String sql2 = "select DEALER from BPM_ASSIGN where TASK_ID='" + taskId + "'";
			
			List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql2);
					
			String[] users = new String[list.size()];
			for (int i=0; i<list.size(); i++)
				users[i] = (String)((Map)list.get(i)).get("DEALER");
			
			if (PicoContainer.getInstance().containsComponent(ProcessClient.REMIND_ACTION)) {
				try {
					IPortalMessageGateway mg = (IPortalMessageGateway) OpenCC.getComponentContainer()
							.getComponent(IPortalMessageGateway.class);
					mg.sendToAgent(users, IPortalMessageGateway.LEVEL_MIDDLE, "������Ϊ" + instId + 
							"�Ĺ����յ�һ���ߵ���Ϣ: " + msg, false);
					
					PicoContainer.getInstance().getComponent(ProcessClient.REMIND_ACTION).invoke(new Object[]{instId, userId, users});
				} catch(Exception e){
					CommonLogger.logger.error(e,e);
				}
				//ISmsGatewayProxy proxy = null;
				//try { proxy = (ISmsGatewayProxy) OpenCC.getComponentContainer().getComponent(ISmsGatewayProxy.class); }
				//catch(Exception ex) {}
				//if ((proxy != null) && (!proxy.sendToAgent(userId, users, "�ߵ�[" + bizId + "]: " + msg)))
				//	return "����������Ϣ����ʧ�ܣ�����ϵ����Ա����";
			}
		}catch(Exception e){
			CommonLogger.logger.error(e,e);
			return "�ߵ�����ʧ��";
		}
		return "�������˷����˴ߵ���Ϣ";
		//else return "�������˷����˴ߵ���Ϣ����ǰδע����ŷ���δ���Ͷ���֪ͨ";	
	}
}
