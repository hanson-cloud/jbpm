package com.hanson.jbpm.jpdl.exe.util;

public interface IWorkItemLocker {
	public String batchLock(String taskIds, String userId);
	public String batchUnlock(String taskIds, String userId); 
}
