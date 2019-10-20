package com.hanson.jbpm.mgmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.def.flow.ProcessNodesOrder;
import com.hanson.jbpm.jpdl.def.node.NodeBase;
import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.jpdl.util.ProcessDefinitionReader;
import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinitionCache;

public class FlowChartQuery {
	private String processName;
	
	public FlowChartQuery(String processName) {
		this.processName = processName;
	}
	
	@SuppressWarnings("unchecked")
	public List<NodePosition> getNodes(String instanceId, String taskId, boolean beArchived, boolean designMode) 
	throws DocumentException {
		List<NodePosition> nodeList = new ArrayList<NodePosition>();
		
		String tableName = "BPM_TASK";
		if (beArchived)
			tableName = "BPM_TASK_HIS";
		
		List passNodes;
		List<String> passNodeNames = new ArrayList<String>();
		String lastNodeName = "";
		
		if (!designMode) {
    		String sql = "select TASK_NAME, PASS_NODE, TASK_ID from " + tableName + "" +
    					 " where INST_ID='" + instanceId + "' order by ASSIGN_TIME";
    		CommonLogger.logger.info(sql);
    		passNodes = DaoFactory.getJdbc(ProcessClient.MODULE).queryForList(sql);
    		
    		String[] passedTasks;
    		Map row;
    		for (int i=0; i<passNodes.size(); i++) {
    			row = (Map)passNodes.get(i);
    			passNodeNames.add((String)row.get("TASK_NAME"));
    			passedTasks = (((String)row.get("PASS_NODE")==null)?"":(String)row.get("PASS_NODE")).split(",");
    			for (int j=0; j<passedTasks.length; j++) {
    				CommonLogger.logger.debug(passedTasks[j]);
    				if (!passNodeNames.contains(passedTasks[j]))			
    					passNodeNames.add(passedTasks[j]);
    			}
    			if (!passNodeNames.contains((String)row.get("TASK_NAME")))			
    				passNodeNames.add((String)row.get("TASK_NAME"));
    			//if (((String)row.get("TASK_ID")).equals(taskId) || i == passNodes.size()-1) {
    			if (((String)row.get("TASK_ID")).equals(taskId)) {
    				//if (lastNodeName.equals(""))
    					lastNodeName = (String)row.get("TASK_NAME");
    			}
    		}
		}
		
		String processFileName = processName + "/gpd.xml";
		ProcessDefinition pdf = ProcessEngine.getProcessDefinition(processName);
		String content = new ProcessDefinitionReader().getPdfContent(processFileName);
		Document doc = DocumentHelper.parseText(content);
		List nodes = doc.getRootElement().elements();
		NodePosition node;
		Element el;
		String nodeName;
		for (int i=0; i<nodes.size(); i++) {
			node = new NodePosition();
			el = (Element)nodes.get(i);
			nodeName = el.attributeValue("name");
			nodeName = ProcessNodesOrder.trimIndex(processName, nodeName);
			if (passNodeNames.contains(nodeName) || lastNodeName.equals(nodeName) || nodeName.equals("��ʼ") || designMode) {
				node.width = el.attributeValue("width");
				node.height = el.attributeValue("height");
				node.x = String.valueOf(Integer.parseInt(el.attributeValue("x")) + 10);
				node.y = String.valueOf(Integer.parseInt(el.attributeValue("y")) + 15);
				node.cls = (nodeName.equals(lastNodeName))?"todonode":"passednode";
				node.name = nodeName;		
				node.type = pdf.getNode(nodeName).getClass().getSimpleName();
				if(node.type.indexOf("TaskNode") >= 0) {//�˹��ڵ��貹������
					TaskNode taskNode = ((TaskNode)pdf.getNode(nodeName));
					node.actorId = taskNode.getTask().getActorId();
					node.assignType = taskNode.getTask().getType();
					node.blocking = taskNode.getTask().isBlocking();
					node.className = taskNode.getTask().getClassName();
					node.condition = taskNode.getTask().getCondition();
					node.expression = taskNode.getTask().getExpression();
					node.form = taskNode.getTask().getForm();
					node.formName = taskNode.getTask().getFormName();
					node.method = taskNode.getTask().getMethod();
					node.notify = taskNode.getTask().isNotify();
					if(taskNode.getTask().getReminder() != null) {
    					node.dueDate = taskNode.getTask().getReminder().getDuedate();
    					node.remindMsg = taskNode.getTask().getReminder().getMsg();
    					node.repeatTime = taskNode.getTask().getReminder().getRepeatTime();
					}
				}
				//CommonLogger.logger.debug(node);
			}
			nodeList.add(node);
		}
		return nodeList;
	}
	
	public List<String> getProcessTaskNodes() throws BpmException {
		try {
			ProcessDefinition pdf = ProcessDefinitionCache.getProcessDefinition(processName);
			List<NodeBase> nodes = pdf.getNodeList();
			
			List<String> list = new ArrayList<String>();
			
			String processFileName = processName + "/processdefinition.xml";
			/*String content = new ProcessDefinitionReader().getPdfContent(processFileName);
			Document doc = DocumentHelper.parseText(content);
			//CommonLogger.logger.debug(doc.getRootElement().asXML());
			List nodes = doc.getRootElement().elements("task-node");
			*/
			for (int i=0; i<nodes.size(); i++) {
				list.add(ProcessNodesOrder.trimIndex(processFileName, nodes.get(i).getName()));
			}
			return list;
		} catch(Exception ex) {
			CommonLogger.logger.error(ex,ex);
			throw new BpmException(ex);
		}
	}
}
