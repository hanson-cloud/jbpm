package com.hanson.jbpm.dev.action;

import javax.servlet.http.HttpServletRequest;

import com.hanson.jbpm.dev.pico.PicoContainer;
import com.hanson.jbpm.jpdl.exe.ctx.State;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.suntek.ccf.pico.IComponentAdvice;
import com.suntek.ccf.web.service.ServiceInvocationException;

import com.hanson.jbpm.mgmt.ProcessClient;

public class DeleteAction implements IComponentAdvice
{
	private String userCode = "";
	
	public String execute(String instanceId, String bizId, String processName) {
		String sql = "select max(TASK_ID) TASK_ID from BPM_TASK where INST_ID='" + instanceId + "'";		
		String taskId = (String)DaoFactory.getJdbc(ProcessClient.MODULE).queryForMap(sql).get("TASK_ID");
		
		sql = "update BPM_INST set DELETED='" + State.TRUE + "' where INST_ID='" + instanceId + "'";
		
		//sqls[1] = "delete from BPM_ASSIGN where TASK_ID in" +
		//		  		" (select TASK_ID from BPM_TASK where INST_ID='" + instanceId + "')";
		//sqls[2] = "delete from BPM_TASK where INST_ID='" + instanceId + "'";
		
		try {
			if (PicoContainer.getInstance().containsComponent(ProcessClient.DELETE_ACTION)) {
				CommonLogger.logger.debug("[DeleteAction]����Pico���ע�Ṥ�����ϵ��������ȡ�������Ե��ã�");
				PicoContainer.getInstance().getComponent(ProcessClient.DELETE_ACTION).invoke(new String[]{bizId, processName});
			}
				
			
			DaoFactory.getJdbc(ProcessClient.MODULE).update(sql);
			
			RecordTaskTrackAction action = new RecordTaskTrackAction();
			action.execute(this.userCode, taskId, "���Ϲ���", "������" + this.userCode + "ִ�������ϲ�����" );
			
			return "ѡ��ļ�¼������";
		} catch(Exception ex) {
			CommonLogger.logger.error(ex, ex);
			return "�������ϲ���ʧ��: " + ex.getMessage();
		}
	}
	public void after(HttpServletRequest request) throws ServiceInvocationException
	{		
	}
	
	public void before(HttpServletRequest request) throws ServiceInvocationException
	{
		this.userCode = request.getRemoteUser();		
	}
}
