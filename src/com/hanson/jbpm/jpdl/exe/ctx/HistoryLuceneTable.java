package com.hanson.jbpm.jpdl.exe.ctx;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.log.LoggerUtil;
import com.hanson.jbpm.lucene.LuceneRecord;
import com.hanson.jbpm.lucene.LuceneTable;
import com.hanson.jbpm.lucene.PKDuplicatedException;
import com.hanson.jbpm.lucene.PKNullException;

public class HistoryLuceneTable extends LuceneTable {
	public final static String instanceTableName = "BPM_INST_HIS";
	protected final static String primaryKey = "INST_ID";
		
	public HistoryLuceneTable(String tableName) throws Exception {
		super(tableName);
		setPrimaryKey(primaryKey);
	}
	
	public void insert(String instanceId, String processName, String title, String selfDefinedVariable) 
	throws BpmException {
		try {
			LuceneRecord record = newRecord();
			
			record.setPrimaryKeyValue(instanceId);
			
			record.setEx("INST_ID", instanceId, false);
			record.setEx("PROC_NAME", processName, false);
			record.set("INST_TITLE", title);			
			record.set(Context.values.LUCENETEXT, selfDefinedVariable);
			
			record.insert();
		} catch (PKNullException ex) {
			LoggerUtil.getLogger().error(ex, ex);
			throw new BpmException("Lucene ��������Ϊ���쳣: " + ex.getMessage());
		} catch (PKDuplicatedException ex) {
			LoggerUtil.getLogger().error(ex, ex);
			throw new BpmException("Lucene ���������ظ��쳣: " + ex.getMessage());
		} catch (Exception ex) {
			LoggerUtil.getLogger().error(ex, ex);
			throw new BpmException("Lucene ��ͬ���쳣: " + ex.getMessage());
		}
	}
	
}
