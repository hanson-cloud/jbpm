package com.hanson.jbpm.jpdl.exe.impl;

import java.util.List;

import com.hanson.jbpm.log.CommonLogger;
import com.suntek.eap.jdbc.RowModel;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;

public class CurrentNodeLastDealerAssignHandler extends AbstractAssignHandler implements AssignmentHandler {

	public CurrentNodeLastDealerAssignHandler(String condition, String ccflag)
	{
		super(condition, ccflag);
	}

	public void assign(ExecutionContext ctx) throws Exception {
		if (!this.expressionIsTrue(ctx))
			return;
		
		String currentNodeName = ctx.getEnterNode().getName();
		String instId = ctx.getInstanceContext().getParameter("//INST_ID");
		String sql = "select DEAL_UID from BPM_TASK where INST_ID='" + instId + "'" +
							" and TASK_NAME='" + currentNodeName + "'" +
							" and DEAL_TIME is not null order by DEAL_TIME desc";
		CommonLogger.logger.debug(sql);
		List<RowModel> list = ctx.getJdbc().executeQuery(sql);
			
		for (int i=0; i<list.size(); i++) {
			String userId = list.get(i).getColumnValue("DEAL_UID");			
			if (this.isCC())
				ctx.getAssignable().setCCUserId(userId);
			else
				ctx.getAssignable().setUserId(userId);
		}
	}
}
