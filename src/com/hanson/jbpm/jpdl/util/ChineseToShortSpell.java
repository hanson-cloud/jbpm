package com.hanson.jbpm.jpdl.util;


import java.util.*;
import java.io.*;
/**
 *
 * <p>Title:����gb2312-80���뼯������ת��Ϊƴ����д </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006  </p>
 * <p>Company:suntek </p>
 * @author not attributable
 * @version 1.0
 */
public class ChineseToShortSpell {
	//��ŵ�һ�����ֱ���ķ�Χ(gb2312-80)
	private static int[] alphGBcode = {
		1601, // ƴ������ĸa��ͷ�ĺ��ֱ�������;
		1637, // --b
		1833, //--c
		2078, //--d
		2274, //--e
		2302, //--f
		2433, //--g
		2594, //--h
		0, //--i,û����i��ͷ��ƴ��
		2787, //--j
		3106, //--k
		3212, //--l
		3472, //--m
		3635, //--n
		3722, //--o
		3730, //--p
		3858, //--q
		4027, //--r
		4086, //--s
		4390, //--t
		0, //--u,û����u��ͷ��ƴ��
		0, //--v,û����v��ͷ��ƴ��
		4558, //--w
		4684, //--x
		4925, //--y
		5249 //--z
	};
	/**
	 * ���ݺ����ַ������غ��ֶ�Ӧ����λ��
	 * @param cnchar
	 * @return
	 */
	private static int getGBcode(char cnchar) {
		byte[] bytes = String.valueOf(cnchar).getBytes();
		if (bytes.length == 1) { //cncharΪ���ַ���Ӣ����ĸʱ
			return 0;
		}
		else if (bytes.length == 2) {//Ϊ����
			int section = 256 + bytes[0] - 160; //���ֱ�������;
			int position = 256 + bytes[1] - 160; //���ֱ���λ��;
			return section * 100 + position;
		}
		return 0;
	}
	/**
	 * ���ض�Ӧ���ִ��Ķ�Ӧƴ����д
	 * @param cnStr
	 * @return
	 */
	public static String getShortSpell(String cnStr) {
		if (cnStr == null || "".equals(cnStr.trim())) {
			return cnStr;
		}
		char[] chars = cnStr.toCharArray();
		// System.out.print(""+chars[0]);
		StringBuffer retuBuf = new StringBuffer();
		for (int i = 0, len = chars.length; i < len; i++) {
			int gbcode = getGBcode(chars[i]);
			//System.out.print(""+gbcode);
			if (gbcode == 0) {
				retuBuf.append(chars[i]);
			}
			else {
				char first = getCharByGBcode(gbcode);
				// System.out.print(""+first);
				if (first == '\0') {
					retuBuf.append(chars[i]);
				}
				else {
					retuBuf.append(first);
				}
			}
		}
		return retuBuf.toString();
	}
	
	/**
	 * ���ݺ��ֵı���ֵ���ض�Ӧ��ƴ����ĸ
	 * @param gbcode-���ֵ���λ����ֵ
	 * @return
	 */
	private static char getCharByGBcode(int gbcode) {
		if (gbcode < 1601) {
			return '\0';
		}else if (gbcode <= 5589) {
			//1601--5589�ǵ�һ�����ֱ���ķ�Χ;
			int index = -1;
			for (int i = 25; i >= 0; i--) {
				if (gbcode > alphGBcode[i] && alphGBcode[i] != 0) {
					index = i;
					break;
				}
			}
			if (index != -1) {
				return (char) (index + 97);
			}else {
				return '\0';
			}
		} else if (gbcode > 5600 && gbcode < 8790) {
			//5601--8789�ǵڶ������ֱ���ķ�Χ;
			char ch = '\0';
			String line = null;
			boolean isFind=false;
			try {
				BufferedReader file = new BufferedReader(new FileReader("code.ini"));
				while ( (!isFind)&&(line = file.readLine()) != null) {
					/* ��ȡ�ļ��� */
					StringTokenizer stokenizer = new StringTokenizer(line);
					while ((!isFind)&&stokenizer.hasMoreTokens()) {
						String str = stokenizer.nextToken().trim();
						if(str.length()==1){
							ch=str.charAt(0);
						}
						if (str.length() == 4) {
							int code=Integer.parseInt(str);
							if(gbcode==code) isFind=true;
						}
					}
				}
				file.close();
				if(isFind==false){
					ch='\0';
				}
			}
			catch (Exception ex) {
				ch='\0';
			}
			return ch;
		}
		else {
			return '\0';
		}
	}
	public static void main(String[] args) {
		String str = "��ѯ����";
		System.out.println("short=" + ChineseToShortSpell.getShortSpell(str));
		str = "abc12";
		System.out.println("short=" + ChineseToShortSpell.getShortSpell(str));    
	}
}