package com.docbrief.document.service;

import com.docbrief.document.domain.Document;
import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.domain.DocumentType;
import com.docbrief.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentStatusService documentStatusService;
    private final DocumentParsingService documentParsingService;
    private final SummaryRequestService summaryRequestService;

    private final DocumentRepository documentRepository;

    public Long create(MultipartFile file){
        if(file == null || file.isEmpty()) { throw new IllegalArgumentException("file is required"); }
        if(file.getOriginalFilename() == null) { throw new IllegalArgumentException("invalid file name"); }

        DocumentType type = DocumentType.fromFileName(file.getOriginalFilename());
        Document document = new Document(file.getOriginalFilename(), type);
        documentRepository.save(document);
        return document.getDocumentId();
    }

    @Transactional(readOnly = true)
    public DocumentStatus getStatus(Long documentId){
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("documentId for getStatus not found ::: documentId : " + documentId));
        return document.getStatus();
    }

    public String processDocument(Long documentId, MultipartFile file){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for processing not found ::: documentId : " + documentId));
        DocumentStatus status = document.getStatus();

        switch(status) {
            case CREATED:
            case FAILED:
                documentParsingService.parseAndSaveDocument(documentId, file);
                return summarizeDocument(documentId);
            case EXTRACTING:
                return "this document is currently being extracted.";
            case EXTRACTED:
                return summarizeDocument(documentId);
            case SUMMARIZING:
                return "this document is currently being summarized.";
            case SUMMARIZED:
                return summaryRequestService.requestSummary(document);
            default:
                throw new IllegalStateException("unknown document status ::: status : " + status);
        }
    }

    public String summarizeDocument(Long documentId) {
        // 상태값 업데이트 시 document 엔티티의 영속이 분리될 수 있으므로 재조회
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for summarizing not found ::: documentId : " + documentId));
        documentStatusService.updateDocumentStatus(documentId, DocumentStatus.SUMMARIZING);

       try {
            String summary = summaryRequestService.requestSummary(document);
            documentStatusService.updateDocumentStatus(documentId, DocumentStatus.SUMMARIZED);
            return summary;
        } catch (Exception e) {
            documentStatusService.updateDocumentStatus(documentId, DocumentStatus.FAILED);
            throw e;
        }
    }

}
