package com.docbrief.document.text;

public class SentenceSplitter {

    private SentenceSplitter() {}

    public static String[] splitToSentences(String text) {
        // 번호, 로마자, 괄호 번호
        if (text.matches("^(\\d+|[IVX]+|\\([0-9]+\\))\\.\\s+.*")) {
            return new String[]{text};
        }
        return text.split("(?<=[.!?])\\s+");
    }
}
