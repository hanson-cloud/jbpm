package com.hanson.jbpm.identity;

/**
 * Created by Hanson on 2019/10/17.
 */
public class StringUtil {
    public StringUtil() {
    }

    public static boolean isChineseString(String s) {
        char c = s.charAt(0);
        return 19968 <= c && c < '麯';
    }

    public static void main(String[] args) {
        System.out.println(isChineseString("0123"));
        System.out.println(isChineseString("abcd"));
        System.out.println(isChineseString("受理组"));
    }
}
