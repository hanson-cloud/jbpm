package com.hanson.jbpm.web.service;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.jpdl.util.ClassLoaderUtil;
import com.hanson.jbpm.jpdl.util.ProcessDefinitionReader;

public class ProcessDefImageService extends HttpRequestService {
	private final static long serialVersionUID = 1l;
	
	private  int PROCESS_IMAGE_BYTES_MAX_LENGTH = 100000;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		String processName = request.getParameter("pdf");
		
		/* ��ȡ���̵Ķ�Ӧ��ͼƬ�ļ� */
		InputStream processInputStream = null;
		try {
			processInputStream = ClassLoaderUtil.getStreamByExtendResource(
					ProcessDefinitionReader.BPM_DIR_PATH + processName +"/processimage.jpg");
		
			/* �����̵�ͼƬ�ļ�����Ϊ�ֽ������ */
			byte[] b = new byte[PROCESS_IMAGE_BYTES_MAX_LENGTH];
		
			processInputStream.read(b);
			ServletOutputStream stream = response.getOutputStream();
			stream.write(b);		
			response.flushBuffer();
			stream.close();
		} catch (IOException ex) {
			throw ex;
		} finally {
			try { if (processInputStream != null) processInputStream.close(); } catch(Exception ex) {}
		}
	}

}
