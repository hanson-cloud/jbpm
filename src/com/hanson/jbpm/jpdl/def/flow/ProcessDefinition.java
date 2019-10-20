package com.hanson.jbpm.jpdl.def.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.def.EntityDef;
import com.hanson.jbpm.jpdl.def.ExtendQuery;
import com.hanson.jbpm.jpdl.def.FormField;
import com.hanson.jbpm.jpdl.def.LinkDef;
import com.hanson.jbpm.jpdl.def.form.TaskFormDef;
import com.hanson.jbpm.jpdl.def.form.TaskFormDefinition;
import com.hanson.jbpm.jpdl.def.node.NodeBase;
import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.mgmt.IPrinterProvider;
import com.hanson.jbpm.jpdl.def.BizTable;
import com.hanson.jbpm.jpdl.def.event.Event;
import com.hanson.jbpm.jpdl.def.node.Start;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * ע��: Ĭ�� Start �ڵ��ǲ����κδ����! 
 * @author zhout
 *
 */
public class ProcessDefinition {
	private String processName;
	private String moduleName = ProcessClient.MODULE;

	/* Map<�ڵ�����, �ڵ�> */
	private Map<String, NodeBase> nodesMap = new HashMap<String, NodeBase>();
	private List<NodeBase> nodesList = new ArrayList<NodeBase>();
	
	private IPrinterProvider printerProvider;
	private ExtendQuery extendQuery;
	private BizTable bizTable;
	private EntityDef entityDef;                     //ҵ���ģ���ඨ��-added by tl
	private List<LinkDef> relativeURLs;
	
	private Map<String, TaskFormDef> taskFormDefinition = new HashMap<String, TaskFormDef>();
	
	private Start start;
	private String processDuetime;
	
	private String assignStrategy = "share";
	
	private List<FormField> fields = new ArrayList<FormField>();
	
	private Event event;

	public NodeBase addNode(NodeBase node) {
		if (node == null)
			throw new IllegalArgumentException("��ӵ����̵Ľڵ㲻��Ϊ��!");

		node.setProcessDefinition(this);		
		nodesList.add(node);
		nodesMap.put(node.getName(), node);

		if (node instanceof Start) {
			this.start = (Start) node;
		}
		return node;
	}

	public ExecutionContext getExecutionContext() {
		return new ExecutionContext(this);
	}

	public void resolveTransitionDestinations() { }

	public NodeBase getNode(String nodeName) {
		//if (nodesMap.containsKey(nodeName))
		//	throw new BpmException("��ǰ���̲������ڵ��쳣��" + nodeName);
		return nodesMap.get(nodeName);
	}

	/*public TaskFormDefinition getTaskFormDefinition() {
		return new TaskFormDefinition(this);
	}*/

	/**
	 * Ĭ�� Start �����κδ��� !!!
	 * @return
	 */
	public TaskNode getStart() {
		return (TaskNode)getNode(start.getLeavingTransitions().get(0).getTo());
	}

	public void setStart(Start start) {
		this.start = start;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}		
	
	public IPrinterProvider getPrinterProvider() {
		return printerProvider;
	}
	
	public void setPrinterProvider(IPrinterProvider printerProvider) {
		this.printerProvider = printerProvider;
		
	}

	public void setExtendQuery(ExtendQuery extendQuery) {
		this.extendQuery = extendQuery;
	}

	public ExtendQuery getExtendQuery() {
		return extendQuery;
	}

	public void setBizTable(BizTable bizTable) {
		this.bizTable = bizTable;
	}

	public BizTable getBizTable() {
		return bizTable;
	}

	public void setRelativeURLs(List<LinkDef> relativeURLs) {
		this.relativeURLs = relativeURLs;
	}

	public List<LinkDef> getRelativeURLs() {
		return relativeURLs;
	}


	public void setEntityDef(EntityDef entityDef) {
		this.entityDef = entityDef;
	}

	public EntityDef getEntityDef() {
		return entityDef;
	}
	
	public TaskFormDef getFormURLByTask(String taskName) {
		if (taskFormDefinition.containsKey(taskName))
			return taskFormDefinition.get(taskName);
		else {
			TaskFormDef def = new TaskFormDefinition(this).getFormURLByTask(taskName);
			taskFormDefinition.put(taskName, def);
			return def;
		}
	}
	
	public TaskFormDef getFormURLByTaskNode(String taskNodeName) {
		if (taskFormDefinition.containsKey(taskNodeName))
			return taskFormDefinition.get(taskNodeName);
		else {
			TaskFormDef def = new TaskFormDefinition(this).getFormURLByTaskNode(taskNodeName);
			taskFormDefinition.put(taskNodeName, def);
			return def;
		}
	}

	public TaskFormDef getStartFormURL() {
		if (taskFormDefinition.containsKey("start"))
			return taskFormDefinition.get("start");
		else {
			TaskFormDef def = new TaskFormDefinition(this).getStartFormURL();
			taskFormDefinition.put("start", def);
			return def;
		}
	}
	
	public List<NodeBase> getNodeList() {
		return this.nodesList;
	}

	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}

	public List<FormField> getFields() {
		return fields;
	}

	public String getAssignStrategy()
	{
		return assignStrategy;
	}

	public void setAssignStrategy(String assignStrategy)
	{
		this.assignStrategy = assignStrategy;
	}

	public String getProcessDuetime()
	{
		return processDuetime;
	}

	public void setProcessDuetime(String processDuetime)
	{
		this.processDuetime = processDuetime;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}
}
