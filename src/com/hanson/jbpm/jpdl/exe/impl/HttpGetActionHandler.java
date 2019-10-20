package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;
import com.hanson.jbpm.jpdl.exe.util.HttpRequestSender;

public class HttpGetActionHandler implements ActionHandler {
	private String getUrl; 
	private String to;
	private String msg;
	
	public HttpGetActionHandler(String getUrl, String to, String msg) {
		this.getUrl = getUrl;
		this.to = to;
		this.msg = msg;
	}
	
	public String execute(ExecutionContext ctx) throws Exception {
	
		try {     
			msg = ExpressionEvaluator.evaluate("'" + msg + "'", ctx);	
			
			String data = "?to=" + to + "&msg=" + msg;
			CommonLogger.logger.debug("HttpGetActionHandler: " + getUrl + data);
			
			HttpRequestSender request = new HttpRequestSender();
			request.sendGet(getUrl + data);
			
			return "true";
		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
			return "false";
		} 	
	}
}
