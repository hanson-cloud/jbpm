package com.hanson.jbpm.web;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class XMLFileUtil {
	public static Document read(String file) throws DocumentException {
		File xml = new File(file);
		SAXReader reader = new SAXReader();
		reader.setEncoding("GBK");
		Document doc = reader.read(xml);
		return doc;
	}
}
