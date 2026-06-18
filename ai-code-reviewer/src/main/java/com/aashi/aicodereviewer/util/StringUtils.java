package com.aashi.aicodereviewer.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for string operations.
 */
public class StringUtils {

    // Reverse a string
    public static String reverse(String input) {
        String result = "";
        for (int i = input.length() - 1; i >= 0; i--) {
            result += input.charAt(i);
        }
        return result;
    }

    // Check if a string is a palindrome
    public static boolean isPalindrome(String str) {
        str = str.toLowerCase();
        return str.equals(reverse(str));
    }

    // Count occurrences of a character
    public static int countChar(String text, char c) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    // Split string into words
    public static List<String> splitWords(String sentence) {
        List<String> words = new ArrayList<>();
        String[] parts = sentence.split(" ");
        for (String part : parts) {
            words.add(part);
        }
        return words;
    }
}
