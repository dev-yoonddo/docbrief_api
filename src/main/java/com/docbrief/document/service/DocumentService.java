package com.docbrief.document.service;

import com.docbrief.document.domain.Document;
import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.domain.DocumentType;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
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

    public Long createFromUrl(String url){
        if (url == null || url.isBlank()) { throw new IllegalArgumentException("url is required"); }

        DocumentType type = DocumentType.HTML;
        Document document = new Document(url, type);
        documentRepository.save(document);
        return document.getDocumentId();
    }

    @Transactional(readOnly = true)
    public DocumentStatus getStatus(Long documentId){
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("documentId for getStatus not found ::: documentId : " + documentId));
        return document.getStatus();
    }

    public SummaryInternalRequest processDocumentParsing(Long documentId, MultipartFile file){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for processing not found ::: documentId : " + documentId));
        DocumentStatus status = document.getStatus();
        switch(status) {
            case CREATED:
            case FAILED:
                documentParsingService.parseAndSaveDocument(documentId, file);
                break;
            case EXTRACTING:
                throw new IllegalStateException("this document is currently being extracted.");
            case EXTRACTED:
                break;
            case SUMMARIZING:
                throw new IllegalStateException("this document is currently being summarized.");
            case SUMMARIZED:
                break;
            default:
                throw new IllegalStateException("unknown document status ::: status : " + status);
        }

        if (document.getStatus() != DocumentStatus.EXTRACTED){ throw new IllegalStateException("document is not ready for summarizing ::: documentStatus : " + document.getStatus());         }

        return summaryRequestService.buildSummaryInternalRequest(document);
    }

    public SummaryInternalRequest processUrlParsing(Long documentId, String url){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for processing not found ::: documentId : " + documentId));
        DocumentStatus status = document.getStatus();
        switch(status) {
            case CREATED:
            case FAILED:
                documentParsingService.parseAndSaveUrlHtml(documentId, url);
                break;
            case EXTRACTING:
                throw new IllegalStateException("this document is currently being extracted.");
            case EXTRACTED:
            case SUMMARIZING:
                throw new IllegalStateException("this document is currently being summarized.");
            case SUMMARIZED:
                break;
            default:
                throw new IllegalStateException("unknown document status ::: status : " + status);
        }
        return summaryRequestService.buildSummaryInternalRequest(document);
    }

}
