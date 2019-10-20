package com.hanson.jbpm.web.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.dev.action.RecordTaskTrackAction;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstance;
import com.hanson.jbpm.jpdl.exe.util.RequestTransformer;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.log.LoggerUtil;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.util.string.IDGenerator;
import com.usemon.lib.org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.node.End;
import com.hanson.jbpm.jpdl.def.node.NodeBase;

import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstanceMgmt;
import com.hanson.jbpm.jpdl.exe.util.DateFormat;
import com.suntek.util.string.StringHelper;

/**
 * ������������ύ����
 * @author zhout
 *
 */
public class TaskNodeFormSubmitService extends HttpRequestService {
	private final static long serialVersionUID = 1l;
	
	private boolean isSingleFormSubmit = true;
	
	private String processName = "";
	private String transition = "";
	private String instanceId = "";
	private String taskId = "";
	private String bizId = "";
	private String batchIds = "";
	private String dueTime = "";
		
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {	
		
		prepareParameters(request);			
		
		if (isSingleFormSubmit) {
			LoggerUtil.getLogger().debug("���Ź�������" + bizId);
			singleFormSubmit(request, response);
		} else {
			LoggerUtil.getLogger().debug("���Ź�������" + batchIds);
			multiFormSubmit(request, response);
		}		
	}
	
	private void prepareParameters(HttpServletRequest request) {
		processName = request.getParameter("processName");
		
		transition = request.getParameter("transition");
		if (transition == null || transition.equals("null")) transition = "";
		
		instanceId = request.getParameter("INST_ID");
		taskId = request.getParameter("TASK_ID");
		bizId = request.getParameter("BIZ_ID");
		if (bizId == null || bizId.equals("null")) bizId = "";
		
		batchIds = request.getParameter("batchIDs");
		if (batchIds != null && !batchIds.trim().equals("")) 
			isSingleFormSubmit = false;
			
		//setCloseWin(clientHopeToCloseSubmitWin(request));
		
		CommonLogger.logger.debug("processName = " + processName);
		CommonLogger.logger.debug("instanceId = " + instanceId);
		CommonLogger.logger.debug("taskId = " + taskId);
		CommonLogger.logger.debug("bizId = " + bizId);
		CommonLogger.logger.debug("batchIds = " + batchIds);
	}
	
	private void multiFormSubmit(HttpServletRequest request, HttpServletResponse response) 
	throws IOException {
		String[] allids = batchIds.split(";");
		String[] bizIds = allids[0].split(",");
		String[] instanceIds = allids[1].split(",");
		String[] taskIds = allids[2].split(",");
		String[] duetimes = allids[3].split(",");
		for (int i=0; i<bizIds.length; i++) {
			bizId = bizIds[i];
			if (instanceIds.length == 0) { // ֻ�ύ BizId ʱ����
				String sql = "select INST_ID,TASK_ID,TASK_NAME,DUE_TIME from BPM_TASK" +
							 " where BIZ_ID='" + bizIds[i] + "' order by TASK_ID desc";
				CommonLogger.logger.debug(sql);
				Map row = (Map) DaoFactory.getJdbc("jbpm").queryForList(sql).get(0);
				instanceId = (String) row.get("INST_ID");
				taskId = (String) row.get("TASK_ID");
				dueTime = row.get("DUE_TIME").toString();
			} else {
				instanceId = instanceIds[i];
				taskId = taskIds[i];
				dueTime = duetimes[i];
			}
			CommonLogger.logger.debug("���Ź�������[" + (i+1) + "/" + bizIds.length + "]��" + batchIds);
			singleFormSubmit(request, response);
		}
	}
	
