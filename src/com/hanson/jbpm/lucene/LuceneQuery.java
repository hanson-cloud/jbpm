package com.hanson.jbpm.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.WorkItemListLuceneQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;

import com.suntek.eap.jdbc.RowModel;

/**
 * XML���ݱ��ѯ��
 * @author zhout
 *
 */
@SuppressWarnings("deprecation")
public class LuceneQuery {
	//private static int MAX_RESULTSET_SIZE = 1000;
	
	public static String UNTOKENIZED = "0";
	public static String TOKENIZED = "1";	
	
	/* ��ѯ�����ݱ� */
	private LuceneTable table;
	
	/**
	 * ���췽��
	 * @param table
	 */
	public LuceneQuery(LuceneTable table) {
		this.table = table;
	}
	
	/**
	 * �����������Ҽ�¼
	 * @param pkValue
	 * @return
	 */
	public LuceneRecord findByPrimaryKey(String pkValue) throws IOException, ParseException {	
		try {
			long start = System.currentTimeMillis();

			BooleanQuery bq = new BooleanQuery();
			
			/* ��������ƥ�� */
			Query query = new TermQuery(new Term(table.getPrimaryKey(), pkValue));
            bq.add(query, BooleanClause.Occur.MUST);
            
            /* �������ڵ�ǰ�� */
            Query query2 = new TermQuery(new Term("table", table.getName()));
            bq.add(query2, BooleanClause.Occur.MUST);
                        
			Hits hits = IndexSearcherFactory.getInstance().search(bq);
			if (hits.length()>0) {
				LuceneRecord record = new LuceneRecord(hits.doc(0), hits.id(0), hits.score(0));
				record.setTable(table);	
				
				CommonLogger.logger.debug("LuceneQuery find use: " + (System.currentTimeMillis() - start));
				
				return record;
			}
			else
				return null;						
		} catch (IOException e) {
			throw e;
		} 
	}
	
	/**
	 * ��������¼��ѯ
	 * �����ѯ��������Ϊ AND ��ϵ
	 * @param condition 	��ѯ����, ÿ������Ԫ�ض���һ������Lucene��Parse�﷨ 
	 * @param sortFieldName	�����ֶ�����
	 * @return
	 */	
	public List<RowModel> find(String[] condition, String sortFieldName) throws IOException, ParseException { 
		List<RowModel> alist = new ArrayList<RowModel>();
		
		try {
			long start = System.currentTimeMillis();
			
			BooleanQuery bq = new BooleanQuery();
			
			/* �������ڵ�ǰ�� */               
            Query query = new TermQuery(new Term("table", table.getName()));              
            
            bq.add(query, BooleanClause.Occur.MUST);            
			
            /* ������ѯ����, �������Lucene��Parse�﷨ */
            QueryParser parser = null;
            String[] term;
            for (int i=0; i<condition.length; i++) {
            	CommonLogger.logger.debug(condition[i]);
            	term = condition[i].split(":");
            	if (term[0].equals("allfields") || term[2].equals(TOKENIZED)) {
            		parser = new QueryParser(term[0], IndexWriterFactory.getAnalyzer());            
        			parser.setDefaultOperator(QueryParser.OR_OPERATOR);        			
        			query = parser.parse(term[1]);
            	} else {
            		query = new TermQuery(new Term(term[0], term[1]));
            	}
            	bq.add(query, BooleanClause.Occur.MUST);
            }          
            
            /* �������м�¼���췵�ؽ����List, ��ָ�������ֶ� sortFieldName ���� */
			Hits hits = null;
			
			if (sortFieldName == null)
				hits = IndexSearcherFactory.getInstance().search(bq);
			else
				hits = IndexSearcherFactory.getInstance().search(bq, new Sort(sortFieldName));
			
			for (int i = 0; (i< WorkItemListLuceneQuery.RETURN_MAX_ROWS && i<hits.length()); i++) {
				alist.add(new LuceneRecord(hits.doc(i), hits.id(i), hits.score(i)).toRowModel());
			}
			
			CommonLogger.logger.debug("LuceneQuery find use: " + (System.currentTimeMillis() - start));
			
		} catch (IOException e) {
			throw e;
		} catch (ParseException e) {
			throw e;
		} 
		
		return alist;
	}
	
	/**
	 * ��������Ķ�������ѯ
	 * @param condition
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<RowModel> find(String[] condition) throws IOException, ParseException {
		return find(condition, null);
	}
}
