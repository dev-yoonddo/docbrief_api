package com.docbrief.document.controller;

import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.dto.api.DocumentCreateResponse;
import com.docbrief.document.dto.api.DocumentStatusResponse;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/{id}/status")
    public ResponseEntity<DocumentStatusResponse> getDocumentStatus(@PathVariable Long id){
        DocumentStatus status = documentService.getStatus(id);
        return ResponseEntity.ok(new DocumentStatusResponse(id, status));
    }

    @PostMapping("/{id}/process")
    public SummaryInternalRequest process(@PathVariable Long id, @RequestParam("file") MultipartFile file){
        return documentService.buildSummaryRequest(id, file);
    }

}
