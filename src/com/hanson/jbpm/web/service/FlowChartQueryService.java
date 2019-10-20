package com.hanson.jbpm.web.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.mgmt.FlowChartQuery;

public class FlowChartQueryService extends HttpRequestService {
	
	private static final long serialVersionUID = 5435384755440166037L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String procName = request.getParameter("procName");
		List<String> nodes = new FlowChartQuery(procName).getProcessTaskNodes();
		String ret = "";
		for (int i=0; i<nodes.size(); i++)
			ret = ret + nodes.get(i) + ",";
		if (ret.length()>1) ret = ret.substring(0, ret.length()-1);
		response.getWriter().print(ret);
	}

}
