package com.aashi.aicodereviewer.util;

public class StringUtils {

    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public static boolean isPalindrome(String str) {
        return str.equals(reverse(str));
    }
}
