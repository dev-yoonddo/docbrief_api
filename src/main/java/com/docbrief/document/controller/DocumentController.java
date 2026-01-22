package com.docbrief.document.controller;

import com.docbrief.document.domain.DocumentStatus;
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
@CrossOrigin(origins="http://localhost:5173")
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

    @PostMapping("/{id}/parse")
    // TODO:규격 협의 후 최종 반환타입 변경
    public ResponseEntity<?> parsDocument(@PathVariable Long id,  @RequestParam("file") MultipartFile file){
        String result = documentService.processDocument(id, file);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/{id}/url/parse")
    public ResponseEntity<?> parsDocument(@PathVariable Long id,  @RequestParam("url") String url){
        String result = documentService.processUrlParsing(id, url);
        return ResponseEntity.ok(result);
    }
}
