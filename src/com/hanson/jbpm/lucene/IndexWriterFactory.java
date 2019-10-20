package com.hanson.jbpm.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;
import com.suntek.eap.core.config.SystemConfig;
import com.suntek.eap.util.lucene.analyze.MyAnalyzer;

public class IndexWriterFactory {
	private static IndexWriter indexWriter; 
	
	/* LuceneTable �����·�� */
	public final static  String LUCENE_INDEX_PATH = SystemConfig.getEAPHome()+"/luceneIndex/";
		
	/**
	 * ���lucene������·��
	 * @return
	 */
	public static String getIndexPath(){
		return System.getProperty("LUCENE_INDEX_PATH", LUCENE_INDEX_PATH);
	}
	
	@SuppressWarnings("deprecation")
	public static IndexWriter getInstance() throws CorruptIndexException, LockObtainFailedException, IOException {
		if (indexWriter == null) {
			if (IndexReader.indexExists(IndexWriterFactory.getIndexPath()))
				indexWriter = new IndexWriter(IndexWriterFactory.getIndexPath(), getAnalyzer(), false);
			else
				indexWriter = new IndexWriter(IndexWriterFactory.getIndexPath(), getAnalyzer(), true);						
		}
		return indexWriter;
	}
	
	public static void close() throws CorruptIndexException, IOException {
		if (indexWriter != null) indexWriter.close();
		indexWriter = null;
	}
		
	public static Analyzer getAnalyzer() {
		//return new StandardAnalyzer();
		return new MyAnalyzer();
	}
}
