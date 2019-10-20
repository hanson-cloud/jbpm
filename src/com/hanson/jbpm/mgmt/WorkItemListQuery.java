package com.hanson.jbpm.mgmt;


import java.util.List;
import java.util.Map;

import com.hanson.jbpm.identity.Role;
import com.hanson.jbpm.identity.User;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.ExtQueryField;
import com.hanson.jbpm.jpdl.def.ExtendQuery;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.suntek.ccf.ui.grid.DBGTGridDataProvider;
import com.suntek.ccf.ui.grid.FieldSort;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.util.calendar.DateUtil;
import com.suntek.eap.util.jdbc.Dialect; 
import com.suntek.eap.util.jdbc.impl.OracleDialect;
import com.suntek.eap.util.jdbc.impl.SybaseDialect;
import com.hanson.jbpm.identity.Department;
import com.hanson.jbpm.identity.SysUser;

import com.suntek.util.string.StringHelper;
import com.suntek.util.time.CurrentDateTime;

public class WorkItemListQuery  extends DBGTGridDataProvider { 
	
	public static int QUERY_TIMELIMIT = Integer.parseInt(AppHandle.getHandle("jbpm").getProperty("QUERY_TIMELIMIT", "15"));
	
	public static int QUERY_MAXROW = Integer.parseInt(AppHandle.getHandle("jbpm").getProperty("QUERY_MAXROW", "1000"));
	
	protected static Dialect dialect = null;
	private ExtendQuery extendQuery = null;
	private String extQueryFromStat = ""; 
	private StringBuffer extQueryFieldStat = new StringBuffer();
	private String extQueryWhereStat = "";
	private String processName = "";
	private String roleFilter = "";
	private String deptFilter = "";
	private String groupFilter = "";
	 
	protected List resultList = null;
	
	public WorkItemListQuery() {		 
		super(AppHandle.getHandle(ProcessClient.MODULE).getDatasourceName());
		if (dialect == null) {
			dialect = ProcessEngine.getDialect();
		}
		this.setQueryTimeOut(5);
		this.setMaxRows(QUERY_MAXROW);
	}
		
