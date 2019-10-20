package com.hanson.jbpm.jpdl.def.node;


import com.hanson.jbpm.jpdl.def.base.StateException;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.jpdl.exe.ctx.ProcessBackup;
import com.hanson.jbpm.jpdl.exe.ctx.TaskPersistence;
import com.hanson.jbpm.jpdl.exe.impl.DefaultStartActionHandler;
import com.hanson.jbpm.log.CommonLogger;

public class End extends NodeBase {
	public End(String name, StateException exception) {
		super(name, exception);
	}

	public void enter(String transition, ExecutionContext ctx) throws Exception{
		CommonLogger.logger.debug("Enter node: " + this.getName() + ", persisted");
		//new EndActionHandler().execute(ctx);
		ctx.setEnterNode(this); 
		executeAction(ctx);	
		
		if (ctx.isNewInstance() && ctx.getLeavedNode().getActionHandlerExecuteResult() == null) {
			new DefaultStartActionHandler().execute(ctx);
		}
			
		/* ���г־û����ݴ��� */
		TaskPersistence tp = new TaskPersistence( null, null, null, ctx, transition, true);
		tp.execute(false);
		CommonLogger.logger.debug("Task persistence finished");
		new ProcessBackup(tp).save(ctx);
		CommonLogger.logger.debug("Process backup finished");
		
	}

	public Transition addLeavingTransition(Transition t) {
		throw new UnsupportedOperationException("can't add a leaving transition to an end-state");
	}
}
