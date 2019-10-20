package com.hanson.jbpm.mgmt;

import java.util.List;
import java.util.Map;

public interface IPrinterProvider {
	
	
	
	public Map getData(String bizId);	
	
	public List getMultipleOrderData(String bizIds);
}
