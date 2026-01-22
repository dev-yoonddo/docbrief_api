package com.docbrief.summary.controller;

import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.summary.service.SummaryProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@AllArgsConstructor
public class SummaryController {
    public SummaryProcessor summaryProcessor;

    @PostMapping("/{id}/summary")
    public String process(@PathVariable Long id
            , @RequestParam("type") String type
            , @RequestBody SummaryInternalRequest summaryInternalRequest){
        return summaryProcessor.startSummaryEngine(summaryInternalRequest);
    }
}

