package com.hanson.jbpm.jpdl.exe.impl;


import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.log.CommonLogger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.IdentityCreator;

public class DefaultStartActionHandler implements ActionHandler {
	
	public String execute(ExecutionContext ctx) throws Exception {
		CommonLogger.logger.debug("DefaultStartActionHandler.execute(" + ctx.getLeavedNode().getName() + ")");

		String bizId = getBizId(ctx);
		setTitle(ctx);
		
		/*�趨ʱ��*/
		String dueTime = ctx.getInstanceContext().getParameter(DefaultActionHandler.DEFAULT_DUETIME_NAME, 
				Context.types.FORM);
		if(!dueTime.equals("")) {
			ctx.getInstanceContext().setTaskDueTime(dueTime);
		}
				
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.BIZID, bizId);	
		//ctx.getProcessDefinition().getBizTable().insert(ctx);
		
		return bizId;
	}
	
	public void setTitle(ExecutionContext ctx){
		String title = ctx.getInstanceContext().getParameter(DefaultActionHandler.DEFAULT_BIZTITLE_NAME, 
			Context.types.FORM);	
		if (title.equals(""))
			title = ctx.getLeavedNode().getName() + " OK";
		if(title.length() > 1000) {title = title.substring(0, 1000);}
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.TASKTITLE, title);
	}
	
	
	/**
	 * ���ɹ������
	 * @param ctx
	 * @return
	 * @throws SQLException 
	 */
	public String getBizId(ExecutionContext ctx) throws SQLException {
		String bizId = ctx.getInstanceContext().getParameter(Context.values.BIZID, 
				 Context.types.FORM);
		if(bizId == null || "".equals(bizId)) {//���bizId����ҳ�������ɲ�����,���Զ�����
			bizId = getMaxId(ctx);
		}
		return bizId;
	}
	
	
	public String getMaxId(ExecutionContext ctx) throws SQLException {
		int MAXID = getIdentity(ctx);
		String maxId =  String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2)+"-"+ String.format("%03d", MAXID);
		CommonLogger.logger.debug("maxid = " + maxId);
		return maxId;
	}
	
	/**
	 * ��ȡ����������ţ���ʽ yyyy bizFlag dddd, 2009TS0001
	 * @param ctx 
	 * @param bizFlag ҵ�����
	 * @param seqLen  ��ų���
	 * @return
	 * @throws SQLException
	 */
	public String getMaxId(ExecutionContext ctx, String bizFlag, int seqLen) throws SQLException {
		int MAXID = getIdentity(ctx);
		String maxId =  String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + 
						bizFlag + 
						String.format("%0" + seqLen + "d", MAXID);
		CommonLogger.logger.debug("maxid = " + maxId);
		return maxId;
	} 
	
	public int getIdentity(ExecutionContext ctx) throws SQLException 
	{		
		JdbcTemplate jdbc = DaoFactory.getJdbc("jbpm");
		
		String flowName = ctx.getLeavedNode().getProcessDefinition().getProcessName();
		
		String sql = "select MAX_ID from BPM_FLOW where FLOW_NAME='" + flowName + "'";
		
		int maxId = Integer.parseInt(((Map)jdbc.queryForList(sql).get(0)).get("MAX_ID").toString());
		maxId = maxId + 1;
		
		int sysMaxId = IdentityCreator.checkBizidSequenceDuplicated(maxId);
		
		if (sysMaxId != maxId) {
			maxId = sysMaxId;
		}
		
		sql = "update BPM_FLOW set MAX_ID = " + maxId + " where FLOW_NAME='" + flowName + "'";	
		jdbc.execute(sql);
		
		return maxId;				
	}
}
