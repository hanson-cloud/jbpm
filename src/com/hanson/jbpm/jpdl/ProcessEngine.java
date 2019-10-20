package com.hanson.jbpm.jpdl;

import java.io.IOException;
import java.util.List;

import com.hanson.jbpm.dev.LuceneService;
import com.hanson.jbpm.dev.NewInstanceService;
import com.hanson.jbpm.jpdl.def.ExtendQuery;
import com.hanson.jbpm.jpdl.def.FormField;
import com.hanson.jbpm.jpdl.util.ProcessDeployer;
import com.hanson.jbpm.mgmt.IPrinterProvider;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.util.jdbc.Dialect;
import com.suntek.eap.util.jdbc.DialectFactory;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinitionCache;
import com.hanson.jbpm.jpdl.exe.ctx.ProcessInstanceMgmt;

public class ProcessEngine { 
	
	private static String clientModuleName = "jbpm"; 	
	private static Dialect dialect = null;
	
	/**
	 * ��ȡ����ʵ��������
	 * @param processName
	 * @return
	 */
	public static ProcessInstanceMgmt getProcessInstanceMgmt(String processName) {
		return new ProcessInstanceMgmt(getProcessDefinition(processName));		
	}	
	
	/**
	 * �����������ļ�����һ������
	 * @param processName
	 */
	public static ProcessDefinition getProcessDefinition(String processName) {		
		return ProcessDefinitionCache.getProcessDefinition(processName);
	}
	
	public static IPrinterProvider getPrinterProvider(String processName) {
		return getProcessDefinition(processName).getPrinterProvider();
	}
	
	public static ExtendQuery getExtendQuery(String processName) {
		return getProcessDefinition(processName).getExtendQuery();
	}
	
	public static LuceneService getLuceneService(String processName) {
		return new LuceneService(processName);
	}
	
	public static List<FormField> getFields(String processName) {
		return getProcessDefinition(processName).getFields();
	}
	
	/**
	 * ����ģ������
	 * @param module
	 * @param processName
	 */
	public static void deploy(String moduleName, String processName) {
		ProcessDeployer.deploy(moduleName, processName);
	}
	
	public static void copyDeployFile(String moduleName, String processName) throws IOException {
		ProcessDeployer.copyDeployFile(moduleName, processName);		
	}
		
	/**
	 * ��������ʵ���������������̷���ĵ���
	 * ��������Ҫ���ϼ���������ͬһ��ģ��
	 * @return
	 */
	public static NewInstanceService getNewInstanceService() {
		return new NewInstanceService();
	}
		
	public static boolean luceneSupportInvalid() {
		return AppHandle.getHandle(clientModuleName).getProperty("luceneSupport").equals("true");
	}
	
	public static boolean luceneRealtimeIndex() {
		return AppHandle.getHandle(clientModuleName).getProperty("realtimeIndex").equals("true");
	}
	
	public static Dialect getDialect() {
		if (dialect == null)
			return DialectFactory.getDialect(AppHandle.getHandle("jbpm").getDatasourceName());
		else
			return dialect;
	}
	
	public static String getClientModuleName() {
		return clientModuleName;
	}
}
