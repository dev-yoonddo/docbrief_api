package com.docbrief.document.dto;

import com.docbrief.document.domain.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DocumentStatusResponse {
    private Long id;
    private DocumentStatus status;

}
