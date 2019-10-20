package com.hanson.jbpm.web.service.template;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.log.LoggerUtil;
import com.hanson.jbpm.mgmt.ProcessDetailLinkedQuery;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.exe.util.DateFormat;
import com.hanson.jbpm.mgmt.ProcessClient;

public class ProcessedInfoDetailMapSetter implements TemplateMapSetter {
	public void doGet(Map<String, Object> map) throws BpmException {
		
		List<TaskInfo> recordlist = getRecordList(map);
    	
    	map.put("recordlist", recordlist);
	}
	
	
	/**
	 * ����ȡ����������ݽӿڿ��ų��� modified by tl 2011-9-22
	 * @param map
	 * @return
	 */
	public List<TaskInfo> getRecordList(Map<String, Object> map) {
		List list = null;
		
		boolean history = ((String)map.get("history")).equals("true");
		String instId = (String)map.get("INST_ID"); 
		
		if (history)
			list = new ProcessDetailLinkedQuery().getDetailHis(instId);
		else
			list = new ProcessDetailLinkedQuery().getDetail(instId);
		
    	Map row;
    	String nextMemo = "";
    	List<TaskInfo> recordlist = new ArrayList<TaskInfo>();
    	TaskInfo task;
    	for (int i=0; i<list.size(); i++) {
    		row = (Map)list.get(i);
    		task = new TaskInfo();
    		//��������е�ASSIGN_MEMOӦ����һ������̼�¼��ȡ
    		if(i < list.size()-1) {
    			nextMemo = (String)((Map)list.get(i+1)).get("ASSIGN_MEMO");
    			nextMemo = (nextMemo==null)?"":((String)nextMemo).replaceAll("\n", "<BR>");
    		}
    		task.setINST_ID((String)row.get("INST_ID"));
    		if (i==0)
    			task.setCALL_ID(getCallIDFromInst(task.getINST_ID(), history));    			
    		task.setTASK_ID((String)row.get("TASK_ID"));
    		task.setTASK_NAME((String)row.get("TASK_NAME"));
    		task.setFLAG(TaskInfo.SUBMIT);
    		
    		task.setASSIGN_DEPT((row.get("ASSIGN_DEPT")==null)?"":(String)row.get("ASSIGN_DEPT"));
    		task.setASSIGN_UNAME((row.get("ASSIGN_UNAME")==null)?((String)row.get("ASSIGN_UID")):(String)row.get("ASSIGN_UNAME"));
    		if (row.get("ASSIGN_TIME")!=null) {
    			task.setASSIGN_TIME(new DateFormat().format((Timestamp)row.get("ASSIGN_TIME")).substring(0,19));
    		}
    		task.setASSIGN_MEMO(nextMemo);
    		
    		task.setDEAL_DEPT((row.get("DEAL_DEPT")==null)?"":(String)row.get("DEAL_DEPT"));
    		task.setDEAL_UNAME((row.get("DEAL_UNAME")==null)?((String)row.get("DEAL_UID")):(String)row.get("DEAL_UNAME"));
    		
    		if (row.get("DEAL_TIME")!=null) {
    			task.setDEAL_TIME(new DateFormat().format((Timestamp)row.get("DEAL_TIME")));// .substring(0,19));
    			recordlist.add(task);
    		}
    	} 
    	recordlist = addComments(recordlist, instId);
    	Collections.sort(recordlist, new TimeComparator(true));
    	return recordlist;
	}
	
	/**
	 * ����Զ���Ĵ���켣
	 * @param worklist
	 * @param instId
	 * @return
	 */
	private List<TaskInfo> addComments(List<TaskInfo> worklist, String instId) {
		String sql = "select TASK_ID, INST_ID, CALL_ID, TASK_NAME, DEAL_UID, DEAL_UNAME, DEAL_DEPTID," +
				 	 " DEAL_DEPT, DEAL_TIME, CONTEXT from BPM_TASK_COMMENTS" +
				 	 " where INST_ID='" + instId + "'";
		LoggerUtil.getLogger().info(sql);
		List list = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
		TaskInfo task;
		Map row;
		for (int i=0; i<list.size(); i++) {
			task = new TaskInfo();
			row = (Map)list.get(i);
			
			task.setINST_ID((String)row.get("INST_ID"));
    		task.setTASK_ID((String)row.get("TASK_ID"));
    		task.setCALL_ID((String)row.get("CALL_ID"));
    		
			task.setTASK_NAME((String)row.get("TASK_NAME"));
			task.setFLAG(TaskInfo.COMMENT);
			
			
    		task.setASSIGN_DEPT((row.get("DEAL_DEPT")==null)?"":(String)row.get("DEAL_DEPT"));
    		task.setASSIGN_UNAME((row.get("DEAL_UNAME")==null)?((String)row.get("DEAL_UID")):(String)row.get("DEAL_UNAME"));
    		System.out.println(new DateFormat().format((Timestamp)row.get("DEAL_TIME")));
    		task.setASSIGN_TIME(new DateFormat().format((Timestamp)row.get("DEAL_TIME")).substring(0, 19));
    		task.setDEAL_TIME(new DateFormat().format((Timestamp)row.get("DEAL_TIME")).substring(0, 19));
    		task.setASSIGN_MEMO((row.get("CONTEXT")==null)?"":((String)row.get("CONTEXT")).replaceAll("\n", "<br>"));
    		
    		task.setDEAL_DEPT((row.get("DEAL_DEPT")==null)?"":(String)row.get("DEAL_DEPT"));
    		task.setDEAL_UNAME((row.get("DEAL_UNAME")==null)?((String)row.get("DEAL_UID")):(String)row.get("DEAL_UNAME"));
    		  
    		worklist.add(task);
		}
		return worklist;
	}
	
	private String getCallIDFromInst(String instId, boolean history) {
		String sql = "select CALL_ID from BPM_INST where INST_ID='" + instId + "'";
		if (history)
			sql = "select CALL_ID from BPM_INST_HIS where INST_ID='" + instId + "'";
		CommonLogger.logger.debug(sql);
		return (String) DaoFactory.getJdbc(ProcessClient.MODULE).queryForMap(sql).get("CALL_ID");
	}
}
