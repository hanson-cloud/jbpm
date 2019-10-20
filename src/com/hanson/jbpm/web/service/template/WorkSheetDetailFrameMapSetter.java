package com.hanson.jbpm.web.service.template;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.mgmt.ProcessDetailQuery;
import com.hanson.jbpm.jpdl.def.flow.ProcessNodesOrder;
import com.hanson.jbpm.mgmt.UserTaskStatusDao;
import com.hanson.jbpm.web.service.PageTemplateService;

public class WorkSheetDetailFrameMapSetter implements TemplateMapSetter {
	
	private List taskList = null;
	private String currentTaskId = "";
	
	public void doGet(Map map) {		
		
		String taskId = (String)map.get("TASK_ID");		
		String instId = (String)map.get("INST_ID");
		String processName = (String)map.get("processName");
		
		currentTaskId = taskId;
		
		
		/* �򿪹����������ʱ, �޸�������Ѷ�״̬ */
		if  (taskId == null) {
			new UserTaskStatusDao().setReadFlagByInstId(instId, (String)map.get(PageTemplateService.USER));
		}
		else {
			new UserTaskStatusDao().setReadFlag(taskId, (String)map.get(PageTemplateService.USER));			
		}		
				
		if ("true".equals(map.get("history"))) 
			taskList = new ProcessDetailQuery().getDetailHis(instId, "");
		else
			taskList = new ProcessDetailQuery().getDetail(instId, "");
		
		process(map, instId);
	}
	
	private void process(Map map, String instId) {
		//if (ProcessClient.TEMPLATE.equals("notes")) {
		
		List<TaskInfo> tasks = new ArrayList<TaskInfo>();
		Map row;
		TaskInfo task, currentTask = new TaskInfo();
		List<String> names = new ArrayList<String>();
		Timestamp t;
		for (int i=0; i<taskList.size(); i++) {				 
			row = (Map)taskList.get(i);
			task = new TaskInfo();								
			task.TASK_ID = (String)row.get("TASK_ID");
			task.BIZ_ID = (String)row.get("BIZ_ID");
			task.INST_ID = (String)row.get("INST_ID");
			task.TASK_NAME = (String)row.get("TASK_NAME");
							
			task.PROC_NAME = (String)row.get("PROC_NAME");
			t = (Timestamp)row.get("DEAL_TIME");
			if (t != null) {
				task.DEAL_TIME = t.toString();
			} else {
				task.DEAL_TIME = "";
				
				if (task.TASK_ID.equals(currentTaskId))
					currentTask = task;
				if (currentTask.TASK_ID == null)
					currentTask = task;
			}
			
			if ("true".equals(map.get("history")) && i==0)
				currentTask = task;
			
			if (!names.contains(task.TASK_NAME)) {
				tasks.add(task);
				names.add(task.TASK_NAME);
			} 
		}
		Collections.sort(tasks, new TaskComparator());
		//Collections.reverse(tasks);
		map.put("taskList", tasks);		
		map.put("currentTask", currentTask);
		map.put("linkPages", ProcessEngine.getProcessDefinition(currentTask.PROC_NAME).getRelativeURLs());
		
		//} else {
		//	String history = (String)map.get("history");
		//	String sql = "select min(TASK_ID) TASK_ID from BPM_TASK where INST_ID='" + instid + "'";
		//	if ("true".equals(history))
		//		sql = "select min(TASK_ID) TASK_ID from BPM_TASK_HIS where INST_ID='" + instid + "'";
		//	String startTaskId = (String)DaoFactory.getJdbc(ProcessClient.MODULE).queryForMap(sql).get("TASK_ID");
		//	map.put("startTaskId", startTaskId);
		//	map.put("currentTaskId", taskId);
		//}
	}
	
	static class TaskComparator implements Comparator, Serializable {
		public int compare(Object o1, Object o2) {			
			TaskInfo task1 = (TaskInfo)o1;
			TaskInfo task2 = (TaskInfo)o2;
			String procName = task1.getPROC_NAME();
			if (ProcessNodesOrder.containProcess(procName)) {
				String order1 = ProcessNodesOrder.getIndex(procName, task1.getTASK_NAME());
				String order2 = ProcessNodesOrder.getIndex(procName, task2.getTASK_NAME());
				if (order1 == null || order2 == null)
					return ((TaskInfo)o1).TASK_ID.compareTo(((TaskInfo)o2).TASK_ID);
				else
					return order1.compareTo(order2);
			} else
				return ((TaskInfo)o1).TASK_ID.compareTo(((TaskInfo)o2).TASK_ID);
		}		
	}
}
