package com.hanson.jbpm.web;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.suntek.ccf.dao.DaoFactory;
import com.suntek.eap.core.config.SystemConfig;
import com.hanson.jbpm.jpdl.util.ClassLoaderUtil;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.suntek.util.string.StringHelper;

public class BoTopicXMLDB extends XMLDB {
	
	private final static String topicFile = SystemConfig.getEAPHome() + "/eapserver/application/report/topic.xml";
	
	public BoTopicXMLDB() throws DocumentException {
		this.setFileFullPath(topicFile);
		Document doc = XMLFileUtil.read(topicFile);
		this.setDocument(doc);
	}
	
	public void deployFlowInTopicTree() throws DocumentException {		
		Element templateDs = (Element)getDocument().selectSingleNode("//datasource[@name='BPM_INST_HIS']");
		String templateDsXml = templateDs.asXML();
		Element templateTopic = (Element)getDocument().selectSingleNode("//topic[@id='03']");
		String templateTopicXml = templateTopic.asXML();		
		//getDocument().getRootElement().remove(templateEl);
		
		String sql = "select FLOW_ID, FLOW_NAME from BPM_FLOW";
		List flows = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
		
		int index = 4;
		
		String xml;
		Map row;
		Element el;
		for (int i=0; i<flows.size(); i++) {			
			xml = templateTopicXml;
			row = (Map)flows.get(i);
			
			el = (Element)getDocument().selectSingleNode("//topic[@id='0" + index + "']");
			if (el != null) {
				//getDocument().getRootElement().remove(el);
				continue;
			}
			xml = StringHelper.replace(xml, "id=\"03", "id=\"0" + index);
			xml = StringHelper.replace(xml, "������", (String)row.get("FLOW_NAME"));
			xml = StringHelper.replace(xml, "BPM_INST_HIS", "BPM_" + (String)row.get("FLOW_ID"));
			
			el = DocumentHelper.parseText(xml).getRootElement();
			
			getDocument().getRootElement().add(el);
			index = index + 1;
			
			xml = templateDsXml;
			xml = StringHelper.replace(xml, "BPM_INST_HIS", "BPM_" + (String)row.get("FLOW_ID"));			
			Element root = DocumentHelper.parseText(xml).getRootElement();
			el = root.element("dimensions");
			
			String path = ClassLoaderUtil.getAbsolutePathOfClassLoaderClassPath();
			path = path.substring(6) + "../../META-INF/flow";
			String file = path + "/" + (String)row.get("FLOW_NAME") + "/fields.xml";
			
			if (!new File(file).exists())
				continue;
			
			List fields = XMLFileUtil.read(file).getRootElement().selectNodes("//field[@provider]");
			for (int j=0; j<fields.size(); j++) {
				Element tmp = (Element)fields.get(j);
				Element dim = el.addElement("dimension");
				dim.addAttribute("name", tmp.attributeValue("title"));
				dim.addAttribute("field", tmp.attributeValue("name"));
			}
			
			getDocument().getRootElement().add(root);
		}
		this.write2File();
	}
}
