package com.hanson.jbpm.jpdl.exe.ctx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hanson.jbpm.jpdl.def.EntityDef;
import com.hanson.jbpm.jpdl.def.base.BpmBiz;

import com.suntek.ccf.orm.utils.BeanUtil;
import com.hanson.jbpm.jpdl.def.base.PreparedSqlBean;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * ҵ�����ݳ־û�
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�BizPersistence.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Nov 6, 2013
 */
@SuppressWarnings("unchecked")
public class BizPersistence {
	
	private TaskPersistence taskPersist;
	
	private SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	/**
	 * �־û�ҵ������(Ĭ��ʹ��hibernate)
	 * @param instId
	 * @param lastTaskId
	 * @param ec
	 */
	public BizPersistence(TaskPersistence taskPersist) {
		this.taskPersist = taskPersist;
	}
	
	
	public void insert() throws Exception {
		EntityDef entityDef = taskPersist.getExecutionContext().getProcessDefinition().getEntityDef();
		if(entityDef != null) {
			
			if(entityDef.getEntityClass() != null && BpmDaoSQLExecutor.PersistenceMode.equals("hibernate")) {
				//���־û���ʽΪhibernate����������ʵ����,���Զ�д�������
				entityDef.update(taskPersist.getExecutionContext());
			}else {
				//�־û�ͨ��ҵ���
				BpmBiz biz = new BpmBiz();
				setBpmBaseData(biz);
				
				biz.setProcessName(taskPersist.getExecutionContext().getProcessDefinition().getProcessName());
				biz.setModuleName(ProcessClient.MODULE);
				biz.setRegisterDate(taskPersist.currentTimeOfDate);
				
				//���´�������
				biz.setDealDeadline(fomat.parse(taskPersist.dueTime));
				biz.setDeadlineDays(taskPersist.dueDays); 
				
				//�󶨱�ֵ
				biz = (BpmBiz) BeanUtil.mapToBean(taskPersist.getExecutionContext()
						.getInstanceContext().getFormDataInMap(), biz);
				
				biz.setLastTaskid(taskPersist.taskId);
				
				
				PreparedSqlBean sql = biz.toPreparedInsertSql();
				BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql.getSql(), 
						sql.getArgs());
			}
			
		}
		
		
	}
	
	
	public void save() throws Exception {
		EntityDef entityDef = taskPersist.getExecutionContext().getProcessDefinition().getEntityDef();
		if(entityDef != null) {
			
			if(entityDef.getEntityClass() != null && BpmDaoSQLExecutor.PersistenceMode.equals("hibernate")) {
				//���־û���ʽΪhibernate����������ʵ����,���Զ�д�������
				entityDef.update(taskPersist.getExecutionContext());
			}else {
				BpmBiz biz = new BpmBiz();
				setBpmBaseData(biz);
				if(taskPersist.isDraft.equals("true")) {
					
					biz.setProcessName(taskPersist.getExecutionContext().getProcessDefinition().getProcessName());
					biz.setModuleName(ProcessClient.MODULE);
					//biz.setRegisterDate(taskPersist.currentTimeOfDate);
					
					//���´�������
					biz.setDealDeadline(fomat.parse(taskPersist.dueTime));
					biz.setDeadlineDays(taskPersist.dueDays); 
				}
				
				//�󶨱�
				biz = (BpmBiz) BeanUtil.mapToBean(taskPersist.getExecutionContext()
						.getInstanceContext().getFormDataInMap(), biz);
				
				biz.setLastTaskid(taskPersist.taskId);
				
				PreparedSqlBean sql = biz.toPreparedUpdateSql();
				BpmDaoSQLExecutor.executeBatch(taskPersist.getExecutionContext(), sql.getSql(), 
						sql.getArgs());
				
				
			}
		}
		
	}
	
	
	/**
	 * ��ҵ���ֵjbpm��������
	 * @param biz
	 * @throws ParseException 
	 */
	private void setBpmBaseData(BpmBiz biz) throws Exception {
		ExecutionContext ec = taskPersist.getExecutionContext();
		
		biz.setInstId(taskPersist.processId);
		biz.setCcDealer(ec.getAssignable().getDealsOmit(ec.getAssignable().getTaskCcDealers()));
		biz.setCoDealer(ec.getAssignable().getDealsOmit(ec.getAssignable().getTaskCoDealers()));
		
		biz.setDealDeptid(taskPersist.currentUser.getDeptId());
		biz.setDealDeptname(taskPersist.currentUser.getDeptName());
		biz.setDealer(ec.getAssignable().getDealsOmit(ec.getAssignable().getTaskDealers()));
		biz.setDealerDate(fomat.parse(taskPersist.currentTime));
		biz.setDealId(taskPersist.currentUser.getUserId());
		biz.setDealName(taskPersist.currentUser.getUserName());
		if(taskPersist.currentUser.getDeptId() != null 
				&& taskPersist.currentUser.getDeptId().length() >= 3) {
			biz.setEntCode(taskPersist.currentUser.getDeptId().substring(0, 3));
		}
		
		biz.setIsReminded("0");  //�����һ�δߵ���Ϣ
		biz.setRemindMsg("");
		biz.setRemindUid("");
		biz.setLocked("0");
		biz.setLockUserid("");
		biz.setLockTime(new Date(0));
		biz.setLastTaskName(taskPersist.getEnterNode().getName());
		
		if(taskPersist.isEnd) {//�رսڵ�ʱ����¹ر�ʱ��
			biz.setArchiveDate(fomat.parse(taskPersist.currentTime));
			biz.setOrderStatus("�ѹر�");
			biz.setIsClosed("1");
		}
	}

}
