package com.hanson.jbpm.jpdl.exe.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils
{
	
	public static final String DEFAULT_DATE = "2100-01-01 00:00:00";
	
	
	public static String getFirstDayOfWeek() 
	{
		Calendar calendar = Calendar.getInstance(Locale.FRANCE);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(calendar.getTime());
	}
	
	public static int getYear() 
	{
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return Integer.parseInt(sdf.format(calendar.getTime()));
	}
	
	public static int getMonth() 
	{
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		return Integer.parseInt(sdf.format(calendar.getTime()));
	}
	

	public static int getDate() 
	{
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return Integer.parseInt(sdf.format(calendar.getTime()));
	}

	public static String getFirstDayOfMonth() 
	{
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(calendar.getTime());
	}
	
	public static String getLastDayOfMonth() 
	{
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(calendar.getTime());
	}
	
	public static int getWeekOfYear() 
	{
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}
	
	public static String getThreeMonthsBeforeDate() 
	{
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-3);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(calendar.getTime());
	}
	
	public static String[] getDaysOfLastWeek(String format) 
	{
		String[] days = new String[7];
		for (int i=6, j=0; i>=0; i--,j++) {
			days[j] = getDayOffset((i+1)*-1, format);
		}
		return days;
	}
	
	public static String getDayOffset(int offset, String format) 
	{
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + offset);		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(calendar.getTime());
	}
	
	public static String toString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	public static SimpleDateFormat getDateTimeFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public static void main(String[] args) 
	{		
		System.out.println(Calendar.getInstance().get(Calendar.MONTH));
		System.out.println(DateUtils.getFirstDayOfWeek());
		System.out.println(DateUtils.getFirstDayOfMonth());
		System.out.println(DateUtils.getLastDayOfMonth());
		System.out.println(DateUtils.getWeekOfYear());
		System.out.println(DateUtils.getThreeMonthsBeforeDate());
		String[] days = DateUtils.getDaysOfLastWeek("yyyyMMdd");
		for (int i=0; i<days.length; i++) System.out.println(days[i]);
	}
}
