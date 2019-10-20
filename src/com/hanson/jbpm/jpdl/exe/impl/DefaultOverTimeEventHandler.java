package com.hanson.jbpm.jpdl.exe.impl;

import com.hanson.jbpm.jpdl.def.event.EventInfo;
import com.hanson.jbpm.jpdl.def.event.EventTargetUser;
import com.hanson.jbpm.log.LoggerUtil;
import com.suntek.opencc.OpenCC;
import com.suntek.opencc.um.IPortalMessageGateway;

/**
 * Ĭ�ϳ�ʱ��������ʵ��:������ϯ�����Ϣ
 * Copyright (C)2013 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm2
 * <p>�ļ����ƣ�DefaultOverTimeEventHandler.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�tl
 * <p>������ڣ�Nov 19, 2013
 */
public class DefaultOverTimeEventHandler implements EventHandler{


	public void handle(EventInfo event) {
		if(EventInfo.TYPE_OVERTIME.equals(event.getEventType())) {
			try {
				IPortalMessageGateway mg = (IPortalMessageGateway) OpenCC.getComponentContainer()
						.getComponent(IPortalMessageGateway.class);
				if(mg != null) {
					EventTargetUser[] users = event.getTargetUsers();
					for(EventTargetUser user : users) {
						String userId = user.getUser().getLoginName();
						String msg = "����";
						if(user.getEaryWarnCount() > 0) {
							msg += user.getEaryWarnCount() + "�Ź�����Ԥ��,";
						}
						if(user.getWarnCount() > 0) {
							msg += user.getWarnCount() + "�Ź����Ѹ澯,";
						}
						if(user.getSeriousWarnCount() > 0) {
							msg += user.getSeriousWarnCount() + "�Ź��������ظ澯,";
						}
						msg += "�뼰ʱ����!";
						mg.sendToAgent(new String[]{userId}, IPortalMessageGateway.LEVEL_MIDDLE, msg, false);
					}
				}
			} catch(Exception e) {
				LoggerUtil.getLogger().error(e, e);
			}
		}
	}

}
