package com.hanson.jbpm.web.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.mgmt.ctx.NewInstanceSavingContextQuery;
import org.dom4j.DocumentException;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.form.TaskFormDef;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * ���̱�����
 * @author zhout
 *
 */
public class NewInstanceService extends HttpRequestService {
	private final static long serialVersionUID = 1l;	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		String processName = request.getParameter("processName");		
		
		String draftId = request.getParameter("draftId");
		
		request.setAttribute("processName", processName);
		request.setAttribute("instanceId", null);
		request.setAttribute("draftId", draftId);
		request.setAttribute("formReadOnly", "false");
		
		String closeWin = request.getParameter("closeWin"); // ���ύ֮���Ƿ�رմ���
		if (closeWin == null) closeWin = "true"; 
		
		request.setAttribute("closeWin", closeWin);
		
		String mainFormData;
		try {
			//mainFormData = new NewInstanceSavingContextQuery(request.getRemoteUser(), processName).getContextInJson();
			mainFormData = new NewInstanceSavingContextQuery(request.getRemoteUser(), draftId).getContextInJson();
		} catch (BpmException e) {
			response.getWriter().print(e.getMessage());
			return;
		} catch (DocumentException e) {
			response.getWriter().print(e.getMessage());
			return;
		}
		
		ProcessDefinition pdf = ProcessEngine.getProcessDefinition(processName);
		TaskFormDef taskFormDef = pdf.getFormURLByTaskNode(pdf.getStart().getName());
		
		/* ����ָ�����̵ķ������ URL, ��ִ�ж��� */		
		String startURL = taskFormDef.getUrl();
		String writableElement = taskFormDef.getWritableElement();
		
		request.setAttribute("mainFormData", new String[]{mainFormData, writableElement});
		
		if (startURL.startsWith("/jbpm/"))	// ת jbpm �ͻ��˵�ȱʡ������ҳ��
			response.getWriter().print("<script>location = '/" + ProcessClient.MODULE + startURL + 
					"?processName=" + processName + "&readonly=false&instanceId=&taskId=&bizId=&dueTime='</script>");
		else
			forward(request, response, startURL);
	}
}
