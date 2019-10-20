package com.hanson.jbpm.jpdl.exe.util;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.util.DateUtil;

public class DataTransformer {
	
	@SuppressWarnings("unchecked")
	public static String map2Xml(Map map) 
	{
		StringBuffer sb = new StringBuffer();
		Iterator it = map.entrySet().iterator();
		Object value;
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			value = entry.getValue();
			
			if (value instanceof String) {
				sb.append(CDataUtil.get(entry.getKey().toString(), value.toString()));
			}
			if (value instanceof Date) {
				sb.append("<").append(entry.getKey()).append(">");			
				sb.append(DateUtil.formatDate((Date)value, "yyyy-MM-dd hh:mm:ss"));
				sb.append("</").append(entry.getKey()).append(">");
			}
			if (value instanceof Timestamp || value instanceof Integer || value instanceof Float || value instanceof BigDecimal) {
				sb.append("<").append(entry.getKey()).append(">");			
				sb.append(value.toString());
				sb.append("</").append(entry.getKey()).append(">");
			}
		}
		return sb.toString();
	}
}
