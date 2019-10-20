package com.hanson.jbpm.mgmt;

import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.ExtendQuery;
import com.hanson.jbpm.log.CommonLogger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.suntek.ccf.ui.grid.FieldSort;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.util.calendar.DateUtil;
import com.suntek.eap.util.jdbc.Dialect;
import com.hanson.jbpm.jpdl.def.ExtQueryField;
import com.suntek.util.string.StringHelper;
import com.suntek.util.time.CurrentDateTime;

public class WorkItemListHisQuery  extends WorkItemListQuery {
	
	private ExtendQuery extendQuery = null;
	private String extQueryFromStat = ""; 
	private StringBuffer extQueryFieldStat = new StringBuffer();
	private String extQueryWhereStat = "";
	
	public WorkItemListHisQuery() {
		
	} 
	
	/*public String getWorkItemListByUserId(String where) {
		String sql = "select INST_ID, ATTACHFILE, INST_TITLE, PROC_NAME, INIT_TIME," +
					 " INIT_UNAME, CLOSE_TIME, OVERTIME" +
					 " FROM BPM_INST_HIS " + where + "  order by INIT_TIME desc";
		return sql;
	}*/
	
	private String buildWhereStatement(Map map) {
		String processName = (String)map.get("processName");
		if (processName != null && processName.indexOf("processName")>0) 
			processName = null;
		String title = (String)map.get("sheetTitle");
		String overtime = (String)map.get("overtime");
		String startTime = (String)map.get("startTime");
		String endTime = (String)map.get("endTime");
		String startTime2 = (String)map.get("startTime2");
		String instId = (String)map.get("instId");
		String creater = (String)map.get("creater");
		String caller = (String)map.get("caller");
		
		String current = CurrentDateTime.getCurrentDate();
		if (startTime == null) 
			startTime = DateUtil.getPreDate(current, DateUtil.DAY, -1 * QUERY_TIMELIMIT);		
		if (endTime == null)
			endTime = current;
		
		StringBuffer ret = new StringBuffer(" where INIT_TIME between ").append(dialect.todate("'" + startTime + " 00:00:00'"))
					.append(" and ").append(dialect.todate("'" + endTime + " 23:59:59'"));
		
		if (startTime2 != null && !startTime2.equals("")) {
			CommonLogger.logger.debug("CloseTime = " + startTime2);
			ret.append(" and CLOSE_TIME between ").append(dialect.todate("'" + startTime2 + " 00:00:00'"))
					.append(" and ").append(dialect.todate("'" + startTime2 + " 23:59:59'"));
		}
		if (title != null && !title.equals(""))
			ret.append(" and INST_TITLE like '").append(title).append("%'");
		if (processName != null)
			ret.append(" and PROC_NAME in ('").append(processName.replaceAll(" ","").replaceAll(",", "','")).append("')");
		if (instId != null && !instId.equals(""))
			ret.append(" and a.INST_ID in ('").append(StringHelper.replace(instId,",","','")).append("')");
		if (creater != null && !creater.equals(""))
			ret.append(" and INIT_UID ='").append(creater).append("'");
		if (caller != null && !caller.equals(""))
			ret.append(" and CUST_TEL ='").append(caller).append("'");
		
		if (overtime!=null && overtime.equals("1"))
			ret.append(" and a.DUE_TIME < ").append(dialect.todate("'" + CurrentDateTime.getCurrentDateTime() + "'"));
		
		if (processName != null) {
			extendQuery = ProcessEngine.getProcessDefinition(processName).getExtendQuery();
			String subtable = extendQuery.getTableName();
			if (!subtable.equals("")) {
				extQueryFromStat = "," + subtable + " d";
				buildExtQueryFieldStatement(map);
				ret.append(extQueryWhereStat);
				ret.append(" and a.INST_ID=d.INST_ID ");
			}
		}		
		
		return ret.toString();
	}
	
	public String buildQuerySql(Map map) {
		return "";
	}
	
	public String buildCountSql(Map map) {
		return "";
	}
	
