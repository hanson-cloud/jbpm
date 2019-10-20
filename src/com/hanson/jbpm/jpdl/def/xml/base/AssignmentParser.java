package com.hanson.jbpm.jpdl.def.xml.base;

import java.lang.reflect.Constructor;
import java.util.List;

import org.dom4j.Element;

import com.hanson.jbpm.jpdl.def.base.Task;
import com.hanson.jbpm.jpdl.def.flow.ProcessDefinition;
import com.hanson.jbpm.jpdl.exe.impl.AssignmentHandler;
import com.hanson.jbpm.jpdl.exe.impl.DefaultAssignHandler;
import com.hanson.jbpm.jpdl.exe.impl.DeptAssignHandler;
import com.hanson.jbpm.jpdl.exe.impl.ExpressionAssignHandler;
import com.hanson.jbpm.jpdl.exe.impl.RoleAssignHandler;
import com.hanson.jbpm.jpdl.exe.impl.UserAssignHandler;
import com.hanson.jbpm.jpdl.exe.impl.WorkGroupAssignHandler;

public class AssignmentParser
{
	private Task task;
	
	public AssignmentParser(Task task)
	{
		this.task = task;
	}
	
	@SuppressWarnings("unchecked")
	public void parser(Element el, ProcessDefinition pdf) throws Exception {
		List<Element> assignments = el.elements("assignment");
		for (Element e: assignments) {
			AssignmentHandler assignment = null;
			if (e != null) {
				/* Assignment 3�����ͣ�Class|expression|actor-id */
				String expression = e.attributeValue("expression");				
				String method = e.attributeValue("method"); //�ɵ���ʽ: mail,sms
				String type = e.attributeValue("type");
				if (expression != null) {
					task.setExpression(expression);
					assignment = new ExpressionAssignHandler(expression, type);					
				} else {				
					String attr = e.attributeValue("class");
					if (attr != null && !attr.equals("")) {
						task.setClassName(attr);
						Class clazz = Class.forName(attr);	
						String condition = e.attributeValue("condition");
						String ccflag = e.attributeValue("ccflag");
						if (ccflag != null) {
							Class[] parameterTypes = new Class[2];
							for (int i=0; i<2; i++) { parameterTypes[i] = String.class; }
							Object[] initargs = new Object[]{ condition, ccflag };
							Constructor constructor = clazz.getConstructor(parameterTypes);
							assignment = (AssignmentHandler)constructor.newInstance(initargs);
						} else {
							assignment = (AssignmentHandler) clazz.newInstance();
						}
					} else {
						attr = e.attributeValue("actor-id");
						String condition = e.attributeValue("condition");
						String ccflag = e.attributeValue("ccflag");						
						task.setType(type);
						task.setActorId(attr);
						if (type == null || type.equalsIgnoreCase("user"))
							assignment = new UserAssignHandler(condition, ccflag, attr);
						if ("dept".equalsIgnoreCase(type))
							assignment = new DeptAssignHandler(condition, ccflag, attr);
						if ("role".equalsIgnoreCase(type))
							assignment = new RoleAssignHandler(condition, ccflag, attr);
						if ("workgroup".equalsIgnoreCase(type))
							assignment = new WorkGroupAssignHandler(condition, ccflag, attr);
					}
				}
				task.addAssignment(assignment);
				task.setMethod(method);
			} else {
				task.addAssignment(new DefaultAssignHandler());
			}
		}	
	}
}
