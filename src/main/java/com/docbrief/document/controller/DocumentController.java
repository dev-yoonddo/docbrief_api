package com.docbrief.document.controller;

import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.dto.api.DocumentCreateRequest;
import com.docbrief.document.dto.api.DocumentCreateResponse;
import com.docbrief.document.dto.api.DocumentStatusResponse;
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
    // TODO:규격 협의 후 최종 반환타입 변경
    public ResponseEntity<?> parsDocument(@PathVariable Long id, @RequestParam MultipartFile file){
        String result = documentService.processDocument(id, file);
        return ResponseEntity.ok(result);
    }

}