	/**
	 * �����ѯ��SQL
	 */
	public String buildQuerySql2(Map map) {
		String where = buildWhereStatement(map);
		String sql = "select INST_ID, ATTACHFILE, INST_TITLE, PROC_NAME, " +
					 //"INIT_TIME," +
					 dialectDateToChar("INIT_TIME", Dialect.DATETIME_FORMAT_STANDARD) + " INIT_TIME, " +
					 " INIT_UID, INIT_UNAME," +
					 " INIT_UID ASSIGN_UID, INIT_UNAME ASSIGN_UNAME, " +
					 //"CLOSE_TIME, " +
					 dialectDateToChar("CLOSE_TIME", Dialect.DATETIME_FORMAT_STANDARD) + " CLOSE_TIME, " +
					 "DUE_TIME," +
					 " OVERTIME, 1 READ_FLAG, 2 DEAL_FLAG, '�ѹ鵵' TASK_NAME" +
					 extQueryFieldStat + 
					 " FROM BPM_INST_HIS a " + extQueryFromStat + where + extQueryWhereStat; 		
		FieldSort fieldSort = (FieldSort)map.get("sort");
		if (fieldSort.isSorted()) {
			sql = sql + " order by INIT_TIME desc, " + fieldSort.toSortStatement().trim();
		} else {
			sql = sql + " order by INIT_TIME desc";
		}
		sql = this.addSetMaxRowClause(sql, map);
		CommonLogger.logger.info(sql);
		return sql;
	}
	
	
	
	/**
	 * ����ͳ�Ƶ�SQL
	 */
	public String buildCountSql2(Map map) {
		String where = buildWhereStatement(map);
		String sql = "select count(1) FROM BPM_INST_HIS a " + extQueryFromStat + where + extQueryWhereStat;
		CommonLogger.logger.info(sql);
		return sql;
	}
	
	public int directCount(Map map) {
		String sql = buildQuerySql2(map);
		JdbcTemplate jdbc = DaoFactory.getJdbc("jbpm");
		if ("true".equals(map.get("forExport")))
			jdbc.setMaxRows(Integer.parseInt(AppHandle.getHandle("eaptag").getProperty("GRID_EXPORT_MAX")));
		jdbc.setQueryTimeout(5);
		CommonLogger.logger.debug(sql);
		resultList = jdbc.queryForList(sql);
		return resultList.size();
	}
	
	
	public List<Map> directFetch(Map map, int pageNo, int pageSize) {
		return scroll(resultList, pageNo, pageSize);
	}
		
	private void buildExtQueryFieldStatement(Map map) {
		List<ExtQueryField> fields = extendQuery.getFields();
		ExtQueryField field;
		String value, begint, endt;
		StringBuffer where = new StringBuffer();
		for (int i=0; i<fields.size(); i++) {
			field = fields.get(i);
			value = (String) map.get(field.getId());
			if (field.getFieldExpr() != null) {
				if (field.getFieldExpr().startsWith("c.")) 
					continue;
				else
					extQueryFieldStat.append(",").append(field.getFieldExpr()).append(" ").append(field.getId());
			}
			else
				extQueryFieldStat.append(",d.").append(field.getId());
			if (value != null && !value.equals("")) {
				if (field.getOperator().equals("equals"))
					where.append(" and d.").append(field.getId()).append("='").append(value).append("'");
				if (field.getOperator().equals("="))
					where.append(" and d.").append(field.getId()).append("=").append(value);
				if (field.getOperator().equals("like"))
					where.append(" and d.").append(field.getId()).append(" like '").append(value).append("%'");				
			} else {
				begint = (String)map.get("startTime_" + field.getId());
				endt = (String)map.get("endTime_" + field.getId());			
				if ("".equals(begint)) begint = null;
				if ("".equals(begint)) endt = null;
				if ("between".equals(field.getOperator()) && begint != null && endt != null) {					
					where.append(" and d.").append(field.getId()).append(" between ")
										   .append(dialect.todate("'" + begint + " 00:00:00'"))
										   .append(" and ")
										   .append(dialect.todate("'" + endt + " 23:59:59'"));
				}
				if ("strbetween".equals(field.getOperator()) && begint != null && endt != null 
						&& !begint.equals("") && !endt.equals("")) {					
					where.append(" and d.").append(field.getId()).append(" > ")
										   .append("'" + begint + "'")
										   .append(" and d.").append(field.getId()).append(" < ")
										   .append("'" + endt + "'");
				}
			}
		}
		extQueryWhereStat =  where.toString();
	}
}
