package com.hanson.jbpm.jpdl.exe.ctx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hanson.jbpm.dev.FormElement;


public class InstanceContext {
	/* Root */
	private final static String contextRoot = "ctx";
	/* ��ǰ user, call, cust ����Ϣ*/
	private final static String envContextRoot = "env";
	/* ��������ʱ���� */
	private final static String varContextRoot = "var";
	/* ��ǰ�������� */
	private final static String taskContextRoot = "task";
	/* ��ǰ���������ʵ������ */
	private final static String processContextRoot = "proc";
	
	private String envContext, processContext;
	
	private Map parameterMap;
	
	/* XML ������ */
	private Element context;	
	public InstanceContext(String envContext, String taskContext, String processContext) 
	throws DocumentException {
		String _taskContext = H(taskContextRoot) + taskContext + T(taskContextRoot);
		construct(envContext, _taskContext, processContext);
	}
	
	private void construct(String envContext, String taskContext, String processContext) 
	throws DocumentException {
		this.envContext = envContext;
		this.processContext = processContext;
		
		String text = H(contextRoot);
		text = text + H(envContextRoot) + envContext + T(envContextRoot);
		text = text + taskContext;		
		text = text + H(processContextRoot) + processContext + T(processContextRoot);
		text = text + H(varContextRoot) + "" + T(varContextRoot);
		text = text + T(contextRoot);
		text.replace("&nbsp;", "");
		CommonLogger.logger.debug(text);
		context = DocumentHelper.parseText(text).getRootElement();
		
	}
	
	/**
	 * ��������ִ������ʱ����
	 * @param name		��������
	 * @param value		����ֵ
	 */
	public void appendRuntimeVariable(String name, String value) {
		Element el = context.element(varContextRoot).element(name);
		if (el == null) {
			el = context.element(varContextRoot).addElement(name);
			el.addText(value);
		} else {
			el.setText(value);
		}
		
		if (name.equals(Context.values.BIZID))
			setFormElement(Context.values.BIZID, value);
	}
	
	public String getTaskDueTime() {
		return getParameter(Context.values.DUETIME, Context.types.RUNTIME);
	}
	
	public void setTaskDueTime(int dueTime) {
		appendRuntimeVariable(Context.values.DUETIME, String.valueOf(dueTime));
	}
	
	public void setTaskDueTime(String dueTime) {
		appendRuntimeVariable(Context.values.DUETIME, dueTime);
	}
	
	public String getPrevTaskAssignUID() {
		return getParameter("ASSIGN_UID", Context.types.MAINFORM);
	}
	
	public String getPrevTaskAssignDeptID() {
		return getParameter("ASSIGN_DEPTID", Context.types.MAINFORM);
	}

	public String getPrevTaskName() {
		return getParameter("PRE_TASKNAME", Context.types.MAINFORM);
	}
	
	/**
	 * �� XML ���ұ���
	 * @param param		��������
	 * @param type		Xpath ·��, ������������
	 * @return
	 */
	public String getParameter(String param, String type) {
		
		Element el = null;
		
		if (type.equals(Context.types.FORM) || type.equals(Context.types.RUNTIME))
			el = (Element)context.selectSingleNode("/" + contextRoot + "/" + type + "/" + param);		
		if (type.equals(Context.types.MAINFORM)) {
			if ((Context.values.INIT_DEPTID + "," + Context.values.INIT_UID + ",ASSIGN_UID,ASSIGN_DEPTID,PRE_TASKNAME," +
				 Context.values.INSTID).indexOf(param)>=0)
				el = (Element)context.selectSingleNode("/" + contextRoot + "/proc/" + param);
			else
				el = (Element)context.selectSingleNode("/" + contextRoot + "/proc/task/" + param);
		}
		if (type.equals(Context.types.USER) || type.equals(Context.types.CALL))
			el = (Element)context.selectSingleNode("//" + type + "/" + param);
				
		if (el != null)	{
			String ret = el.getText();
			if (ret.equals("null")) return "";
			else return ret;
		}
		else {
			return "";
		}
	}
	
	/**
	 * ���ñ���һЩ��Ҫ�����й������Զ����ɵ�Ԫ�ص�ֵ.��
	 * ��ȷ����֮��,��������ܻ�ԭ����ʾ.
	 * ����:��������,���ϴ��ļ������ӡ���.
	 * @param param		Ԫ������
	 * @param value		ֵ
	 */
	public void setFormElement(String param, String value) {
		Element el = ((Element)context.selectSingleNode("/" + contextRoot + "/" + Context.types.FORM + "/" + param));
		if (el == null)
			el = ((Element)context.selectSingleNode("/" + contextRoot + "/" + Context.types.FORM)).addElement(param);
		el.setText(value);
	}
	
	public void setFormContext(String formDataInXml) throws DocumentException {
		construct(envContext, formDataInXml, processContext);
	}
	
	public String getFormContext() {
		return context.selectSingleNode("/" + contextRoot + "/" + Context.types.FORM).asXML();
	}
	
	/**
	 * ��ȡһ�������
	 * @param elNames
	 * @return
	 */
	protected List<FormElement> getElements(String[] elNames) {
		List<FormElement> list = new ArrayList<FormElement>();
		FormElement fel = null;			
		for (int i=0; i<elNames.length; i++) {
			fel = new FormElement();
			fel.elementName = elNames[i];
			fel.elementValue = getParameter(elNames[i], Context.types.FORM);
			list.add(fel);
		}
		return list;
	}
	
	/**
	 * ���������ĵ� Xml ����, ֻ���� ctx �� form �������. 
	 * Runtime ���ݽ��ڵ�ǰ������Ч, ���Բ���Ҫ�־û�.
	 * @return
	 */
	protected String getContextDataInXml() {
		return  "<ctx>" + 
				context.element(envContextRoot).asXML() + 
				context.element(taskContextRoot).asXML() + 
				"</ctx>";
	}
	
	/**
	 * �����ر��� Xml ����
	 * @return
	 */
	protected String getFormDataInXml() {
		return context.element(taskContextRoot).asXML();		
	}
	
	public Map getFormDataInMap() {
		Map<String, String> map = new HashMap<String, String>();
		List elements = context.element(taskContextRoot).elements();
		for (Object o: elements) {
			Element el = (Element)o;
			map.put(el.getName(), el.getText());
		}
		return map;
	}
	
	/**
	 * �������Ͳ���Ԫ�ص�ֵ
	 * @param param
	 * @return
	 */
	public String getParameter(String param) {
		Element el = (Element)context.selectSingleNode(param);
		if (el == null) return "";
		String ret = el.getText();		
		CommonLogger.logger.debug("Find XPath [" + param + "] = " + ret);
		return ret;
	}
	
	public String toXML() {
		return context.asXML();
	}
	
	/**
	 * ��� XML �ڵ�� Header | Tail ��ǩ 
	 * @param nodeName
	 * @return
	 */
	private String H(String nodeName) { return "<" + nodeName + ">"; }
	private String T(String nodeName) {	return "</" + nodeName + ">"; }
	
	public static void main(String[] args) throws DocumentException {
		//Element el = DocumentHelper.parseText("<a><b>b</b></a>").getRootElement();
		//System.out.println(((Element)el.selectSingleNode("/a/b")).getText());
		System.out.println("123[]".replaceAll("[]", ""));
	}

	public Map getParameterMap()
	{
		return parameterMap;
	}

	public void setParameterMap(Map parameterMap)
	{
		this.parameterMap = parameterMap;
	}
}
