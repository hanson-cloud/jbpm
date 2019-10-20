package com.hanson.jbpm.jpdl.exe.ctx.dao;

import java.sql.SQLException;
import java.util.List;

import com.hanson.jbpm.jpdl.exe.ctx.ExecutionContext;
import com.suntek.eap.core.app.AppHandle;

/**
 * BPM ���ݳ־û��� SQL ִ���࣬�ṩ Hibernate �� Jdbc ����ʵ�ַ�ʽ��ͨ���������֡�<br>
 * 
 * <p>Copyright (C)2011 , ������̫�Ƽ��ɷ����޹�˾
 * <p>All rights reserved.
 * <p>��Ŀ���ƣ�com.suntek.jbpm
 * <p>�ļ����ƣ�BpmDaoSQLExecutor.java
 * <p>ժ����Ҫ��
 * <p>��ǰ�汾��1.0
 * <p>�������ߣ�zhout
 * <p>������ڣ�2011-1-25
 */
public class BpmDaoSQLExecutor 
{	
	/* Hibernate ��  Jdbc ʵ�ֵ����ÿ��� */
	public final static String PersistenceMode = AppHandle.getHandle("jbpm").getProperty("PersistenceMode");
	
	/**
	 * ִ�� SQL ���
	 * @param ctx	�����ģ����Ի�ȡ�� Hibernate Session �� Jdbc QueryHelper ʵ��
	 * @param sql	Ҫִ�е� SQL
	 * @throws SQLException
	 */
	public static void executeBatch(ExecutionContext ctx, String sql) throws SQLException {
		
		//CommonLogger.logger.debug(sql);
		
		/*if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc"))
			ctx.getJdbc().execute(sql);
		
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate"))
			ctx.getSession().createSQLQuery(sql).executeUpdate();*/
		
		ctx.addSQL(sql);
	}	
	
	public static void executeBatch(ExecutionContext ctx, List<String> sqlList) throws SQLException {
		for (int i=0; i<sqlList.size(); i++)
			ctx.addSQL(sqlList.get(i));
	}
	
	
	/**
	 * �󶨱�����ʽ�ύSQL
	 * @param ctx
	 * @param sql
	 * @throws SQLException
	 */
	public static void executeBatch(ExecutionContext ctx, String sql, Object... params) throws SQLException {
		
		//CommonLogger.logger.debug(sql);
		
		/*if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc"))
			ctx.getJdbc().execute(sql);
		
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate"))
			ctx.getSession().createSQLQuery(sql).executeUpdate();*/
		
		ctx.addSQL(sql, params);
	}	
	
	/**
	 * �󶨱�����ʽ�ύ����SQL
	 * @param ctx
	 * @param sqlList
	 * @throws SQLException
	 */
	public static void executeBatch(ExecutionContext ctx, List<String> sqlList, List<Object[]> paramList) throws SQLException {
		for (int i=0; i<sqlList.size(); i++)
			ctx.addSQL(sqlList.get(i), paramList.get(i));
	}
}
