package com.hanson.jbpm.jpdl.exe.ctx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hanson.jbpm.jpdl.def.base.OracleBigString;
import com.hanson.jbpm.jpdl.def.base.OracleClob;
import com.hanson.jbpm.log.CommonLogger;
import oracle.sql.CLOB;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jboss.resource.adapter.jdbc.WrappedConnection;
import org.jboss.resource.adapter.jdbc.jdk5.WrappedConnectionJDK5;

import com.suntek.ccf.orm.hibernate.DynamicSessionFactory;
import com.suntek.ccf.orm.hibernate.EAPSessionFactory;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.jdbc.QueryHelper;
import com.suntek.eap.util.jdbc.Dialect;
import com.suntek.eap.util.jdbc.DialectFactory;
import com.hanson.jbpm.jpdl.exe.ctx.dao.BpmDaoSQLExecutor;
import com.hanson.jbpm.mgmt.ProcessClient;

/**
 * <b>ExecutionContext �� SQLHelper ��������ʵ����</b>
 * @author zhout
 * @since 4.0 
 * @version 2011-8-19
 * @Copyright (C)2011 , Suntektech
 */
public class ExecutionContextSQLHelper implements IExecutionContextSQLHelper {
	/* ������ִ�е�SQL��� */
	private ArrayList<String> batchSQL = new ArrayList<String>();
	
	/* SQL�󶨱��� */
	private ArrayList<Object[]> batchParam = new ArrayList<Object[]>();
	
	//private ArrayList<String> backupSQL = new ArrayList<String>();
	
	/* JDBC �־û����� */
	private QueryHelper jdbc = null;
	/* QueryHelper ʹ�õ����ݿⷽ�� */
	private Dialect dialect = null;
	/* Hibernate �־û����� */
	private Session session = null;	
	private DynamicSessionFactory sessionFactory = null;
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	
	private int queryTimeOut = 5;
	
	public void addSQL(String sql) {
		batchSQL.add(sql);	
		batchParam.add(new Object[0]);
	}
	
	
	/**
	 * �󶨲�����ʽ
	 */
	public void addSQL(String sql, Object... params) {
		batchSQL.add(sql);	
		batchParam.add(params);
	}
	
	
	/*public void addBackupSQL(String sql) {
		this.backupSQL.add(sql);
	}*/
	
	public void executeBatchSQL() throws Exception {
		executeBatchSQL(this.batchSQL, this.batchParam);
	}
	

	/*public void executeBackupSQL() throws SQLException {
		executeBatchSQL(this.backupSQL);
	}*/
	
