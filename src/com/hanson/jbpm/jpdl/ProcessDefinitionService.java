package com.hanson.jbpm.jpdl;


import com.hanson.jbpm.jpdl.def.flow.ProcessDefinitionCache;
import com.suntek.opencc.pico.ILocalComponent;

public class ProcessDefinitionService implements ILocalComponent
{

	public Object invoke(Object[] args)
	{
		ProcessDefinitionCache.clear();
		return "��������̻���!";
	}
	 
}
