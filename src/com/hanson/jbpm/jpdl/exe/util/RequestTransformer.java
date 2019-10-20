package com.hanson.jbpm.jpdl.exe.util;

import com.hanson.jbpm.dev.FormElement;
import com.hanson.jbpm.identity.OrgCache;
import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.pico.custmgr.Customer;
import com.suntek.util.string.StringHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

public class RequestTransformer {
	 

	/**
	 * �� request �� form �ı���ת���� XML ����
	 * @param request
	 * @return
	 */
	public String formDataToXml(HttpServletRequest request) {
		String text = "";
		Enumeration enu = request.getParameterNames();
		String name = "";
		String value = "";
		String[] values;		
		while(enu.hasMoreElements()) {
			name = ((String)enu.nextElement());
			value = "";
			values = request.getParameterValues(name);
			for (int i=0; i<values.length; i++) {
				if (values[i] != null && !values[i].equals(""))
					value = value + values[i] + ",";
			}			
			if (value.indexOf(",") >= 0 )
				value = value.substring(0, value.length()-1);
			
			if (!value.equals("")) {
				value = value.replaceAll("'", "��");
				name = StringHelper.replace(name, "[]", "");
				text = text + CDataUtil.get(name, value);
			}
		}
		return text;
	}
	
	public String formDataToXml(List<FormElement> formElements) {
		String text = "";
		FormElement el;
		for(int i=0; i<formElements.size(); i++) {
			el = formElements.get(i);
			if (!el.elementValue.equals("")) {
				text = text + CDataUtil.get(el.elementName, el.elementValue);
			}
		}
		return text;
	}
	
	/**
	 * �� user, call, cust ����ת���� XML ����
	 * @param request
	 * @return
	 */
	public String envDataToXml(HttpServletRequest request) {				
		return userToXml(request.getRemoteUser()) + callToXml(request);
	}
	
	/**
	 * ��ʽ����ǰ�����û���xml
	 * @param user
	 * @return
	 */
	public String userToXml(String userCode) {
		String userName = "", deptId = "", deptName = "";
		
		userName = OrgCache.getCache().getUser(userCode);
		deptId = OrgCache.getCache().getUserDept(userCode);
		deptName = OrgCache.getCache().getDept(deptId);
		
		String text = "<" + Context.types.USER + ">";
		//text = text + El(Context.values.UID, userCode);
		text = text + El(Context.values.ULOGNAME, userCode);
		text = text + El(Context.values.UNAME, userName);		
		
		
		text = text + El(Context.values.DEPTID, deptId);
		text = text + El(Context.values.DEPTNAME, deptName);
		
		text = text + "</" + Context.types.USER + ">";
		return text;
	}
	
	/**
	 * ��ʽ����ǰ�������ݵ�xml
	 * @param user
	 * @return
	 */
	private String callToXml(HttpServletRequest request) {
		String text = "<" + Context.types.CALL + ">";
		
		String userCode = request.getRemoteUser();
		
		//Customer customer = OpenCC.getCallContext(userCode).getCallinCust(userCode);
		Customer customer = OpenCC.getCallContext(userCode).getCallinCust(userCode);
		
		if(customer == null) return "";
		if(request.getParameter("CUST_ID") == null) {
			String custId = (customer==null)?"":String.valueOf(customer.get("ID"));
			text = text + El(Context.values.CUSTID, custId);
		}
		if(request.getParameter("CALL_ID") == null) {
			String callId = (String)customer.get("CALLID");
			text = text + El(Context.values.CALLID, callId);
		}
		if(request.getParameter("CUST_TEL") == null) {
			String custTel = customer.getTelephoneNum();
			text = text + El(Context.values.CUSTTEL, custTel);
		}
		
		text = text + "</" + Context.types.CALL + ">";
		return text;
	}
	
	/**
	 * ��� XML �ڵ�� Header | Tail ��ǩ 
	 * @param nodeName
	 * @return
	 */
	private String El(String nodeName, String nodeValue) { 
		return "<" + nodeName + ">" + nodeValue + "</" + nodeName + ">"; 
	}
}