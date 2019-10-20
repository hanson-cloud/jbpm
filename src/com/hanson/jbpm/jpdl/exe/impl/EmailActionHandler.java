package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.MailSender;

public class EmailActionHandler implements ActionHandler {
	private String host;
	private String from;
	private String pwd;
	private String to;
	private String subject;
	private String content;
	
	public EmailActionHandler(String host, String from, String pwd, String to,
			String subject, String content) {
		this.host = host;
		this.from = from;
		this.pwd = pwd;
		this.to = to;
		this.subject = subject;
		this.content = content;
	}

	public String execute(ExecutionContext ctx) throws Exception {
		preProcessParameter(ctx);
		MailSender mail = new MailSender(host);
		mail.setFrom(from, pwd);
		mail.send(to, subject, content, null);
		return "true";
	}
	
	public void preProcessParameter(ExecutionContext ctx) {
		host = ExpressionEvaluator.evaluate("'" + host + "'", ctx);
		from = ExpressionEvaluator.evaluate("'" + from + "'", ctx);
		pwd = ExpressionEvaluator.evaluate("'" + pwd + "'", ctx);
		to = ExpressionEvaluator.evaluate("'" + to + "'", ctx);
		subject = ExpressionEvaluator.evaluate("'" + subject + "'", ctx);
		content = ExpressionEvaluator.evaluate("'" + content + "'", ctx);
		CommonLogger.logger.debug(host + "," + from + "," + to + "," + subject + ", " + content);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
