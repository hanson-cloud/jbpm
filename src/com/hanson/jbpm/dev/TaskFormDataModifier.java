package com.hanson.jbpm.dev;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.exe.ctx.InstanceLuceneTable;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.hanson.jbpm.mgmt.ctx.CurrentTaskContextQuery;
import com.hanson.jbpm.mgmt.ctx.HistoryTaskContextQuery;

/**
 * ����������޸��� 
 * 
 * @author zhout
 *
 */
public class TaskFormDataModifier {
	private String instanceId;
	private boolean isHistory;
	private String processName;
	private String taskTableName = "BPM_TASK";
	private String instTableName = "BPM_INST";
	
	/**
	 * ָ�����޸ı�������������������ʵ��ID
	 * @param processName
	 * @param instanceId
	 */
	public TaskFormDataModifier(String processName, String instanceId, boolean isHistory) {
		this.instanceId = instanceId;		
		this.processName = processName;
		this.isHistory = isHistory;
		if (isHistory) {
			taskTableName = "BPM_TASK_HIS";
			instTableName = "BPM_INST_HIS";
		}
	}	
	
	/**
	 * Ĭ���޸ķ������̱����ݣ����� bizId ���� ��ȫ�ļ����ֶθ�������
	 * @param map
	 * @param isHistory
	 * @throws BpmException
	 */
	protected void modify(Map<String, Object> map) throws BpmException {
		try {						
			String taskId = (String)map.get("TASK_ID");	
			
			String context = "";
			if (isHistory)
				context = new HistoryTaskContextQuery(taskId).getContextByTaskId();
			else
				context = new CurrentTaskContextQuery(taskId).getContext();
			
			context = replaceFormDataByParametersMap(context, map);	
			
			String title = (String) map.get(BpmConstants.FULLTEXT_FIELDNAME);
			
			formDataPersistence(taskId, context, this.instanceId, title);
			
			if (ProcessEngine.luceneSupportInvalid()) {								
				new InstanceLuceneTable(InstanceLuceneTable.instanceTableName).update(this.instanceId, 
						processName, title, "");
			}
			
		} catch (DocumentException e) {
			CommonLogger.logger.error(e, e);
			throw new BpmException("�����ı�����ת��XML�ĵ��쳣:" + e.getMessage());
		} catch (Exception e) {
			CommonLogger.logger.error(e, e);
			throw new BpmException("�޸ı�����ʷ�������쳣:"+e.getMessage());
		}
		
	}
	
	/**
	 * �޸ı��־û�����
	 * 		taskId/context �޸� Task ����
	 * @param taskId
	 * @param context
	 * 		instId/title �޸� inst ����
	 * @param instId
	 * @param title
	 */
	private void formDataPersistence(String taskId, String context, String instId, String title) {		
		String[] sql = new String[2];
		sql[0] = "update " + taskTableName + " set CONTEXT='" + context + "'" +
					 " where INST_ID='" + instanceId + "' and TASK_ID='" + taskId + "'";
		sql[1] = "update " + instTableName + " set INST_TITLE='" + title + "'" +
					 " where INST_ID='" + instanceId + "'";
		CommonLogger.logger.debug(sql[0]);
		CommonLogger.logger.debug(sql[1]);
		DaoFactory.getTransactionTemplate(ProcessClient.MODULE).batchUpdate(sql);
	}
		
	/**
	 * �Ѵ��޸ĵ������滻����Context�������� 
	 * @param context
	 * @param map
	 * @return
	 * @throws DocumentException
	 */
	private String replaceFormDataByParametersMap(String context, Map<String, Object> map) 
	throws DocumentException {
		Element root = DocumentHelper.parseText(context).getRootElement();
		Iterator it = map.keySet().iterator();
		String elName, elValue;
		Element node;
		Object obj;
		while(it.hasNext()) {
			elName = (String)it.next();
			if (elName.equals("")) continue;
			obj = map.get(elName);
			if (obj instanceof ArrayList)
				obj = listToString((List)obj);			
			elValue = (String)obj;
			node = (Element)root.selectSingleNode("//task/" + elName);
			if (node != null) {
				//throw new DocumentException("��ȱ��Ԫ��: " + elName);
				if (elValue.indexOf("\r")>=0) {
					node.clearContent();
					node.addCDATA(elValue);
				}
				else
					node.setText(elValue);
			}
		}
		return root.asXML();
	}
	
	/**
	 * List ת String�� ���ŷָ�
	 * @param obj
	 * @return
	 */
	private String listToString(List obj) {
		StringBuffer ret = new StringBuffer("");
		for (int i=0; i<obj.size(); i++) {
			ret.append(obj.get(i)).append(",");
		}
		if (ret.length()>1) 
			ret = ret.deleteCharAt(ret.length() -1);
		return ret.toString();
	}
}
