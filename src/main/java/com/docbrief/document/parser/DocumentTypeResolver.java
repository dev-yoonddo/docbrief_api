package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class DocumentTypeResolver {

     public DocumentType resolveFromFileName(String fileName){
         int idx = fileName.lastIndexOf(".");
         if(idx == -1) { throw new IllegalArgumentException("this file has no extension ::: originalFileName : " + fileName); }

         String extension = fileName.substring(idx + 1).toLowerCase();

         return switch (extension) {
             case "txt" -> DocumentType.TXT;
             case "pdf" -> DocumentType.PDF;
             case "docx" -> DocumentType.DOCX;
             case "hwpx" -> DocumentType.HWPX;
             default -> throw new IllegalArgumentException("unsupported file extension ::: extension : ß" + extension);
         };
     }
}
