package com.hanson.jbpm.tag.form;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.suntek.ccf.ui.tag.lib.CCFTag;

public class RadioBoxTag extends CCFTag {
	private final static long serialVersionUID = 1l;
	
	/* ��ǩ�����ֺ�id */
	//String id;              //split��ID
	String style;			//���صķ���
	
	//public void setId(String id) { this.id = id; }
	//public String getId() { return this.id; }

	public String getStyle () {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	
	public int doEndTag() {
		try {
			String cont = "<Items>" + this.getBodyContent().getString() + "</Items>";
			Document doc = DocumentHelper.parseText(cont);
			List list = doc.selectNodes("//selectItem");
			Element el;
			for (int i=0; i<list.size(); i++) {
				el = (Element)list.get(i);
				print(el.attributeValue("itemLabel") + ":<input type='radio'" +
						" name='" + id + "'" +
						" value='" + el.attributeValue("itemValue") + "'/>&nbsp;");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		return SKIP_BODY;
	}

}
