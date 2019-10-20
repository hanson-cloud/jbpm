package com.hanson.jbpm.tag.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.suntek.ccf.log.CCFLogger;
import com.suntek.ccf.ui.util.VelocityUtil;

public class GridBodyBuilder {
	/* ģ���ļ��� */
	private static final String templateFileName = "notes/GridTemplate.html";	
	/**
	 * ��ģ������GTGrid��ҳ������
	 * @param id
	 * @param rowNumPerPage
	 * @param css
	 * @param moduleName
	 * @param provider
	 * @param parameters
	 * @param onCellClicked
	 * @param body
	 * @param serviceId
	 * @return
	 */
	public String build(String id, String rowNumPerPage, String css, String moduleName, 
			String provider, String parameters, String body, String exportSupport, 
			String exportHandler) {
		try {
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			body = "<body>" + body + "</body>";
			Document doc = DocumentHelper.parseText(body);			
			Element root = doc.getRootElement();		
			
			map.put("gridId", id);
							
			map.put("rowNumPerPage", rowNumPerPage);
			
			map.put("css", css);
			map.put("parameters", parameters);
			map.put("moduleName", moduleName);		
			map.put("exportHandler", exportHandler);
			
			map.put("provider", "/" + moduleName + "/ccftag/pojo/" + provider);
						
			if("true".equals(exportSupport)){
				map.put("exportBtn", "| exportbtn ");
			}
						
			/* ��ȡ��ͷ���� */
			map.put("header", readHeaderDef(root));
						
			body = VelocityUtil.build(templateFileName, map);
			return body;
		} catch (Exception ex) {
			CCFLogger.logger.error(ex, ex);
			return "��ǩ���ݷǷ�: " + ex.getMessage();
		}
	}
	
	/**
	 * ��ȡ��ͷ������Ϣ
	 * @param root
	 * @return
	 */
	private List<GridFieldTag> readHeaderDef(Element root) {
		List list = root.selectNodes("//field");
		List<GridFieldTag> ret = new ArrayList<GridFieldTag>();
		Element el;
		GridFieldTag field;
		for (int i=0; i<list.size(); i++) {
			el = (Element)list.get(i);
			field = new GridFieldTag();
			
			field.setAlign(el.attributeValue("align"));
			field.setHeader(el.attributeValue("header"));
			field.setName(el.attributeValue("name").toUpperCase());
			field.setRender(el.attributeValue("render"));
			field.setWidth(el.attributeValue("width"));
			field.setNeedExport(el.attributeValue("needExport"));
			ret.add(field);
		}
		return ret;
	}	
}
