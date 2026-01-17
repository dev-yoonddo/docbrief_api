package com.docbrief.document.dto.api;

import com.docbrief.document.domain.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DocumentCreateRequest {
    private String title;
    private DocumentType documentType;

}
