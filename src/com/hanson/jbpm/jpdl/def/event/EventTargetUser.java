package com.hanson.jbpm.jpdl.def.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanson.jbpm.identity.SysUser;
import com.hanson.jbpm.identity.User;

/**
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�EventTargetUser.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Nov 19, 2013
 */
public class EventTargetUser {
	
	private User user;
	private int earyWarnCount = 0;    //Ԥ��������
	private int warnCount = 0;        //�澯������
	private int seriousWarnCount = 0; //���ظ澯������
	
	
	/**
	 * new ���ʵ��
	 * @param userIds
	 * @param earlyWarnCounts
	 * @param warnCounts
	 * @param seriousWarnCounts
	 * @return
	 */
	public static EventTargetUser[] newMutilInstance(String[] userIds, int[] earlyWarnCounts,
			int[] warnCounts, int[] seriousWarnCounts) {
		
		Map<String, Integer> userIndexMap = new HashMap<String, Integer>();
		List<String> userIdList = new ArrayList<String>();
		for(int i=0;i<userIds.length;i++) {//�����ź�����������һ��ӳ��
			userIdList.add(userIds[i]);
			userIndexMap.put(userIds[i], i);
		}
		
		//Collections.addAll(userIdList, userIds);
		User[] users = SysUser.newMultiInstance(userIdList);
		EventTargetUser[] tus = new EventTargetUser[users.length];
		for(int i=0;i<users.length;i++) {
			User user = users[i];
			tus[i] = new EventTargetUser();
			tus[i].setUser(user);
			int index = userIndexMap.get(user.getLoginName());
			tus[i].setEaryWarnCount(getValueFromIntArray(earlyWarnCounts, index));
			tus[i].setWarnCount(getValueFromIntArray(warnCounts, index));
			tus[i].setSeriousWarnCount(getValueFromIntArray(seriousWarnCounts, index));
		}
		return tus;
	}
	
	
	private static int getValueFromIntArray(int[] values, int i) {
		if(i >= values.length) {
			return 0;
		}else {
			return values[i];
		}
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getEaryWarnCount() {
		return earyWarnCount;
	}
	public void setEaryWarnCount(int earyWarnCount) {
		this.earyWarnCount = earyWarnCount;
	}
	public int getWarnCount() {
		return warnCount;
	}
	public void setWarnCount(int warnCount) {
		this.warnCount = warnCount;
	}
	public int getSeriousWarnCount() {
		return seriousWarnCount;
	}
	public void setSeriousWarnCount(int seriousWarnCount) {
		this.seriousWarnCount = seriousWarnCount;
	}
	
	
	

}
