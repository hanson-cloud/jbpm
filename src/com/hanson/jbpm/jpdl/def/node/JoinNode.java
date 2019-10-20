package com.hanson.jbpm.jpdl.def.node;

import java.math.BigDecimal;
import java.util.Map;

import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.log.CommonLogger;
import com.suntek.ccf.dao.DaoFactory;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.ctx.TaskPersistence;
import com.hanson.jbpm.mgmt.ProcessClient;

public class JoinNode  extends NodeBase {
	public JoinNode(String name, StateException exception) {
		super(name, exception);
	}
	
	/**
	 * ���ظ÷���, ����fork�����нڵ�, ��forknodeͨ��forkid��¼��־
	 * @param executionContext
	 */
	public void leave(String transition, ExecutionContext ctx) throws Exception {	
		String instanceId = ctx.getInstanceContext().getParameter(Context.values.INSTID, Context.types.MAINFORM);
		String forkId = ctx.getInstanceContext().getParameter(Context.values.FORKID, Context.types.RUNTIME);
		
		ctx.setEnterNode(this);
		
		if (checkAllForkedRouteFinished(instanceId, forkId, ctx)) {
			CommonLogger.logger.debug("Leave node: " + this.getName() + ". Fork-" + forkId + ".split[0] ���нڵ�����ɴ���");
			
			new TaskPersistence(null, null, null, ctx, transition).execute(false);
			
			//CommonLogger.logger.debug(ctx.getInstanceContext().getParameter(Context.V_TASKTITLE, Context.T_RUNTIME));
			super.leave(transition, ctx);
		} else {
			CommonLogger.logger.debug(". Fork-" + forkId + ".split[0] ���нڵ�δ��ɴ�����������");
			new TaskPersistence( null, null, null, ctx, transition).execute(true);
			//CommonLogger.logger.debug(ctx.getInstanceContext().getParameter(Context.V_TASKTITLE, Context.T_RUNTIME));
		}				
	}
	
	/**
	 * ����Ƿ���������������Ľڵ�
	 * @param instanceId	ʵ��id
	 * @param forkId		������id
	 * @return
	 */
	private boolean checkAllForkedRouteFinished(String instanceId, String forkId, ExecutionContext ctx) {	
		String currentForkId = forkId;
		String[] arr = forkId.split(",");
		if (arr.length>0) currentForkId = arr[0];
		
		String sql = "select count(1) FORK from BPM_TASK where INST_ID='" + instanceId + "'" +
					 " and FORK_ID like '%" + currentForkId + "%' and DEAL_TIME is null";
		CommonLogger.logger.info(sql);
		Map row = DaoFactory.getJdbc(ProcessClient.MODULE).queryForMap(sql);
		if (((BigDecimal)row.get("FORK")).doubleValue() == 1) {
			forkId = currentForkId.replaceFirst(currentForkId + ",", "");
			ctx.getInstanceContext().appendRuntimeVariable(Context.values.FORKID, forkId);
			return true;
		}
		else
			return false;
	}
}
