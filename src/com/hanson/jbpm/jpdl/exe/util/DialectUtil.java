package com.hanson.jbpm.jpdl.exe.util;

import com.suntek.ccf.ui.grid.dao.QueryInfo;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.util.jdbc.Dialect;
import com.suntek.eap.util.jdbc.DialectFactory;
import com.suntek.eap.util.jdbc.impl.MySQLDialect;
import com.suntek.eap.util.jdbc.impl.OracleDialect;
import com.suntek.eap.util.jdbc.impl.SybaseDialect;

public class DialectUtil
{
	

	public static boolean isSybase(Dialect dialect)
	{
		if (dialect instanceof SybaseDialect) return true;
		else return false;
	}

	public static boolean isOracle(Dialect dialect)
	{
		if (dialect instanceof OracleDialect) return true;
		else return false;
	}

	public static boolean isMySQL(Dialect dialect)
	{
		if (dialect instanceof MySQLDialect) return true;
		else return false;
	}
}
