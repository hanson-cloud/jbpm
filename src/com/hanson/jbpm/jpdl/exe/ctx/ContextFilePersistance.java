package com.hanson.jbpm.jpdl.exe.ctx;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.hanson.jbpm.jpdl.util.FileUtil;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�ContextFilePersistance.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Dec 30, 2013
 */
@SuppressWarnings("unchecked")
public class ContextFilePersistance {
	
	private static List<String> contentCache = Collections.synchronizedList(new ArrayList());//����
	
	private Object signal = new Object();
	
	public static int maxCacheCount = 50;
	
	public static ContextFilePersistance build() {
		return new ContextFilePersistance();
	}
	
	
	/**
	 * д�ļ�
	 * @param instId
	 * @param taskId
	 * @param context
	 */
	public void save(String instId, String taskId, String context) {
		if(!"".equals(context) && !" ".equals(context)) {
			String content = buildContext(instId, taskId, context);
			
			writeFile(content);
			
		}
	}
	
	
	/**
	 * 
	 * @param content
	 */
	private void writeFile(String content) {
		contentCache.add(content);
		if(contentCache.size() >= maxCacheCount) {
			synchronized(signal) {
				if(contentCache.size() < maxCacheCount) return;
				try {
					String path = new FileUtil().getContextFilePath();
					String filePath = path + "/" + ProcessClient.HOST + "_"
							+ new SimpleDateFormat("yyyyMMdd")
							.format(Calendar.getInstance().getTime()) + ".txt";
					File file = new File(filePath);
					if(!file.exists()) {
						file.createNewFile();
					}
					FileWriter w = new FileWriter(file, true);
					for(String s : contentCache) {
						w.write(s);
					}
					w.close();
					contentCache.clear();
					CommonLogger.logger.debug("[ContextFilePersistance]дContext�ļ����, ��д��"
							+ contentCache.size() + "�����ݵ��ļ�:" + filePath + "��");
				} catch (IOException e) {
					CommonLogger.logger.error(e, e);
				}
			}
		}
	}
	
	
	private String buildContext(String instId, String taskId, String context) {
		StringBuffer b = new StringBuffer();
		b.append(instId).append(",").append(taskId).append(",").append(context);
		return b.toString().replace("\r", "").replace("\n", "").replace("\t", "") + "\r\n";
	}
}
