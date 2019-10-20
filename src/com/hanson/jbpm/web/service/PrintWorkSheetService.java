package com.hanson.jbpm.web.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.IPrinterProvider;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.suntek.ccf.dao.DaoFactory;
import com.suntek.ccf.pico.IComponentAdvice;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.core.config.SystemConfig;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * 文件导出Service(通过模板)
 * 
 * @author suntek
 * @create：2017-12-8
 */
@SuppressWarnings("unchecked")
public class PrintWorkSheetService extends HttpRequestService {
	private static final long serialVersionUID = 2616819045822843154L;

	private static VelocityEngine engine;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		this.doPost(req, resp); 
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		//获取打印文件类型
		String printFileType = AppHandle.getHandle("jbpm").getProperty("PrintFiletype");
		if(printFileType.contains(",")){
			String[]  fileTypes = printFileType.split(",");
			for(String s : fileTypes){
				if(s.contains("excel")){
					doExcelExport(request, response);
				}
				if(s.contains("pdf")){
					doPdfExport(request, response);
				}
				if(s.contains("word")) {
					doWordExport(request, response);
				}
			}
		}else{
			if(printFileType.contains("excel")){
				doExcelExport(request, response);
				return;
			}
			if(printFileType.contains("pdf")){
				doPdfExport(request, response);
				return;
			}
			if(printFileType.contains("word")) {
				doWordExport(request, response);
				return;
			}
		}

	}

	/**
	 * pdf导出
	 * @param request
	 * @param response
	 */
	private void doPdfExport(HttpServletRequest request,
			HttpServletResponse response) {
		String processName = request.getParameter("processName"); 
		String INST_ID = request.getParameter("INST_ID");
		String userId = request.getRemoteUser();
		try {			
			engine = new VelocityEngine();

			//设置模板加载路径
			String basePath = request.getSession().getServletContext().getRealPath("/META-INF/flow/" + processName + "/");
			CommonLogger.logger.info("Engine: " + basePath);
			engine.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);

			engine.init();
			//加载模板，设定模板编码
			Template t = engine.getTemplate(processName + ".html", "GBK");

			//设置初始化数据
			VelocityContext context = new VelocityContext();
			if(INST_ID.split(",").length>1){//批量打印
				INST_ID = INST_ID.replace(",", "','");
				context.put("dataList", getPrintData(processName, INST_ID, request));
			}else{//单张工单打印
				Map map = getPrintData(processName, INST_ID, request);
				if (map!=null && map.size()>0){
					String requestContent = (String) map.get("REQUEST_CONTENT");
					requestContent.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
					map.put("REQUEST_CONTENT",requestContent);
				}
				//设置上下文参数
				Iterator<String> sets = map.keySet().iterator();
				String key;
				while(sets.hasNext()) {
					key = sets.next();
					context.put(key, (map.get(key)==null)?" ":map.get(key));
				}
			}
			//设置输出
			StringWriter writer = new StringWriter();
			//将环境数据转化输出
			t.merge(context, writer);
			//简化操作
			
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(writer.toString());
			//			System.out.println(writer.toString());
			
			
			//解决中文问题
			ITextFontResolver fontResolver = renderer.getFontResolver();
			String fontsPath = SystemConfig.getEAPHome()+"/eapserver/application/jbpm/fonts";
			//添加中文字体(宋体)
			fontResolver.addFont(fontsPath+"/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

			renderer.layout();
			
			//浏览器直接输出
			response.setContentType("application/pdf");
			renderer.createPDF(response.getOutputStream());
			
			if (this.pageHeight != 0 && this.pageWidth != 0) {
				Rectangle rect = renderer.getWriter().getPageSize();
				rect.setBottom(this.pageHeight);
				rect.setRight(this.pageWidth);
				renderer.getWriter().setPageSize(rect);
			}
			
			String sql = "update BPM_INST set PRINTED='1',PRINT_USERID='" + userId + "' where INST_ID='" + INST_ID + "'";
			DaoFactory.getJdbc(ProcessClient.MODULE).execute(sql);

			/*//文件格式输出
			String outputFileUrl = "temp/velocityWebTest.pdf";
			outputFileUrl = this.getServletContext().getRealPath(outputFileUrl);
			OutputStream os = new FileOutputStream(outputFileUrl);
			renderer.createPDF(os);
			os.close();
			this.doDownLoad(outputFileUrl, processName + INST_ID +".pdf", response);*/

		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
		}
	}
	
	
	/**
	 * word导出
	 * @param request
	 * @param response
	 */
	private void doWordExport(HttpServletRequest request, HttpServletResponse response) {
		String processName = request.getParameter("processName"); 
		String INST_ID = request.getParameter("INST_ID");
		String userId = request.getRemoteUser();
		try {			
			engine = new VelocityEngine();

			//设置模板加载路径
			String basePath = request.getSession().getServletContext().getRealPath("/META-INF/flow/" + processName + "/");
			CommonLogger.logger.info("Engine: " + basePath);
			engine.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);

			engine.init();
			//加载模板，设定模板编码
			Template t = engine.getTemplate(processName + ".html", "GBK");

			//设置初始化数据
			VelocityContext context = new VelocityContext();
			if(INST_ID.split(",").length>1){//批量打印
				INST_ID = INST_ID.replace(",", "','");
				context.put("dataList", getPrintData(processName, INST_ID, request));
			}else{//单张工单打印
				Map map = getPrintData(processName, INST_ID, request);
				decodeMap(map);
				 //设置上下文参数 
				Iterator<String> sets = map.keySet().iterator();
				String key;
				while(sets.hasNext()) {
					key = sets.next();
					context.put(key, (map.get(key)==null)?" ":map.get(key));
				}
			}
			//设置输出
			String destPath = basePath + "wordvelocity.html";
			PrintWriter pw = new PrintWriter(destPath);
			//将环境数据转化输出
			t.merge(context, pw);
			
			pw.close();

			String sql = "update BPM_INST set PRINTED='1',PRINT_USERID='" + userId + "' where INST_ID='" + INST_ID + "'";
			DaoFactory.getJdbc(ProcessClient.MODULE).execute(sql);

			//文件格式输出
			
			this.doDownLoad(destPath, processName + INST_ID +".doc", response);

		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
		}
	}

	/**
	 * excel导出
	 * @param request
	 * @param response
	 */
	private void doExcelExport(HttpServletRequest request,
			HttpServletResponse response) {
		String processName = request.getParameter("processName"); 
		String instId = request.getParameter("INST_ID");
		String userId = request.getRemoteUser();


		XLSTransformer transformer = new XLSTransformer();

		try {			
			//设置模板加载路径
			String basePath = request.getSession().getServletContext().getRealPath("/META-INF/flow/" + processName + "/");
			CommonLogger.logger.info("Excel Template Path: " + basePath);
			Map beans = getPrintData(processName, instId, request);

			//模板文件路径
			String srcFilePath ="";
			//输出文件路径
			String destFilePath = "";

			srcFilePath = basePath + "/" + processName + ".xls";
			destFilePath = basePath + "_output.xls";

			transformer.transformXLS(srcFilePath, beans, destFilePath);
			CommonLogger.logger.info("Excel File Generate Path: " + basePath);
			this.doDownLoad(destFilePath, processName + instId +".xls", response);
			CommonLogger.logger.info("Excel doDownLoad end: " + basePath);

			String sql = "update BPM_INST set PRINTED='1', PRINT_USERID='" + userId + "'" +
						 " where INST_ID='" + instId + "'";
			DaoFactory.getJdbc(ProcessClient.MODULE).execute(sql);

		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
		}
	}
	
	
	private Map getPrintData(String processName, String instId, HttpServletRequest request) {
		IPrinterProvider printProvider = ProcessEngine.getPrinterProvider(processName);
		
		if (printProvider instanceof IComponentAdvice || printProvider instanceof com.hanson.jbpm.dev.pico.IComponentAdvice) {
			CommonLogger.logger.debug("[PrintWorkSheetService]printProvider实现了IComponentAdvice接口，调用before方法..");
			((IComponentAdvice)printProvider).before(request);
		}
		Map data = printProvider.getData(instId);
		
		if (printProvider instanceof IComponentAdvice || printProvider instanceof com.hanson.jbpm.dev.pico.IComponentAdvice) {
			CommonLogger.logger.debug("[PrintWorkSheetService]printProvider实现了IComponentAdvice接口，调用after方法..");
			((IComponentAdvice)printProvider).after(request);
		}
		return data;
	}
	

	/**
	 * 下载文件
	 * @param path
	 * @param name
	 * @param response
	 */
	public  void doDownLoad(String path, String name, HttpServletResponse response) {  
		BufferedInputStream bis = null;  
		BufferedOutputStream bos = null;  
		OutputStream fos = null;  
		InputStream fis = null; 
		try {  
			
			response.reset();  
			response.setHeader("Content-disposition", "attachment;success=true;filename ="  + URLEncoder.encode(name, "utf-8"));  

			File uploadFile = new File(path);  
			fis = new FileInputStream(uploadFile);  
			bis = new BufferedInputStream(fis);  
			fos = response.getOutputStream();  
			bos = new BufferedOutputStream(fos);  

			// 弹出下载对话框  
			int bytesRead = 0;  
			byte[] buffer = new byte[8192];  
			while ((bytesRead = bis.read(buffer, 0, 8192)) != -1) {  
				bos.write(buffer, 0, bytesRead);  
			}  

			bos.flush();  
			fis.close();  
			bis.close();  
			fos.close();  
			bos.close();  
		} catch (Exception e) {  
			CommonLogger.logger.error("doDownLoad error:"+e.getMessage(),e);
		} finally {
			try { if (bis != null) bis.close(); } catch (Exception ex) {}
			try { if (bos != null) bos.close(); } catch (Exception ex) {}
			try { if (fis != null) fis.close(); } catch (Exception ex) {}
			try { if (fos != null) fos.close(); } catch (Exception ex) {}
		}
	}  
	
	private void decodeMap(Map<String, Object> map) {
		for(String key : map.keySet()) {
			Object value = map.get(key);
			if(value != null && value.getClass().getSimpleName().indexOf("String") >= 0) {
				value=value.toString().replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\\r\\n","<w:br/>");
				map.put(key, value);
			}
		}
	}

}
