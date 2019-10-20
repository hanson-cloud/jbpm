package com.hanson.jbpm.web.service.template;

import java.io.Serializable;
import java.util.Comparator;

public class TimeComparator implements Comparator, Serializable {
	private static final long serialVersionUID = -6180449060211140828L;
	
	private boolean isDesc = false; //�Ƿ���
	
	public TimeComparator() {
		
	}
	
	public TimeComparator(boolean isDesc) {
		this.isDesc = isDesc;
	}

	public int compare(Object o1, Object o2) {
		String t1 = ((TaskInfo)o1).DEAL_TIME;
		String t2 = ((TaskInfo)o2).DEAL_TIME;
		if(isDesc) {
			return t2.compareTo(t1);
		}else {
			return t1.compareTo(t2);
		}
	}

}