	private void executeBatchSQL(ArrayList<String> SQL) throws SQLException {
		
		for (int i=0; i<SQL.size(); i++)
			CommonLogger.logger.debug(SQL.get(i));
		
		if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc")) {	
			this.getJdbc().begin();
			this.getJdbc().setMaxQueryTime(this.queryTimeOut);
			this.getJdbc().executeBatch( SQL);
		}
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate")) {
			conn = this.getSession().connection();
			Statement stat = conn.createStatement();
			for (int i=0; i<SQL.size(); i++)
				stat.addBatch(SQL.get(i));
			stat.executeBatch();
		}
	}
	
	
	/**
	 * ʹ��prepareStatment�ύ
	 * @param SQL
	 * @param params
	 * @throws SQLException 
	 */
	@SuppressWarnings("deprecation")
	private void executeBatchSQL(ArrayList<String> SQL, ArrayList<Object[]> params) throws Exception {
		/*for (int i=0; i<SQL.size(); i++) {
			CommonLogger.logger.debug("[���ݳ־û�]" + SQL.get(i) + 
					"; args[" + Arrays.toString(params.get(i)) + "]");
		}*/
		
		/*if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc")) {	
			conn = AppHandle.getHandle(ProcessClient.MODULE).getConnection();
			conn.setAutoCommit(false);
		}
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate")) {
			conn = SessionFactoryUtils.getDataSource(sessionFactory.getSessionFactory()).getConnection();
		}*/
		
		//hibernateģʽ�����½�һ������, ����ʱͬʱ�ع�, ����ʹ��ͬһ��hibernate�����Ӵ���
		conn = AppHandle.getHandle(ProcessClient.MODULE).getConnection();
		conn.setAutoCommit(false);
		for(int i=0;i<SQL.size();i++) {
			CommonLogger.logger.debug("[���ݳ־û�]" + SQL.get(i) + 
					"; args[" + Arrays.toString(params.get(i)) + "]");
			
			ps = conn.prepareStatement(SQL.get(i));
			ps.setQueryTimeout(queryTimeOut);
			Object[] args = params.get(i);
			
			for(int j=0;j<args.length;j++) {
				
				if(args[j] != null && "OracleClob".equals(args[j].getClass().getSimpleName())) {
					//oracle CLOB�ֶε�������
					//org.jboss.resource.adapter.jdbc.jdk5.WrappedConnectionJDK5@119c916
					Connection oracleConn = null;
					try {//����CLOB����������Ҫ�����ӳصĴ���ת���ɱ���������
						oracleConn = ((WrappedConnectionJDK5) conn).getUnderlyingConnection();
					} catch (Exception e) {
						oracleConn = ((WrappedConnection) conn).getUnderlyingConnection();
					}
					CLOB clob = CLOB.createTemporary(oracleConn, false, CLOB.DURATION_SESSION);
					clob.setString(1, ((OracleClob)args[j]).getText());
					ps.setClob(j+1, clob);
				} else if(args[j] != null && "OracleBigString".equals(args[j].getClass().getSimpleName())) {
					//oracle���ַ����ֶ�ҲҪ����ʹ��setString����
					ps.setString(j+1, ((OracleBigString)args[j]).toString());
				} else {
					if(args[j] != null) {
						ps.setObject(j+1, args[j]);
					}else {
						ps.setNull(j+1, Types.VARCHAR);
					}
				}
			}
			ps.executeUpdate();
			ps.close();
		}
	}
	

	public Dialect getDialect() throws SQLException {
		if (dialect == null) {
			dialect = DialectFactory.getDialect(AppHandle.getHandle(ProcessClient.MODULE).getDatasourceName());
		}
		return dialect;
	}

	public QueryHelper getJdbc() throws SQLException {
		if (jdbc == null) {
			jdbc = QueryHelper.getDefault(AppHandle.getHandle(ProcessClient.MODULE).getDatasourceName());
		}
		return jdbc;
	}

	public Session getSession() throws HibernateException {
		if (session == null) {
			sessionFactory = EAPSessionFactory.getInstance().getSessionFactory(ProcessClient.MODULE);			
			session = sessionFactory.getSession();
		}
		return session;
	}

	public DynamicSessionFactory getSessionFactory() throws HibernateException {
		return sessionFactory;
	}

	public void beginTransaction() throws SQLException {
		if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc")) {
			//this.getJdbc().begin(); // �Ƶ�ִ�е�ʱ���ٵ��ã�
		}
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate")) {
			this.getSession().beginTransaction();				
		}	
	}

	public void commitTransaction() throws SQLException {
		/*if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc")) {				
			//this.getJdbc().commit();
			if(conn != null) {
				conn.commit();
			}
		}*/
		
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate"))
			this.getSession().getTransaction().commit();
		
		if(conn != null) {
			conn.commit();
		}
	}
	
	public void rollbackTransaction() throws SQLException {
		/*if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc")) {
			//this.getJdbc().roolback();
			if(conn != null) {
				conn.rollback();
			}
		}*/
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate"))
			this.getSession().getTransaction().rollback();	
		
		conn.rollback();
	}

	public void setTimeout(int seconds) throws SQLException {
		queryTimeOut = seconds;
		if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc")) {
			//this.getJdbc().setMaxQueryTime(seconds);
			//this.queryTimeOut = seconds;
			
		}
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate")) {
			this.getSession().getTransaction().setTimeout(seconds);
		}		
	}

	public void closeHibernateSession() {
		//if (BpmDaoSQLExecutor.PersistenceMode.equals("jdbc")) {
		if(ps != null) {
			try {ps.clearBatch();} catch (SQLException e) {}
			try {ps.close();} catch (SQLException e) {}
		}
		if(conn != null) {
			try {conn.close();} catch (SQLException e) {}
		}
			
		//}
		if (BpmDaoSQLExecutor.PersistenceMode.equals("hibernate"))
			this.getSessionFactory().closeSession();
	}

	public void executeSQL(String sql) throws SQLException {
		this.addSQL(sql);		
	}

	public void executeSQL(List<String> sqlList) throws SQLException {
		for (int i=0; i<sqlList.size(); i++) {
			this.addSQL(sqlList.get(i));
		}
	}
	
	public void executeSQL(String sql, Object... params) throws SQLException {
		this.addSQL(sql, params);
	}
	
	
	public void executeSQL(List<String> sqlList, ArrayList<Object[]> paramList) throws SQLException {
		for (int i=0; i<sqlList.size(); i++) {
			this.addSQL(sqlList.get(i), paramList.get(i));
		}
	}
	

	public List<String> getBatchSQL() {
		return this.batchSQL;
	}

	public void setJdbc(QueryHelper jdbc) {
		this.jdbc = jdbc;
	}

	public void setBatchParam(ArrayList<Object[]> batchParam) {
		this.batchParam = batchParam;
	}

	public ArrayList<Object[]> getBatchParam() {
		return batchParam;
	}
}
