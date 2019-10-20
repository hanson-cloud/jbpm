package com.hanson.jbpm.jpdl.def.form;

import com.hanson.jbpm.jpdl.def.node.TaskNode;
import com.hanson.jbpm.log.CommonLogger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.suntek.eap.core.config.SystemConfig;
import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.base.Task;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.node.End;
import com.hanson.jbpm.mgmt.ProcessClient;
import com.hanson.jbpm.web.XMLFileUtil;

public class TaskFormDefinition {
	private ProcessDefinition pdf;
	
	public TaskFormDefinition(ProcessDefinition pdf) {
		this.pdf = pdf;
	}
	
	/**
	 * �����������ڽڵ���ұ�����
	 * @param taskName
	 * @return
	 */
	public TaskFormDef getFormURLByTaskNode(String taskNodeName) throws BpmException {		
		if (pdf.getNode(taskNodeName) instanceof End) {
			TaskFormDef taskFormDef = new TaskFormDef();
			taskFormDef.setUrl(null);
			return taskFormDef;
		}
		
		TaskNode node = (TaskNode)pdf.getNode(taskNodeName);
		if (node == null)
			throw new BpmException("����ڵ�[" + taskNodeName + "]�����������ļ��в�����");
		
		Task task = node.getTask();		
		if (task == null)
			throw new BpmException("����ڵ�[" + taskNodeName + "]û�а�������һ������");		
		
		return getFormURLByTask(task.getName());		
	}
	
	/**
	 * ����������ұ�����
	 * @param taskName
	 * @return
	 */
	public TaskFormDef getFormURLByTask(String taskName) throws BpmException {		
		String forms;
		Element el = null;
		TaskFormDef taskFormDef = new TaskFormDef();
		
		try {
			/*forms = ClassLoaderUtil.getStringByExtendResource(
					BpmConstants.BPM_DIR_PATH + pdf.getProcessName() + "/forms.xml");
			CommonLogger.logger.debug("��ȡ���̱��������Ϣ:" + BpmConstants.BPM_DIR_PATH + pdf.getProcessName() + "/forms.xml");
			Document doc = DocumentHelper.parseText(forms);*/
			String file = SystemConfig.getEAPHome() + "/eapserver/application/jbpm/" + 
								ProcessClient.MODULE + "/" + pdf.getProcessName() + "/forms.xml";
			Document doc = XMLFileUtil.read(file);
			el = (Element) doc.getRootElement().selectSingleNode("//form[@task='" + taskName + "']");
		} catch(Exception ex) {
			CommonLogger.logger.error(ex, ex);
			throw new BpmException(ex);
		} 
		if (el == null) {
			//throw new BpmException("Task node [" + taskName + "] doesn't include a formview definition!");
			taskFormDef.setUrl("/jbpm/defaultTask.html");
			taskFormDef.setWritableElement("");
		}
		else {
			taskFormDef.setUrl(el.attributeValue("form"));
			taskFormDef.setWritableElement(el.attributeValue("writableElement"));
		}
		return taskFormDef;
	}
	
	/**
	 * ��ѯ��ǰ���̵ķ����URL
	 * @return
	 */
	public TaskFormDef getStartFormURL() {
		return getFormURLByTaskNode(pdf.getStart().getName());
	}
}
