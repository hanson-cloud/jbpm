package com.hanson.jbpm.lucene;

import java.util.HashMap;
import java.util.Map;

import com.suntek.eap.jdbc.RowModel;

/**
 * ��lucene�ֶμ�����RowModel�Ķ���
 * @author tzc
 *
 */
public class LuceneRowModel implements RowModel {
	
	private Map fields ;
	
	private float score;
	
	public int getColumnCount() {
		return fields.size();
	}

	public String getColumnName(int index) {
		// TODO Auto-generated method stub
		return (String)fields.keySet().toArray()[index];
	}

	public String[] getColumnNames() {
		Object[] obj =  fields.keySet().toArray();
		String[] columns = new String[obj.length];
		for(int i = 0 ;i <columns.length;i++){
			columns[i] = (String)obj[i];
		}
		return columns;
	}

	public String getColumnValue(String key) {
		Object value = fields.get(key) ;
		if(value == null) return null;
		else return value  + "";
		
	}

	public String getColumnValue(int index) {
		Object value = fields.get(this.getColumnName(index)) ;
		if(value == null) return null;
		else return value  + "";
	}
	
	public void setColumnValue(String columnName,String columnValue){
		if(fields == null ) fields = new HashMap();
		fields.put(columnName, columnValue);
	}
	
	public LuceneRowModel(Map fields){
		this.fields = fields;
		
	}

	public void setScore(float score) {
		this.score = score;
	}

	public float getScore() {
		return score;
	}

}
