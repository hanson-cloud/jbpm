package com.hanson.jbpm.dev.pico;

import javax.servlet.http.HttpServletRequest;

import com.suntek.ccf.web.service.ServiceInvocationException;

public interface IComponentAdvice {
	public void before(HttpServletRequest request) throws ServiceInvocationException;
	public void after(HttpServletRequest request) throws ServiceInvocationException;
}
