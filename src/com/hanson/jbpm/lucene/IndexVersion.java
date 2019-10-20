package com.hanson.jbpm.lucene;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.suntek.opencc.OpenCC;

public class IndexVersion {

	private static long version = 0l;
	
	public static boolean hasNewVersion() {
		long newVersion = 0l;
		try {
			newVersion = (Long)OpenCC.getComponentContainer().getComponent(null, BpmConstants.INDEX_VERSION)
					.invoke(new String[]{BpmConstants.OP_GETVERSION});
			CommonLogger.logger.info("GetVersion: " + newVersion);
		} catch (InstantiationException e) {
			CommonLogger.logger.error(e);
		} catch (IllegalAccessException e) {
			CommonLogger.logger.error(e);
		}
		if (version < newVersion) {
			version = newVersion;
			return true;
		} else
			return false;
	}
	
	public static void newVersion() {
		try {
			OpenCC.getComponentContainer().getComponent(null, 
					BpmConstants.INDEX_VERSION).invoke(new String[]{BpmConstants.OP_SETVERSION});			
		} catch (InstantiationException e) {
			CommonLogger.logger.error(e);
		} catch (IllegalAccessException e) {
			CommonLogger.logger.error(e);
		}
	}
}
