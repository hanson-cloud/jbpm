package com.hanson.jbpm.dev.action;


import com.hanson.jbpm.jpdl.exe.ctx.ProcessBackup;
import com.hanson.jbpm.log.CommonLogger;

public class ArchiveAction {
	public String execute(String processId) {
		try {
			new ProcessBackup(null).save(processId);
			return "�����ѹ鵵����ע���鵵��Ĺ������ڹ鵵������ѯ��";
		} catch (Exception ex) {
			CommonLogger.logger.error(ex, ex);
			return "�����鵵����ʧ��: " + ex.getMessage();
		}		
	}
}
