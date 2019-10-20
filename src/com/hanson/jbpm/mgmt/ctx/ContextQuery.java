package com.hanson.jbpm.mgmt.ctx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public abstract class ContextQuery {
	
	public String queryByTaskId;
	
	protected abstract String getContext() throws BpmException;
	
	public String getContextInJson() throws BpmException, DocumentException {
		return toJsonString(getContext());
	}
	
	public Map getContextInMap(String text) throws DocumentException {
		Map<String, String> map = new HashMap<String, String>();
		if (text==null || text.equals("")) text = "<a></a>";
		Document doc = DocumentHelper.parseText(text);
		List list = doc.getRootElement().element("task").elements();
		Element el;
		for (int i=0; i<list.size(); i++) {
			el = (Element)list.get(i);
			map.put(el.getName(), el.getText());
		}
		return map;
	}
	
	private String toJsonString(String text) throws DocumentException {
		String data = "{";
		text =  text.replaceAll("\n", "<br/>").replaceAll("\r", "").replaceAll("'", "��");
		CommonLogger.logger.debug("Bind Context: " + text);
		if (text.trim().equals("")) text = "<a></a>";
		Document doc = DocumentHelper.parseText(text);
		List list = doc.getRootElement().elements();
		Element el;
		for (int i=0; i<list.size(); i++) {
			el = (Element)list.get(i);
			data = data + el.getName() + ":'" + el.getText() + "',";
		}
		if (data.indexOf(",")>0)
			data = data.substring(0, data.length()-1);
		return data + "}";
	}
}
