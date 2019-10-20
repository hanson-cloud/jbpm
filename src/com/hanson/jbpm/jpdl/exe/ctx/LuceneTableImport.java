package com.hanson.jbpm.jpdl.exe.ctx;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.log.LoggerUtil;
import com.hanson.jbpm.lucene.IndexReaderFactory;
import com.hanson.jbpm.lucene.IndexSearcherFactory;
import com.hanson.jbpm.lucene.IndexVersion;
import com.hanson.jbpm.lucene.IndexWriterFactory;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.pico.ILocalComponent;

public class LuceneTableImport implements ILocalComponent {

	public Object invoke(Object[] arg) {
		return exec((String)arg[0], (String)arg[1]);
	}
	
	public Object execRequest(String procName, String module) 
	throws BpmException, InstantiationException, IllegalAccessException {
		return OpenCC.getComponentContainer().getComponent(null, "LuceneTableImport").invoke(new String[]{procName, module});
	}
	
	public String exec(String procName, String module) throws BpmException {
		try {						
			IndexWriterFactory.close();
			
			if (module == null || module.equals("")) module = "jbpm";
			
			InstanceLuceneTable table = new InstanceLuceneTable(InstanceLuceneTable.instanceTableName);			
			table.truncate(procName);
			
			JdbcTemplate jdbc = DaoFactory.getJdbc(module);
			
			String sql = "select INST_ID, PROC_NAME, INST_TITLE from BPM_INST" + 
						 " where PROC_NAME='" + procName + "'";
			LoggerUtil.getLogger().debug(sql);
			List list = jdbc.queryForList(sql);		
			
			Map row = null;
			for (int i=0; i<list.size(); i++) {	
				row = (Map)list.get(i);
				table.insert((String)row.get("INST_ID"), (String)row.get("PROC_NAME"), 
							 (String)row.get("INST_TITLE"), "");
			}
			
			//-------------------------------------
			HistoryLuceneTable table2 = new HistoryLuceneTable(HistoryLuceneTable.instanceTableName);	
			//table2.truncate(tableName);
			
			sql = "select INST_ID, PROC_NAME, INST_TITLE from BPM_INST_HIS" + 
				  " where PROC_NAME='" + procName + "'";
			LoggerUtil.getLogger().debug(sql);			
			list = jdbc.queryForList(sql);		
			
			row = null;
			for (int i=0; i<list.size(); i++) {	
				row = (Map)list.get(i);
				table2.insert((String)row.get("INST_ID"), (String)row.get("PROC_NAME"), 
							(String)row.get("INST_TITLE"), "");
			}
			//------------------------------------
			IndexWriterFactory.getInstance().commit();			
			IndexWriterFactory.getInstance().optimize();
			
			IndexWriterFactory.close();
			
			IndexReaderFactory.close();
			IndexSearcherFactory.close();
			
			LoggerUtil.getLogger().info("�ؽ�����  " + procName + " ��������!");
			
			IndexVersion.newVersion();
			
			return "�ؽ����� " + procName + " �������";
		}
		catch (Exception ex) {
			LoggerUtil.getLogger().error(ex, ex);			
			throw new BpmException("Lucene �������ؽ�����ʧ��: " + ex.getMessage());
		}
	}	
	
	public static void main(String[] args) {
		new LuceneTableImport().exec("", "");
	}
}
