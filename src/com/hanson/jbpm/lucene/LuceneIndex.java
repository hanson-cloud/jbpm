package com.hanson.jbpm.lucene;

import java.io.IOException;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.log.LoggerUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;

@SuppressWarnings("deprecation")
public class LuceneIndex {
	
	/**
	 * ������¼
	 * @param record
	 * @return
	 * @throws IOException
	 */
	public boolean insert(LuceneRecord record) throws IOException {
		try {
			Document doc = record.toDocument();
			LoggerUtil.getLogger().debug("Insert: " +doc);
			IndexWriterFactory.getInstance().addDocument(doc);
			IndexWriterFactory.getInstance().commit();
			return true;
		} catch (IOException ex) {
			throw ex;
		} 
	}
	
	/**
	 * ɾ����¼
	 * @param record
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public boolean delete(LuceneRecord record) throws IOException, ParseException {
		try {			
			LoggerUtil.getLogger().debug("Delete[" + record.getDocumentId()+ "]: ");
			IndexWriterFactory.getInstance().deleteDocuments(new Term("INST_ID", record.get("INST_ID")));
			IndexWriterFactory.getInstance().commit();
			return true;
		} catch (IOException ex) {
			throw ex;
		} 		
	}
	/**
	 * ����ɾ���ĵ���������ɾ��
	 * @param tableName	����
	 * @return
	 * @throws IOException
	 */
	public boolean delete(String tableName, String procName) throws IOException {
		try {			
			IndexWriterFactory.getInstance().deleteDocuments(new Term("PROC_NAME", procName));
			return true;
		} catch (IOException ex) {
			throw ex;
		} 
	}
	
	public void list() throws IOException {
		try {			
			Document doc = null;
			for (int i=0; i<IndexReaderFactory.getInstance().maxDoc(); i++) {
				if (IndexReaderFactory.getInstance().isDeleted(i))
					continue;
				doc = IndexReaderFactory.getInstance().document(i);
				CommonLogger.logger.debug(doc.toString());
			}
		} catch (IOException ex) {
			throw ex;
		} 
	}
	
	/**
	 * �޸ļ�¼
	 * @param record
	 * @return
	 * @throws IOException
	 */
	public boolean update(LuceneRecord record) throws IOException, ParseException {
		return delete(record) && insert(record);
	}
	
	/**
	 * ��ȡ������д��
	 * @return
	 * @throws IOException
	 */
	/*private IndexWriter getIndexWriter() throws IOException {
		IndexWriter indexWriter = null;
		if (IndexReader.indexExists(IndexWriterFactory.getIndexPath()))
			indexWriter = new IndexWriter(IndexWriterFactory.getIndexPath(), getAnalyzer(), false);
		else
			indexWriter = new IndexWriter(IndexWriterFactory.getIndexPath(), getAnalyzer(), true);
		
        indexWriter.setMaxMergeDocs(MAX_MERGEDOCS);
        indexWriter.setMergeFactor(MERGE_FACTOR);	
        
		return indexWriter;		
	}*/
	
	
	public static void main(String[] arg) throws Exception {
		System.setProperty("EAP_HOME", "c:/openeap318");
		System.setProperty("EAP_DEBUG", "true");
		System.setProperty("ECLIPSE_HOME", "f:/eclipse");
		
		LoggerUtil.setDebugMode();
				
		new LuceneIndex().list();
		
		/*IndexSearcher is = null;
		try {
			is = new IndexSearcher(IndexReader.open(IndexWriterFactory.LUCENE_INDEX_PATH));

			BooleanQuery bq = new BooleanQuery();
			
			 �������ڵ�ǰ��                
            Query query;
            
            query= new TermQuery(new Term("table", InstanceLuceneTable.instanceTableName));                          
            //query= new TermQuery(new Term("table", HistoryLuceneTable.instanceTableName));
            bq.add(query, BooleanClause.Occur.MUST);            
			
             ������ѯ����, �������Lucene��Parse�﷨ 
            String[] condition = new String[]{"allfields:����"};
            QueryParser parser = null;
            String[] term;
            for (int i=0; i<condition.length; i++) {
            	term = condition[i].split(":");
            	if (term[0].equals("allfields") || term[2].equals("1")) {
            		parser = new QueryParser(term[0], IndexWriterFactory.getAnalyzer());            
        			parser.setDefaultOperator(QueryParser.OR_OPERATOR);        			
        			query = parser.parse(term[1]);
            	} else {
            		query = new TermQuery(new Term(term[0], term[1]));
            	}
            	bq.add(query, BooleanClause.Occur.MUST);
            }          
            
             �������м�¼���췵�ؽ����List, ��ָ�������ֶ� sortFieldName ���� 
			Hits hits = is.search(bq);			
			
			for (int i=0; i<hits.length(); i++) {				
				System.out.println(hits.doc(i));
			}
		} catch (IOException e) {
			throw e;
		} catch (ParseException e) {
			throw e;
		} finally {
			try { is.close(); } catch(Exception ex) {}
		}*/
	}
}
