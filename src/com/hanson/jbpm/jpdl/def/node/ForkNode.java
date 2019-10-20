package com.hanson.jbpm.jpdl.def.node;

import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.util.IdentityCreator;
import com.hanson.jbpm.log.CommonLogger;

public class ForkNode extends NodeBase {
	public ForkNode(String name, StateException exception) {
		super(name, exception);
	}
	
	/**
	 * ���ظ÷���, ����fork�����нڵ�, ��forknodeͨ��forkid��¼��־
	 * @param executionContext 
	 */
	public void leave(String transition, ExecutionContext ctx) throws Exception {
		CommonLogger.logger.debug("Leave node: " + this.getName());
		
		//String newForkId = String.valueOf(Calendar.getInstance().getTimeInMillis());
		String newForkId = ctx.getInstanceContext().getParameter(Context.values.FORKID, Context.types.RUNTIME);
		newForkId = IdentityCreator.getRandom(100) + "," + newForkId;
		
		CommonLogger.logger.debug(" ForkActionHandler create ForkId = " + newForkId);
		
		ctx.getInstanceContext().appendRuntimeVariable(Context.values.FORKID, newForkId);
		ctx.setForked(true);
		
		for (int i = 0; i < leavingTransitions.size(); i++) {
			if (i>0)
				ctx.setForkLoopPassSecond(true);
			if (leavingTransitions.get(i).conditionIsMeeted(ctx))
				processDefinition.getNode(leavingTransitions.get(i).getTo()).enter(leavingTransitions.get(i).getName(), ctx);			
		}
	}
}
