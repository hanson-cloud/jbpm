package com.hanson.jbpm.jpdl.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.log.CCFLogger;
import com.suntek.eap.core.app.AppHandle;
import com.hanson.jbpm.jpdl.def.BpmConstants;

public class FileUtil {
	/**
	 * �ļ�����
	 * @param fromFile
	 * @param toFile
	 * @throws IOException
	 */
	public void copy(String fromFile, String toFile, boolean checkExist) throws IOException {
		if (checkExist && new File(toFile).exists())
			return;
        InputStream in = null;
        OutputStream out = null;
        try {        	 
        	in = new BufferedInputStream(new FileInputStream(fromFile));
        	out = new BufferedOutputStream(new FileOutputStream(toFile));
        	for (int b = in.read(); b != -1; b = in.read()) {
        		out.write(b);
        	}
        } catch(Exception ex) {     
        	CCFLogger.logger.error(ex, ex);
        } finally {
        	try { in.close(); } catch(IOException e) {}
        	try { out.close(); } catch(IOException e) {}
        }
    }
	
	/**
	 * ����ģ���ļ���ָ��λ��
	 * @param fileName
	 * @throws IOException
	 */
	public void copy(String module, String process, String fileName, boolean checkExist) 
	{
		String toFile = BpmConstants.CONFIG_PATH + module + "/" + process + "/" + fileName;		
		
		CommonLogger.logger.info("Copy deployed file: " + toFile);
		
		if (checkExist && new File(toFile).exists())
			return;
        InputStream in = null;
        OutputStream out = null;
        try {
        	in = this.getClass().getClassLoader().getResourceAsStream("../../META-INF/flow/" + process + "/" + fileName);
        	out = new BufferedOutputStream(new FileOutputStream(toFile));
        	for (int b = in.read(); b != -1; b = in.read()) {
        		out.write(b);
        	}
        } catch(Exception ex) {  
        	
        	if (!fileName.contains("fields.xml")) {
        		CCFLogger.logger.error(ex, ex);
        		throw new RuntimeException(ex);	
        	}else {
        		CCFLogger.logger.warn("��fields.xml�ļ�,����");
        	}
        } finally {
        	try { in.close(); } catch(Exception e) {}
        	try { out.close(); } catch(Exception e) {}
        }
    }
	
	
	/**
	 * ��ȡ����������ļ���·��
	 * @param batchId
	 * @return
	 */
	public String getContextFilePath() {
		String path = "/context/" + new SimpleDateFormat("yyyyMM")
			.format(Calendar.getInstance().getTime());
		String filePath = AppHandle.getHandle("jbpm").getProperty("uploadPath")
			+ path;
		try {
			
			File file = new File(filePath);
			if(!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			CommonLogger.logger.error("��ȡ��������������ļ���·��ʧ��!");
			CommonLogger.logger.error(e, e);
		}
		return filePath;
	}
	
	
	
}
