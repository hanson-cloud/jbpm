package com.hanson.jbpm.web.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspTagIncludeService extends HttpRequestService {
private final static long serialVersionUID = 1l;	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.print(request.getAttribute("processId"));
	}
}
