package com.hanson.jbpm.web;

import java.io.FileOutputStream;

import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


public class XMLDB {
	private Document document = null;
	private String fileFullPath = null;
	
	public void modify(String xpath, String attrName, String attrValue) {
		Element el = (Element)document.selectSingleNode(xpath);
		if (el == null)
			throw new RuntimeException("�����ڵ�Xpath·����" + xpath);
		
		el.attribute(attrName).setValue(attrValue);		
		write2File();
	}
	
	protected void write2File() {
		XMLWriter output = null;
		FileOutputStream fos = null;
		try {
			OutputFormat format = OutputFormat.createPrettyPrint(); // ����XML�ĵ������ʽ
			
			format.setEncoding("GBK"); 								// ����XML�ĵ��ı�������
			format.setSuppressDeclaration(false);
			format.setIndent(true); 								// �����Ƿ�����
			format.setIndent("   "); 								// �Կո�ʽʵ������
			format.setNewlines(true); 								// �����Ƿ���
			
			fos = new FileOutputStream(fileFullPath);
			output = new XMLWriter(fos, format);			
			output.write(document);				
		} catch (Exception ex) {
			CommonLogger.logger.error(ex,ex);
		} finally {
			try {output.close();}catch(Exception e){}
			try {fos.close();}catch(Exception e){}
		}
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setFileFullPath(String fileFullPath) {
		this.fileFullPath = fileFullPath;
	}

	public Document getDocument() {
		return document;
	}

	public String getFileFullPath() {
		return fileFullPath;
	}
}
