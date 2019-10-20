package com.hanson.jbpm.lucene;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

public class IndexReaderFactory {
	
	private static IndexReader indexReader;		
		
	public synchronized static IndexReader getInstance() throws CorruptIndexException, IOException {		
		if (indexReader == null) {
			indexReader = IndexReader.open(IndexWriterFactory.getIndexPath());
		}
		return indexReader;
	}
	
	public static void close() throws IOException {
		if (indexReader != null) indexReader.close();
		indexReader = null;
	}
}