	private String buildWhereStatement(Map map) {
		processName = (String)map.get("processName");	
		if (processName != null && processName.indexOf("processName")>0) 
			processName = null;
		
		String instStatus = (String)map.get("sheetType");
		String title = (String)map.get("sheetTitle");
		String userId = (String)map.get("userId");
		String startTime = (String)map.get("startTime");
		String endTime = (String)map.get("endTime");
		String instId = (String)map.get("instId");
		String overtime = (String)map.get("overtime");
		String creater = (String)map.get("creater");
		String caller = (String)map.get("caller");
		String view = (String)map.get("view");
				
		String current = CurrentDateTime.getCurrentDate();
		
		if (view.equals("${view}"))	
			view = "mine";
		if (overtime == null) 	overtime = "";
		if (startTime == null) 	{
			if (view.equals("all"))
				startTime = DateUtil.getPreDate(current, DateUtil.DAY, -1 * QUERY_TIMELIMIT); 
			else
				startTime = DateUtil.getPreDate(current, DateUtil.DAY, -3 * QUERY_TIMELIMIT);
		}
		if (endTime == null)	endTime = current;
				
		StringBuffer ret = new StringBuffer(" where");
		ret.append(" INIT_TIME between ").append(dialect.todate("'" + startTime + " 00:00:00'"))
		   .append(" and " + dialect.todate("'" + endTime + " 23:59:59'"));
		
		if (title != null && !title.equals(""))
			ret.append(" and INST_TITLE like '").append(title).append("%'");
		
		if (instId != null && !instId.equals(""))
			ret.append(" and a.INST_ID in ('").append(StringHelper.replace(instId,",","','")).append("')");
		
		if (processName != null) {
			if (map.get("view").equals("all") || map.get("view").equals("fromMe"))
				ret.append(" and a.PROC_NAME='").append(processName.trim()).append("'");
			else
				ret.append(" and b.PROC_NAME='").append(processName.trim()).append("'");
		}
		if (instStatus != null && !instStatus.equals(""))
			ret.append(" and c.TASK_NAME in ('").append(instStatus.replaceAll(",", "','")).append("')");		
		
		if (overtime!=null && overtime.equals("2"))
			ret.append(" and c.DUE_TIME < ").append(dialect.todate("'" + CurrentDateTime.getCurrentDateTime() + "'"));
		if (overtime!=null && overtime.equals("1"))
			ret.append(" and a.DUE_TIME < ").append(dialect.todate("'" + CurrentDateTime.getCurrentDateTime() + "'"));
		
		if (creater != null && !creater.equals(""))
			ret.append(" and a.INIT_UID ='").append(creater).append("'");
		if (caller != null && !caller.equals(""))
			ret.append(" and a.CUST_TEL ='").append(caller).append("'");
		
		if (view.equals("mine"))
			ret.append(" and b.DEALER='").append(userId).append("'");
		if (view.equals("dept"))				
			ret.append(buildDeptFilter(userId));
		if (view.equals("role"))
			ret.append(buildRoleFilter(userId));
		if (view.equals("group"))
			ret.append(buildGroupFilter(userId));
		
		if (view.equals("mine") || view.equals("dept") || view.equals("role") || view.equals("group")) {
			ret.append(" and b.INST_ID=a.INST_ID and b.TASK_ID=c.TASK_ID and a.COMMENT_CNT=0");
			ret.append(" and b.DEAL_FLAG='0' ");
		} 
		
		if (view.equals("all")) {
			ret.append(" and a.INST_ID=c.INST_ID and c.DEAL_TIME is null and a.COMMENT_CNT=0");
		}
		
		if (view.equals("fromMe")) {
			//ret.append(" and a.INST_ID=c.INST_ID and c.PRE_TASKID=b.TASK_ID ");
			//ret.append(" and READ_FLAG ='2' and b.DEALER='" + userId + "'");
			//ret.append(" and a.INST_ID=c.INST_ID and c.PRE_TASKID<>' ' and c.ASSIGN_UID='" + userId + "'"); 
			// �ĳ��ѷ�δ����ģ��Ѵ���Ĳ���ʾ
			ret.append(" and a.INST_ID=c.INST_ID and c.PRE_TASKID<>' ' and c.ASSIGN_UID='" + userId + "' and DEAL_TIME is null");
			ret.append(" and a.COMMENT_CNT=0 and TASK_NAME<>'����' "); //
		}
				
		String transition = (String)map.get("transition");		
		if (isNotNull(transition) && !transition.equals("all")) {
			ret.append(" and c.TRANS_NAME='" + map.get("transition") + "'");
		}
		
		if (processName != null) {
			extendQuery = ProcessEngine.getProcessDefinition(processName).getExtendQuery();
			String subtable = extendQuery.getTableName();
			if (!subtable.equals("")) {
				extQueryFromStat = "," + subtable + " d";
				buildExtQueryFieldStatement(map);
				ret.append(extQueryWhereStat);
				if (map.get("view").equals("all") || map.get("view").equals("fromMe"))
					ret.append(" and a.INST_ID=d.INST_ID ");
				else
					ret.append(" and b.INST_ID=d.INST_ID ");
			}
		}
		
		return ret.toString(); 
	}
	
