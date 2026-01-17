package com.docbrief.document.service;

import com.docbrief.document.domain.Document;
import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.dto.api.DocumentCreateRequest;
import com.docbrief.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final SummaryRequestService summaryRequestService;
    private final DocumentParsingService documentParsingService;

    private final DocumentRepository documentRepository;

    public Long create(DocumentCreateRequest request){
        Document document = new Document(request.getTitle(), request.getDocumentType());
        documentRepository.save(document);
        return document.getDocumentId();
    }

    @Transactional(readOnly = true)
    public DocumentStatus getStatus(Long documentId){
            Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for getStatus not found"));
        return document.getStatus();
    }

    @Transactional
    public String processDocument(Long documentId, MultipartFile file){
        Document document = documentRepository.findByDocumentId(documentId);
        DocumentStatus status = document.getStatus();
        switch(status) {
            case UPLOADED:
            case FAILED:
                documentParsingService.parseAndSaveDocument(document, file);
                return summarizeDocumentAsync(document);
            case EXTRACTING:
                return "this document is currently being extracted.";
            case EXTRACTED:
                return summarizeDocumentAsync(document);
            case SUMMARIZING:
                return "this document is currently being summarized.";
            case SUMMARIZED:
                return summaryRequestService.requestSummary(documentId);
            default:
                throw new IllegalStateException("unknown document status: " + status);
        }
    }

    @Async
    @Transactional
    public String summarizeDocumentAsync(Document document) {
        document.updateStatus(DocumentStatus.SUMMARIZING);
        try {
            String summary = summaryRequestService.requestSummary(document.getDocumentId());
            document.updateStatus(DocumentStatus.SUMMARIZED);
            return summary;
        } catch (Exception e) {
            document.updateStatus(DocumentStatus.FAILED);
            throw e;
        }
    }

}
