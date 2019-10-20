package com.hanson.jbpm.web.service;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.web.XMLFileUtil;
import org.dom4j.DocumentException;

import com.hanson.jbpm.jpdl.util.ClassLoaderUtil;

public class PageFieldsService extends HttpRequestService {
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String procName = request.getParameter("procName");
		String path = ClassLoaderUtil.getAbsolutePathOfClassLoaderClassPath();
		path = path.substring(6) + "../../META-INF/flow";
		String file = path + "/" + procName + "/fields.xml";
		PrintWriter print = response.getWriter();
		try {
			print.print(XMLFileUtil.read(file).asXML());
		} catch (DocumentException e) {
			CommonLogger.logger.error(e,e);
			print.print("<fields></fields>");
		}
	}
}
