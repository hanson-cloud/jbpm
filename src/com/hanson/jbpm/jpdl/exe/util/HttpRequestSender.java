package com.hanson.jbpm.jpdl.exe.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Vector;

import com.hanson.jbpm.log.LoggerUtil;

public class HttpRequestSender {

	private String defaultContentEncoding;

	public HttpRequestSender() {
		this.defaultContentEncoding = Charset.defaultCharset().name();
	}

	/**
	 * ����GET����
	 * 
	 * @param urlString URL��ַ
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	public HttpResponse sendGet(String urlString) throws IOException {
		return this.send(urlString, "GET", null, null);
	}

	/**
	 * ����GET����
	 * 
	 * @param urlString URL��ַ
	 * @param params  ��������
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	public HttpResponse sendGet(String urlString, Map<String, String> params) throws IOException {
		return this.send(urlString, "GET", params, null);
	}

	/**
	 * ����GET����
	 * 
	 * @param urlString  URL��ַ
	 * @param params  ��������
	 * @param propertys  ��������
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	public HttpResponse sendGet(String urlString, Map<String, String> params, 
			Map<String, String> propertys) throws IOException {
		return this.send(urlString, "GET", params, propertys);
	}

	/**
	 * ����POST����
	 * 
	 * @param urlString  URL��ַ
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	public HttpResponse sendPost(String urlString) throws IOException {
		return this.send(urlString, "POST", null, null);
	}

	/**
	 * ����POST����
	 * 
	 * @param urlString  URL��ַ
	 * @param params  ��������
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	public HttpResponse sendPost(String urlString, Map<String, String> params) throws IOException {
		return this.send(urlString, "POST", params, null);
	}

	/**
	 * ����POST����
	 * 
	 * @param urlString URL��ַ
	 * @param params  ��������
	 * @param propertys ��������
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	public HttpResponse sendPost(String urlString, Map<String, String> params, 
			Map<String, String> propertys) throws IOException {
		return this.send(urlString, "POST", params, propertys);
	}

	/**
	 * ����HTTP����
	 * 
	 * @param urlString
	 * @return ��ӳ����
	 * @throws IOException
	 */
	private HttpResponse send(String urlString, String method, 
			Map<String, String> parameters, Map<String, String> propertys) throws IOException {
		//HttpURLConnectionΪ�ֲ�����
		HttpURLConnection urlConnection = null;
		//URL����
		URL url = null;

		try {
			//�������ΪGET���������Ҳ�����Ϊ��
			if (method.equalsIgnoreCase("GET") && parameters != null) {
				//������ƴ�Ӳ����ַ���
				StringBuffer param = new StringBuffer();
				int i = 0;
				for (String key : parameters.keySet()) {
					if (i == 0)
						param.append("?");
					else
						param.append("&");
					param.append(key).append("=").append(parameters.get(key));
					i++;
				}
				//ƴ��URL�� + ����
				urlString += param;
			}
			//NEWһ��URL�����ɸö����openConnection()����������һ��URLConnection����
			url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();

			//����������ԣ����庬�������JDK�ĵ�
			urlConnection.setRequestMethod(method);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setUseCaches(false);

			//������������
			if (propertys != null)
				for (String key : propertys.keySet()) {
					urlConnection.addRequestProperty(key, propertys.get(key));
				}

			//�������ΪPOST���������Ҳ�����Ϊ��
			if (method.equalsIgnoreCase("POST") && parameters != null) {
				StringBuffer param = new StringBuffer();
				for (String key : parameters.keySet()) {
					param.append("&");
					param.append(key).append("=").append(parameters.get(key));
				}
				//��������Ϣ���͵�HTTP������
				//Ҫע�⣺һ��ʹ����urlConnection.getOutputStream().write()������
				//urlConnection.setRequestMethod("GET");��ʧЧ�������󷽷����Զ�תΪPOST
				urlConnection.getOutputStream().write(param.toString().getBytes());
				urlConnection.getOutputStream().flush();
				urlConnection.getOutputStream().close();
			}

			return this.makeContent(urlString, urlConnection);
		} catch (Exception e) {
			LoggerUtil.getLogger().error(e,e);
			return null;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}


	}

	/**
	 * �õ���Ӧ����
	 * 
	 * @param urlConnection
	 * @return ��Ӧ����
	 * @throws IOException
	 */
	private HttpResponse makeContent(String urlString, HttpURLConnection urlConnection) throws IOException {
		HttpResponse httpResponser = new HttpResponse();
		//�õ���Ӧ��
		InputStream in = urlConnection.getInputStream();
		//��װ�ɸ߼�����
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		//���ݼ���(������Ϊ������)
		httpResponser.contentCollection = new Vector<String>();
		StringBuffer temp = new StringBuffer();
		String line = bufferedReader.readLine();
		while (line != null) {
			httpResponser.contentCollection.add(line);
			temp.append(line).append("\r\n");
			line = bufferedReader.readLine();
		}
		bufferedReader.close();

		//�õ��������ӵ��ַ���
		String ecod = urlConnection.getContentEncoding();
		if (ecod == null)
			ecod = this.defaultContentEncoding;

		//�������Ը�ֵ����Ӧ����
		httpResponser.urlString = urlString;
		httpResponser.defaultPort = urlConnection.getURL().getDefaultPort();
		httpResponser.file = urlConnection.getURL().getFile();
		httpResponser.host = urlConnection.getURL().getHost();
		httpResponser.path = urlConnection.getURL().getPath();
		httpResponser.port = urlConnection.getURL().getPort();
		httpResponser.protocol = urlConnection.getURL().getProtocol();
		httpResponser.query = urlConnection.getURL().getQuery();
		httpResponser.ref = urlConnection.getURL().getRef();
		httpResponser.userInfo = urlConnection.getURL().getUserInfo();
		httpResponser.content = new String(temp.toString().getBytes(), ecod);
		httpResponser.contentEncoding = ecod;
		httpResponser.code = urlConnection.getResponseCode();
		httpResponser.message = urlConnection.getResponseMessage();
		httpResponser.contentType = urlConnection.getContentType();
		httpResponser.method = urlConnection.getRequestMethod();
		httpResponser.connectTimeout = urlConnection.getConnectTimeout();
		httpResponser.readTimeout = urlConnection.getReadTimeout();

		return httpResponser;		
	}

	/**
	 * Ĭ�ϵ���Ӧ�ַ���
	 */
	public String getDefaultContentEncoding() {
		return this.defaultContentEncoding;
	}

	/**
	 * ����Ĭ�ϵ���Ӧ�ַ���
	 */
	public void setDefaultContentEncoding(String defaultContentEncoding) {
		this.defaultContentEncoding = defaultContentEncoding;
	}

	public static void main(String[] args) {
		HttpRequestSender request = new HttpRequestSender();
		HttpResponse hr = null;
		String urlStr = "http://172.16.53.126:8080?msg=sdf&phoneNum=123123&report=1";
		try {
			hr = request.sendPost(urlStr);
			//��ӡ��Ӧ����
			System.out.println(hr.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
