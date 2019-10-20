package com.hanson.jbpm.test;

import java.util.List;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.impl.NullActionHandler;

public class TestActionHandler extends NullActionHandler {
	public String execute(ExecutionContext ctx) throws Exception {
		CommonLogger.logger.debug(ctx.getInstanceContext().getParameter("SettingBureau", Context.types.FORM));
		
		String title = ctx.getInstanceContext().getParameter("SheetTitle", Context.types.FORM);
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.TASKTITLE, title);
		
		String memo = ctx.getInstanceContext().getParameter("defaultTaskMemo", Context.types.FORM);
		if (memo.equals(""))
			memo = ctx.getLeavedNode().getName() + " OK";
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.TASKTITLE, memo);		
		
		return "true";
	}
	
	public static void main(String[] args) {
		//ProcessEngine.getProcessInstanceMgmt("test").reopenInstance(instId, bizId, userCode);
		
		System.setProperty("EAP_HOME", "f:/openeap315");
		System.setProperty("EAP_DEBUG", "true");
		System.setProperty("ECLIPSE_HOME", "f:/eclipse");

		String sql = "select * from petra.CCS_AGENT";
		List list = DaoFactory.getJdbc("opencc").queryForList(sql);
		System.out.println(list.size());
	}
}
