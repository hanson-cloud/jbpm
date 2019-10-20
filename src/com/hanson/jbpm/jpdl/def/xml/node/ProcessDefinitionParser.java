package com.hanson.jbpm.jpdl.def.xml.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.def.event.Event;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.flow.ProcessNodesOrder;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.IPrinterProvider;
import com.hanson.jbpm.mgmt.PrinterProviderSQL;
import com.hanson.jbpm.mgmt.ProcessClient;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.EntityDef;
import com.hanson.jbpm.jpdl.def.ExtQueryField;
import com.hanson.jbpm.jpdl.def.ExtendQuery;
import com.hanson.jbpm.jpdl.def.FormField;
import com.hanson.jbpm.jpdl.def.LinkDef;
import com.hanson.jbpm.jpdl.util.ClassLoaderUtil;

@SuppressWarnings("unchecked")
public class ProcessDefinitionParser {	
	
	public static String ATTRIBUTE_NAME = "name";
	public static String ATTRIBUTE_ENTITY = "entity";
	
	
	
	public ProcessDefinition parse(String xml) {
		try {
			ProcessDefinition pdf = new ProcessDefinition();
			
			Document doc  = DocumentHelper.parseText(xml);
			Element el = doc.getRootElement();
			
			pdf.setProcessName(el.attributeValue(ATTRIBUTE_NAME));
			String entity = el.attributeValue(ATTRIBUTE_ENTITY);
			if(entity != null && !"".equals(entity)) { //����ʵ��������Ϣ
				pdf.setEntityDef(new EntityDef(entity));
			}
			pdf.setModuleName(ProcessClient.MODULE);
			
			pdf.setPrinterProvider(parsePrintProvider(el));
			pdf.setRelativeURLs(parseRelativeURLs(el));
			pdf.setExtendQuery(parseExtendQuery(ProcessClient.MODULE, el));
			pdf.setAssignStrategy(parseAssignStrategy(el));			
			pdf.setProcessDuetime(parseProcessDuetime(el));
			pdf.setEvent(parseEvent(el));
			//pdf.setFields(parseFormFields(pdf.getProcessName()));
			
			ProcessNodesOrder.addProcess(pdf.getProcessName());
			
			CommonLogger.logger.info("����ģ��[" + ProcessClient.MODULE + "]����" + pdf.getProcessName() + "������Ϣ");
			
			String nodeClass;
			List<Element> list = el.elements();
			for (int i=0; i<list.size(); i++) {
				el = list.get(i);
				nodeClass = el.getName();				
				/* Ŀǰ�ݽ������½ڵ�����{start|end|state|node|decision|task} */				
				if (nodeClass.equals("start-state"))
					pdf.addNode(new StartStateParser().parser(el, pdf, this));
				if (nodeClass.equals("end-state"))
					pdf.addNode(new EndStateParser().parser(el, pdf, this));
				if (nodeClass.equals("state"))
					pdf.addNode(new StateParser().parser(el, pdf, this));
				if (nodeClass.equals("node"))
					pdf.addNode(new NodeParser().parser(el, pdf, this));
				if (nodeClass.equals("decision"))
					pdf.addNode(new DecisionParser().parser(el, pdf, this));
				if (nodeClass.equals("task-node"))
					pdf.addNode(new TaskNodeParser().parser(el, pdf, this));
				if (nodeClass.equals("fork"))
					pdf.addNode(new ForkParser().parser(el, pdf, this));
				if (nodeClass.equals("join"))
					pdf.addNode(new JoinParser().parser(el, pdf, this));
			}			
			
			pdf.resolveTransitionDestinations();
			
			return pdf;
		} catch (DocumentException e) {
			CommonLogger.logger.error(e,e);
			throw new RuntimeException("���������ļ��쳣: ", e);
		} catch (Exception e) {
			CommonLogger.logger.error(e,e);
			throw new RuntimeException("���������ļ��쳣: ", e);
		}
	}
	
