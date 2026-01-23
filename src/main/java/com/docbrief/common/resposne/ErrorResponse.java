package com.docbrief.common.resposne;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String Code;
    private String message;
}
