package com.hanson.jbpm.mgmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.exe.ctx.HistoryLuceneTable;
import com.hanson.jbpm.jpdl.exe.ctx.InstanceLuceneTable;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.ui.grid.DBGTGridDataProvider;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.jdbc.RowModel;
import com.suntek.eap.util.jdbc.Dialect;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.lucene.LuceneQuery;

public class WorkItemListLuceneQuery extends DBGTGridDataProvider {

	private final static int INDEXRECORD_READNUM = 
				Integer.parseInt(AppHandle.getHandle(ProcessEngine.getClientModuleName()).getProperty("INDEXRECORD_READNUM"));

	private static Dialect dialect = null;

	public WorkItemListLuceneQuery() {		
		super(AppHandle.getHandle(ProcessClient.MODULE).getDatasourceName());
		if (dialect == null)
			dialect = ProcessEngine.getDialect();
	}

	private List<RowModel> resultSet = new ArrayList<RowModel>();

	private DBGTGridDataProvider provider = null;

	public String buildQuerySql(Map map) {
		fulltextFindInstanceIds(map);
		return provider.buildQuerySql(map);
	}

	public String buildCountSql(Map map) {
		//fulltextFindInstanceIds(map);
		return provider.buildCountSql(map);
		//return String.valueOf(map.get("bizId").toString().split(",").length);
	}

	/**
	 * ��ȫ������������ƥ���ʵ�� id
	 * @param map ����
	 */
	public String fulltextFindInstanceIds(Map map) {
		try {
			String findText = (String)map.get("findText");
			String processName = (String)map.get("processName");

			String view = (String)map.get("view");

			if ("history".equals(view)) {
				provider = new WorkItemListHisQuery();
			} else {
				provider = new WorkItemListQuery();
			}

			String[] condition = new String[] { "allfields:" + findText + ":" + LuceneQuery.TOKENIZED,	
					"PROC_NAME:" + processName + ":" + LuceneQuery.UNTOKENIZED};

			if ("history".equals(view))
				resultSet = new HistoryLuceneTable(HistoryLuceneTable.instanceTableName).find(condition, null);
			else {
				resultSet = new InstanceLuceneTable(InstanceLuceneTable.instanceTableName).find(condition, null);
			}

			//List<Map> list = new ArrayList<Map>();
			RowModel row;			
			//Map<String, String> record;
			StringBuffer instIds = new StringBuffer("");
			CommonLogger.logger.debug("����" + processName + "�ؼ���{" + findText + "}ȫ�ļ���, ƥ���¼: " + resultSet.size());
			for (int i=0; i<resultSet.size() && i<INDEXRECORD_READNUM; i++) {
				row = resultSet.get(i);
				String[] columns = row.getColumnNames();
				//record = new HashMap<String, String>();
				instIds.append(row.getColumnValue("INST_ID")).append(",");
			}			
			if (instIds.length()>1) {
				instIds = instIds.deleteCharAt(instIds.length()-1);
				map.put("instId", instIds.toString());
				return instIds.toString();
			}
		} catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
		}		

		map.put("instId", "-");
		return "-";
	}
}
