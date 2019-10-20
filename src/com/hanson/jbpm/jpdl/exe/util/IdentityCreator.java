package com.hanson.jbpm.jpdl.exe.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.hanson.jbpm.mgmt.ProcessClient;


public class IdentityCreator {
	
	private static AtomicInteger taskIdx = new AtomicInteger(0);
	private static AtomicInteger instIdx = new AtomicInteger(0);
	private static AtomicInteger draftIdx = new AtomicInteger(0);
	
	private static AtomicInteger bizidSequence = new AtomicInteger(0);
	
	private static Random rand = new Random();
	
	public static int checkBizidSequenceDuplicated(int bizid) {
		if (bizid <= bizidSequence.intValue()) {
			bizidSequence.incrementAndGet();
			return bizidSequence.intValue();
		} else {
			bizidSequence.set(bizid);
			return bizid;
		}
	}
	
	public static int getBizId()
	{
		return bizidSequence.incrementAndGet();
	}
	
	public static void initBizId(int maxId)
	{
		bizidSequence.set(maxId);
	}
	
	public static String getRandom(int i) {
		int x = rand.nextInt(i);
		return String.format("%0" + String.valueOf(i).length() + "d", x);
	}
	
	private static String getIncrementId(AtomicInteger idx, int len) {
		boolean stepIsOne = false;
		int next;
		
		do {
			idx.compareAndSet((int) Math.pow(10,3), 0);
			next = idx.get();
			stepIsOne = idx.compareAndSet(next, next+1);			
		} while (!stepIsOne);
		
		return String.format("%0" + len + "d", next);
	}
	
	/**
	 * ���� ������ʱ���� + �����
	 * @param len ���������
	 */ 
	public static String getTaskId(int len) {
		
		return dateFullFormat(new Date()) + getHostNum() + getIncrementId(taskIdx, len);
	}
	
	public static String getDraftId(int len) {
		return dateFullFormat(new Date()) + getHostNum() + getIncrementId(draftIdx, len);
	}
	
	public static String getInstId(int len) {
		return dateFullFormat(new Date()) + getHostNum() + getIncrementId(instIdx, len);
	}
	
	
	private static String getHostNum() {
		String host = ProcessClient.HOST;
		if(host.length() > 1) {
			host = host.substring(host.length()-1, host.length());
		}
		return host;
	}
	
	/**
	 * ���ڸ�ʽ�����
	 * @param date
	 * @return
	 */
	private static String dateFullFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        if (date != null) {
            return sdf.format(date);
        } else {
            return "";
        }
    }
	
	/**
     * �����漴����/
     * @param pwd_len ���ɵ�������ܳ���
     * @return  ������ַ���
     */
    /*private String genRandomNum(int pwd_len) {
        //35����Ϊ�����Ǵ�0��ʼ�ģ�26����ĸ+10������
        final int maxNum = 36;
        int i; //���ɵ������;
        int count = 0; //���ɵ�����ĳ���?
        char[] str = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                     'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                     'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
                     '9'};
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < pwd_len) {
            //�����������ȡ����ֵ����ֹ���ɸ����� 
            i = Math.abs(r.nextInt(maxNum)); //���ɵ������Ϊ36-1
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }*/
    
    public static void main(String[] args) {
    	//System.out.println(Long.parseLong("2012032209512900985"));
    	//System.out.println(System.currentTimeMillis()/1000);
    	//System.out.println(IdentityCreator.getId(3));
    }
}