	private void singleFormSubmit(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		long debugStartTime = System.currentTimeMillis();
		
		CommonLogger.logger.debug("����������ת begin...");
		
		ProcessInstance instance = null;
		
		if (("null".equals(instanceId) || "".equals(instanceId))) { 
			instanceId = null;
			taskId = null;
		}
		
		try {
			ProcessInstanceMgmt pim = ProcessEngine.getProcessInstanceMgmt(processName);
						
			instance = pim.findInstance(instanceId, taskId);
			
			if (taskId != null) {
				TaskFinishedInfo finishedInfo = checkWhoHasFinishedIt(instance);
				
				if (checkHasFinishedIt(instanceId, finishedInfo, response)) {
					return;
				}
				
				//if (BpmConstants.checkoutTypeConfig.equals("exclusive") && 
				//		checkHasLockedByOther(response, finishedInfo, request.getRemoteUser())) 
				//	return;
			}
			
			RequestTransformer transformer = new RequestTransformer();
			
			instance.setTaskContext2(transformer.formDataToXml(request));
			instance.setEnvContext(transformer.envDataToXml(request));
			
			if (!isSingleFormSubmit) { // ����������Ҫ�� context ��ȡ���� hidden ����
				instance.getInstanceContext().setFormElement(Context.values.INSTID, instanceId);
				instance.getInstanceContext().setFormElement(Context.values.TASKID, taskId);
				instance.getInstanceContext().setFormElement(Context.values.DUETIME, dueTime);
			}
			
			instance.setRequest(request);
			
			/* ��ǰ����̳е� forkid */
			if (instance.getCurrentTaskForkId() != null)
				instance.getExecutionContext().getInstanceContext().appendRuntimeVariable(
						Context.values.FORKID, instance.getCurrentTaskForkId());
			
			//���ô�����������
			instance.getExecutionContext().setTriggerTransitionName(transition);
		} catch (Exception ex) {
			String msg = ex.getMessage();
			if (msg == null) msg = "null";
			else msg = StringHelper.replace(msg, "'", "��");
			response.getWriter().print(toJavascriptAlert("������" + instanceId + "��ִ��Ԥ�����쳣: " + msg, false));
			CommonLogger.logger.error(ex, ex);
			return;
		}
		
		CommonLogger.logger.debug("�������������ʱ(parse data cost)" + (System.currentTimeMillis() - debugStartTime));
		
		try {
			instance.getToken().leave(transition);
			
			NodeBase node = instance.getExecutionContext().getEnterNode();
//			NodeBase leaveNode = instance.getExecutionContext().getLeavedNode();
//			CommonLogger.logger.debug("ZNJ enter node" + node.getName());
//			CommonLogger.logger.debug("ZNJ leave node" + leaveNode.getName());
//			if ("�а쵥λ����".equals(node.getName()) && ("���߹���������".equals(leaveNode.getName()) || "���߹����������".equals(leaveNode.getName()))) {
//				this.checkZNJSubmitThreeTimes(this.instanceId);
//			}
			
			responseToBrowser(node, instance, response);
			
		} catch (Exception ex) {					
			String msg = ex.getMessage();
			if (msg == null) msg = "null";
			else msg = StringHelper.replace(msg, "'", "��").replace('\r',' ').replace('\n', ' ');
			response.getWriter().print(toJavascriptAlert("������" + instanceId + "���ύ,ִ���쳣: " + msg, false));
			CommonLogger.logger.error(ex, ex);
		} finally {
			CommonLogger.logger.debug("�����ύ��ʱ(persistance data cost): " + (System.currentTimeMillis() - debugStartTime));
		}
	}
	
	private boolean checkHasLockedByOther(HttpServletResponse response, TaskFinishedInfo finishedInfo, String me) 
	throws IOException {		
		if (finishedInfo.isLocked() && !me.equals(finishedInfo.getLockedUser())) {
			response.getWriter().print(toJavascriptAlert("�Բ���, �ù�����" + instanceId + "���ѱ� " + 
					finishedInfo.getLockedUser()+" ����, ��ˢ�����, ѡ����������.", false));
			return true;
		} else
			return false;
	}
	
	public boolean checkHasFinishedIt(String instId, TaskFinishedInfo finishedInfo, HttpServletResponse response) 
	throws IOException {
		String message = "";
		if (!isNewInstance(instId) && finishedInfo.isFinished()) {
			if (!finishedInfo.isArchived()) {
				CommonLogger.logger.debug("�Բ���, �ù������� " + finishedInfo.getDealer() + " �� " + finishedInfo.getDealTime() + " �������.");
				message = "�Բ���, �ù����ѱ� ������Ա����";
				CommonLogger.logger.debug(message);
				response.getWriter().print(toJavascriptAlert(message, false));					
			} else {
				message = "�Բ��𣬸ù�������������Ա�����鵵.";
				CommonLogger.logger.debug(message);
				response.getWriter().print(toJavascriptAlert(message, false));
			}
			return true;
		}
		return false;
	}
	
