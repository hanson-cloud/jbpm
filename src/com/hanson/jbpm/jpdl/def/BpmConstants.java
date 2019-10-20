package com.hanson.jbpm.jpdl.def;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.suntek.eap.core.app.AppHandle;
import com.suntek.eap.core.config.SystemConfig;

public class BpmConstants {	
	
	public final static String INST_FIELDS = "INST_ID, INST_TITLE, ATTACHFILE, PROC_NAME, INIT_UID, INIT_TIME, INIT_DEPTID, DUE_TIME, CLOSE_TYPE, CLOSE_UID, CLOSE_TIME, OVERTIME, LAST_TASKID, RELEVANTED, DELETED, FIRST_RESOLVE, IMPORTANCE_LEVEL, EMERGENCY_LEVEL, ESCALATED, IS_ERROR, PRINTED, PRINT_USERID, BO_YEAR, BO_MONTH, BO_DAY";
	public final static String TASK_FIELDS = " TASK_ID, INST_ID, PROC_NAME, PRE_TASKID, PRE_TASKNAME, TASK_NAME, ASSIGN_UID, ASSIGN_DEPTID, ASSIGN_TIME, DUE_TIME, DEALER, DEAL_UID, DEAL_DEPTID, DEAL_TIME, TRANS_NAME, OVERTIME, LOCKED, LOCK_USERID, REMINDED, REMIND_USERID, REMIND_MSG, BO_YEAR, BO_MONTH, BO_DAY, CC_DEALER, CO_DEALER";
	
	public final static String TASK_EXT_FIELDS = "TASK_ID,INST_ID,ASSIGN_MEMO,PASS_NODE,FORK_ID,COMMENT_CNT,CONTEXT";
	public final static String TASK_COMMENTS_FIELDS = "TASK_ID,INST_ID,CALL_ID,TASK_NAME,DEAL_UID,DEAL_UNAME,DEAL_DEPTID,DEAL_DEPT,DEAL_TIME,CONTEXT";
	public final static String ASSIGN_FIELDS = "INST_ID,TASK_ID,DEALER,CC_FLAG,CO_FLAG,READ_FLAG,DEAL_FLAG,RUN_IDX,EXTRACT_ID,OLD_DEALER,ASSIGN_TIME,READ_TIME,DEAL_TIME,DUE_TIME,REMINDED,PROC_NAME,TASK_NAME,DEAL_DEPTID ";
	
	public final static boolean isOpenCCEnv = !(AppHandle.getHandle("spaportal2").getProperty("RECORD_TYPE", "").equals(""));
	
	public final static String CONFIG_PATH = SystemConfig.getEAPHome() + "/eapserver/application/jbpm/";
	
	public static final String BPM_DIR_PATH =  
			(SystemConfig.getEAPHome().indexOf("tomcat")>=0)?"../../META-INF/flow/":"META-INF/flow/";
	
	public static final boolean RoleDeptDispatchMode = 
			AppHandle.getHandle("jbpm").getProperty("RoleDeptDispatchMode", "false").equals("true");
	
	public static final boolean AutoArchived = 
			AppHandle.getHandle("jbpm").getProperty("AutoArchived", "false").equals("true");
	
	public final static String checkoutTypeConfig = 
			AppHandle.getHandle(ProcessEngine.getClientModuleName()).getProperty("checkout");
	
	public static final String BPM_CHAR_SET = "GBK";
	
	public static final String PROCESS_DEFINITION_NAME = "test";
	
	public static final String INDEX_VERSION = "JBPMLuceneVersion";
	public static final String OP_SETVERSION = "SetVersion";
	public static final String OP_GETVERSION = "GetVersion";
	
	public static final boolean DEBUG = false;
		
	public static final String INITFLAG = "0";
	public static final String READFLAG = "1";
	
	public static final String DEALFLAG = "2";
	public static final String CCFLAG = "9";		
	public static final String DELETED = "9";	
	public static final String INVALID = "6";
	
	public static final String PREFIX_DEPT = "D";
	public static final String PREFIX_ROLE = "R";
	public static final String PREFIX_GROUP = "G";
	
	public static final String LOCKED = "1";
	public static final String UNLOCKED = "NULL";
	
	
	/* ȱʡ��ʱʱ�� 24Сʱ*/
	public static int DEFAULT_DUEDATE = 24 * 15;
	public static int DEFAULT_PROC_DUEDATE = 24 * 30 ;
	
	/* ȱʡ��ȫ�ļ����ֶε��ֶ��� */
	public static String FULLTEXT_FIELDNAME = "fulltextFieldName";
	
	public static String RET_TRANS_PREFIX = "JBPM_RET_";
}
