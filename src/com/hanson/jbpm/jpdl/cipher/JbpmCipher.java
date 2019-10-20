/**
 * Copyright (C) 2010, ������̫�Ƽ��ɷ����޹�˾
 * All rights reserved.
 * 
 * ��Ŀ���ƣ�@pavexternalws
 * �ļ����ƣ�@AVCipher.java
 * �ļ���ʶ��
 * ժ����Ҫ��
 *
 *
 * ��ǰ�汾��1.0
 * �������ߣ�@tl <a href="mailto:kingtay@tom.com">����</a>
 * ������ڣ�@Aug 17, 2010
 */
package com.hanson.jbpm.jpdl.cipher;



/**
 * @author tl
 * �ṩͳһ�ļӽ��ܺͱ��뷽��
 * ����ʱ,�Ƚ���BASE64����,�ٽ���DES����
 * ����ʱ,����DES����,�ٽ���BASE64����
 */
public class JbpmCipher {
	
	//8λ DES��Կ
	private String DESKey = "rO0ABXNy";
	
	public JbpmCipher() {
		
	}
	
	/**
	 * �����͵������Ƚ���DES����,�ٽ���BASE64����
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	public String enCrypt(String data) throws Exception {
		
		return Base64Cipher.encode(DESCipher.encryptData(data, DESKey));
	}
	
	
	/**
	 * ���յ�����ʱ�Ƚ���BASE64����,�ٽ���DES����
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String deCrypt(String data) throws Exception {
		
		return DESCipher.decryptData(Base64Cipher.decode(data), DESKey);
	}
	
	
	public static void main(String args[]) {
		String s = "qZ2sKytJCGn/7pLeRr9SYK4lkyK0PQOCgtqjm6bJA9HaveSoW8Wrd8mkYQ+60D1g+oT3Vo7G4NzXFp55yoIdukIDTFtB8GWwHh18mMzJtr12tT0nrXsKCS6jajg666xw";
		try {
			String pwd = new JbpmCipher().enCrypt("suntek");
			System.out.println(pwd);
			System.out.println(new JbpmCipher().deCrypt(pwd));
			
		}catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