	private void responseToBrowser(NodeBase node, ProcessInstance instance, HttpServletResponse response) 
	throws IOException, DocumentException {
		String nextDealer = instance.getExecutionContext().getInstanceContext().
									getParameter(Context.values.NEXTSTEP_DEALER, Context.types.RUNTIME);
		if (nextDealer.length()>20)
			nextDealer = nextDealer.substring(0, 20) + "...";
		
		if (bizId.equals("")) bizId = "�½�";
		
		if (node instanceof End) {
			if (BpmConstants.AutoArchived)
				response.getWriter().print(toJavascriptAlert("�����������, �ѹ鵵", true));
			else
				response.getWriter().print(toJavascriptAlert("�����������, �ѹر�", true));			
		}
		else {
			if (node == null)
				response.getWriter().print(toJavascriptAlert("�������ͳɹ�, ������һ������", true));
			else
				response.getWriter().print(toJavascriptAlert("�������ͳɹ�, ת��һ������  " + nextDealer + " ���� ", true));
		}
	}
	
	/**
	 * �жϵ�ǰ�򿪵Ĺ����Ƿ��ѱ������˴���
	 * @param instance
	 * @return
	 */
	public TaskFinishedInfo checkWhoHasFinishedIt(ProcessInstance instance) {		
		TaskFinishedInfo finishedInfo = new TaskFinishedInfo();
		
		if (instance.hasBeenClosed) {
			finishedInfo.setArchived(true);
		} else {
			if (instance.taskDealer == null || instance.taskDealer.trim().equals("")) {
				finishedInfo.setFinished(false);
				String locked = instance.taskIsLocked;
				if (locked != null && !"0".equals(locked))	{
					finishedInfo.setLocked(true);
					finishedInfo.setLockedUser(instance.taskLockUserId);
				} else 
					finishedInfo.setLocked(false);				
			} else {
				finishedInfo.setFinished(true);
				finishedInfo.setDealer(instance.taskDealer);
				finishedInfo.setDealTime(new DateFormat().format((Timestamp)instance.taskDealTime));
			}
		}
		return finishedInfo;
	}
	
	private boolean isNewInstance(String instanceId) {
		return (instanceId == null);
	}

