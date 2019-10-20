package com.hanson.jbpm.web.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanson.jbpm.jpdl.def.EntityDef;
import com.hanson.jbpm.jpdl.exe.util.IdentityCreator;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.orm.utils.BeanUtil;
import com.suntek.eap.util.jdbc.Dialect;
import com.hanson.jbpm.identity.OrgCache;
import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.base.BpmBiz;
import com.hanson.jbpm.jpdl.def.base.OracleClob;
import com.hanson.jbpm.jpdl.def.base.PreparedSqlBean;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContextSQLHelper;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;
import com.hanson.jbpm.jpdl.exe.util.DialectUtil;
import com.hanson.jbpm.jpdl.exe.util.ParameterRequestWrapper;
import com.hanson.jbpm.jpdl.exe.util.RequestTransformer;
import com.suntek.util.string.StringHelper;

public class SaveFormSubmitService  extends HttpRequestService {
	private final static long serialVersionUID = 1l;
	
	public transient Dialect dialect = ProcessEngine.getDialect();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		ExecutionContextSQLHelper sqlHelper = null;
		try {
			sqlHelper = new ExecutionContextSQLHelper();
			
			String taskId = request.getParameter("TASK_ID");
			String instId = request.getParameter("INST_ID");
			
			String procName = request.getParameter("processName");
			
			String type = request.getParameter("type");
			
			String userId = request.getRemoteUser();
			SysUser user = new SysUser(userId);
			String userName = user.getName();
			String deptId = OrgCache.getCache().getUserDept(userId);
			String deptName = OrgCache.getCache().getDept(deptId);
			
			String title = request.getParameter("bizTITLE");
			
			
			if (title == null) title = "�ޱ���";
			
			
			//ȥ��request�е�bizTITLE
			Map requestMap = new HashMap(request.getParameterMap());
			requestMap.remove("bizTITLE");
			ParameterRequestWrapper requestWrapper = new ParameterRequestWrapper(request, requestMap);
			
			Timestamp currentTimeOfDate = new Timestamp(Calendar.getInstance().getTime().getTime());
			
			if (instId == null || instId.equals("") || instId.equals("null")) {					
				String draftId = IdentityCreator.getDraftId(2);
				/*sqls.add("INSERT INTO BPM_TASK_DRAFT (DRAFT_ID, PROC_NAME, INIT_UID, INIT_UNAME," +
						 " INIT_DEPTID, INIT_DEPT, INIT_TIME, INST_TITLE, CONTEXT )" +  
						 " VALUES ('" + draftId +"','" + procName + "', '" + userId + "'," + " '" + userName + "'," +
						 		  "'" + deptId +"', '" + deptName + "'," + dialect.todate("'" + current + "'") +
						 		  ",'" + title + "'," +
						 		  "'<task>" + new RequestTransformer().formDataToXml(requestWrapper) + "</task>') ");*/	
				Object context = "'<task>" + new RequestTransformer().formDataToXml(requestWrapper) + "</task>";
				if(DialectUtil.isOracle(ProcessEngine.getDialect())) {
					context = OracleClob.build((String)context);
				}
				if(title.length() >= 950) {
					title = title.substring(0, 950) + "...";
				}
				sqlHelper.addSQL("INSERT INTO BPM_TASK_DRAFT (DRAFT_ID, PROC_NAME, INIT_UID, INIT_UNAME," +
						 " INIT_DEPTID, INIT_DEPT, INIT_TIME, INST_TITLE, CONTEXT )" +  
						 " VALUES (?,?,?,?,?,?,?,?,?)", draftId, procName, userId, userName, deptId, deptName,
						 currentTimeOfDate, title, context
						 );
				instId = " ";
				
			} else {			
				//sqls.add("delete from BPM_TASK_MEMO where TASK_ID='" + taskId + "' and MEMO_TYPE='save'");			
				sqlHelper.addSQL("delete from BPM_TASK_MEMO where TASK_ID=? and MEMO_TYPE='save'", taskId);
				
				Object context = "'<task>" + new RequestTransformer().formDataToXml(requestWrapper) + "</task>";
				if(DialectUtil.isOracle(ProcessEngine.getDialect())) {
					context = OracleClob.build((String)context);
				}
				sqlHelper.addSQL("INSERT INTO BPM_TASK_MEMO ( TASK_ID, INST_ID, PROC_NAME, DEAL_UID, DEAL_UNAME," +
						 " DEAL_DEPTID, DEAL_DEPT, DEAL_TIME, CONTEXT, MEMO_TYPE )" +  
						 " VALUES (?,?,?,?,?,?,?,?,?,?)", taskId, instId, procName, userId, userName, deptId, 
						 deptName, currentTimeOfDate, 
						 context,
						 "save");
				
				Object commentsMemo = userName + "ִ���˹����޸�:" + title;
				if(DialectUtil.isOracle(ProcessEngine.getDialect())) {
					commentsMemo = OracleClob.build((String)commentsMemo);
				}
				sqlHelper.addSQL("insert into BPM_TASK_COMMENTS(TASK_ID, INST_ID, TASK_NAME, DEAL_UID, DEAL_UNAME," +
					 	  " DEAL_DEPTID, DEAL_DEPT, DEAL_TIME, CONTEXT) values(?,?,?,?,?,?,?,?,?)", 
					 	  taskId, instId, "�����޸�", userId, userName, deptId, deptName, 
					 	  currentTimeOfDate,  commentsMemo);
				if("update".equals(type)) {
					Object contextObj = "<task>" + new RequestTransformer().formDataToXml(requestWrapper) + "</task>";
					if(DialectUtil.isOracle(ProcessEngine.getDialect())) {
						contextObj = OracleClob.build((String)contextObj);
					}
					sqlHelper.addSQL("update BPM_TASK_EXT set CONTEXT=? where TASK_ID=?", 
							contextObj, taskId);
					
					
					EntityDef entityDef = ProcessEngine.getProcessDefinition(procName).getEntityDef();
					if(entityDef != null) {
						if("BpmBiz".equals(entityDef.getClassName()) && BpmDaoSQLExecutor.PersistenceMode.equals("hibernate")) {
							//����ͨ��ҵ�������
							BpmBiz biz = new BpmBiz();
							biz = (BpmBiz) BeanUtil.instantiate(request, biz);
							biz.setInstId(instId);
							PreparedSqlBean sql = biz.toPreparedUpdateSql();
							sqlHelper.addSQL(sql.getSql(), sql.getArgs());
						}
					}
				}
			}
				
			sqlHelper.beginTransaction();
			sqlHelper.setTimeout(5);
			
			sqlHelper.executeBatchSQL();			
			
			sqlHelper.commitTransaction();	
			
			if (instId == null || instId.trim().equals("")) instId = "�½�";
			response.getWriter().print("<script>if (confirm('�������ѱ���'))" +
					" { parent.parent.parent.window.location.href=parent.parent.parent.window.location.href;} </script>");
		} catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			String msg = "�����ݱ���ʧ��:" + ex.getMessage();
			msg = StringHelper.replace(msg, "'", "��").replace('\r', ' ').replace('\n', ' ');
			response.getWriter().print("<script>alert('" + msg + "');</script>");
		}finally {
			sqlHelper.closeHibernateSession();
		}
	}
}
