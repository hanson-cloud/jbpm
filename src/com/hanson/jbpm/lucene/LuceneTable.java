package com.hanson.jbpm.lucene;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import com.suntek.eap.jdbc.RowModel;

/**
 * <p>Lucene�������ݱ� 
 * 
 * <p>��װ�˶�Lucene���ݱ��������ɾ�Ĳ����. 

 * <p>Lucene�������ݱ�ļ�¼��ѯ�Ͳ���ʾ��: 
 * 		���ݲ���֮ǰ, ��Ҫ new һ�������  �������ñ�������ֶ�:
 * 		new LuceneTable("test"); 
 * 		table.setPrimaryKey("flow_id");
 * 
 * 		1. �����������в�ѯ:��table.findByPrimaryKey("1000"); <br>
 * 		2. ��������ѯ:       table.find(new String[]{"flow_id:1000", "flow_name:satisfaction","allfields:ddd"}); <br>
 *                          ��������ѯ�������ؽ������QueryHelp, ���Եõ�List<RowModel>.	 <br>
 * 		3. ��¼ɾ��: 		table.findByPrimaryKey("1000").delete(); <br>
 * 		4. ��¼�޸�: <br> 
 * 		                    1)LuceneRecord rec = table.findByPrimaryKey("1000"); <br>
 * 							2)rec.set('key', 'value'); ... <br>
 * 							3)rec.update(); <br>
 * 		5. ������¼: <br>		
 * 							1)LuceneRecord rec = table.newRecord(); <br>
 * 							2)rec.setPrimaryKey('1000'); 
 * 							  rec.set('key','value'); ... <br>
 * 							3)rec.insert(); <br>
 * @see LuceneRecord, PKNullException
 */
public class LuceneTable {
	/* ������ */
	private String tableName;
	/* �������� */
	private String primaryKey;
	
	/**
	 * ���췽��
	 * @param 	tableName	����
	 * @param	primaryKey	�����ֶ���
	 * @throws Exception
	 */
	public LuceneTable(String tableName) throws Exception {
		this.tableName = tableName;		
	}
		
	/**
	 * ����һ����¼, ����Ϊ��
	 * @return
	 */
	public LuceneRecord newRecord() {
		LuceneRecord rec = new LuceneRecord(primaryKey);
		rec.setTable(this);
		return rec;
	}
	
	/**
	 * ���������м�¼��ѯ
	 * @param pkValue
	 * @return
	 */
	public LuceneRecord findByPrimaryKey(String pkValue) throws IOException, ParseException {				
		return new LuceneQuery(this).findByPrimaryKey(pkValue);
	}
	
	/**
	 * ��������¼��ѯ
	 * ��ѯ���������е�ÿ��Ԫ�ض��������Lucene��Parse�﷨
	 * ʾ��: flow_id:1000
	 * @param condition
	 * @return
	 */
	public List<RowModel> find(String[] condition) throws IOException, ParseException {
		return new LuceneQuery(this).find(condition, null);
	}
	
	/**
	 * �������ֶεĶ�������¼��ѯ
	 * ��ѯ���������е�ÿ��Ԫ�ض��������Lucene��Parse�﷨
	 * ����: new String[]{"flow_id:1000"}
	 * @param condition		��ѯ��������
	 * @param sortFieldName	�����ֶ�
	 * @return
	 */
	public List<RowModel> find(String[] condition, String sortFieldName) throws IOException, ParseException {
		return new LuceneQuery(this).find(condition, sortFieldName);
	}
	
	/**
	 * ����ɾ����������������
	 * @return
	 */
	public boolean truncate(String procName) throws IOException {
		return new LuceneIndex().delete(this.tableName, procName);
	}
	
	public void list() {
		
	}
	
	public String getPrimaryKey() {
		return this.primaryKey;
	}
	
	public String getName() {
		return this.tableName;
	}
	
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public static void main(String[] arg) throws Exception {
		LuceneTable table = new LuceneTable("test");
		table.setPrimaryKey("flow_id");
	
		System.out.println(table.findByPrimaryKey("1004"));
		System.out.println(table.find(new String[]{"flow_id:1004", "allfields:234"}).size());
		//System.out.println(table.find(new String[]{"allfields:�л�"}).size());
		
		/*LuceneRecord rec = table.findByPrimaryKey("1004");
		rec.set("flow_name", "�л����񹲺͹�", true);
		rec.update();*/
		
		//table.truncate();
		
		/*LuceneRecord rec = table.newRecord();
		rec.setPrimaryKey("1004");
		rec.setEx("flow_name", "�л�����1234���͹�");
		rec.insert();*/	
		
		//LuceneRecord rec = table.findByPrimaryKey("1004");
		//rec.delete();
		
	}
	
}