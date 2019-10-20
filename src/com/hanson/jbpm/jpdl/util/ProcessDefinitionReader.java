package com.hanson.jbpm.jpdl.util;


import com.hanson.jbpm.web.XMLFileUtil;
import org.dom4j.DocumentException;

import com.suntek.eap.core.config.SystemConfig;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.mgmt.ProcessClient;

public class ProcessDefinitionReader {
	public final static String BPM_DIR_PATH = BpmConstants.BPM_DIR_PATH; 
	private final static String BPM_CONFIG_PATH = SystemConfig.getEAPHome() + "/eapserver/application/jbpm/";
	/**
	 * ��ȡ���̶����ļ�������
	 * @param pdfName
	 * @return
	 * @throws DocumentException 
	 */
	public String getPdfContent(String pdfName) throws DocumentException{
		return XMLFileUtil.read(BPM_CONFIG_PATH + ProcessClient.MODULE + "/" + pdfName).asXML();
	}
}
