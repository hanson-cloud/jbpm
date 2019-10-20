package com.hanson.jbpm.web.service.template;

import java.util.List;
import java.util.Map;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.mgmt.FlowChartQuery;
import org.dom4j.DocumentException;

import com.hanson.jbpm.mgmt.NodePosition;

public class FlowChartMapSetter implements TemplateMapSetter {
	public void doGet(Map<String, Object> map) throws BpmException {
		String processName = (String)map.get("processName");
		String instanceId = (String)map.get("INST_ID");
		String taskId = (String)map.get("TASK_ID");
		try {
			boolean history = ((String)map.get("history")).equals("true");
			List<NodePosition> list;
			if (map.get("design") != null)
				list = new FlowChartQuery(processName).getNodes(instanceId, taskId, history, true);
			else
				list = new FlowChartQuery(processName).getNodes(instanceId, taskId, history, false);
			map.put("passedNodes", list);
		} catch (DocumentException ex) {
			CommonLogger.logger.error(ex, ex);
			throw new BpmException(ex);
		}
	}
}
