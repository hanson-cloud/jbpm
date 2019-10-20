package com.hanson.jbpm.jpdl.def.xml.base;


import com.hanson.jbpm.jpdl.def.base.Reminder;
import org.dom4j.Element;

import com.hanson.jbpm.jpdl.BpmException;
import com.hanson.jbpm.jpdl.def.BpmConstants;
import com.hanson.jbpm.jpdl.def.base.Task;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.def.form.TaskFormDefinition;
import com.hanson.jbpm.jpdl.def.xml.DefinitionParserException;

public class TaskParser {	
		
	public Task parser(Element el, ProcessDefinition pdf) throws Exception {
		Task task = null;
		if (el == null) 
			throw new DefinitionParserException(el, new BpmException("TaskNode����û��Task����"));
		
		task = new Task();
		String taskName = el.attributeValue("name");		
		task.setName(taskName); 
		
		if (el.attributeValue("blocking") == null)
			task.setBlocking(false);
		else
			task.setBlocking(true);
		
		if (el.attributeValue("notify") == null)
			task.setNotify(false);
		else
			task.setNotify(true);
		
		if (el.attributeValue("independent") == null)
			task.setIndependent(false);
		else
			task.setIndependent(true);
		
		new AssignmentParser(task).parser(el, pdf);
		new TriggerParser(task).parser(el, pdf);
		
		String duedate = el.attributeValue("duedate");
		if (duedate == null) duedate = String.valueOf(BpmConstants.DEFAULT_DUEDATE);
		
		if (duedate != null && !duedate.equals(""))
			task.setDueDate(Double.parseDouble(duedate));

		/* ��form.xml�в��ұ��� */
		task.setForm(new TaskFormDefinition(pdf).getFormURLByTask(task.getName()).getUrl());
		
		Element e = (Element)el.element("reminder");
		if (e != null) {
			Reminder reminder = new Reminder();
			String repeat = e.attributeValue("repeat");
			if (repeat == null) repeat = "0";
			reminder.setRepeatTime(Integer.parseInt(repeat));
			duedate = e.attributeValue("duedate");
			if (duedate == null) duedate = String.valueOf(BpmConstants.DEFAULT_DUEDATE);
			//reminder.setDuedate(Double.parseDouble(duedate));
			reminder.setMsg(e.attributeValue("msg"));
			task.setReminder(reminder);
		}
		return task;
	}
}
