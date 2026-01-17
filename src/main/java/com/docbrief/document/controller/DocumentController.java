package com.docbrief.document.controller;

import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.dto.DocumentCreateRequest;
import com.docbrief.document.dto.DocumentCreateResponse;
import com.docbrief.document.dto.DocumentStatusResponse;
import com.docbrief.document.service.DocumentParsingService;
import com.docbrief.document.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
@AllArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    private final DocumentParsingService documentParsingService;

    @PostMapping
    public ResponseEntity<DocumentCreateResponse> createDocumentId(@RequestBody DocumentCreateRequest request){
        Long documentId = documentService.create(request);
        return ResponseEntity.ok(new DocumentCreateResponse(documentId));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<DocumentStatusResponse> getDocumentStatus(@PathVariable Long id){
        DocumentStatus status = documentService.getStatus(id);
        return ResponseEntity.ok(new DocumentStatusResponse(id, status));
    }

    @PostMapping("/{id}/parse")
    public ResponseEntity<?> parsDocument(@PathVariable Long id, @RequestParam MultipartFile file){
        documentParsingService.parseAndSaveDocument(id, file);
        return ResponseEntity.ok().build();
    }

}
