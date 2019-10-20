package com.hanson.jbpm.web.service.template;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.hanson.jbpm.log.CommonLogger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.suntek.ccf.env.RuntimeUtils;
import com.suntek.ccf.ui.util.FileUtil;
import com.hanson.jbpm.mgmt.ProcessClient;

public class TemplateUtil {
	private final static VelocityEngine engine = new VelocityEngine();
	private final static TemplateUtil instance = new TemplateUtil();
	
	public final static TemplateUtil getInstance() {
		return instance;
	}
	public void init() {
		try {			
			copyJBPMTemplate();
			Properties p = new Properties(); 
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, RuntimeUtils.getConfigDir() + ProcessClient.TEMPLATE);
			p.setProperty(Velocity.INPUT_ENCODING, "GBK");
			p.setProperty(Velocity.OUTPUT_ENCODING, "GBK");
			p.setProperty(Velocity.ENCODING_DEFAULT, "GBK");
			engine.init(p);
		} catch (Exception ex) { 
			CommonLogger.logger.error("��ʼ��ģ���쳣: " + ex.getMessage(), ex);
		}
	}
	
	private void copyJBPMTemplate() {
		checkDirExistedAndCreate(RuntimeUtils.getConfigDir(), ProcessClient.TEMPLATE);
		CommonLogger.logger.info(ProcessClient.TEMPLATE + "/defaultTask.html");
		new FileUtil().copyTemplate(ProcessClient.TEMPLATE + "/defaultTask.html");
		new FileUtil().copyTemplate(ProcessClient.TEMPLATE + "/FlowChart.html");
		new FileUtil().copyTemplate(ProcessClient.TEMPLATE + "/MyTodoWorklist.html");
		new FileUtil().copyTemplate(ProcessClient.TEMPLATE + "/ProcessedInfoDetail.html");
		new FileUtil().copyTemplate(ProcessClient.TEMPLATE + "/WorkSheetDetailFrame.html");
	}
	
	/**
	 * ����ģ�幹���ļ�
	 * @param templateFileName	ģ���ļ���
	 * @param map				Context����
	 */
	public String build(String templateFileName, Map<String, Object> map)
	throws Exception {
		StringWriter writer = null;
		try {	
			VelocityContext context = new VelocityContext();
			
			/* ���������Ĳ��� */
			Iterator<String> sets = map.keySet().iterator();
			String key;
			
			while(sets.hasNext()) {
				key = sets.next();
				context.put(key, map.get(key));
			}
			
			/* ����ļ� */			
			Template template = engine.getTemplate(templateFileName);
			writer = new StringWriter();
			if ( template != null)
				template.merge(context, writer);
			
			StringBuffer buf = writer.getBuffer();
			
			writer.flush();	
			
			return buf.toString();
		} catch (ResourceNotFoundException ex) {
			copyJBPMTemplate();
			throw ex;
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {writer.close();} catch(Exception ex) {}
		}
	}
	
	private void checkDirExistedAndCreate(String path, String dir) {
		File file = new File(path + dir);
		if (!file.exists()) {
			CommonLogger.logger.debug("��������ģ��Ŀ¼: " + dir);
			file.mkdir();
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new File("1").getAbsolutePath());
		String templateFileName = "/src/com/suntek/web/plugin/reportchart/plugin.vm";
		Map<String, Object> map = new HashMap<String, Object>();
		System.out.println(TemplateUtil.getInstance().build(templateFileName, map));
	}
}
