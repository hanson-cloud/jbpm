package com.hanson.jbpm.jpdl.exe.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.hanson.jbpm.jpdl.exe.ctx.Context;
import com.hanson.jbpm.jpdl.exe.ctx.InstanceContext;
import com.suntek.eap.util.calendar.DateUtil;
import com.suntek.util.time.CurrentDateTime;

public class DateFormat {
	public String format(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		String[] dayofweek = new String[]{"","��","һ","��","��","��","��","��"};
		return date.toString().substring(0,10) + " (����" + dayofweek[ca.get(Calendar.DAY_OF_WEEK)] + ") " + date.toString().substring(11,19);
	}
	
	public String format(Timestamp timestamp) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(timestamp.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(ca.getTime());
	}
	
	public String getFormSetDueTime(InstanceContext ctx) {
		String duetimeParam = ctx.getParameter(Context.values.DUETIME, Context.types.RUNTIME);
		String dueTime = "";
		if (!duetimeParam.equals("")) {
			if (duetimeParam.indexOf(":")>0) {
				dueTime = duetimeParam;				
			} else {
				String current = CurrentDateTime.getCurrentDateTime();
				int due1 = Integer.parseInt(duetimeParam);
				dueTime = DateUtil.getPreDateTime(current, DateUtil.HOUR, due1);
			}
		} 
		return dueTime;	
	}
}
