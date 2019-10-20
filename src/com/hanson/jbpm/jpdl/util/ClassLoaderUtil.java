package com.hanson.jbpm.jpdl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ���������࣬classpath�µ���Դ�ļ��������ļ��ȡ�
 * getExtendResource(StringrelativePath)����������ʹ��../����������classpath�ⲿ����Դ��
 */
public class ClassLoaderUtil {
	private static ClassLoader classLoader = ClassLoaderUtil.class.getClassLoader();
	/*private static Class loadClass(String className) {
		try {
			return getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("class not found '" + className + "'", e);
		}
	}*/

	/**
	 * �õ��������
	 * 
	 * @return
	 */
	private static ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * �ṩ�����classpath����Դ·���������ļ���������
	 * 
	 * @param relativePath
	 *            ���봫����Դ�����·�����������classpath��·���������Ҫ����classpath�ⲿ����Դ����Ҫʹ�� ../������
	 * @return �ļ�������
	 * @throws IOException
	 * @throws MalformedURLException
	 */ 
	/*public static InputStream getStream(String relativePath)
			throws MalformedURLException, IOException {
		if (!relativePath.contains("../")) {
			return getClassLoader().getResourceAsStream(relativePath);
		} else {
			return ClassLoaderUtil.getStreamByExtendResource(relativePath);
		}
	}*/

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static InputStream getStream(URL url) throws IOException {
		if (url != null) {
			return url.openStream();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param relativePath
	 *            ���봫����Դ�����·�����������classpath��·���������Ҫ����classpath�ⲿ����Դ����Ҫʹ�� ../������
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream getStreamByExtendResource(String relativePath)
			throws MalformedURLException, IOException {
		return ClassLoaderUtil.getStream(ClassLoaderUtil.getExtendResource(relativePath));
	}
	/**
	 * ��ȡָ���ļ������ݣ����ַ�����ʽ����
	 * @param relativePath �����classpath��·��
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getStringByExtendResource(String relativePath)
			throws MalformedURLException, IOException {
		InputStream is = null;
		BufferedReader br = null; 
		try {
			is = ClassLoaderUtil.getStreamByExtendResource(relativePath);
			br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			while (br.ready()) {
				sb.append(br.readLine() + "\n");
			}			
			return sb.toString();
		} catch (IOException ex) {
			throw ex;
		} finally {			
			try { if (br != null) br.close(); } catch(Exception ex){}
			try { if (is != null) is.close(); } catch(Exception ex){}
		}		
	}

	/**
	 * �ṩ�����classpath����Դ·�����������Զ�������һ��ɢ�б�
	 * 
	 * @param resource
	 * @return
	 */
	/*public static Properties getProperties(String resource) {
		Properties properties = new Properties();
		try {
			properties.load(getStream(resource));
		} catch (IOException e) {
			throw new RuntimeException("couldn't load properties file '"
					+ resource + "'", e);
		}
		return properties;
	}*/

	/**
	 * �õ���Class���ڵ�ClassLoader��Classpat�ľ���·���� URL��ʽ��
	 * 
	 * @return
	 */
	public static String getAbsolutePathOfClassLoaderClassPath() {
		return ClassLoaderUtil.getClassLoader().getResource("").toString();
	}

	/**
	 * 
	 * @param relativePath
	 *            ���봫����Դ�����·�����������classpath��·���������Ҫ����classpath�ⲿ����Դ����Ҫʹ ��../������
	 * @return ��Դ�ľ���URL
	 * @throws MalformedURLException
	 */
	private static URL getExtendResource(String relativePath)
			throws MalformedURLException {
		
		if (!relativePath.contains("../")) {
			return ClassLoaderUtil.getResource(relativePath);
		}
		
		String classPathAbsolutePath = ClassLoaderUtil.getAbsolutePathOfClassLoaderClassPath();
		
		if (relativePath.substring(0, 1).equals("/")) {
			relativePath = relativePath.substring(1);
		}

		String wildcardString = relativePath.substring(0, relativePath.lastIndexOf("../") + 3);
		relativePath = relativePath.substring(relativePath.lastIndexOf("../") + 3);
		int containSum = ClassLoaderUtil.containSum(wildcardString, "../");
		classPathAbsolutePath = ClassLoaderUtil.cutLastString(classPathAbsolutePath, "/", containSum);
		String resourceAbsolutePath = classPathAbsolutePath + relativePath;

		URL resourceAbsoluteURL = new URL(resourceAbsolutePath);
		return resourceAbsoluteURL;
	}

	/**
	 * 
	 * @param source
	 * @param dest
	 * @return
	 */
	private static int containSum(String source, String dest) {
		int containSum = 0;
		int destLength = dest.length();
		while (source.contains(dest)) {
			containSum = containSum + 1;
			source = source.substring(destLength);
		}
		return containSum;
	}

	/**
	 * 
	 * @param source
	 * @param dest
	 * @param num
	 * @return
	 */
	private static String cutLastString(String source, String dest, int num) {
		for (int i = 0; i < num; i++) {
			source = source.substring(0, source.lastIndexOf(dest, source.length() - 2) + 1);
		}
		return source;
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	public static URL getResource(String resource) {
		return ClassLoaderUtil.getClassLoader().getResource(resource);
	}

	public static void setClassLoader(ClassLoader classLoader) {
		ClassLoaderUtil.classLoader = classLoader;
	}
}