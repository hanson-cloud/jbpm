package com.hanson.jbpm.web.service.template;

import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.ExtendQuery;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.log.LoggerUtil;
import com.hanson.jbpm.identity.FunctionList;
import com.hanson.jbpm.identity.FunctionPermission;
import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.mgmt.FlowChartQuery;
import com.hanson.jbpm.mgmt.WorkItemListQuery;

public class TodoWorklistQueryFormMapSetter implements TemplateMapSetter {
	public void doGet(Map<String, Object> map) throws BpmException {
		
		String processName = (String)map.get("procName");
		
		ProcessDefinition pdf = null;
		if (processName != null)
			pdf = ProcessEngine.getProcessDefinition(processName);
		
		if (pdf == null || pdf.getPrinterProvider() == null) {
			map.put("printerDef", "false");
		} else {
			map.put("printerDef", "true");
		}
		
		if (pdf == null)
			map.put("extendQuery", new ExtendQuery());
		else
			map.put("extendQuery", pdf.getExtendQuery());
		
		map.put("checkout", BpmConstants.checkoutTypeConfig);
		map.put("openwin", map.get("openwin"));
		map.put("transition", (map.get("transition")==null)?"all":map.get("transition"));
		
		/* ��ѯ��ǰ�û�Ȩ�� */
    	String userLoginName = (String)map.get("user");
    	if (userLoginName == null)
    		throw new BpmException("��ȡ��¼�û���Ϣ�쳣�������Ƿ��¼����Session�ѹ���");
    	
    	FunctionList funcList = new FunctionPermission().getFunctionList(userLoginName);
    	    	
    	String startNodeName = "";
    	
		if (processName != null) {
			map.put("processName", processName);
	    	map.put("recordlist", new FlowChartQuery(processName).getProcessTaskNodes());
	    	startNodeName = pdf.getStart().getName();
	    	String js = pdf.getFormURLByTask("TodoWorklist").getUrl();
	    	map.put("TodoWorklistJavaScript", js);
	    	CommonLogger.logger.info("ע�빤���б��ѯJavascript����" + js);
		} else {
			//map.put("recordlist", getDeployedModuleProcesses());
		}
		
    	if (userLoginName.equals("admin") || new SysUser(userLoginName).isAdministrator()) {
    		funcList.add("�½�����");
    		if (!startNodeName.equals(""))
    			funcList.add(startNodeName);
    		funcList.add("���Ϲ���");
    		funcList.add("����������");
    		funcList.add("�鵵����");
    		funcList.add("�س�����");
    		funcList.add("���·��ɹ���");
    		funcList.add("���´򿪹���");
    	}
    	LoggerUtil.getLogger().info("CreateSheetPermission = " + funcList.containsFunction("�½�����"));
    	map.put("CreateSheetPermission", funcList.containsFunction("�½�����") 
    			|| funcList.containsFunction(startNodeName)
    			|| funcList.containsFunction("func_" + startNodeName));
    	map.put("CancelSheetPermission", funcList.containsFunction("���Ϲ���"));
    	map.put("RedrawSheetPermission", funcList.containsFunction("�س�����"));
    	map.put("BatchDealPermission", funcList.containsFunction("����������"));
    	map.put("ArchiveSheetPermission", funcList.containsFunction("�鵵����"));
    	map.put("RedistributePermission", funcList.containsFunction("���·��ɹ���"));
    	map.put("ReopenPermission", funcList.containsFunction("���´򿪹���"));
    	map.put("QUERY_TIMELIMIT", WorkItemListQuery.QUERY_TIMELIMIT);
    	
    	map.put("LuceneSupport", ProcessEngine.luceneSupportInvalid()?"inline-block":"none");
	}
	
	/*private List<String> getDeployedModuleProcesses() {  
		// ��ȡdeployed�������ƣ����ڲ�ѯ������ʾ�����б�
		String sql = "select FLOW_NAME from BPM_FLOW where MODULE_NAME='" + ProcessClient.MODULE + "'";
		List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
    	Map row;
    	List<String> recordlist = new ArrayList<String>();  
    	for (int i=0; i<list.size(); i++) {
    		row = (Map)list.get(i);
    		recordlist.add((String)row.get("FLOW_NAME"));
    	}   
    	return recordlist;
	}*/
}
