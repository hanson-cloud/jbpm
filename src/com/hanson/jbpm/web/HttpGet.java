package com.hanson.jbpm.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.hanson.jbpm.log.CommonLogger;

public class HttpGet {
	
	public static String jsp(HttpServletRequest request, String path) {
		HttpURLConnection conn = null;
	    InputStream   is = null;
	    BufferedReader br = null;
	    String str = "";
	    String target = "http://" + request.getServerName() +":"+ request.getServerPort() +"/"+ request.getContextPath() + path;
	    try {            
            URL url = new URL(target);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            is = conn.getInputStream();   
            br = new   BufferedReader(new   InputStreamReader(is));   
            while (br.ready())   {   
            	str   +=   br.readLine()   +   "\n";   
            }   
            return str;
        } catch (IOException e) {
            CommonLogger.logger.error(e);
            return null;
        } finally {
        	try { if (is != null) is.close(); } catch (IOException e) {} 
        	try { if (br != null) br.close(); } catch (IOException e) {}         	          
            if (conn != null) conn.disconnect() ;
        }

	}
	
	public static String html(ServletContext processDefinitionContext, String path) {
		if (path == null) 
			throw new RuntimeException("HttpGetting path can't be null");
		
		CommonLogger.logger.debug("HttpGet: " + path);
		
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String line = "";
		String text = "";
		try {	      			
			is = processDefinitionContext.getResourceAsStream(path);
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			do {
				text = text + line;
				line = br.readLine();				
			} while (line != null);		
			
			return text;
		} catch (MalformedURLException e) {			
			CommonLogger.logger.error(e, e);
			throw new RuntimeException("HttpGetting path is invalid��" + e.getMessage());
		} catch (IOException e) {
			CommonLogger.logger.error(e);
			throw new RuntimeException("HttpGetting occur a IO exception��" + e.getMessage());
		} finally {
			try { br.close(); } catch (Exception e) {}
			try { is.close(); } catch (Exception e) {}
			try { isr.close(); } catch (Exception e) {}
		}
	}
	
	
	/**
	 * ��http����������й���ָ���Ĳ�����
	 * ��queryString: id=1&name=2323&bizId=90 ����name���id=1&bizId=90
	 * @param queryString
	 * @param filters
	 * @return
	 */
	public static String filterQueryString(String queryString, String[] filters) {
		
		for(String filter : filters) {
			int begin = queryString.indexOf(filter + "=");
			if(begin == -1) continue;
			int end = queryString.indexOf("&", begin) + 1;
			if((end == 0 || end == -1)) {
				end = queryString.length();
				if(begin != 0) {
					begin--;
				}
			}
			queryString = queryString.replace(queryString.substring(begin, end), "");
			
		}
		return queryString;
	}
	
	public static void main(String[] args) {
		
	}
}
