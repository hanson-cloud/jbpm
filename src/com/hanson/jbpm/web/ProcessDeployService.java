package com.hanson.jbpm.web;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.log.LoggerUtil;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.util.ClassLoaderUtil;
import com.hanson.jbpm.mgmt.ProcessClient;

public class ProcessDeployService {
	/**
	 * ����ģ�鶨���������̣��Ѳ�������̱Ƚϰ汾�ž����Ƿ����
	 * @param moduleName
	 * @throws IOException 
	 */
	public void deployProcesses() throws IOException {
		List<MetaProcDef> list = findDefinedProcess();		
		MetaProcDef def;		
		for (int i=0; i<list.size(); i++) {
			def = list.get(i);
			if (!checkVersionDeployed(def.moduleName, def.processName)) {
				LoggerUtil.getLogger().info("Deploying process: ģ��:" + def.moduleName + ", ����:" + def.processName);
				ProcessEngine.deploy(def.moduleName, def.processName);
			}
			
			makeModuleJbpmDirWhileNotExist(def.moduleName, def.processName);
			
			ProcessEngine.copyDeployFile(def.moduleName, def.processName);
		}
	} 
	
	private void makeModuleJbpmDirWhileNotExist(String moduleName, String processName)
	{
		
		File dir = new File(BpmConstants.CONFIG_PATH + moduleName);
		if (! (dir.exists() && dir.isDirectory()) ) {
			LoggerUtil.getLogger().info("Make dir:" + dir.getAbsolutePath());
			dir.mkdir();
		}
		
		dir = new File(BpmConstants.CONFIG_PATH + moduleName + "/" + processName);
		if (! (dir.exists() && dir.isDirectory()) ) {
			LoggerUtil.getLogger().info("Make dir:" + dir.getAbsolutePath());
			dir.mkdir();
		}
	}
	
	private boolean checkVersionDeployed(String module, String process) {
		String sql = "select * from BPM_FLOW where MODULE_NAME=? and FLOW_NAME=?";
		List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql, new String[]{module, process});
		return list.size()>0;
	}
	
	/**
	 * ��ģ��war��metainf/flowĿ¼����Ҫ�����������Ϣ
	 * @return
	 */
	private List<MetaProcDef> findDefinedProcess() {
		List<MetaProcDef> list = new ArrayList<MetaProcDef>();
		MetaProcDef def;
		String path = ClassLoaderUtil.getAbsolutePathOfClassLoaderClassPath();
		if(isLinux()) {
			path = path.substring(5);
		}else {
			path = path.substring(6);
		}
		path = path.substring(0, path.length() - 17) + "/META-INF/flow";
		
		LoggerUtil.getLogger().info("���̼��·�� = " + path);
		
		File[] flowPaths = new File(path).listFiles();
		
		if (flowPaths == null) {
			LoggerUtil.getLogger().info("���̼��·��������Ŀ¼�ļ� ...");
			return list;
		}
		
		for (int i=0; i<flowPaths.length; i++) {
			if (flowPaths[i].isDirectory()) {
				if (new File(flowPaths[i] + "/processdefinition.xml").exists()) {
					def = new MetaProcDef();
					def.moduleName = ProcessClient.MODULE;				
					def.processName = flowPaths[i].getName();
					LoggerUtil.getLogger().info("���̼��·���ļ�: " + flowPaths[i].getName());
					list.add(def);
				}
			}
		}
		return list;
	}	
	
	
	/**
	 * 
	 * @return
	 */
	public static boolean isLinux() {
		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
			return false;
		} else {
			return true;
		}
	}
	
	/*public BizTable findDefinedBizTable(String processName) throws DocumentException {
		BizTable bizTable = new BizTable(processName);
		FieldDef fieldDef;
		String path = ClassLoaderUtil.getAbsolutePathOfClassLoaderClassPath();
		//String tablesFile = path.substring(6) + "../../META-INF/script/sql.xml";
		String tablesFile = "../../META-INF/script/sql.xml";		
		if (new File(path.substring(6) + tablesFile).exists()) {			
			String content = readFile(tablesFile);			
			if (!content.equals("")) {
				List<Element> tables = DocumentHelper.parseText(content).getRootElement().elements("table");
				List<Element> fields;
				for (Element el: tables) {					
					if (el.attributeValue("flow") == null || 
						!el.attributeValue("flow").equals(processName))
						continue;
					LoggerUtil.getLogger().info("��ȡ���̱���:" + el.attributeValue("flow"));
					//bizTable = new BizTable((String)el.attributeValue("name"));					
					fields = el.elements("field");
					if (fields.size() > 0) bizTable.initFields(); 
					for (Element field: fields) {
						fieldDef = new FieldDef(field.attributeValue("name"), field.attributeValue("type"));
						bizTable.getFields().put(field.attributeValue("name"), fieldDef);
					}
				}
			}				
		} 
		return bizTable;		
	}*/
}
