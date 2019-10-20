/**
 * Copyright (C) 2010, ������̫�Ƽ��ɷ����޹�˾
 * All rights reserved.
 * 
 * ��Ŀ���ƣ�@pavexternalws
 * �ļ����ƣ�@DESCipher.java
 * �ļ���ʶ��
 * ժ����Ҫ��
 *
 *
 * ��ǰ�汾��1.0
 * �������ߣ�@tl <a href="mailto:kingtay@tom.com">����</a>
 * ������ڣ�@Aug 17, 2010
 */
package com.hanson.jbpm.jpdl.cipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author tl
 * DES�ӽ��ܷ���
 */
public class DESCipher {
	
	
	private static String ALGORITHM = "DES";
	
	
	/**
	 * DES����
	 * @param s
	 * @param key ��Կ
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptData(String s,String key) throws Exception {
	    Cipher cipher = Cipher.getInstance(ALGORITHM + "/CBC/PKCS5Padding");
	    //��ԭʼ��Կ���ݴ���DESKeySpec����
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        //����һ����Կ������Ȼ��������DESKeySpecת����Secret Key����
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey keySpec = keyFactory.generateSecret(dks);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
	    cipher.init(Cipher.ENCRYPT_MODE, keySpec,iv);
	    return cipher.doFinal(s.getBytes("UTF-8"));
	}
	
	
	/**
	 * DES����
	 * @param b
	 * @param key ��Կ
	 * @return
	 * @throws Exception
	 */
	public static String decryptData(byte[] b,String key) throws Exception {
		 Cipher cipher = Cipher.getInstance(ALGORITHM + "/CBC/PKCS5Padding");
		    //��ԭʼ��Կ���ݴ���DESKeySpec����
	        DESKeySpec dks = new DESKeySpec(key.getBytes("UTF-8"));
	        //����һ����Կ������Ȼ��������DESKeySpecת����Secret Key����
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
	        SecretKey keySpec = keyFactory.generateSecret(dks);

		    //KeyGenerator generator = KeyGenerator.getInstance(algorithm, "123456");
		    //key = generator.generateKey();
	        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		    cipher.init(Cipher.DECRYPT_MODE, keySpec,iv);
		    return new String (cipher.doFinal(b),"UTF-8");
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println(encryptData("hello","12345678"));
	}
}
