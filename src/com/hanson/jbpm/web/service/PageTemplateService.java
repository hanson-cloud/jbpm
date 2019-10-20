package com.hanson.jbpm.web.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.hanson.jbpm.web.HttpGet;
import com.hanson.jbpm.web.service.template.DefaultTaskMapSetter;
import com.hanson.jbpm.web.service.template.FlowChartMapSetter;
import com.hanson.jbpm.web.service.template.ProcessedInfoDetailMapSetter;
import com.hanson.jbpm.web.service.template.TemplateUtil;
import com.hanson.jbpm.web.service.template.TodoWorklistQueryFormMapSetter;
import com.hanson.jbpm.web.service.template.WorkSheetDetailFrameMapSetter;

public class PageTemplateService extends HttpRequestService {
	private final static long serialVersionUID = 1l;	
	public final static String USER = "user";
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {		
		if (ProcessClient.TEMPLATE == null || ProcessClient.TEMPLATE.equals("")) {
			ProcessClient.TEMPLATE = request.getParameter("template");
		} 
		
		/* ҳ������ */
		String page = request.getPathInfo().substring(1);
		
		CommonLogger.logger.debug("Get template page: " + ProcessClient.TEMPLATE + "/" + page);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("module", ProcessClient.MODULE);
		map.put(USER, request.getRemoteUser());
		map.put("ccflag", BpmConstants.CCFLAG);
		
		try {
			/* request ��������ģ�幹������ map */
			Enumeration enu = request.getParameterNames();
			String key, value;
			while(enu.hasMoreElements()) {
				key = (String)enu.nextElement();
				value = request.getParameter(key);
				map.put(key, value);
			}
			
			if (page.indexOf("FlowChart.html") == 0)
				new FlowChartMapSetter().doGet(map);
			if (page.indexOf("ProcessedInfoDetail.html") == 0)
				new ProcessedInfoDetailMapSetter().doGet(map);
			if (page.indexOf("MyTodoWorklist.html") == 0) {			
				if ("true".equals(request.getParameter("history")) || "history".equals(request.getParameter("view"))) {
					request.setAttribute("gridDataProvider", "bean://WorkItemListHisQuery:getData");
					request.getSession().setAttribute("gridDataProvider", "bean://com.suntek.jbpm.mgmt.WorkItemListHisQuery:getData");
				} else {
					request.setAttribute("gridDataProvider", "bean://WorkItemListQuery:getData");
					request.getSession().setAttribute("gridDataProvider", "bean://com.suntek.jbpm.mgmt.WorkItemListQuery:getData");
				}
				new TodoWorklistQueryFormMapSetter().doGet(map);
			}
			if (page.indexOf("WorkSheetDetailFrame.html") == 0) {
				map.put("queryString", filterBpmQueryParam(request.getQueryString()));
				new WorkSheetDetailFrameMapSetter().doGet(map);
			}
			if (page.indexOf("defaultTask.html") == 0)
				new DefaultTaskMapSetter((String)request.getAttribute("mainFormData")).doGet(map);
		
			PrintWriter writer = response.getWriter();
			writer.print(TemplateUtil.getInstance().build(page, map));
			
		} catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			PrintWriter writer = response.getWriter();
			writer.print(ex.getMessage());
			throw new ServletException(ex);			
		}
	}	
	
	
	/**
	 * ȥ��jbpm��ҳ���ϴ��ݵļ�������
	 * @param queryString
	 * @return
	 */
	public static String filterBpmQueryParam(String queryString) {
		return HttpGet.filterQueryString(queryString,
				new String[]{"INST_ID", "processName", "TASK_ID", 
				"TASK_NAME", "dueTime", "dealer", "view", "readonly"});
	}
}
