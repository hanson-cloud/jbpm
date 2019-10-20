package com.hanson.jbpm.tag.grid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.suntek.ccf.log.CCFLogger;
import com.suntek.eap.core.app.AppHandle;

public class GridTag extends BodyTagSupport {
	private final static long serialVersionUID = 1l;
	
	/* ��ǩ�����ֺ�id */
	private String id;
	/* ÿҳ���� */
	private String rowNumPerPage;
	/* ��ʽ */
	private String css;
	/* dataProvider */
	private String dataProvider;
	/* ���� */
	private String parameters;
	/* �Ƿ�֧�ֵ��� */
	private String exportSupport;
	
	/* Excel ������̨������ */
	private String exportHandler = ""; // Add By ZhouHuan, 2010.2.5
	
	public String getExportHandler() {
		return exportHandler;
	}
	public void setExportHandler(String exportHandler) {
		this.exportHandler = exportHandler;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getDataProvider() {
		return dataProvider;
	}
	public void setDataProvider(String dataProvider) {
		this.dataProvider = dataProvider;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRowNumPerPage() {
		return rowNumPerPage;
	}
	public void setRowNumPerPage(String rowNumPerPage) {
		this.rowNumPerPage = rowNumPerPage;
	}
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
		
	public int doEndTag() {
		try {		
			if (css == null) css = "width:100%; height:100%";
			if (rowNumPerPage == null) rowNumPerPage = "10";
			
			String moduleName = ((HttpServletRequest)pageContext.getRequest()).getContextPath().substring(1);
			
			//if (serviceId == null) {
				String dsName = "";
				dsName = AppHandle.getHandle(moduleName).getDatasourceName();
				if (parameters == null || parameters.equals(""))
					parameters = "DataSourceName:\"" + dsName + "\"";
				else {
					parameters = parameters.replaceFirst("DataSourceName:\"" + dsName + "\",", "");
					parameters = "DataSourceName:\"" + dsName + "\"," + parameters;
				}
			//}
			BodyContent bc = getBodyContent(); 
			//��body���������ַ����ĸ�ʽ��ȡ���� 
			String body = bc.getString(); 				
			/* �����ǩ���� */
			body = (String)new GridBodyBuilder().build(id, rowNumPerPage, css, 
					moduleName, dataProvider, parameters, body, exportSupport, exportHandler);
			/* ������� */			
			pageContext.getOut().print(body);	
			
			// ��dataProvider����������session��������cookie��; By ZhouHuan, 2010.3.2
			pageContext.getSession().setAttribute("gridDataProvider", dataProvider);
			
		} catch (Exception ex) { 
			CCFLogger.logger.error(ex, ex);
		} 		
		return EVAL_BODY_AGAIN;
	}	
	public String getExportSupport() {
		return exportSupport;
	}
	public void setExportSupport(String exportSupport) {
		this.exportSupport = exportSupport;
	}		
}
