package com.hanson.jbpm.jpdl.exe.util;

class CDataUtil
{
	private final static String[] RESERVE_KEYNAME = "action,processName,moduleName,_ID,MAN,USER,CODE,TIME,DATE,TYPE,PHONE,MOBILE,POSTAL".split(",");
	
	public static String get(String key, String value) {
		String normal = "<" + key + ">" + value + "</" + key + ">";
		if (value.length()<5) {
			return normal;
		}
		for (int i=0; i<RESERVE_KEYNAME.length; i++) {
			if (key.contains(RESERVE_KEYNAME[i])) {
				return normal;
			}
		}
		return "<" + key + "><![CDATA[" + value + "]]></" + key + ">";
	}
}
