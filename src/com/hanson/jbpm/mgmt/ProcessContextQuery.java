package com.hanson.jbpm.mgmt;

public class ProcessContextQuery {	
	public final static int NEW_INSTANCE_SAVING = 0;
	public final static int START_TASK = 1;
	public final static int TASK = 2;
	public final static int SAMETASK_OR_SAVING = 3;
	
	/*public static ContextQuery getInstance(int type) {
		switch (type) {
		case NEW_INSTANCE_SAVING:
			return new NewInstanceSavingContextQuery();
		case START_TASK:
			return new StartTaskContextQuery();
		case SAMETASK_OR_SAVING:
			return new SameTaskOrSavingContextQuery();
		default:
			return new TaskContextQuery();
		}
	}*/			
}
