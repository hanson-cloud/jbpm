/**
 * Copyright (C) 2010, ������̫�Ƽ��ɷ����޹�˾
 * All rights reserved.
 * 
 * ��Ŀ���ƣ�@pavexternalws
 * �ļ����ƣ�@Base64Cipher.java
 * �ļ���ʶ��
 * ժ����Ҫ��
 *
 *
 * ��ǰ�汾��1.0
 * �������ߣ�@tl <a href="mailto:kingtay@tom.com">����</a>
 * ������ڣ�@Aug 17, 2010
 */
package com.hanson.jbpm.jpdl.cipher;

import java.io.IOException;

/**
 * @author tl
 * �ṩBase64�ı���ͽ��뷽��
 */
public class Base64Cipher {
	
	
	/**  
     * ����  
     * @param bstr  
     * @return String  
     */  
    public static String encode(byte[] bstr){   
    	return new sun.misc.BASE64Encoder().encode(bstr).replace("\n", "").replace("\r", "");   
    }   
  
    /**  
     * ����  
     * @param str  
     * @return string  
     * @throws IOException 
     */  
    public static byte[] decode(String str) throws IOException{   
    	byte[] bt = null;   
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();   
        bt = decoder.decodeBuffer( str );   
        return bt;   
    }   
    
    

}
