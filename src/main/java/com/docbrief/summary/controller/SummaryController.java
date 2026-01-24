package com.docbrief.summary.controller;

import com.docbrief.document.dto.api.DocumentCreateResponse;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.summary.domain.SummaryResponse;
import com.docbrief.summary.service.SummaryProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@AllArgsConstructor
@CrossOrigin(origins="http://localhost:5173")
public class SummaryController {

    public SummaryProcessor summaryProcessor;

    /**
     * AI 요약 요청
     *
     * @param id: 문서 키값
     *        ,type: 요약 대상 타입 (document/url)
     *        , summaryInternalRequest: 파싱된 요약 대상
     * @return SummaryResponse
     */
    @PostMapping("/{id}/summary")
    public ResponseEntity<SummaryResponse>  process(@PathVariable Long id
            , @RequestParam("type") String type
            , @RequestBody SummaryInternalRequest summaryInternalRequest){
        SummaryResponse summaryResponse = summaryProcessor.startSummaryEngine(summaryInternalRequest);
        return ResponseEntity.ok(summaryResponse);
    }
}

