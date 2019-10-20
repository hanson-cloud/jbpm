package com.hanson.jbpm.web.service;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.form.TaskFormDef;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.hanson.jbpm.mgmt.ctx.HistoryTaskContextQuery;
import com.hanson.jbpm.mgmt.ctx.LastSameTaskOrSavingCtxQuery;
import com.hanson.jbpm.mgmt.ctx.NewInstanceSavingContextQuery;
import com.suntek.ccf.dao.DaoFactory;

public class GetTaskNodeFormService extends HttpRequestService {
	private final static long serialVersionUID = 1l;	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException {
		try {
			String processName = request.getParameter("processName");
			String instanceId = request.getParameter("INST_ID");
			if (instanceId == null) instanceId = "";
			String taskId = request.getParameter("TASK_ID");
			if (taskId == null) taskId = "";
			
			String taskName = request.getParameter("TASK_NAME");
			
			if(taskName == null && !"".equals(taskId)) {
				taskName = getTaskName(taskId);
			}
			
			String formReadonly = request.getParameter("readonly");
			
			String queryByTaskId = request.getParameter("queryByTaskId");
			
			String dueTime = request.getParameter("dueTime");
			
			/*if (bizId != null && instanceId == null) {
				String sql = "select INST_ID,TASK_ID,TASK_NAME,DUE_TIME from BPM_TASK" +
							 " where BIZ_ID='" + bizId + "' order by TASK_ID desc";
				Map row = (Map) DaoFactory.getJdbc("jbpm").queryForList(sql).get(0);
				instanceId = (String) row.get("INST_ID");
				taskId = (String) row.get("TASK_ID");
				taskName = (String) row.get("TASK_NAME");
				dueTime = row.get("DUE_TIME").toString();
			}*/
			
			String userCode = request.getRemoteUser();
			
			/* �������㳬ʱ */
						
			if (formReadonly == null) formReadonly = "false";
			
			String history = request.getParameter("history");
			if (history == null) history = "false";
			
			/*request.setAttribute("processName", processName);			
			request.setAttribute("instanceId", instanceId);
			request.setAttribute("taskId", taskId);
			request.setAttribute("bizId", bizId);
			request.setAttribute("formReadOnly", formReadonly);
			request.setAttribute("dueTime", dueTime);*/
			
			String mainFormData = "";
			
			if (formReadonly.equals("true") && history.equals("true"))
				mainFormData = new HistoryTaskContextQuery(instanceId, taskId, taskName, queryByTaskId).getContextInJson();
			if (formReadonly.equals("true") && !history.equals("true"))
				mainFormData = new LastSameTaskOrSavingCtxQuery(instanceId, taskId, taskName, userCode, queryByTaskId).getContextInJson();
							
			if (formReadonly.equals("false") && isNewInstanceTask(instanceId))
				mainFormData = new NewInstanceSavingContextQuery(request.getRemoteUser(), taskName).getContextInJson();
			if (formReadonly.equals("false") && !isNewInstanceTask(instanceId))
				mainFormData = new LastSameTaskOrSavingCtxQuery(instanceId, taskId, taskName, userCode, queryByTaskId).getContextInJson();
			
			ProcessDefinition pdf = ProcessEngine.getProcessDefinition(processName);
			
			if (taskName == null)
				taskName = pdf.getStart().getName();
			
			/* ����ָ�����̵ķ������ URL, ��ִ�ж��� */
			TaskFormDef taskFormDef = pdf.getFormURLByTaskNode(taskName);
			String url = taskFormDef.getUrl();		
			
			//if (formReadonly.equals("false"))
			//String initFormData = new LastSameTaskOrSavingCtxQuery(instanceId, null, taskName, userCode, "").getContextInJson(); 
			
			//request.setAttribute("mainFormData", mainFormData);
			
			if (url == null) {
				response.getWriter().print("�ù����Ѵ�����ɡ�");
			} else {
				/* �˴����п��ع�!!! */
				if (url.startsWith("/jbpm/")) {
					PrintWriter writer = response.getWriter();
					StringBuffer ret = new StringBuffer("<script>top.Page.previewTask = " + mainFormData + "; alert(top.Page.previewTask); " +
														" location = '/").append(ProcessClient.MODULE).append(url).
														append("'</script>");
					System.out.println(ret.toString());
					writer.print(ret.toString());
					return;
				}				
				if (url.startsWith("..")) {
					PrintWriter writer = response.getWriter();
					//mainFormData = URLEncoder.encode(mainFormData, "UTF-8");
					StringBuffer ret = new StringBuffer("<script>top.Page.previewTask = " + mainFormData + "; alert(top.Page.previewTask); " +
										" location = \"").append(url.substring(2))
										.append("\"</script>");
					System.out.println(ret.toString());
					writer.print(ret.toString());
					return;					
				}
				forward(request, response, url);
			}			
		} catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			throw new ServletException(ex);
		}		
	}
	
	private String getTaskName(String taskId) {
		String sql = "select TASK_NAME FROM BPM_TASK WHERE TASK_ID='" + taskId + "'"
			+ "union select TASK_NAME FROM BPM_TASK_HIS WHERE TASK_ID='" + taskId + "'";
		CommonLogger.logger.debug("[query taskName by taskId]" + sql);
		List list =  DaoFactory.getJdbc("jbpm").queryForList(sql);
		return (String)((Map)list.get(0)).get("TASK_NAME");
	}
	
	private boolean isNewInstanceTask(String instId) {
		return (instId == null || instId.equals(""));
	}
}
