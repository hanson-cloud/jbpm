package com.hanson.jbpm.lucene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.log.CommonLogger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.ParseException;

import com.suntek.eap.jdbc.RowModel;
import com.hanson.jbpm.log.LoggerUtil;

/**
 * ���¼ 
 * @author zhout
 */
public class LuceneRecord {	
	/* ��¼һ�����ݵ��ֶ�ֵ�б�<k, v> */
	private Map<String, Object> fields = new HashMap<String, Object>();	
	private Map<String, String> tokenizedFields = new HashMap<String, String>();
	private StringBuffer allFields = new StringBuffer("");
	
	/* ��¼����������� */
	private String primaryKey;	
	
	/* Lucene �������ĵ�ID*/
	private int documentId;
	private float score;
	
	/* ��������, ������¼���޸�ɾ������*/
	private LuceneTable table;	
	
	/**
	 * ���췽��
	 * @param primaryKey
	 */
	public LuceneRecord(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/** 
	 * ��Document����LuceneRecord����
	 * @param doc
	 */
	public LuceneRecord(Document doc, int documentId, float score) {	
		this.documentId = documentId;
		this.score = score;
		
		List<Field> list = doc.getFields();
		Field f = null;
		for (int i=0; i<list.size(); i++) {
			f = list.get(i);
			if (f.name().equals("allfields")) {
				continue;
			}			
			if (f.name().equals(primaryKey))
				this.setPrimaryKeyValue(f.stringValue());
			else
				this.set(f.name(), f.stringValue());
		}
	}
	
	/**
	 * ת����Lucene��Document���� 
	 * @return
	 */
	public Document toDocument() {
		Document doc = new Document();
		String tableName = (String)fields.get("table");
		if (tableName == null) tableName = table.getName();
		/* ���� */
		doc.add(new Field("table", tableName, Field.Store.YES, Field.Index.UN_TOKENIZED));
		/* ���� */
		doc.add(new Field("primarykey", (String)fields.get(table.getPrimaryKey()), Field.Store.YES, Field.Index.UN_TOKENIZED));
		
		/* �����ֶ� */		
		Iterator<String> it = fields.keySet().iterator();
		String key = "";
		while(it.hasNext()) {
			key = (String)it.next();
			if (key.equals("allfields") || key.equals("table") || key.equals("primarykey"))
				continue;
			if (tokenizedFields.containsKey(key))
				doc.add(new Field(key, (String)fields.get(key), Field.Store.NO, Field.Index.TOKENIZED));
			else
				doc.add(new Field(key, (String)fields.get(key), Field.Store.YES, Field.Index.UN_TOKENIZED));
		}		
		
		doc.add(new Field("allfields", allFields.toString(), Field.Store.YES, Field.Index.TOKENIZED));
				
		return doc;
	}
	
	/**
	 * ���ü�¼�������Ƿ��ظ�
	 * @return
	 * @throws PrimaryKeyNullException
	 */
	public boolean primaryKeyNullCheck() throws PKNullException {
		String s = (String)fields.get(primaryKey);
		if (s == null || s.equals(""))
			throw new PKNullException();		
		return true;		
	}
	
	/**
	 * ���ü�¼�������Ƿ��ظ�
	 * @return
	 * @throws PrimaryKeyDuplicatedException
	 */
	public boolean primaryKeyDuplicatedCheck() 
	throws IOException, ParseException, PKDuplicatedException {
		/*String s = (String)fields.get(primaryKey);
		if (table.findByPrimaryKey(s) != null)
			throw new PKDuplicatedException();*/
		
		return true;		
	}
	
	/**
	 * ��¼ת����RowModel����
	 * @return
	 */
	public RowModel toRowModel() {		
		LuceneRowModel row = new LuceneRowModel(this.fields);
		row.setScore(this.score);
		return row;
	}
	
	/**
	 * ��Xml����ɾ����������
	 * @throws Exception
	 */
	public void delete() throws Exception {
		LoggerUtil.getLogger().debug("Delete: " + this.toString());
		new LuceneIndex().delete(this);
		IndexVersion.newVersion();
	}
	
	/**
	 * ���±������ݵ�Xml��
	 * @throws Exception
	 */
	public void update() throws Exception {
		LoggerUtil.getLogger().debug("Update: " + this.toString());
		new LuceneIndex().update(this);
		IndexVersion.newVersion();
	}
	
	/**
	 * ������м�¼
	 * @throws Exception
	 */
	public void insert() 
	throws PKDuplicatedException, PKNullException, IOException, ParseException {
		if (primaryKeyNullCheck() && primaryKeyDuplicatedCheck()) {
			CommonLogger.logger.debug("Insert: " + this.toString());
			new LuceneIndex().insert(this);
			IndexVersion.newVersion();
		}
	}
	
	/**
	 * �ֶθ�ֵ
	 * @param fieldName		�ֶ���
	 * @param fieldValue	�ֶ�ֵ
	 */
	public void set(String fieldName, String fieldValue) {
		if(fieldValue == null) fieldValue = "";
		fields.put(fieldName, fieldValue);
		allFields.append(fieldValue).append(", ");
	}
	
	/**
	 * �ֶθ�ֵ, ���ֶν�����allfields���зִ�����
	 * @param fieldName		�ֶ���
	 * @param fieldValue	�ֶ�ֵ
	 */
	public void setEx(String fieldName, String fieldValue, boolean tokenized) {
		if(fieldValue == null) fieldValue = "";
		fields.put(fieldName, fieldValue);
		if (tokenized)
			tokenizedFields.put(fieldName, "");
	}
	
	/**
	 * �ֶθ�ֵ���Զ����벢�����µı����ֶΡ�
	 * @param fieldName		�ֶ���
	 * @param fieldValue	�ֶ�ֵ
	 * @param spellCoding	�Ƿ���б���
	 */
	public void set(String fieldName, String fieldValue, boolean spellCoding) {		
		if(fieldValue == null) fieldValue = "";
		set(fieldName, fieldValue);
		
		allFields.append(fieldValue).append(", ");
		
		if (spellCoding  && !fieldValue.trim().equals("")) {
			/* ����ת�� */
			DefaultCnToSpell coder = new DefaultCnToSpell();		
			String code = coder.cnToSingleSpell(fieldValue);
			code = code + ", " + coder.cnToSpell(fieldValue);
			code = code + ", " + coder.cnToSpellTail(fieldValue);
			code = code + ", " + coder.cnToSpellHead(fieldValue);
			set(fieldName + "_code", code);
		
			allFields.append(code).append(", ");
		}		
	}
	
	/**
	 * ������ֵ
	 * @param pkValue
	 */
	public void setPrimaryKeyValue(String pkValue) {
		fields.put(primaryKey, pkValue);
	}
	
	/**
	 * ���ؼ�¼�������ֶ�����
	 * @return
	 */
	public Iterator<String> getFieldNames() {
		return fields.keySet().iterator();
	}
	
	public LuceneTable getTable() {
		return table;
	}

	public void setTable(LuceneTable table) {
		this.table = table;
		this.primaryKey = table.getPrimaryKey();
	}
	
	public String get(String fieldName) {
		return (String)fields.get(fieldName);
	}
		
	public String toString() {
		Iterator<String> it = fields.keySet().iterator();
		String ret = "primarykey=" + this.primaryKey + ", ";
		String key = "";
		while(it.hasNext()) {
			key = (String)it.next();
			ret = ret + key + "=" + fields.get(key) + ", ";
		}
		return ret;
	}

	/*public String getPrimaryKeyName() {
		return primaryKey;
	}*/

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	public float getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
