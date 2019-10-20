package com.hanson.jbpm.mgmt;

import java.util.List;
import java.util.Map;

import com.hanson.jbpm.log.CommonLogger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.suntek.util.string.StringHelper;

public class PrinterProviderSQL implements IPrinterProvider {
	private String sql;
	private String[] sqlSubtable = new String[]{};
	private String[] subtable = new String[]{};
	
	public PrinterProviderSQL(String sql, String[] subtable, String[] sqlSubtable) {
		this.sql = sql;
		this.subtable = subtable;
		this.sqlSubtable = sqlSubtable;
	}
	
	public Map getData(String instId) {
		JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
		jdbc.setQueryTimeout(60*60*60*1000);
		String printSql = StringHelper.replace(sql, "?INST_ID", instId);
		CommonLogger.logger.info("������ӡ���:" + printSql);
		Map map = jdbc.queryForMap(printSql);
		if (subtable != null) {
			for (int i=0; i<subtable.length; i++) {
				printSql = sqlSubtable[i];
				printSql = StringHelper.replace(printSql, "?INST_ID", instId);
				CommonLogger.logger.info("�ӱ��ӡ���[" + subtable[i] + "]:" + printSql);
				map.put(subtable[i], jdbc.queryForList(printSql));
			}
		}		
		return map;
	}

	public List getMultipleOrderData(String bizIds)
	{
	    JdbcTemplate jdbc = DaoFactory.getJdbc(ProcessClient.MODULE);
	    String printSql = StringHelper.replace(sql, "?INST_ID", bizIds);
	    return jdbc.queryForList(printSql);
	}	
	
	
	
}
