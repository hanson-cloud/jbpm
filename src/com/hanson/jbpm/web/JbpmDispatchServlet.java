package com.hanson.jbpm.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.jpdl.exe.async.TriggerTaskCache;
import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.suntek.eap.core.config.SystemConfig;
import com.hanson.jbpm.identity.OrgCache;
import com.hanson.jbpm.identity.OrgCacheClient;
import com.hanson.jbpm.jpdl.ProcessDefinitionService;
import com.hanson.jbpm.jpdl.exe.calendar.BpmCalendar;
import com.hanson.jbpm.jpdl.util.ClassLoaderUtil;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.hanson.jbpm.web.service.HttpRequestService;
import com.hanson.jbpm.web.service.PageTemplateService;
import com.hanson.jbpm.web.service.template.TemplateUtil;
import com.suntek.opencc.OpenCC;


/**
 * ProcessServlet
 */
public class JbpmDispatchServlet extends HttpServlet {
	private final static long serialVersionUID = 1l;
	
	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(2);;
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doPost(request, response);
	}
	 
	public void init(ServletConfig config) throws ServletException {
		super.init(config);	
		ProcessClient.MODULE = getServletContext().getContextPath();
		ProcessClient.MODULE = ProcessClient.MODULE.substring(1);
		ProcessClient.TEMPLATE = readModuleConfig("JBPMtemplate");
		CommonLogger.logger.info("����ģ��:" + ProcessClient.MODULE + "������, ģ������ " + ProcessClient.TEMPLATE);
		try {
			new ProcessDeployService().deployProcesses();
		} catch (IOException e1) {
			throw new ServletException(e1);
		}
		
		TemplateUtil.getInstance().init();	
		
		OpenCC.getComponentContainer().register(ProcessDefinitionService.class);
		OpenCC.getComponentContainer().register(OrgCacheClient.class);
		
		try { 
			//ProcessClient.HOST = InetAddress.getLocalHost().getHostAddress().split("\\.")[3];
			//ProcessClient.HOST = ProcessClient.HOST.substring(ProcessClient.HOST.length()-1);
			//new BoTopicXMLDB().deployFlowInTopicTree();			
		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
		}
		
		CommonLogger.logger.info("��ʼ����֯�ܹ�����");
		//ÿ��������֯�ܹ�����
		service.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				OrgCache.getCache().unload();
				OrgCache.getCache().load();
			}
		}, 5, 24 * 3600, TimeUnit.SECONDS);
		
		TriggerTaskCache.getInstance().start(); //�����¼���������ִ���߳�
		
		BpmCalendar.getInstance().load();
		if(BpmCalendar.getInstance().hasHolidayConfig()) {
			//ÿ�춨ʱ���ؽڼ�������
			service.scheduleAtFixedRate(new TimerTask(){
				public void run() {
					BpmCalendar.getInstance().reload();
				}
			}, 24 * 3600, 24 * 3600, TimeUnit.SECONDS);
		}
	}
	
	/**
	 * ���� action ������ֵ, ת����ͬ�� service ���д���.
	 * service Name = actionֵ + 'Service'
	 * service ���Ͱ���:
	 * 1. ����ͼ��ѯ
	 * 2. ����ڵ�����ύ����
	 * 3. ����
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {		
		try {
    		ProcessClient.MODULE = getServletContext().getContextPath();
    		ProcessClient.MODULE = ProcessClient.MODULE.substring(1);
    		//CommonLogger.logger.debug("Current Module's name is: " + ProcessClient.MODULE);
    		
    		response.setContentType("text/html; charset=GBK");
    				  
    		String action = request.getParameter("action");
    		CommonLogger.logger.info(ProcessClient.MODULE + "/ JBMP action = " + action);
		
			HttpRequestService service = null;
			if (action == null) {
				service = new PageTemplateService();
			} else {
				action = "com.suntek.jbpm.web.service." + action + "Service";
				service = (HttpRequestService)Class.forName(action).newInstance();
				service.setServletContext(this.getServletContext());				
			}	
			service.doPost(request, response);
		} catch (Exception ex) {
		    CommonLogger.logger.error(ex.getMessage());
			CommonLogger.logger.error(ex, ex);
			throw new ServletException(ex.getMessage());
		}
	}		
	
	/**
	 * ����Դ�ļ���ȡģ��������
	 * @param prop
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws DocumentException 
	 */
	private String readModuleConfig(String prop) {
		try {
			String path = (SystemConfig.getEAPHome().indexOf("tomcat")>=0)?"../../META-INF/":"META-INF";
			String config = ClassLoaderUtil.getStringByExtendResource(path + "/appconfig.xml");
			
			Document doc = DocumentHelper.parseText(config);
			Element el = (Element) doc.selectSingleNode("//param[@key='JBPMtemplate']");
			if (el != null) {
				String template = el.getText();
				CommonLogger.logger.info("��ȡģ������ģ�����ͣ�" + template);
				return template;
			}
			CommonLogger.logger.info("δ��ȡ��ģ������ģ�����ͣ�ȡĬ������");
			return "notes";
		} catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			return "notes";
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		TriggerTaskCache.getInstance().stop();	
		BpmCalendar.getInstance().clear();
		service.shutdown();
		service = null;
	}
}
