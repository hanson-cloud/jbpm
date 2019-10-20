package com.hanson.jbpm.jpdl.def;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.log.LoggerUtil;
import com.suntek.eap.util.jdbc.Dialect;

@Deprecated
public class BizTable {
	private String name;
	private Map<String, FieldDef> fields;
	
	public Map<String, FieldDef> getFields() {
		return fields;
	}

	public BizTable(String name) {
		this.name = name;		
	}
	
	public void initFields() {
		fields = new HashMap<String, FieldDef>();
	}
	
	public void insert(ExecutionContext ctx) throws SQLException {
		if (fields == null || fields.size() == 0) return;
		String instId = ctx.getInstanceContext().getParameter("//INST_ID");
		String sql = buildInsertSQL(ctx.getDialect(), ctx.getRequest(), instId);
		ctx.getJdbc().execute(sql);
		
	}
	
	public void update(ExecutionContext ctx) throws SQLException {
		if (fields == null || fields.size() == 0) return;
		String sql = buildUpdateSQL(ctx.getDialect(), ctx.getRequest());
		ctx.getJdbc().execute(sql);
	}
	
	public void delete(ExecutionContext ctx) throws SQLException {
		if (fields == null || fields.size() == 0) return;
		String sql = buildDeleteSQL(ctx.getInstanceContext().getParameter("//INST_ID"));
		ctx.getJdbc().execute(sql);
	}
	
	private String buildInsertSQL(Dialect dialect, HttpServletRequest request, String instId) {
		StringBuffer sql = new StringBuffer("insert into " + name + "(");
		Iterator it = fields.keySet().iterator();
		String key;		
		while(it.hasNext()) {
			key = (String) it.next();
			if (request.getParameter(key) != null && !request.getParameter(key).equals("null")) {
				sql.append(fields.get(key).name).append(",");
			}
		}
		
		sql.deleteCharAt(sql.length()-1).append(") values(");
		
		it = fields.keySet().iterator();
		String param = null;
		while(it.hasNext()) {
			key = (String) it.next();
			if (request.getParameter(key) != null && !request.getParameter(key).equals("null")) {				
				param = request.getParameter(key);
				if (key.equals("INST_ID")) param = instId;
				sql.append(fields.get(key).getValue(dialect, param)).append(",");
			}
		}
		sql.deleteCharAt(sql.length()-1).append(")").toString();
		LoggerUtil.getLogger().info(sql);
		return sql.toString();
	}	

	private String buildUpdateSQL(Dialect dialect, HttpServletRequest request) {
		StringBuffer sql = new StringBuffer("update " + name + " set ");
		Iterator it = fields.keySet().iterator();
		String key;
		FieldDef field;
		while(it.hasNext()) {
			key = (String) it.next();
			if (request.getParameter(key) != null && !request.getParameter(key).equals("null")) {
				field = fields.get(key);
				sql.append(field.name).append("=").append(field.getValue(dialect, request.getParameter(key)));
				sql.append(",");
			}
		}		
		sql.deleteCharAt(sql.length()-1).append(" where INST_ID='" + request.getParameter("INST_ID") + "'");
		LoggerUtil.getLogger().info(sql);
		return sql.toString();
	}	

	private String buildDeleteSQL(String instId) {
		return "delete from " + name + " where INST_ID='" + instId + "'";
	}	
}
