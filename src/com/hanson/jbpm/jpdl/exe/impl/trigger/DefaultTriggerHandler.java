package com.hanson.jbpm.jpdl.exe.impl.trigger;

import com.hanson.jbpm.jpdl.exe.async.TriggerTask;
import com.hanson.jbpm.jpdl.exe.async.TriggerTaskCache;
import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.util.ExpressionEvaluator;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.hanson.jbpm.log.TimerLogger;

public class DefaultTriggerHandler extends TriggerHandler implements ITriggerHandler
{
	public DefaultTriggerHandler(EventType event, String condition, String expression, 
			TriggerAction action, TriggerTarget target)
	{
		super(event, condition, expression, action, target);
	}
	
	public void exec(ExecutionContext ctx) throws Exception
	{
		TimerLogger.logger.debug("ִ�д�����: " + this.event + ", " + expression + ", " +  action + ", " +  target);
				
		if (condition != null && !condition.equals("")) {
			String result = ExpressionEvaluator.evaluate(condition, ctx);
			if (!"true".equals(result))
				return;
		}
		
		String targetFK = getTargetFK(ctx);
		
		if (targetFK.equals("")) {
			TimerLogger.logger.debug("δ���ҵ���Ӧ�� TargetFK���޷�������ȷ�� TriggerTask");
			return;
		}
		
		expression = ExpressionEvaluator.replaceXpathWithData(expression, ctx);
		
		TriggerTask task = new TriggerTask(expression, action, target, targetFK);
		
		TriggerTaskCache.getInstance().addTask(task);
	}
	
	private String getTargetFK(ExecutionContext ctx)
	{
		if (target == TriggerTarget.cust || target == TriggerTarget.custMgr) {			
			return ctx.getInstanceContext().getParameter("//" + Context.values.CUSTID);
		}
		
		if (target == TriggerTarget.dealer || target == TriggerTarget.deptMgr) {
			if (ctx.getRequest() != null) {
				return ctx.getRequest().getRemoteUser();
			} else {
				return ctx.getInstanceContext().getParameter(Context.values.ULOGNAME, Context.types.USER);
			}
		}
		
		return "";
	}
}