	public IPrinterProvider parsePrintProvider(Element el)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Element elPrint = el.element("printerProvider");		
		if (elPrint != null) {
			IPrinterProvider printerProvider;
			List subtables = elPrint.elements("subtable");
			
			String[] subtable = null;
			String[] sqlSubtable = null;
			
			if (subtables != null) {
				int len = subtables.size();
				subtable = new String[len];
				sqlSubtable = new String[len]; 
				Element subt;
				for (int i=0; i<subtables.size(); i++) {
					subt = (Element) subtables.get(i);					
					subtable[i] = subt.attributeValue("name");
					sqlSubtable[i] = subt.attributeValue("SQL");
				}
			}
			String sql = elPrint.attributeValue("SQL");
			if (sql == null) {
				if (elPrint.element("SQL") != null)
					sql = elPrint.element("SQL").getText();
			}
			if (sql != null)
				printerProvider = new PrinterProviderSQL(sql, subtable, sqlSubtable);
			else
				printerProvider = (IPrinterProvider) Class.forName(elPrint.attributeValue("class")).newInstance();
			return printerProvider;
		} else
			return null;
	}
	
	private String parseAssignStrategy(Element el) {
		Element assign = el.element("assignment");
		if (assign == null) return "share";
		else return assign.attributeValue("strategy");
	}
	
	private String parseProcessDuetime(Element el) {
		String duetime = el.attributeValue("duedate");
		if (duetime != null) return duetime;
		else return String.valueOf(BpmConstants.DEFAULT_PROC_DUEDATE);
			
	}
	
	public ExtendQuery parseExtendQuery(String moduleName, Element el) {
		Element elQuery = el.element("extendQuery");
		ExtendQuery extQuery = new ExtendQuery();
		
		extQuery.setBpmFieldLeft(ExtendQuery.BPM_FIELD_LEFT);
		
		if (elQuery != null) {		
			JdbcTemplate jdbc = DaoFactory.getJdbc(moduleName);
			
			extQuery.setTableName(elQuery.attributeValue("tableName"));
			extQuery.setBpmFieldLeft(elQuery.attributeValue("bpmFieldLeft"));
			extQuery.setBpmFieldRight(elQuery.attributeValue("bpmFieldRight"));
			
			List<ExtQueryField> fields = new ArrayList<ExtQueryField>();
			List elFields = elQuery.elements("field");
			Element elField;
			ExtQueryField extField;
			String optionSQL, fieldExpr;
			for (int i=0; i<elFields.size(); i++) {
				extField = new ExtQueryField();
				elField = (Element) elFields.get(i);
				extField.setId(elField.attributeValue("id"));
				extField.setName(elField.attributeValue("name"));
				extField.setWidth(elField.attributeValue("width"));
				extField.setAlign(elField.attributeValue("align"));
				extField.setOperator(elField.attributeValue("operator"));
				extField.setSubElement(elField.attributeValue("subElement"));								
				fieldExpr = elField.attributeValue("fieldExpr");				
				extField.setFieldExpr(fieldExpr);				
				optionSQL = elField.attributeValue("option");	
				extField.setOptionSQL(optionSQL);
				if (optionSQL != null && optionSQL.trim().indexOf("select")>=0) {
					if (optionSQL.indexOf("?")<0)
						extField.setOption(jdbc.queryForList(optionSQL));		
					else {
						extField.setOption(getDefaultOptions());
					}
				} else {
					if (!optionSQL.trim().equals(""))
						extField.setOption(splitParameters(optionSQL));
				}
				extField.setAsParam(elField.attributeValue("asparam"));
				fields.add(extField);
			}
			extQuery.setFields(fields);
			
		} 
		return extQuery;
	}
	
	public List<LinkDef> parseRelativeURLs(Element el) {
		List<LinkDef> urls= new ArrayList<LinkDef>();
		Element root = el.element("linkPages");
		
		if (root == null) return urls;
		
		List<Element> links = root.elements("link");
		LinkDef link;
		for (int i=0; i<links.size(); i++) {
			link = new LinkDef();
			link.setName(links.get(i).attributeValue("name"));
			link.setUrl(links.get(i).attributeValue("url"));
			urls.add(link);
		}
		return urls;
	}
	
	
	public List<FormField> parseFormFields(String procName) {
		List<FormField> formFields = new ArrayList<FormField>();
		try {
			String fieldsPath = ClassLoaderUtil.getStringByExtendResource(
					BpmConstants.BPM_DIR_PATH + procName + "/fields.xml");
			CommonLogger.logger.debug("��ȡ���̱��ؼ���Ϣ:" + BpmConstants.BPM_DIR_PATH + procName + "/fields.xml");
			Document doc = DocumentHelper.parseText(fieldsPath);
			if(doc != null) {
				List<Element> fields = doc.getRootElement().selectNodes("//page/field");
				for(Element field : fields) {
					FormField formField = new FormField();
					formField.setName(field.attributeValue("name"));
					formField.setTitle(field.attributeValue("title"));
					formField.setDsName(field.attributeValue("dsName"));
					formField.setProvider(field.attributeValue("provider"));
					formField.setFormName(field.getParent().attributeValue("name"));
					formFields.add(formField);
				}
			}
			
		} catch(IOException ex) {
			CommonLogger.logger.error(ex, ex);
		} catch(DocumentException ex) {
			CommonLogger.logger.error(ex, ex);
		}
		return formFields;
	}
	
	/**
	 * 
	 * @param el
	 * @return
	 */
	public Event parseEvent(Element el) {
		Element eventEl = el.element("event");
		if(eventEl != null) {
			Event event = new Event();
			String type = eventEl.attributeValue("type");
			event.setType(type);
			String className = eventEl.getText();
			event.setClassName(className);
			return event;
		}
		return null;
	}
	
	/**
	 * �Ѹ�ʽ k,v..�ԵĲ�����ת�� List<Map> ����
	 * @param params
	 * @return
	 */
	private List<Map> splitParameters(String params) {
		String[] param = params.split(",");
		List<Map> list = new ArrayList();
		Map<String, String> m;
		for (int i=0; i<param.length; i++,i++) {
			m = new HashMap<String, String>();
			m.put("FKEY", param[i]);
			m.put("FVALUE", param[i+1]);
			list.add(m);
		}
		return list;
	}
	
	private List<Map> getDefaultOptions() {
		List<Map> list = new ArrayList<Map>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("FKEY", "��ѡ��");
		map.put("FVALUE", "");
		list.add(map);
		return list;
	}
}
