package com.hanson.jbpm.lucene;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;

public class IndexSearcherFactory {	
	private static IndexSearcher indexSearcher;
	
	public synchronized static IndexSearcher getInstance() throws CorruptIndexException, IOException {
		if (IndexVersion.hasNewVersion()) {
			IndexReaderFactory.close();
			close();
		}
		
		if (indexSearcher == null)
			indexSearcher = new IndexSearcher(IndexReaderFactory.getInstance());
		return indexSearcher;
	}
	
	public synchronized static void close() throws IOException {
		if (indexSearcher != null) indexSearcher.close();
		indexSearcher = null;
	}
}
