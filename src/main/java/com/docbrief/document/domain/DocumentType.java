package com.docbrief.document.domain;

public enum DocumentType {
    PDF,
    DOCX,
    HWPX,
    TXT,
    URL;

    public static DocumentType fromFileName(String fileName){
        String lower = fileName.toLowerCase();

        if(lower.endsWith(".pdf")) { return PDF; }
        if(lower.endsWith(".docx")) { return DOCX; }
        if(lower.endsWith(".hwpx")) { return HWPX; }
        if(lower.endsWith(".txt")) { return TXT; }

        throw new IllegalArgumentException("unsupported file type : " + fileName);
    }
}

