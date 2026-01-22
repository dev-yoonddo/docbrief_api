package com.docbrief.document.controller;

import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.dto.api.DocumentCreateResponse;
import com.docbrief.document.dto.api.DocumentStatusResponse;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.service.DocumentService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@CrossOrigin(origins="http://localhost:5173")
@AllArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentCreateResponse> create(@RequestParam("file") MultipartFile file){
        Long documentId = documentService.create(file);
        return ResponseEntity.ok(new DocumentCreateResponse(documentId));
    }

    @PostMapping("/from-url")
    public ResponseEntity<DocumentCreateResponse> createFromUrl(@RequestParam("url") String url){
        Long documentId = documentService.createFromUrl(url);
        return ResponseEntity.ok(new DocumentCreateResponse(documentId));
    }

    @PostMapping("/{id}/process")
    public SummaryInternalRequest process(@PathVariable Long id, @RequestParam("file") MultipartFile file){
        return documentService.processDocumentParsing(id, file);
    }

    @PostMapping("/{id}/url/process")
    public SummaryInternalRequest processFromUrl(@PathVariable Long id,  @RequestParam("url") String url){
        SummaryInternalRequest request = documentService.processUrlParsing(id, url);

        log.info("dddddd");
        return request;
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<DocumentStatusResponse> getDocumentStatus(@PathVariable Long id){
        DocumentStatus status = documentService.getStatus(id);
        return ResponseEntity.ok(new DocumentStatusResponse(id, status));
    }
}
