package com.hanson.jbpm.jpdl.exe.util;

import bsh.EvalError;
import bsh.Interpreter;

import com.hanson.jbpm.log.CommonLogger;
import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.suntek.util.string.StringHelper;

public class ExpressionEvaluator {
	/* Xpath �Ӵ�ǰ���ַ� */
	private final static char XPATH_PREFIX = '{'; 	
	/* Xpath �Ӵ������ַ� */
	private final static char XPATH_SUFFIX = '}'; 
	
	
	
	/**
	 * ������ֵ���ʽ��doc�����XML���ݣ�������ʽ��ֵ
	 * ���ʽ����ֵʹ��java�﷨�����԰���xpath���ʽ������{}������
	 * @param expression
	 * @return ������ֵ�����ǿ��ת��ΪString����. 
	 * @throws Exception
	 */
	public static String evaluate(String expression, ExecutionContext ctx) {
		try {			
			/* ���滻 xpath */
			CommonLogger.logger.debug("���ʽ��ֵ: " + expression);
			expression = replaceXpathWithData(expression, ctx);
			
			if ((expression == null) || (expression.equals(""))) {
				return null; 
			}
			
			Interpreter evaluator = new Interpreter();
				    
		    /* BeanShell �������Ա��ʽ��ֵ */		
			expression = expression.replaceAll("'", "\"");
			
			expression = "returnObj = (" + expression + ")";
			CommonLogger.logger.debug("���ʽ��ֵ��" + expression);
			
			evaluator.eval(expression);
			Object obj = evaluator.get("returnObj");	
			
			CommonLogger.logger.debug("���ʽ�����" + obj.toString());
			
			/* ǿ��ת��Ϊ�ַ������� */
			return obj.toString();
		} catch (Exception e) {
			CommonLogger.logger.debug(e,e);
			throw new RuntimeException("���ʽ" + expression + "��ֵ�쳣: " + e.getMessage());
		}		
	}	
	
	/**
	 * �Ӵ������ݵ�doc�ĵ��и���xpath��ѯ��Ӧ�ڵ��ֵ
	 * @param doc
	 * @param xpath
	 * @return
	 */
	public static String getValueFromData(String xpath, ExecutionContext ctx) {
		/* ���������ǰ���ַ�������ԭ�� */
		if (xpath.indexOf(XPATH_PREFIX) < 0)
			return null;
		
		xpath = xpath.substring(1, xpath.length()-1); // ȥ�� Xpath ǰ���ͽ����ַ�
		
	    return ctx.getInstanceContext().getParameter(xpath);
	}
	
	/**
	 * ��str���е�xpath���ʽ�ļ����{}��λ��ȡxpath���ʽ
	 * @param str 	������Ĵ�
	 * @return 		���ص�һ��xpath���ʽ
	 */
	public static String getXpathSubString(String str) {
		char[] arr = str.toCharArray();
		int from = 0, to = 0;
		for (int i=0; i<arr.length; i++) {			
			if (arr[i] == XPATH_PREFIX)
				from = i;
			if (arr[i] == XPATH_SUFFIX )
				to = i+1;
		}
		return str.substring(from, to);
	}
	
	/**
	 * ��doc��ȡ����ֵ�滻xpath�еĲ���
	 * @param expression ��ֵ���ʽ
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static String replaceXpathWithData(String expression, ExecutionContext ctx) 
	throws Exception {
		if ((expression == null) || (expression.equals("")))
			return null; 
		
		String xpath = "";
	    String xpathValue = "";
		/* ������ʽ�е�xpath������Document��xpath�ڵ��Text�������滻 */
	    do {
	    	xpath = getXpathSubString(expression);
	    	if(xpath.equals(""))  //������xpath���ʽ������
	    		break;
		    xpathValue = getValueFromData(xpath, ctx);		    
		    if (xpathValue == null) {
		    	xpathValue = "";
		    }
		    /* xpath���ʽ��ֵ�滻*/
		    expression = StringHelper.replace(expression, xpath, xpathValue);
	    } while(true);		    
	    return expression;
	}	
	
	
	public static void main(String[] args) {
		Interpreter evaluator = new Interpreter();
	    
	    /* BeanShell �������Ա��ʽ��ֵ */		
		String expression = "\"�˻��ϼ�\".equals(\"�˻����߻���\")";
		
		try {
			evaluator.eval("returnObj = (" + expression + ")");
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object obj = null;
		try {
			obj = evaluator.get("returnObj");
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		System.out.println("���ʽ�����" + obj.toString());
	}
}