	private void buildExtQueryFieldStatement(Map map) {
		List<ExtQueryField> fields = extendQuery.getFields();
		ExtQueryField field;
		String value, begint, endt;
		StringBuffer where = new StringBuffer();
		for (int i=0; i<fields.size(); i++) {
			field = fields.get(i);
			value = (String) map.get(field.getId());
			if (field.getFieldExpr() != null)
				extQueryFieldStat.append(",").append(field.getFieldExpr()).append(" ").append(field.getId());
			else
				extQueryFieldStat.append(",d.").append(field.getId());
			if (value != null && !value.equals("")) {
				if ("equals".equals(field.getOperator()))
					where.append(" and d.").append(field.getId()).append("='").append(value).append("'");
				if ("=".equals(field.getOperator()))
					where.append(" and d.").append(field.getId()).append("=").append(value);
				if ("like".equals(field.getOperator()))
					where.append(" and d.").append(field.getId()).append(" like '").append(value).append("%'");				
			} else {
				begint = (String)map.get("startTime_" + field.getId());
				if ("".equals(begint)) begint = null;
				if ("".equals(begint)) endt = null;
				endt = (String)map.get("endTime_" + field.getId());				
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
	
	/*public String buildQuerySql(Map map) {
		return "";
	}
	
	public String buildCountSql(Map map) {
		return "";
	}*/
	
	/**
	 * �����ѯ��SQL
	 */
	public String buildQuerySql(Map map) {
		String where = buildWhereStatement(map);
		
		String orderStat = " order by REMINDED desc, ASSIGN_TIME desc";		
		FieldSort fieldSort = (FieldSort)map.get("sort");
		if (fieldSort.isSorted()) {
			orderStat = " order by REMINDED desc, " + fieldSort.toSortStatement().trim();
		} 
		
		String sql = "select distinct a.INST_ID, c.TASK_ID, c.PRE_TASKID, a.ATTACHFILE, a.INST_TITLE," +
					 " a.PROC_NAME, " +
					 //" a.INIT_TIME, " +
					 dialectDateToChar("a.INIT_TIME", Dialect.DATETIME_FORMAT_STANDARD) + " INIT_TIME, " +
					 "a.INIT_UID, a.INIT_UNAME, c.ASSIGN_UID, c.ASSIGN_UNAME, " +
					 //"c.ASSIGN_TIME," +
					 dialectDateToChar("c.ASSIGN_TIME", Dialect.DATETIME_FORMAT_STANDARD) + " ASSIGN_TIME, " +					 
					 " c.TASK_NAME, b.READ_FLAG, b.DEAL_FLAG, c.DUE_TIME DUE_TIME2, a.DUE_TIME, c.DEALER, c.REMINDED, c.REMIND_MSG," +
					 " c.REMIND_USERID, a.PRINTED, c.LOCKED, c.LOCK_USERID, " +
					 //"a.CLOSE_TIME, " +
					 dialectDateToChar("a.CLOSE_TIME", Dialect.DATETIME_FORMAT_STANDARD) + " CLOSE_TIME, " +
					 "c.TRANS_NAME " + 
					 extQueryFieldStat +
					 " FROM BPM_INST a, BPM_ASSIGN b, BPM_TASK c " + extQueryFromStat + 
					 where + orderStat;
					 //" order by c.REMINDED desc, c.ASSIGN_TIME desc";
		if (map.get("view").equals("all") || map.get("view").equals("fromMe")) {
			sql = "select distinct a.INST_ID, c.TASK_ID, c.PRE_TASKID, a.ATTACHFILE, a.INST_TITLE," +
					 " a.PROC_NAME, " +
					 //"a.INIT_TIME, " +
					 dialectDateToChar("a.INIT_TIME", Dialect.DATETIME_FORMAT_STANDARD) + " INIT_TIME, " +
					 "a.INIT_UID, a.INIT_UNAME, c.ASSIGN_UID, c.ASSIGN_UNAME, " +
					 //"c.ASSIGN_TIME," +
					 dialectDateToChar("c.ASSIGN_TIME", Dialect.DATETIME_FORMAT_STANDARD) + " ASSIGN_TIME, " +
					 " c.TASK_NAME, 1 READ_FLAG, 2 DEAL_FLAG, c.DUE_TIME DUE_TIME2, a.DUE_TIME, c.DEALER, c.REMINDED, c.REMIND_MSG," +
					 " c.REMIND_USERID, a.PRINTED, c.LOCKED, c.LOCK_USERID, " +
					 //"a.CLOSE_TIME, " +
					 dialectDateToChar("a.CLOSE_TIME", Dialect.DATETIME_FORMAT_STANDARD) + " CLOSE_TIME, " +
					 "c.TRANS_NAME " + 
					 extQueryFieldStat +
					 " FROM BPM_INST a, BPM_TASK c " + extQueryFromStat + 
					 where + orderStat;
					 //" order by c.REMINDED desc, c.ASSIGN_TIME desc";
		}		
		sql = addSetMaxRowClause(sql, map);
		CommonLogger.logger.info(sql);
		return sql; 
	}
	
	/**
	 * ����ͳ�Ƶ�SQL
	 */
	public String buildCountSql(Map map) {
		String where = buildWhereStatement(map);
		String sql = "select count(distinct a.INST_ID) FROM BPM_INST a, BPM_ASSIGN b, BPM_TASK c " + 
					extQueryFromStat + where;
		if (map.get("view") != null && (map.get("view").equals("all") || map.get("view").equals("fromMe"))) 
			sql = "select count(distinct a.INST_ID) FROM BPM_INST a, BPM_TASK c " + extQueryFromStat +
					where;
		CommonLogger.logger.info(sql);
		return sql; 
	}
	
	/*public int directCount(Map map) {
		String sql = buildQuerySql2(map);
		JdbcTemplate jdbc = DaoFactory.getJdbc("jbpm");
		jdbc.setMaxRows(QUERY_MAXROW);
		jdbc.setQueryTimeout(5);
		CommonLogger.logger.debug(sql);
		resultList = jdbc.queryForList(sql);
		return resultList.size();
	}
	
	public List<Map> directFetch(Map map, int pageNo, int pageSize) {
		return scroll(resultList, pageNo, pageSize);
	}*/
	
	protected List<Map> scroll(List<Map>resultset, int pageNo, int pageSize) {
		if (pageNo*pageSize < resultset.size())
			return resultset.subList((pageNo-1)*pageSize, pageNo*pageSize);
		else
			return resultset.subList((pageNo-1)*pageSize, resultset.size());
	}
	
	/*private String getUserInSameDept(String userId) {
		String ret = "";
		Department[] depts = new SysUser(userId).getDepts();
		if (depts.length > 0) {
			List<User> users = depts[0].getUsers();
			for (int i=0; i<users.size(); i++) {
				ret = users.get(i).getId() + "','";
			}
		} else
			ret = userId;
		return ret;
	}*/
	
	private String buildRoleFilter(String userId) {
		if (!roleFilter.equals(""))
			return roleFilter;
		roleFilter = " b.DEALER='" + userId + "' OR";
		User user = new SysUser();
		user.setLoginName(userId);
		Role[] roles = user.getRoles();
		for (int i=0; i<roles.length; i++) {			
			roleFilter = roleFilter + " b.DEALER='" + BpmConstants.PREFIX_ROLE + roles[i].getId() + "' OR";
		}
		roleFilter = " and (" + roleFilter.substring(0, roleFilter.length()-3) + ")";
		return roleFilter;		
	}
	
	private String buildDeptFilter(String userId) {
		if (!deptFilter.equals(""))
			return deptFilter;
		deptFilter = " b.DEALER='" + userId + "' OR";
		Department[] depts = new SysUser(userId).getDepts();
		for (int i=0; i<depts.length; i++) {			
			deptFilter = deptFilter + " b.DEALER='" + BpmConstants.PREFIX_DEPT + depts[i].getId() + "' OR";
		}
		deptFilter = " and (" + deptFilter.substring(0, deptFilter.length()-3) + ")";
		return deptFilter;
	}
	
	private String buildGroupFilter(String userId) {
		if (!groupFilter.equals(""))
			return groupFilter;
		groupFilter = " b.DEALER='" + userId + "' OR";
		
		List groupIds = getGroupIdsByUserCode(userId);
		String groupId;
		for (int i=0; i<groupIds.size(); i++) {			
			groupId = (String) ((Map)groupIds.get(i)).get("grop_code");
			groupFilter = groupFilter + " b.DEALER='" + BpmConstants.PREFIX_GROUP + groupId + "' OR";
		}
		groupFilter = " and (" + groupFilter.substring(0, groupFilter.length()-3) + ")";
		return groupFilter;
	}
	
	private List getGroupIdsByUserCode(String userId) {
		String sql = "select grop_code from user_group where user_code='" + userId + "'";
		return DaoFactory.getJdbc("jbpm", "openEAP").queryForList(sql);		
	}
	
	protected String addSetMaxRowClause(String sql, Map param) {
		if ("true".equals(param.get("forExport"))) {
			String exportMaxRows = AppHandle.getHandle("eaptag").getProperty("GRID_EXPORT_MAX");
			if (ProcessEngine.getDialect() instanceof SybaseDialect)
				return sql = "set rowcount " + exportMaxRows + " " + sql + " set rowcount 0";
			if (ProcessEngine.getDialect() instanceof OracleDialect)
				return sql = "select * from (" + sql + ") where rownum<" + exportMaxRows;
		}
		if (ProcessEngine.getDialect() instanceof SybaseDialect)
			return sql = "set rowcount " + QUERY_MAXROW + " " + sql + " set rowcount 0";
		if (ProcessEngine.getDialect() instanceof OracleDialect)
			return sql = "select * from (" + sql + ") where rownum<" + QUERY_MAXROW;
		return sql;
	}
	
	protected String dialectDateToChar(String field, int type) {
		if (dialect instanceof OracleDialect) {
			return dialect.datetochar(field, type);
		} else {
			return field;
		}
	}
	
	protected boolean isNotNull(String s) {
		return (s != null && !s.equals("null") && !s.trim().equals(""));
	}
}
