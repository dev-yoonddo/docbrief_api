package com.docbrief.document.controller;

import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.dto.api.DocumentCreateResponse;
import com.docbrief.document.dto.api.DocumentStatusResponse;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.service.DocumentService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
@AllArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    // 최초 세션 생성용 (optional)
    @GetMapping("/session/init")
    public String initSession(HttpSession session) {
        return session.getId();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestParam(value="mode") String mode,
                                    @RequestParam(required = false, value="file") MultipartFile file,
                                    @RequestParam(required = false, value="url") String url){
        Long documentId = documentService.create(mode, file, url);
        return ResponseEntity.ok(new DocumentCreateResponse(documentId));
    }

    @PostMapping("/{id}/process")
    public SummaryInternalRequest process(@PathVariable Long id,
                                     @RequestParam(value="mode") String mode,
                                     @RequestParam(required = false, value="file") MultipartFile file,
                                     @RequestParam(required = false, value="url") String url){
        return documentService.processParsing(mode, id, file, url);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<DocumentStatusResponse> getDocumentStatus(@PathVariable Long id){
        DocumentStatus status = documentService.getStatus(id);
        return ResponseEntity.ok(new DocumentStatusResponse(id, status));
    }
}
