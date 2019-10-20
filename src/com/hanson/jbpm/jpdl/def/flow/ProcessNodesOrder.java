package com.hanson.jbpm.jpdl.def.flow;

import java.util.HashMap;
import java.util.Map;

public class ProcessNodesOrder {
	
	private final static Map<String, Map<String, String>> nodesOrder = new HashMap<String, Map<String, String>>();
		
	public static String getIndex(String procName, String taskName) {
		return nodesOrder.get(procName).get(taskName);
	}
	
	public static synchronized void putTask(String procName, String taskName, String order) {
		if (!nodesOrder.containsKey(procName))
			addProcess(procName);
		nodesOrder.get(procName).put(taskName, order);
	}
	
	public static synchronized void addProcess(String procName) {
		if (!nodesOrder.containsKey(procName))
			nodesOrder.put(procName, new HashMap<String, String>());
	}
	
	public static boolean containProcess(String procName) {
		return nodesOrder.containsKey(procName);
	}
	
	public static String trimIndex(String procName, String name) {
		if (name.indexOf(".")>0) {
			ProcessNodesOrder.putTask(procName, name.split("\\.")[1], name.split("\\.")[0]);
			name = name.split("\\.")[1];
			return name;
		}  else 
			return name;
	}
	
	public static String prefixIndex(String procName, String taskName) {
		String order = ProcessNodesOrder.getIndex(procName, taskName);
		if (order != null)
			return order + "." + taskName;
		else
			return taskName;
	}
}
