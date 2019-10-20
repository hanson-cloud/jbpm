package com.hanson.jbpm.jpdl.def.base;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.hanson.jbpm.jpdl.ProcessEngine;
import com.hanson.jbpm.jpdl.exe.util.DialectUtil;
import com.hanson.jbpm.jpdl.exe.util.TableModelUtil;

/**
 * BpmBiz entity. @author MyEclipse Persistence Tools
 */

public class BpmBiz implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -6956991319898247383L;
	
	private String instId;
	private String entCode;
	private String callId;
	private Date callDate;
	private Date registerDate;
	private String requestFrom;
	private String requestFromApp;
	private String customerId;
	private String customerCaller;
	private String callerLocal;
	private String contactName;
	private String contactTitle;
	private String contactTel;
	private String contactEmail;
	private String contactSecret;
	private String contactAddress;
	private String contactCompany;
	private String contactRemark;
	private String certType;
	private String certCode;
	private String postCode;
	private String orderType;
	private String orderStatus;
	private String requestType;
	private String requestLevel;
	private String requestTitle;
	private String requestTopic;
	private String requestDeptid;
	private String requestDeptname;
	private String requestContent;
	private String requestArea1;
	private String requestArea2;
	private String requestLocal;
	private String agentId;
	private String agentName;
	private String dealId;
	private String dealName;
	private String dealDeptid;
	private String dealDeptname;
	private String dealer;
	private String ccDealer;
	private String coDealer;
	private String dealMonitor;
	private Date dealDeadline;
	private Date dealDeadlineRed;
	private int earlywarnDays = -1;
	private int warnDays = -1;
	private int seriouswarnDays = -1;
	private int dealtDays = -1;
	private int deadlineDays = -1;
	private String dealerSolution;
	private Date dealerDate;
	private String lastTaskid;
	private String lastTaskName;
	private String dealSatisfy;
	private Date archiveDate;
	private String orderRemark;
	private String isDelayed;
	private String isReminded;
	private String remindUid;
	private String remindMsg;
	private String visitAgentId;
	private String visitAgentName;
	private String visitContent;
	private Date visitDate;
	private String processName;
	private String moduleName;
	private String bestRecord;
	private String locked;
	private String lockUserid;
	private Date lockTime;
	private String isSupervised;
	private String isClosed;
	private String firstResolve;
	private String requestTypeid;
	private Date dispatchDate;
	private String requestSubject;
	private String subjectTel;
	private String subjectAddr;
	private String forbidReback;
	private String dispatchUid;
	private String questionType;
	private String isEnglish;
	private int returnOtDays;
	private String dealTypeName;
	private String dealTypeId;
	private String keyWord;
	private String urgentQuestionType;
	private String urgentQuestionTypeid; 
	private String customerEvaluation;
	private String organizerEvaluation;
	private String requestSubjectCode;
	private String questionTypeid;
	private String busType;
	private String busiTemplate;
	private String customerSatisfy;
	private String chatId;
	private String isClassic;
	private String cloReason;
	private Date submitDate; 
	private String dealerMemo;
	private Date dealDate;
	private String loginAgentname;
	private String isMayorMonitor;
	
	private String reverse1;
	private String reverse2;
	private String reverse3;
	
	private String complainTypeCd;
	private String complainType;
	private String complainDept;
	
	private String boCasescecityCd;
	private String boCasescecity;
	private String boConlrangeCd;
	private String boConlrange;
	private Date boAccTime;
	
	private String schoolId;
	private String schoolName;
	private String orderQueue;
	

	


	public String getOrderQueue() {
		return orderQueue;
	}

	public void setOrderQueue(String orderQueue) {
		this.orderQueue = orderQueue;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getBoCasescecityCd() {
		return boCasescecityCd;
	}

	public void setBoCasescecityCd(String boCasescecityCd) {
		this.boCasescecityCd = boCasescecityCd;
	}

	public String getBoCasescecity() {
		return boCasescecity;
	}

	public void setBoCasescecity(String boCasescecity) {
		this.boCasescecity = boCasescecity;
	}

	public String getBoConlrangeCd() {
		return boConlrangeCd;
	}

	public void setBoConlrangeCd(String boConlrangeCd) {
		this.boConlrangeCd = boConlrangeCd;
	}

	public String getBoConlrange() {
		return boConlrange;
	}

	public void setBoConlrange(String boConlrange) {
		this.boConlrange = boConlrange;
	}

	public Date getBoAccTime() {
		return boAccTime;
	}

	public void setBoAccTime(Date boAccTime) {
		this.boAccTime = boAccTime;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getComplainTypeCd() {
		return complainTypeCd;
	}

	public void setComplainTypeCd(String complainTypeCd) {
		this.complainTypeCd = complainTypeCd;
	}

	public String getComplainType() {
		return complainType;
	}

	public void setComplainType(String complainType) {
		this.complainType = complainType;
	}

	public String getComplainDept() {
		return complainDept;
	}

	public void setComplainDept(String complainDept) {
		this.complainDept = complainDept;
	}

	// Constructors
	/** default constructor */
	public BpmBiz() {
	}

	/** minimal constructor */
	public BpmBiz(String instId, int earlywarnDays, int warnDays,
			int seriouswarnDays, String isDelayed) {
		this.instId = instId;
		this.earlywarnDays = earlywarnDays;
		this.warnDays = warnDays;
		this.seriouswarnDays = seriouswarnDays;
		this.isDelayed = isDelayed;
	}

	/** full constructor */
	public BpmBiz(String instId, String entCode, String callId, Date callDate,
			Date registerDate, String requestFrom, String requestFromApp,
			String customerId, String customerCaller, String callerLocal,
			String contactName, String contactTitle, String contactTel,
			String contactEmail, String contactSecret, String contactAddress,
			String contactCompany, String contactRemark, String certType,
			String certCode, String postCode, String orderType,
			String orderStatus, String requestType, String requestLevel,
			String requestTitle, String requestTopic, String requestDeptid,
			String requestContent, String requestArea1, String requestArea2,
			String requestLocal, String agentId, String agentName,
			String dealId, String dealName, String dealDeptid,
			String dealDeptname, String dealer, String ccDealer,
			String coDealer, String dealMonitor, Date dealDeadline,
			Date dealDeadlineRed, int earlywarnDays,
			int warnDays, int seriouswarnDays,
			String dealerSolution, Date dealerDate, String lastTaskid,
			String lastTaskName, String dealSatisfy, Date archiveDate,
			String orderRemark, String isDelayed, String reverse1,
			String reverse2, String reverse3,String complainTypeCd,
			String complainType,String complainDept) {
		this.instId = instId;
		this.entCode = entCode;
		this.callId = callId;
		this.callDate = callDate;
		this.registerDate = registerDate;
		this.requestFrom = requestFrom;
		this.requestFromApp = requestFromApp;
		this.customerId = customerId;
		this.customerCaller = customerCaller;
		this.callerLocal = callerLocal;
		this.contactName = contactName;
		this.contactTitle = contactTitle;
		this.contactTel = contactTel;
		this.contactEmail = contactEmail;
		this.contactSecret = contactSecret;
		this.contactAddress = contactAddress;
		this.contactCompany = contactCompany;
		this.contactRemark = contactRemark;
		this.certType = certType;
		this.certCode = certCode;
		this.postCode = postCode;
		this.orderType = orderType;
		this.orderStatus = orderStatus;
		this.requestType = requestType;
		this.requestLevel = requestLevel;
		this.requestTitle = requestTitle;
		this.requestTopic = requestTopic;
		this.requestDeptid = requestDeptid;
		this.requestContent = requestContent;
		this.requestArea1 = requestArea1;
		this.requestArea2 = requestArea2;
		this.requestLocal = requestLocal;
		this.agentId = agentId;
		this.agentName = agentName;
		this.dealId = dealId;
		this.dealName = dealName;
		this.dealDeptid = dealDeptid;
		this.dealDeptname = dealDeptname;
		this.dealer = dealer;
		this.ccDealer = ccDealer;
		this.coDealer = coDealer;
		this.dealMonitor = dealMonitor;
		this.dealDeadline = dealDeadline;
		this.dealDeadlineRed = dealDeadlineRed;
		this.earlywarnDays = earlywarnDays;
		this.warnDays = warnDays;
		this.seriouswarnDays = seriouswarnDays;
		this.dealerSolution = dealerSolution;
		this.dealerDate = dealerDate;
		this.lastTaskid = lastTaskid;
		this.lastTaskName = lastTaskName;
		this.dealSatisfy = dealSatisfy;
		this.archiveDate = archiveDate;
		this.orderRemark = orderRemark;
		this.isDelayed = isDelayed;
		
		this.reverse1 = reverse1;
		this.reverse2 = reverse2;
		this.reverse3 = reverse3;
		
		this.complainTypeCd = complainTypeCd;
		this.complainType = complainType;
		this.complainDept = complainDept;
		
	}

	// Property accessors

	public String getInstId() {
		return this.instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getEntCode() {
		return this.entCode;
	}

	public void setEntCode(String entCode) {
		this.entCode = entCode;
	}

	public String getCallId() {
		return this.callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public Date getCallDate() {
		return this.callDate;
	}

	public void setCallDate(Date callDate) {
		this.callDate = callDate;
	}

	public Date getRegisterDate() {
		return this.registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public String getRequestFrom() {
		return this.requestFrom;
	}

	public void setRequestFrom(String requestFrom) {
		this.requestFrom = requestFrom;
	}

	public String getRequestFromApp() {
		return this.requestFromApp;
	}

	public void setRequestFromApp(String requestFromApp) {
		this.requestFromApp = requestFromApp;
	}

	public String getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerCaller() {
		return this.customerCaller;
	}

	public void setCustomerCaller(String customerCaller) {
		this.customerCaller = customerCaller;
	}

	public String getCallerLocal() {
		return this.callerLocal;
	}

	public void setCallerLocal(String callerLocal) {
		this.callerLocal = callerLocal;
	}

	public String getContactName() {
		return this.contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTitle() {
		return this.contactTitle;
	}

	public void setContactTitle(String contactTitle) {
		this.contactTitle = contactTitle;
	}

	public String getContactTel() {
		return this.contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public String getContactEmail() {
		return this.contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactSecret() {
		return this.contactSecret;
	}

	public void setContactSecret(String contactSecret) {
		this.contactSecret = contactSecret;
	}

	public String getContactAddress() {
		return this.contactAddress;
	}

	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

	public String getContactCompany() {
		return this.contactCompany;
	}

	public void setContactCompany(String contactCompany) {
		this.contactCompany = contactCompany;
	}

	public String getContactRemark() {
		return this.contactRemark;
	}

	public void setContactRemark(String contactRemark) {
		this.contactRemark = contactRemark;
	}

	public String getCertType() {
		return this.certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertCode() {
		return this.certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public String getPostCode() {
		return this.postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getOrderType() {
		return this.orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderStatus() {
		return this.orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getRequestType() {
		return this.requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestLevel() {
		return this.requestLevel;
	}

	public void setRequestLevel(String requestLevel) {
		this.requestLevel = requestLevel;
	}

	public String getRequestTitle() {
		return this.requestTitle;
	}

	public void setRequestTitle(String requestTitle) {
		this.requestTitle = requestTitle;
	}

	public String getRequestTopic() {
		return this.requestTopic;
	}

	public void setRequestTopic(String requestTopic) {
		this.requestTopic = requestTopic;
	}

	public String getRequestDeptid() {
		return this.requestDeptid;
	}

	public void setRequestDeptid(String requestDeptid) {
		this.requestDeptid = requestDeptid;
	}

	public String getRequestContent() {
		return this.requestContent;
	}

	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}

	public String getRequestArea1() {
		return this.requestArea1;
	}

	public void setRequestArea1(String requestArea1) {
		this.requestArea1 = requestArea1;
	}

	public String getRequestArea2() {
		return this.requestArea2;
	}

	public void setRequestArea2(String requestArea2) {
		this.requestArea2 = requestArea2;
	}

	public String getRequestLocal() {
		return this.requestLocal;
	}

	public void setRequestLocal(String requestLocal) {
		this.requestLocal = requestLocal;
	}

	public String getAgentId() {
		return this.agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getAgentName() {
		return this.agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getDealId() {
		return this.dealId;
	}

	public void setDealId(String dealId) {
		this.dealId = dealId;
	}

	public String getDealName() {
		return this.dealName;
	}

	public void setDealName(String dealName) {
		this.dealName = dealName;
	}

	public String getDealDeptid() {
		return this.dealDeptid;
	}

	public void setDealDeptid(String dealDeptid) {
		this.dealDeptid = dealDeptid;
	}

	public String getDealDeptname() {
		return this.dealDeptname;
	}

	public void setDealDeptname(String dealDeptname) {
		this.dealDeptname = dealDeptname;
	}

	public String getDealer() {
		return this.dealer;
	}

	public void setDealer(String dealer) {
		this.dealer = dealer;
	}

	public String getCcDealer() {
		return this.ccDealer;
	}

	public void setCcDealer(String ccDealer) {
		this.ccDealer = ccDealer;
	}

	public String getCoDealer() {
		return this.coDealer;
	}

	public void setCoDealer(String coDealer) {
		this.coDealer = coDealer;
	}

	public String getDealMonitor() {
		return this.dealMonitor;
	}

	public void setDealMonitor(String dealMonitor) {
		this.dealMonitor = dealMonitor;
	}

	public Date getDealDeadline() {
		return this.dealDeadline;
	}

	public void setDealDeadline(Date dealDeadline) {
		this.dealDeadline = dealDeadline;
	}

	public Date getDealDeadlineRed() {
		return this.dealDeadlineRed;
	}

	public void setDealDeadlineRed(Date dealDeadlineRed) {
		this.dealDeadlineRed = dealDeadlineRed;
	}

	public int getEarlywarnDays() {
		return this.earlywarnDays;
	}

	public void setEarlywarnDays(int earlywarnDays) {
		this.earlywarnDays = earlywarnDays;
	}

	public int getWarnDays() {
		return this.warnDays;
	}

	public void setWarnDays(int warnDays) {
		this.warnDays = warnDays;
	}

	public int getSeriouswarnDays() {
		return this.seriouswarnDays;
	}

	public void setSeriouswarnDays(int seriouswarnDays) {
		this.seriouswarnDays = seriouswarnDays;
	}

	public String getDealerSolution() {
		return this.dealerSolution;
	}

	public void setDealerSolution(String dealerSolution) {
		this.dealerSolution = dealerSolution;
	}

	public Date getDealerDate() {
		return this.dealerDate;
	}

	public void setDealerDate(Date dealerDate) {
		this.dealerDate = dealerDate;
	}

	public String getLastTaskid() {
		return this.lastTaskid;
	}

	public void setLastTaskid(String lastTaskid) {
		this.lastTaskid = lastTaskid;
	}

	public String getLastTaskName() {
		return this.lastTaskName;
	}

	public void setLastTaskName(String lastTaskName) {
		this.lastTaskName = lastTaskName;
	}

	public String getDealSatisfy() {
		return this.dealSatisfy;
	}

	public void setDealSatisfy(String dealSatisfy) {
		this.dealSatisfy = dealSatisfy;
	}

	public Date getArchiveDate() {
		return this.archiveDate;
	}

	public void setArchiveDate(Date archiveDate) {
		this.archiveDate = archiveDate;
	}

	public String getOrderRemark() {
		return this.orderRemark;
	}

	public void setOrderRemark(String orderRemark) {
		this.orderRemark = orderRemark;
	}

	public String getIsDelayed() {
		return this.isDelayed;
	}

	public void setIsDelayed(String isDelayed) {
		this.isDelayed = isDelayed;
	}

	public String getReverse1() {
		return this.reverse1;
	}

	public void setReverse1(String reverse1) {
		this.reverse1 = reverse1;
	}

	public String getReverse2() {
		return this.reverse2;
	}

	public void setReverse2(String reverse2) {
		this.reverse2 = reverse2;
	}

	public String getReverse3() {
		return this.reverse3;
	}

	public void setReverse3(String reverse3) {
		this.reverse3 = reverse3;
	}
	
	
	public void setRequestDeptname(String requestDeptname) {
		this.requestDeptname = requestDeptname;
	}

	public String getRequestDeptname() {
		return requestDeptname;
	}

	

	public int getDealtDays() {
		return dealtDays;
	}

	public void setDealtDays(int dealtDays) {
		this.dealtDays = dealtDays;
	}

	public void setDeadlineDays(int deadlineDays) {
		this.deadlineDays = deadlineDays;
	}

	public int getDeadlineDays() {
		return deadlineDays;
	}

	public void setIsReminded(String isReminded) {
		this.isReminded = isReminded;
	}

	public String getIsReminded() {
		return isReminded;
	}

	public void setRemindUid(String remindUid) {
		this.remindUid = remindUid;
	}

	public String getRemindUid() {
		return remindUid;
	}

	public void setRemindMsg(String remindMsg) {
		this.remindMsg = remindMsg;
	}

	public String getRemindMsg() {
		return remindMsg;
	}

	public void setVisitAgentId(String visitAgentId) {
		this.visitAgentId = visitAgentId;
	}

	public String getVisitAgentId() {
		return visitAgentId;
	}

	public void setVisitAgentName(String visitAgentName) {
		this.visitAgentName = visitAgentName;
	}

	public String getVisitAgentName() {
		return visitAgentName;
	}

	public void setVisitContent(String visitContent) {
		this.visitContent = visitContent;
	}

	public String getVisitContent() {
		return visitContent;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setBestRecord(String bestRecord) {
		this.bestRecord = bestRecord;
	}

	public String getBestRecord() {
		return bestRecord;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public void setLocked(String locked) {
		this.locked = locked;
	}

	public String getLocked() {
		return locked;
	}

	public void setLockUserid(String lockUserid) {
		this.lockUserid = lockUserid;
	}

	public String getLockUserid() {
		return lockUserid;
	}

	public void setIsClosed(String isClosed) {
		this.isClosed = isClosed;
	}

	public String getIsClosed() {
		return isClosed;
	}

	public void setIsSupervised(String isSupervised) {
		this.isSupervised = isSupervised;
	}

	public String getIsSupervised() {
		return isSupervised;
	}

	public void setFirstResolve(String firstResolve) {
		this.firstResolve = firstResolve;
	}

	public String getFirstResolve() {
		return firstResolve;
	}

	public void setRequestTypeid(String requestTypeid) {
		this.requestTypeid = requestTypeid;
	}

	public String getRequestTypeid() {
		return requestTypeid;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public void setRequestSubject(String requestSubject) {
		this.requestSubject = requestSubject;
	}

	public String getRequestSubject() {
		return requestSubject;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setSubjectTel(String subjectTel) {
		this.subjectTel = subjectTel;
	}

	public String getSubjectTel() {
		return subjectTel;
	}

	public void setSubjectAddr(String subjectAddr) {
		this.subjectAddr = subjectAddr;
	}

	public String getSubjectAddr() {
		return subjectAddr;
	}

	/**
	 * ����insert���
	 * (�־û���toPrepareInserSql)
	 * @return
	 * @throws Exception 
	 */
	@Deprecated
	public String toInsertSql() throws Exception {
		StringBuffer cols = new StringBuffer(5000);
		StringBuffer values = new StringBuffer(5000);
		cols.append("insert into BPM_BIZ (INST_ID");
		values.append(" ) values ('").append(instId).append("'");
		BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
		PropertyDescriptor [] propertieDescritors = beanInfo.getPropertyDescriptors();
		for(PropertyDescriptor pd : propertieDescritors) {
			Object value = pd.getReadMethod().invoke(this);
			String name = pd.getName();
			String type = pd.getPropertyType().getSimpleName().toLowerCase();
			String sqlValue = buildSqlValue(value, type);
			if(sqlValue != null && !"instId".equals(name) && !"class".equals(name)) {
				cols.append(",").append(TableModelUtil.toTableField(name));
				values.append(",").append(sqlValue);
				
			}
		}
		values.append(")");
		return cols.append(values).toString();
	}
	
	/**
	 * ����update���
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Deprecated
	public String toUpdateSql() throws Exception {
		StringBuffer sql = new StringBuffer(5000);
		sql.append("update BPM_BIZ set INST_ID=INST_ID");
		BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
		PropertyDescriptor [] propertieDescritors = beanInfo.getPropertyDescriptors();
		for(PropertyDescriptor pd : propertieDescritors) {
			Object value = pd.getReadMethod().invoke(this);
			String name = pd.getName();
			String type = pd.getPropertyType().getSimpleName().toLowerCase();
			String sqlValue = buildSqlValue(value, type);
			if(sqlValue != null && !"instId".equals(name) && !"class".equals(name)) {
				sql.append(",").append(TableModelUtil.toTableField(name))
					.append("=").append(sqlValue);
				
			}
		}
		sql.append(" where INST_ID='").append(instId).append("'");
		return sql.toString();
	}
	
	
	/**
	 * �󶨱�����ʽ
	 * @return
	 * @throws Exception
	 */
	public PreparedSqlBean toPreparedInsertSql() throws Exception {
		StringBuffer cols = new StringBuffer(5000);
		StringBuffer values = new StringBuffer(500);
		ArrayList<Object> args = new ArrayList<Object>();
		args.add(instId);
		cols.append("insert into BPM_BIZ (INST_ID");
		values.append(" ) values (?");
		BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
		PropertyDescriptor [] propertieDescritors = beanInfo.getPropertyDescriptors();
		for(PropertyDescriptor pd : propertieDescritors) {
			Object value = pd.getReadMethod().invoke(this);
			String name = pd.getName();
			String type = pd.getPropertyType().getSimpleName().toLowerCase();
			Object sqlValue = buildPreparedSqlValue(value, type);
			if(sqlValue != null && !"instId".equals(name) && !"class".equals(name)) {
				cols.append(",").append(TableModelUtil.toTableField(name));
				values.append(",?");
				if(("requestContent".equals(name) || "dealerSolution".equals(name)) 
						&& DialectUtil.isOracle(ProcessEngine.getDialect())) {
					//���ݺͽ���취ΪCLOB
					args.add(OracleClob.build((String)sqlValue));
				}else {
					args.add(sqlValue);
				}
			}
			
		}
		values.append(")");
		
		String sql = cols.append(values).toString();
		return PreparedSqlBean.build(sql, args.toArray(new Object[args.size()]));
	}
	
	
	
	/**
	 * �󶨱�����ʽ
	 * @return
	 * @throws Exception
	 */
	public PreparedSqlBean toPreparedUpdateSql() throws Exception {
		StringBuffer sql = new StringBuffer(5000);
		ArrayList<Object> args = new ArrayList<Object>();
		sql.append("update BPM_BIZ set INST_ID=INST_ID");
		BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
		PropertyDescriptor [] propertieDescritors = beanInfo.getPropertyDescriptors();
		for(PropertyDescriptor pd : propertieDescritors) {
			Object value = pd.getReadMethod().invoke(this);
			String name = pd.getName();
			String type = pd.getPropertyType().getSimpleName().toLowerCase();
			Object sqlValue = buildPreparedSqlValue(value, type);
			if(sqlValue != null && !"instId".equals(name) && !"class".equals(name)) {
				sql.append(",").append(TableModelUtil.toTableField(name))
					.append("=?");
				if(("requestContent".equals(name) || "dealerSolution".equals(name)) 
						&& DialectUtil.isOracle(ProcessEngine.getDialect())) {
					//���ݺͽ���취ΪCLOB
					args.add(OracleClob.build((String)sqlValue));
				}else {
					args.add(sqlValue);
				}
			}
		}
		sql.append(" where INST_ID=?");
		args.add(instId);
		return PreparedSqlBean.build(sql.toString(), args.toArray(new Object[args.size()]));
	}
	
	
	
	
	/**
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	@Deprecated
	private String buildSqlValue(Object value, String type) {
		if(value == null) {
			return null;
		}
		if(type.indexOf("int") >= 0) {
			if((Integer)value == -1) {
				return null;
			}
			return String.valueOf(value);
		}else if(type.indexOf("date") >= 0) {
			Date d = (Date)value;
			if(d.getTime() == 0) {
				return "null";
			}
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
			return ProcessEngine.getDialect().todate("'" + date + "'");
			//return date;
		}else {
			return "'" + value + "'";
		}
	}
	
	
	/**
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	private Object buildPreparedSqlValue(Object value, String type) {
		if(value == null) {
			return null;
		}
		if(type.indexOf("int") >= 0) {
			if((Integer)value == -1) {
				return null;
			}
			return value;
		}else if(type.indexOf("date") >= 0) {
			Date d = (Date)value;
			if(d.getTime() == 0) {
				return null;
			}
			return new Timestamp(d.getTime());
			//return date;
		}else {
			return value;
		}
	}
	
	
	public static void main(String[] args) {
		System.setProperty("EAP_HOME", "D:/OpenEAP_318");
		System.setProperty("EAP_DEBUG", "true");
		System.setProperty("ECLIPSE_HOME", "d:/eclipse");
		BpmBiz biz = new BpmBiz();
		biz.setInstId("383000000008384");
		biz.setAgentId("tl");
		biz.setCallDate(Calendar.getInstance().getTime());
		Date d = new Date(0);
		biz.setLockTime(d);
		biz.setReturnOtDays(5);
		try {
			//System.out.println();
			//System.out.println(Arrays.toString(biz.buildPreparedInsertArgs()));
			System.out.println(biz.toPreparedUpdateSql().getSql());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setForbidReback(String forbidReback) {
		this.forbidReback = forbidReback;
	}

	public String getForbidReback() {
		return forbidReback;
	}

	public void setDispatchUid(String dispatchUid) {
		this.dispatchUid = dispatchUid;
	}

	public String getDispatchUid() {
		return dispatchUid;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setIsEnglish(String isEnglish) {
		this.isEnglish = isEnglish;
	}

	public String getIsEnglish() {
		return isEnglish;
	}

	public void setReturnOtDays(int returnOtDays) {
		this.returnOtDays = returnOtDays;
	}

	public int getReturnOtDays() {
		return returnOtDays;
	}

	public void setDealTypeName(String dealTypeName) {
		this.dealTypeName = dealTypeName;
	}

	public String getDealTypeName() {
		return dealTypeName;
	}

	public void setDealTypeId(String dealTypeId) {
		this.dealTypeId = dealTypeId;
	}

	public String getDealTypeId() {
		return dealTypeId;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setUrgentQuestionType(String urgentQuestionType) {
		this.urgentQuestionType = urgentQuestionType;
	}

	public String getUrgentQuestionType() {
		return urgentQuestionType;
	}

	public void setUrgentQuestionTypeid(String urgentQuestionTypeid) {
		this.urgentQuestionTypeid = urgentQuestionTypeid;
	}

	public String getUrgentQuestionTypeid() {
		return urgentQuestionTypeid;
	}

	public void setCustomerEvaluation(String customerEvaluation) {
		this.customerEvaluation = customerEvaluation;
	}

	public String getCustomerEvaluation() {
		return customerEvaluation;
	}

	public void setOrganizerEvaluation(String organizerEvaluation) {
		this.organizerEvaluation = organizerEvaluation;
	}

	public String getOrganizerEvaluation() {
		return organizerEvaluation;
	}

	public void setRequestSubjectCode(String requestSubjectCode) {
		this.requestSubjectCode = requestSubjectCode;
	}

	public String getRequestSubjectCode() {
		return requestSubjectCode;
	}

	public void setQuestionTypeid(String questionTypeid) {
		this.questionTypeid = questionTypeid;
	}

	public String getQuestionTypeid() {
		return questionTypeid;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusiTemplate(String busiTemplate) {
		this.busiTemplate = busiTemplate;
	}

	public String getBusiTemplate() {
		return busiTemplate;
	}

	public void setCustomerSatisfy(String customerSatisfy) {
		this.customerSatisfy = customerSatisfy;
	}

	public String getCustomerSatisfy() {
		return customerSatisfy;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getChatId() {
		return chatId;
	}

	public void setIsClassic(String isClassic) {
		this.isClassic = isClassic;
	}

	public String getIsClassic() {
		return isClassic;
	}

	public void setCloReason(String cloReason) {
		this.cloReason = cloReason;
	}

	public String getCloReason() {
		return cloReason;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setDealerMemo(String dealerMemo) {
		this.dealerMemo = dealerMemo;
	}

	public String getDealerMemo() {
		return dealerMemo;
	}

	public void setDealDate(Date dealDate) {
		this.dealDate = dealDate;
	}

	public Date getDealDate() {
		return dealDate;
	}

	public void setLoginAgentname(String loginAgentname) {
		this.loginAgentname = loginAgentname;
	}

	public String getLoginAgentname() {
		return loginAgentname;
	}

	public void setIsMayorMonitor(String isMayorMonitor) {
		this.isMayorMonitor = isMayorMonitor;
	}

	public String getIsMayorMonitor() {
		return isMayorMonitor;
	}

}