package com.hanson.jbpm.web.service;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.log.CommonLogger;

public abstract class HttpRequestService extends HttpServlet {
	
	private static final long serialVersionUID = 3463239919368518894L;

	public ServletContext servletContext = null;	
	
	protected int pageWidth = 0;
	protected int pageHeight = 0; 
		
	public void setServletContext(ServletContext context) { 
		servletContext = context;
	}
	
	public abstract void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException;
	
	/**
     * Farward to the dispatching page
     * 如果SESSION变量失效，则定向到重新登录提示页面
     * @param request request
     * @param response response
     * @param target 目标页面
     * @throws ServletException
     */
    protected void forward(HttpServletRequest request, HttpServletResponse response,
                           String target) throws ServletException    {
        try {            
        	CommonLogger.logger.debug("URL: " + target);
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher(target);
            if (target != null)
            	dispatcher.forward(request, response);
            else
            	throw new ServletException("target 资源不存在");
        }  catch (IOException ioe) {
            throw new ServletException(ioe);
        }
    }
	
    protected String toJavascriptAlert(String msg, boolean close) {
		return "<script>parent.jbpmMessageBox('" + msg + "', " + close + ");</script>";
	}
    
    protected void setPrinterPageSize(int width, int height) {
    	this.pageWidth = width;
    	this.pageHeight = height;
    }
}