	/**
	 * ��������
	 * @param instId
	 */
	private void checkZNJSubmitThreeTimes(String instId) {
		String isOpen = AppHandle.getHandle("gzorder").getProperty("ZNJ_LOCK_ISOPEN", "0");
		CommonLogger.logger.debug("[checkZNJSubmitThreeTimes]ZNJ_LOCK_ISOPEN:" + isOpen);
		if (!"0".equals(isOpen)) {
			String noticeContent = AppHandle.getHandle("gzorder").getProperty("ZNJ_LOCK_CONTENT", "���������ڶ�ΰ���δ���׼�����������");
			String noticeTransName1 = "��������";
			String var5 = "��������";

			try {
				String queryMultiSubmit = "select TASK_ID,ASSIGN_DEPTID,PRE_TASKID,ASSIGN_UID,DEAL_FLAG,TRANS_NAME from bpm_task t where t.pre_taskname in ('���߹����������','���߹���������') and t.task_name = '�а쵥λ����' and t.trans_name   in ('���','����','�ܾ��˵�')  and t.inst_id = ? order by t.assign_time desc";
				CommonLogger.logger.debug("[checkZNJSubmitThreeTimes]instance.taskDealer" + instId);
				List<Map> rows = DaoFactory.getJdbc("jbpm").queryForList(queryMultiSubmit, new Object[]{instId});
				int sameIndex = 0;
				boolean isThreeTimes = false;
				String deptIdString = null;
				String assignUid = "";
				String nowTaskId = "";
				String transName = "";
				if (CollectionUtils.isNotEmpty(rows) && rows.size() > 2) {
					String getDealDeptIdSql = "select DEAL_DEPTID from bpm_assign t where t.inst_id = ? and t.task_id = ? and t.cc_flag = '0' and t.co_flag = '0' and t.task_name = '�а쵥λ����'";
					CommonLogger.logger.debug("[checkZNJSubmitThreeTimes]queryMultiSubmit rows��" + rows.size());

					for(int i = 0; i < rows.size() && i < 3; ++i) {
						Map row = (Map)rows.get(i);
						String taskId = (String)row.get("TASK_ID");
						String dealFlag = (String)row.get("DEAL_FLAG");
						if ("0".equals(dealFlag)) {
							assignUid = (String)row.get("ASSIGN_UID");
							transName = (String)row.get("TRANS_NAME");
							nowTaskId = taskId;
						}

						List<Map> deptRows = DaoFactory.getJdbc("jbpm").queryForList(getDealDeptIdSql, new Object[]{instId, taskId});
						if (CollectionUtils.isNotEmpty(deptRows)) {
							String dealDeptId = (String)((Map)deptRows.get(0)).get("DEAL_DEPTID");
							if (StringUtils.isBlank(deptIdString)) {
								deptIdString = dealDeptId;
								++sameIndex;
							} else if (deptIdString.equals(dealDeptId)) {
								++sameIndex;
							}
						}

						if (sameIndex >= 3) {
							isThreeTimes = true;
						}

						CommonLogger.logger.debug("[checkZNJSubmitThreeTimes]taskId" + taskId + ",deptIdString" + deptIdString);
					}

					CommonLogger.logger.debug("[checkZNJSubmitThreeTimes]deptIdString��" + deptIdString);
					CommonLogger.logger.debug("[checkZNJSubmitThreeTimes]isThreeTimes��" + isThreeTimes);
					if (isThreeTimes && StringUtils.isNotBlank(deptIdString)) {
						String insertZNJLockStringSqlString = "insert into BPM_BIZ_ZNJ_LOCK(ID,INST_ID,ZNJ_UID,LOCK_TIME,LOCK_STATUS,DISPATCH_TIME) VALUES(?,?,?,?,?,?)";
						String idString = IDGenerator.getIDByCurrentTime(15);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = new Date();
						String now = sdf.format(date);
						Date nowTime = new Date();
						Date date61 = new Date(1527782400000L);
						if ("1".equals(isOpen)) {
							if (StringUtils.isNotBlank(assignUid)) {
								(new RecordTaskTrackAction()).execute(assignUid, nowTaskId, noticeTransName1, noticeContent);
							}

							CommonLogger.logger.debug("[checkZNJSubmitThreeTimes] isThreeTimes:" + isThreeTimes);
						} else if ("2".equals(isOpen)) {
							if (nowTime.after(date61)) {
								int updateCount = DaoFactory.getJdbc("jbpm").update(insertZNJLockStringSqlString, new Object[]{idString, instId, deptIdString, now, "1", now});
								CommonLogger.logger.debug("[checkZNJSubmitThreeTimes] updateCount:" + updateCount + ",assignUid:" + assignUid);
								if (updateCount > 0 && StringUtils.isNotBlank(assignUid)) {
									(new RecordTaskTrackAction()).execute(assignUid, nowTaskId, "��������", "���������ڶ�ΰ���δ�ﵽ����׼�ѱ������������������ɺ��������߰��ύ���湤���������롣");
								}

								CommonLogger.logger.debug("[checkZNJSubmitThreeTimes] isThreeTimes:" + isThreeTimes);
							} else {
								CommonLogger.logger.debug("[checkZNJSubmitThreeTimes] �˹���ԭ����Ҫ����������δ��6��1�գ��������������������:" + instId);
							}
						}
					}
				} else {
					CommonLogger.logger.debug("[checkZNJSubmitThreeTimes]queryMultiSubmit rows is less than two");
				}
			} catch (Exception var23) {
				CommonLogger.logger.error(var23.getMessage(), var23);
			}

		}
	}
}
