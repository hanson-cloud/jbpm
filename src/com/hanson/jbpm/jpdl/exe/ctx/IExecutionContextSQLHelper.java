package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.suntek.ccf.orm.hibernate.DynamicSessionFactory;
import com.suntek.eap.jdbc.QueryHelper;
import com.suntek.eap.util.jdbc.Dialect;

/**
 * <b>ExecutionContext �� SQLHelper �������ӿ�</b><br>
 * ��װ�����ύʱ�־û���SQL�����ύ����֧�� jdbc��Hibernate ���ַ�ʽ��
 * @author zhout
 * @since 4.0
 * @version 2011-8-19
 * @Copyright (C)2011 , Suntektech
 */
public interface IExecutionContextSQLHelper {
	
	public QueryHelper getJdbc() throws SQLException;
	
	public void setJdbc(QueryHelper jdbc);
	
	public Dialect getDialect() throws SQLException;
	
	public Session getSession() throws HibernateException;
	
	public DynamicSessionFactory getSessionFactory() throws HibernateException;
	
	public void addSQL(String sql);
	
	/**
	 * �󶨲�����ʽ��ֵSQL
	 * @param sql
	 * @param params
	 */
	public void addSQL(String sql, Object... params);
	
	//public void addBackupSQL(String sql);
	
	public void setTimeout(int seconds) throws SQLException;
	
	public void beginTransaction() throws SQLException;
	
	public void commitTransaction() throws SQLException;
	
	public void rollbackTransaction() throws SQLException;
	
	public void executeBatchSQL() throws Exception;
	
	public void closeHibernateSession();
	
	public void executeSQL(String sql) throws SQLException;
	
	public void executeSQL(List<String> sql) throws SQLException;
	
	public void executeSQL(String sql, Object... params) throws SQLException;

	public void executeSQL(List<String> sqlList, ArrayList<Object[]> paramList) throws SQLException;

	public List<String> getBatchSQL();
}
